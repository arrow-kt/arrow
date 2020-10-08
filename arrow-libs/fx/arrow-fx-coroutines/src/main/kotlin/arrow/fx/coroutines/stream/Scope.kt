package arrow.fx.coroutines.stream

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.left
import arrow.core.orElse
import arrow.fx.coroutines.Atomic
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.Platform
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.SuspendConnection
import arrow.fx.coroutines.Token
import arrow.fx.coroutines.deleteFirst
import arrow.fx.coroutines.guarantee
import arrow.fx.coroutines.prependTo
import arrow.fx.coroutines.raceN
import arrow.fx.coroutines.uncons
import kotlin.coroutines.coroutineContext

/**
 * Implementation of [Scope] for the internal stream interpreter.
 *
 * Represents a period of stream execution in which resources are acquired and released.
 * A scope has a state, consisting of resources (with associated finalizers) acquired in this scope
 * and child scopes spawned from this scope.
 *
 * === Scope lifetime ===
 *
 * When stream interpretation starts, one `root` scope is created. Scopes are then created and closed based on the
 * stream structure. Every time a `Pull` is converted to a `Stream`, a scope is created.
 *
 * For example, `s.chunks` is defined with `s.repeatPull` which in turn is defined with `Pull.loop(...).stream`.
 * In this case, a single scope is created as a result of the call to `.stream`.
 *
 * Scopes may also be opened and closed manually with `Stream#scope`. For the stream `s.scope`, a scope
 * is opened before evaluation of `s` and closed once `s` finishes evaluation.
 *
 * === Scope organization ===
 *
 * Scopes are organized in tree structure, with each scope having at max one parent (a root scope has no parent)
 * with 0 or more child scopes.
 *
 * Every time a new scope is created, it inherits parent from the current scope and adds itself as a child
 * of that parent.
 *
 * During the interpretation of nondeterministic streams (i.e. merge), there may be multiple scopes attached
 * to a single parent and these scopes may be created and closed in a nondeterministic order.
 *
 * A child scope never outlives its parent scope. I.e., when a parent scope is closed for whatever reason,
 * the child scopes are closed too.
 *
 * === Resources ===
 *
 * The primary role of a scope is tracking resource allocation and release. The stream interpreter guarantees that
 * resources allocated in a scope are always released when the scope closes.
 *
 * === Resource allocation ===
 *
 * Resources are allocated when the interpreter interprets the `Acquire` element, which is typically constructed
 * via `Stream.bracket`. See [arrow.fx.coroutines.Resource] docs for more information.
 *
 * @param id Unique identification of the scope
 * @param parent If empty indicates root scope. If non-empty, indicates parent of this scope.
 * @param interruptible If defined, allows this scope to interrupt any of its operation. Interruption
 *                       is performed using the supplied context.
 *                       Normally the interruption awaits next step in Pull to be evaluated, with exception
 *                       of Eval, that when interruption is enabled on scope will be wrapped in race,
 *                       that eventually allows interruption while eval is evaluating.
 *
 */
