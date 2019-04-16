package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.core.nonFatalOrThrow
import arrow.core.*
import arrow.effects.*
import arrow.effects.IORunLoop.startCancelable
import arrow.effects.internal.Platform
import arrow.effects.suspended.fx.FxRunLoop.startCancelable
import java.util.concurrent.Executor
import kotlin.coroutines.*
import kotlin.coroutines.Continuation

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal object FxRunLoop {

  suspend operator fun <A> invoke(source: FxOf<A>): A = suspendCoroutine { cont ->
    FxRunLoop.start(source) {
      it.fold(cont::resumeWithException, cont::resume)
    }
  }

  fun <A> start(source: FxOf<A>, cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(source, NonCancelable, cb as (Either<Throwable, Any?>) -> Unit, null, null, null)

  fun <A> startCancelable(fa: FxOf<A>,
                          token: CancelContext,
                          cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(fa, token, cb as (Either<Throwable, Any?>) -> Unit, null, null, null)

  @Suppress("CollapsibleIfStatements", "ReturnCount")
  fun loop(fa: FxOf<Any?>,
           currToken: CancelContext,
           cb: (Either<Throwable, Any?>) -> Unit,
           rcbRef: AsyncBoundary?,
           bFirstRef: ((Any?) -> Fx<Any?>)?,
           bRestRef: Platform.ArrayStack<(Any?) -> Fx<Any?>>?): Unit {

    val token: CancelContext = currToken
    var source: Fx<Any?>? = fa as Fx<Any?>
    var asyncBoundary: AsyncBoundary? = rcbRef
    var bFirst: ((Any?) -> Fx<Any?>)? = bFirstRef
    var bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>? = bRestRef
    var hasResult = false
    var result: Any? = null

    while (true) {
      val isCancelled = token.connection.isCanceled()
      if (isCancelled) {
        cb(Either.Left(OnCancel.CancellationException))
        return
      }
      val tag = source?.tag ?: UnknownTag
      when (tag) {
        RaiseErrorTag -> {
          val errorHandler: FxFrame<Any?, Fx<Any?>>? = findErrorHandlerInCallStack(bFirst, bRest)
          when (errorHandler) {
            null -> {
              cb(Either.Left((source as Fx.RaiseError<Any?>).error))
              return
            }
            else -> {
              val error = (source as Fx.RaiseError<Any?>).error
              source = executeSafe { errorHandler.recover(error) }
              bFirst = null
            }
          }
        }
        PureTag -> {
          result = (source as Fx.Pure<Any?>).value
          hasResult = true
        }
        SingleTag -> {
          if (asyncBoundary == null) {
            asyncBoundary = AsyncBoundary(token.connection, cb)
          }
          asyncBoundary.prepare(bFirst, bRest)

          //Run the suspend function in the async boundary and return
          (source as Fx.Single<Any?>).source.startCoroutine(asyncBoundary)
          return
        }
        MapTag -> {
          if (bFirst != null) {
            if (bRest == null) {
              bRest = Platform.ArrayStack()
            }
            bRest.push(bFirst)
          }
          bFirst = source as ((Any?) -> Fx<Any?>)?
          source = (source as Fx.Map<Any?, Any?>).source.fix()
        }
        FlatMapTag -> {
          if (bFirst != null) {
            if (bRest == null) bRest = Platform.ArrayStack()
            bRest.push(bFirst)
          }
          source as Fx.FlatMap<Any?, Any?>
          bFirst = source.fb as ((Any?) -> Fx<Any?>)?
          source = source.source.fix()
        }
        AsyncTag -> {
          if (asyncBoundary == null) {
            asyncBoundary = AsyncBoundary(token.connection, cb)
          }

          asyncBoundary.prepare(bFirst, bRest)
          // Return case for Async operations
          source as Fx.Async<Any?>
          source.proc(token.connection, asyncBoundary)
          return
        }
        ConnectionSwitchTag -> {
          source as Fx.ConnectionSwitch<Any?>
          val next = source.source
          val modify = source.modify
          val restore = source.restore

          val old = token.connection
          token.connection = modify(old)
          source = next as? Fx<Any?>

          if (token.connection != old) {
            asyncBoundary?.contextSwitch(token.connection)
            if (restore != null) {
              source = Fx.FlatMap(next, FxRunLoop.RestoreContext(old, restore), 0)
            }
          }
        }
        UnknownTag -> source = Fx.RaiseError(NullPointerException("Looping on null Fx")) //Improve message
      }

      if (hasResult) {
        val nextBind = popNextBind(bFirst, bRest)

        if (nextBind == null) {
          cb(Either.Right(result))
          return
        } else {
          source = executeSafe { nextBind(result) }
          hasResult = false
          result = null
          bFirst = null
        }
      }
    }
  }

  @Suppress("ReturnCount")
  fun findErrorHandlerInCallStack(bFirst: ((Any?) -> Fx<Any?>)?, bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>?): FxFrame<Any?, Fx<Any?>>? {
    if (bFirst != null && bFirst is FxFrame) {
      return bFirst
    } else if (bRest == null) {
      return null
    }

    var result: FxFrame<Any?, Fx<Any?>>? = null
    var cursor: ((Any?) -> Fx<Any?>)? = bFirst

    @Suppress("LoopWithTooManyJumpStatements")
    do {
      if (cursor != null && cursor is FxFrame) {
        result = cursor
        break
      } else {
        cursor = if (bRest.isNotEmpty()) {
          bRest.pop()
        } else {
          break
        }
      }
    } while (true)
    return result
  }

  private inline fun executeSafe(crossinline f: () -> FxOf<Any?>): Fx<Any?> =
    try {
      f().fix()
    } catch (e: Throwable) {
      Fx.RaiseError(e.nonFatalOrThrow())
    }

  /**
   * Pops the next bind function from the stack,
   * but filters out `IOFrame.ErrorHandler` references, because we know they won't do anything — an optimization for [handleErrorWith].
   */
  @PublishedApi
  internal fun popNextBind(
    bFirst: ((Any?) -> Fx<Any?>)?,
    bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>?
  ): ((Any?) -> Fx<Any?>)? =
    when {
      bFirst != null && bFirst !is FxFrame.Companion.ErrorHandler -> bFirst
      bRest != null -> {
        var cursor: ((Any?) -> Fx<Any?>)? = null
        while (cursor == null && bRest.isNotEmpty()) {
          val ref = bRest.pop()
          if (ref !is FxFrame.Companion.ErrorHandler) cursor = ref
        }
        cursor
      }
      else -> null
    }

  //TODO write law in ConcurrentLaws to check if is cancelable after bracket.
  @PublishedApi
  internal class RestoreContext(
    val old: FxConnection,
    val restore: (Any?, Throwable?, FxConnection, FxConnection) -> FxConnection) : FxFrame<Any?, Fx<Any?>> {

    override fun invoke(a: Any?): Fx<Any?> = Fx.ConnectionSwitch(Fx.Pure(a, 0), { current ->
      restore(a, null, old, current)
    }, null)

    override fun recover(e: Throwable): Fx<Any?> = Fx.ConnectionSwitch(Fx.RaiseError(e), { current ->
      restore(null, e, old, current)
    }, null)
  }

  /**
   * An [AsyncBoundary] gets created only once, per [startCancelable] or [start] invocation.
   * The job of the [AsyncBoundary] is to provide a means of jumping in -and out of the run loop when awaiting an async result.
   *
   * To be able to do this it needs to have following capabilities:
   *   - It needs to save the state of the run loop and restore it when jumping back. See [bFirst], [bRest].
   *
   *   - It needs to act as a callback itself, so it can be called from outside.
   *     So it implements `(Either<Throwable, Any?>) -> Unit` which can model any callback.
   *
   *   - Bridge between `Kotlin`'s suspended world and the normal world.
   *     So we implement `Continuation<Any?>`, which has shape `(Result<Any?>) -> Unit` so we handle it like any other generic callback.
   *
   *   - Trampoline between consecutive async boundaries.
   *     It fills the stack by calling `loop` to jump back to the run loop and needs to be trampolined every [Platform.maxStackDepthSize] frames.
   *
   * This instance is shared across a single  [startCancelable] or [start] invocation to avoid an allocation / async boundary.
   *
   * **IMPORTANT** this mechanism is essential to [Fx] and its [FxRunLoop] because this allows us to go from `suspend () -> A` to `A`.
   * Using that power we can write the `loop` in such a way that it is not suspended and as a result we have full control over the `Continuation`
   * This means it cannot sneak up on us and throw us out of the loop and thus adds support for pattern promoted by kotlinx. i.e.
   * ```
   * Fx {
   *   suspendCoroutine<A> { cont ->
   *     cont.resumeWithException(RuntimeException("When I occur in a suspended runloop I exit/throw immediately"))
   *   }
   * }
   * ```
   */
  //TODO double check that `EmptyCoroutineContext + CancelContext(conn)` is actually what we want here.
  @PublishedApi
  internal class AsyncBoundary(connInit: FxConnection, val cb: (Either<Throwable, Any?>) -> Unit) : (Either<Throwable, Any?>) -> Unit, Continuation<Any?>, () -> Unit {

    private var conn: FxConnection = connInit
    private var canCall = false
    private inline val shouldTrampoline inline get() = contIndex == Platform.maxStackDepthSize
    private var bFirst: ((Any?) -> Fx<Any?>)? = null
    private var bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)? = null
    private var contIndex: Int = 1
    private var result: Fx<Any?>? = null

    fun contextSwitch(conn: FxConnection): Unit {
      this.conn = conn
    }

    fun prepare(bFirst: ((Any?) -> Fx<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)?): Unit {
      contIndex++
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest
    }

    override val context: CoroutineContext
      get() = EmptyCoroutineContext + CancelContext(conn)

    override fun resumeWith(a: Result<Any?>): Unit {
      result = a.fold(
        onSuccess = { Fx.Pure(it, 0) },
        onFailure = { Fx.RaiseError(it) }
      )
      if (shouldTrampoline) {
        contIndex = 1
        Platform.trampoline(this)
      } else invoke()
    }

    override operator fun invoke(either: Either<Throwable, Any?>): Unit {
      result = when (either) {
        is Either.Left -> Fx.RaiseError(either.a)
        is Either.Right -> Fx.Pure(either.b, 0)
      }
      if (shouldTrampoline) {
        contIndex = 1
        Platform.trampoline(this)
      } else invoke()
    }

    override fun invoke(): Unit {
      if (canCall) {
        canCall = false
        val bFirst = bFirst
        val bRest = bRest
        this.bFirst = null //We need to clear the state so GC can cleanup if it wants to.
        this.bRest = null
        loop(requireNotNull(result) { "Fx bug, please contact support! https://arrow-kt.io" }, CancelContext(conn), cb, this, bFirst, bRest)
      }
    }
  }

}

private val trampolineExecutor = Platform.TrampolineExecutor(Executor { it.run() })

///**
// * A `AsyncBoundary` gets created only once, per [[startCancelable]]
// * (`unsafeRunAsync`) invocation, once an `Async` state is hit,
// * its job being to resume the loop after the boundary, but with
// * the bind call-stack restored
// *
// * This is a trick the implementation is using to avoid creating
// * extraneous callback references on asynchronous boundaries, in
// * order to reduce memory pressure.
// *
// * It's an ugly, mutable implementation.
// * For internal use only, here be dragons!
// */
//private class AsyncBoundary(connInit: FxConnection, val cb: (Either<Throwable, Any?>) -> Unit) : (Either<Throwable, Any?>) -> Unit, () -> Unit, Continuation<Any?> {
//
////  import TrampolineEC.{immediate => ec}
//
//  // can change on a ContextSwitch
//  private var conn: FxConnection = connInit
//
//  //State changes on prepareAsync or prepareContinuation
//  private var canCall = false
//  private var trampolineAfter = true
//  private var bFirst: ((Any?) -> Fx<Any?>)? = null
//  private var bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>? = null
//
//  // Used in combination with trampolineAfter = true
//  private var value: Either<Throwable, Any?>? = null
//
//  fun contextSwitch(conn: FxConnection): Unit {
//    this.conn = conn
//  }
//
//  fun start(task: Fx.Async<Any?>, bFirstRef: (Any?) -> Fx<Any?>, bRestRef: Platform.ArrayStack<(Any?) -> Fx<Any?>>): Unit {
//    canCall = true
//    this.bFirst = bFirstRef
//    this.bRest = bRestRef
//    //This seems to be **always** true in cats-effects... This should occur maxStackDepthSize only, right?
////    this.trampolineAfter = task.trampolineAfter
//    // Go, go, go
//    task.proc(conn, this)
//  }
//
//  private fun signal(either: Either<Throwable, Any?>?): Unit {
//    // Allow GC to collect
//    val bFirst = this.bFirst
//    val bRest = this.bRest
//    this.bFirst = null
//    this.bRest = null
//
//    // Auto-cancelable logic: in case the connection was cancelled, we interrupt the bind continuation
//    //TODO SIMON This can potentially be removed. Compare cancellation in the loop with cats-effects to double check if the semantics are the same.
//    if (conn.isNotCanceled()) when (either) {
//      is Either.Right -> TODO() //loop(Fx.Pure(either.b, 0), CancelContext(conn), cb, this, bFirst, bRest)
//      is Either.Left -> TODO() //loop(Fx.RaiseError(either.a), CancelContext(conn), cb, this, bFirst, bRest)
//      null -> throw Impossible
//    } else Unit
//  }
//
//  override fun invoke() {
//    // N.B. this has to be set to null *before* the signal
//    // otherwise a race condition can happen ;-)
//    val v = value
//    value = null
//    signal(v)
//  }
//
//  override fun invoke(either: Either<Throwable, Any?>): Unit {
//    if (canCall) {
//      canCall = false
//      if (trampolineAfter) {
//        this.value = either
//        trampolineExecutor.execute(this)
//      } else {
//        signal(either)
//      }
//    }
//  }
//
//  override val context: CoroutineContext
//    get() = EmptyCoroutineContext
//
//  override fun resumeWith(result: Result<Any?>) {
//    if (canCall) {
//      canCall = false
//      val either = result.fold({ Right(it) }, { Left(it) })
//      if (trampolineAfter) {
//        this.value = either
//        trampolineExecutor.execute(this)
//      } else {
//        signal(either)
//      }
//    }
//  }
//}