class Scope private constructor(
  internal val id: Token,
  internal val parent: Scope?,
  private val interruptible: InterruptContext?
) {

  companion object {
    internal fun newRoot(): Scope =
      Scope(Token(), null, null)
  }

  private val state: Atomic<State> =
    Atomic.unsafe(State.initial)

  /**
   * Opens a child scope.
   *
   * If this scope is currently closed, then the child scope is opened on the first
   * open ancestor of this scope.
   *
   * Returns scope that has to be used in next compilation step.
   */
  internal suspend fun open(
    isInterruptible: Boolean
  ): Scope {
    val scope = createCompileScope(isInterruptible)
    val s = state.modify { s ->
      if (!s.open) Pair(s, null)
      else Pair(s.copy(children = scope prependTo s.children), scope)
    }

    return when (s) {
      null -> {
        // This scope is already closed so try to promote the open to an ancestor; this can fail
        // if the root scope has already been closed, in which case, we can safely throw
        when (parent) {
          null -> throw IllegalStateException("cannot re-open root scope")
          else -> {
            interruptible?.cancelParent?.invoke()
            parent.open(isInterruptible)
          }
        }
      }
      else -> s
    }
  }

  /*
 * Creates a context for a new scope.
 *
 * We need to differentiate between three states:
 *  The new scope is not interruptible -
 *     It should respect the interrupt of the current scope. But it should not
 *     close the listening on parent scope close when the new scope will close.
 *
 *  The new scope is interruptible but this scope is not interruptible -
 *     This is a new interrupt root that can be only interrupted from within the new scope or its children scopes.
 *
 *  The new scope is interruptible as well as this scope is interruptible -
 *     This is a new interrupt root that can be interrupted from within the new scope, its children scopes
 *     or as a result of interrupting this scope. But it should not propagate its own interruption to this scope.
 *
 */
  private suspend fun createCompileScope(
    isInterruptible: Boolean
  ): Scope {
    val newScopeId = Token()
    return when (interruptible) {
      null -> {
        val iCtx = if (true) InterruptContext.unsafeFromInterruptible(newScopeId) else null
        Scope(newScopeId, this, iCtx)
      }
      else -> {
        val fiCtx = interruptible.childContext(isInterruptible, newScopeId)
        Scope(newScopeId, this, fiCtx)
      }
    }
  }

  /**
   * Stream is interpreted synchronously, as such the resource acquisition is fully synchronous.
   * No next step (even when stream was interrupted) is run before the resource
   * is fully acquired.
   *
   * If, during resource acquisition the stream is interrupted, this will still await for the resource to be fully
   * acquired, and then such stream will continue, likely with resource cleanup during the interpretation of the stream.
   *
   * There is only one situation where resource cleanup may be somewhat concurrent and that is when resources are
   * leased in `parJoin`. But even then the order of the lease of the resources respects acquisition of the resources that leased them.
   */
  internal suspend fun <R> acquireResource(
    fr: suspend () -> R,
    release: suspend (R, ExitCase) -> Unit
  ): Either<Throwable, R> {
    val conn = coroutineContext[SuspendConnection] ?: SuspendConnection.uncancellable
    val scope = ScopedResource()
    return Either.catch(fr).flatMap { resource ->
      scope.acquired { ex: ExitCase -> release(resource, ex) }.map { registered ->
        state.modify {
          if (conn.isCancelled() && registered) Pair(it, suspend { release(resource, ExitCase.Cancelled) })
          else Pair(it.copy(resources = scope prependTo it.resources), suspend { Unit })
        }.invoke()
        resource
      }
    }
  }

  /**
   * Unregisters the child scope identified by the supplied id.
   *
   * As a result of unregistering a child scope, its resources are no longer
   * reachable from its parent.
   */
  private suspend fun releaseChildScope(id: Token): Unit =
    state.update { it.unregisterChild(id) }

  /** Returns all direct resources of this scope (does not return resources in ancestor scopes or child scopes). **/
  internal suspend fun resources(): List<ScopedResource> =
    state.get().resources

  /**
   * Traverses supplied `Chain` with `f` that may produce a failure, and collects these failures.
   * Returns failure with collected failures, or `Unit` on successful traversal.
   */
  private suspend fun <A> traverseError(
    ca: List<A>,
    f: suspend (A) -> Either<Throwable, Unit>
  ): Either<Throwable, Unit> {
    val errors = ca.map { a -> f(a) }
      .mapNotNull { it.fold({ it }) { null } }

    return if (errors.isNotEmpty()) Either.Left(Platform.composeErrors(errors.first(), errors.drop(1)))
    else Either.Right(Unit)
  }

  /**
   * Closes this scope.
   *
   * All resources of this scope are released when this is evaluated.
   *
   * Also this will close the child scopes (if any) and release their resources.
   *
   * If this scope has a parent scope, this scope will be unregistered from its parent.
   *
   * Note that if there were leased or not yet acquired resources, these resource will not yet be
   * finalized after this scope is closed, but they will get finalized shortly after. See [ScopedResource] for
   * more details.
   */ // TODO refactor to `throw` instead and remove `Either` wrapping.
  internal suspend fun close(ec: ExitCase): Either<Throwable, Unit> {
    val previous = state.modify { s -> Pair(s.close(), s) }
    val resultChildren = traverseError(previous.children) { it.close(ec) }
    val resultResources = traverseError(previous.resources) { it.release(ec) }
    interruptible?.cancelParent?.invoke()
    parent?.releaseChildScope(id)

    val errors = resultChildren.fold({ listOf(it) }, { emptyList() }) +
      resultResources.fold({ listOf(it) }, { emptyList() })

    return if (errors.isNotEmpty()) Either.Left(Platform.composeErrors(errors.first(), errors.drop(1)))
    else Either.Right(Unit)
  }

  /** Returns closest open parent scope or root. */
  internal suspend fun openAncestor(): Scope =
    when (parent) {
      null -> this
      else -> {
        val s = parent.state.get()
        if (s.open) parent else parent.openAncestor()
      }
    }

  /** Gets all ancestors of this scope, inclusive of root scope. **/
  private fun ancestors(): List<Scope> {
    tailrec fun go(curr: Scope, acc: List<Scope>): List<Scope> =
      when (val parent = curr.parent) {
        null -> acc
        else -> go(parent, acc + parent)
      }

    return go(this, emptyList())
  }

  /** finds ancestor of this scope given `scopeId` **/
  internal fun findSelfOrAncestor(scopeId: Token): Scope? {
    tailrec fun go(curr: Scope): Scope? =
      if (curr.id == scopeId) curr
      else when (curr.parent) {
        null -> null
        else -> go(curr.parent)
      }

    return go(this)
  }

  /** finds scope in child hierarchy of current scope **/
  // TODO refactor to `?`, write nullable test to check nested cases
  internal suspend fun findSelfOrChild(scopeId: Token): Option<Scope> {
    suspend fun go(scopes: List<Scope>): Option<Scope> =
      when (val uncons = scopes.uncons()) {
        null -> None
        else -> {
          if (uncons.first.id == scopeId) Some(uncons.first)
          else {
            val s = uncons.first.state.get()
            if (s.children.isEmpty()) go(uncons.second)
            else when (val ss = go(s.children)) {
              None -> go(uncons.second)
              is Some -> Some(ss.t)
            }
          }
        }
      }

    return if (id == scopeId) Some(this) else go(state.get().children)
  }

  /**
   * Tries to locate scope for the step.
   * It is good chance, that scope is either current scope or the sibling of current scope.
   * As such the order of search is:
   * - check if id is current scope,
   * - check if id is parent or any of its children
   * - traverse all known scope ids, starting from the root.
   *
   */
  internal suspend fun findStepScope(scopeId: Token): Option<Scope> =
    if (scopeId == id) Some(this)
    else when (this.parent) {
      null -> findSelfOrChild(scopeId)
      else -> when (val scope = parent.findSelfOrChild(scopeId)) {
        None -> findStepScopeLoop(this).findSelfOrChild(scopeId)
        is Some -> scope
      }
    }

  private tailrec fun findStepScopeLoop(scope: Scope): Scope =
    when (val p = scope.parent) {
      null -> scope
      else -> findStepScopeLoop(p)
    }

  /**
   * Leases the resources of this scope until the returned lease is cancelled.
   *
   * Note that this leases all resources in this scope, resources in all parent scopes (up to root)
   * and resources of all child scopes.
   *
   * `None` is returned if this scope is already closed. Otherwise a lease is returned,
   * which must be cancelled. Upon cancellation, resource finalizers may be run, depending on the
   * state of the owning scopes.
   *
   * Resources may be finalized during the execution of this method and before the lease has been acquired
   * for a resource. In such an event, the already finalized resource won't be leased. As such, it is
   * important to call `lease` only when all resources are known to be non-finalized / non-finalizing.
   *
   * When the lease is returned, all resources available at the time `lease` was called have been
   * successfully leased.
   */
  suspend fun lease(): Lease? {
    val s = state.get()
    return if (!s.open) null
    else {
      val allScopes = s.children + this + ancestors()
      val allResources = allScopes.flatMap { it.resources() }
      val allLeases = allResources.mapNotNull { it.lease() }
      val lease = object : Lease() {
        override suspend fun cancel(): Either<Throwable, Unit> =
          traverseError(allLeases) { it.cancel() }
      }
      lease
    }
  }

  /**
   * Interrupts evaluation of the current scope. Only scopes previously indicated with Stream.interruptScope may be interrupted.
   * For other scopes this will fail.
   *
   * Interruption is final and may take two forms:
   *
   * When invoked on right side, that will interrupt only current scope evaluation, and will resume when control is given
   * to next scope.
   *
   * When invoked on left side, then this will inject given throwable like it will be caused by stream evaluation,
   * and then, without any error handling the whole stream will fail with supplied throwable.
   *
   */
  suspend fun interrupt(cause: Either<Throwable, Unit>): Unit {
    when (interruptible) {
      null -> throw IllegalStateException("Scope#interrupt called for Scope that cannot be interrupted")
      else -> {
        // note that we guard interruption here by Attempt to prevent failure on multiple sets.
        val interruptCause = cause.map { interruptible.interruptRoot }
        guarantee({ interruptible.deferred.complete(interruptCause) }) {
          interruptible.ref.update { it.orElse { Some(interruptCause) } }
        }
      }
    }
  }

  /**
   * Checks if current scope is interrupted.
   * If yields to None, scope is not interrupted and evaluation may normally proceed.
   * If yields to Some(Right(scope,next)) that yields to next `scope`, that has to be run and `next`  stream
   * to evaluate
   */ // TODO rewrite to try/catch if possible
  internal suspend fun isInterrupted(): Option<Either<Throwable, Token>> =
    when (interruptible) {
      null -> None
      else -> interruptible.ref.get()
    }

  /**
   * When the stream is evaluated, there may be `Eval` that needs to be cancelled early,
   * when scope allows interruption.
   * Instead of just allowing eval to complete, this will race between eval and interruption promise.
   * Then, if eval completes without interrupting, this will return on `Right`.
   *
   * However when the evaluation is normally interrupted the this evaluates on `Left` - `Right` where we signal
   * what is the next scope from which we should calculate the next step to take.
   *
   * Or if the evaluation is interrupted by a failure this evaluates on `Left` - `Left` where the exception
   * that caused the interruption is returned so that it can be handled.
   */ // TODO return Pull.Result here, only usage in `Compiler` reconstructs into Pull.Result
  internal suspend fun <A> interruptibleEval(f: suspend () -> A): Either<Either<Throwable, Token>, A> =
    when (interruptible) {
      null -> Either.catch(f).mapLeft { it.left() }
      else -> {
        val res = raceN({ interruptible.deferred.get() }, { Either.catch(f) })
        when (res) {
          is Either.Right -> res.b.mapLeft { it.left() }
          is Either.Left -> Either.Left(res.a)
        }
      }
    }

  override fun toString() =
    "Scope(id=$id,interruptible=$interruptible)"

  /**
   * State of a scope.
   *
   * @param open Yields to true if the scope is open
   *
   * @param resources All acquired resources (that means synchronously, or the ones acquired asynchronously) are
   *                           registered here. Note that the resources are prepended when acquired, to be released in reverse
   *                           order s they were acquired.
   *
   * @param children Children of this scope. Children may appear during the parallel pulls where one scope may
   *                           split to multiple asynchronously acquired scopes and resources.
   *                           Still, likewise for resources they are released in reverse order.
   *
   */
  private data class State(val open: Boolean, val resources: List<ScopedResource>, val children: List<Scope>) {

    fun unregisterChild(id: Token): State =
      when (val res = children.deleteFirst { it.id == id }) {
        null -> this
        else -> copy(children = res.second)
      }

    fun close(): State = closed

    companion object {
      val initial =
        State(open = true, resources = emptyList(), children = emptyList())

      val closed =
        State(open = false, resources = emptyList(), children = emptyList())
    }
  }

  /**
   * A context of interruption status. This is shared from the parent that was created as interruptible to all
   * its children. It assures consistent view of the interruption through the stack
   *
   * @param ref When None, scope is not interrupted,
   *                 when Some(None) scope was interrupted, and shall continue with `whenInterrupted`
   *                 when Some(Some(err)) scope has to be terminated with supplied failure.
   *
   * @param interruptRoot Id of the scope that is root of this interruption and is guaranteed to be a parent of this scope.
   *
   * @param cancelParent Cancels listening on parent's interrupt.
   */
  internal data class InterruptContext(
    val deferred: Promise<Either<Throwable, Token>>,
    val ref: Atomic<Option<Either<Throwable, Token>>>,
    val interruptRoot: Token,
    val cancelParent: suspend () -> Unit
  ) {

    /**
     * Creates an [InterruptContext] for a interruptible child scope
     * This will ensure that when this scope interrupts it will interrupt the child scope as well.
     *
     * This will also make sure that a close on the child scope will not cancel listening
     * on the parent interruption for this scope.
     *
     * @param newScopeId The id of the new scope.
     */
    suspend fun childContext(isInterruptible: Boolean, newScopeId: Token): InterruptContext =
      if (isInterruptible) {
        val f = ForkConnected { deferred.get() }

        val context = InterruptContext(
          deferred = Promise.unsafe(),
          ref = Atomic.unsafe(None),
          interruptRoot = newScopeId,
          cancelParent = suspend { f.cancel() }
        )

        ForkConnected {
          val interrupt = f.join()
          context.ref.update { it.orElse { Some(interrupt) } }
          context.deferred.complete(interrupt)
        }

        context
      } else copy(cancelParent = suspend { Unit })

    companion object {
      /**
       * Creates a new interrupt context for a new scope if the scope is interruptible.
       *
       * This is UNSAFE method as we are creating promise and ref directly here.
       *
       * @param interruptible Whether the scope is interruptible by providing effect, execution context and the
       *                       continuation in case of interruption.
       * @param newScopeId The id of the new scope.
       */
      fun unsafeFromInterruptible(newScopeId: Token): InterruptContext =
        InterruptContext(
          deferred = Promise.unsafe(),
          ref = Atomic.unsafe(None),
          interruptRoot = newScopeId,
          cancelParent = suspend { Unit }
        )
    }
  }

  /**
   * Represents one or more resources that were leased from a scope, causing their
   * lifetimes to be extended until `cancel` is invoked on this lease.
   */
  abstract class Lease {

    /**
     * Cancels the lease of all resources tracked by this lease.
     *
     * This may run finalizers on some of the resources (depending on the state of their owning scopes).
     * If one or more finalizers fail, the returned action completes with a `Left(t)`, providing the failure.
     */
    abstract suspend fun cancel(): Either<Throwable, Unit>
  }
}
