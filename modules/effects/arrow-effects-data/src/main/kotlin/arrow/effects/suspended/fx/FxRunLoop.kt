package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.core.Right
import arrow.core.handleErrorWith
import arrow.core.nonFatalOrThrow
import arrow.effects.*
import arrow.effects.internal.Platform
import arrow.effects.suspended.fx.FxRunLoop.startCancelable
import kotlin.coroutines.*

/**
 * This is the internal API for all methods that run the effect,
 * this includes all unsafe/safe run methods, [Fx.not], fibers and races.
 */
@Suppress("UNCHECKED_CAST")
//@PublishedApi
object FxRunLoop {

  /** Internal API for [Fx.not] */
  suspend operator fun <A> invoke(source: FxOf<A>): A = suspendCoroutine { cont ->
    FxRunLoop.start(source) {
      it.fold(cont::resumeWithException, cont::resume)
    }
  }

  fun <A> start(source: FxOf<A>, ctx: CoroutineContext = EmptyCoroutineContext, cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(source, FxNonCancelable, ctx, cb as (Either<Throwable, Any?>) -> Unit, null, null, null)

  fun <A> startCancelable(fa: FxOf<A>,
                          token: KindConnection<ForFx>,
                          ctx: CoroutineContext = EmptyCoroutineContext,
                          cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(fa, token, ctx, cb as (Either<Throwable, Any?>) -> Unit, null, null, null)

  /**
   * This is the **only** main entry point to running an [Fx] value.
   *
   * @param currToken [FxConnection] is an important detail because misuse can result in hard to debug code.
   * When started by [start] or [startCancelable] it's decided whether the loop will be cancelable or not.
   * Once a job is started uncancelable it can never become cancelable again,
   * only a cancelable job can become uncancelable temporary or permanently behind a certain point.
   * This is done using [Fx.ConnectionSwitch].
   *
   * @param ctxRef [CoroutineContext] that is visible throughout the suspended program `Fx { coroutineContext }`.
   * This is very important to keep compatibility with kotlins vision of [CoroutineContext] and [suspend].
   * [this](https://github.com/Kotlin/kotlin-coroutines-examples/blob/master/examples/context/auth-example.kt) has to work across any async boundary.
   *
   * @param cb the callback that will be called with the result as [Either].
   * @param rcbRef [AsyncBoundary] helper class instance that is shared across async boundaries
   * @param bFirstRef first [Fx.FlatMap] on the stack to restore to the state
   * @param bRestRef remaining [Fx.FlatMap] stack to restore to the state
   */
  @Suppress("CollapsibleIfStatements", "ReturnCount")
  private fun loop(fa: FxOf<Any?>,
                   currToken: FxConnection,
                   ctxRef: CoroutineContext,
                   cb: (Either<Throwable, Any?>) -> Unit,
                   rcbRef: AsyncBoundary?,
                   bFirstRef: ((Any?) -> Fx<Any?>)?,
                   bRestRef: Platform.ArrayStack<(Any?) -> Fx<Any?>>?): Unit {

    //Once a loop is started it context doesn't change. You can only modify the context through `startCoroutine`.
    val ctx: CoroutineContext = ctxRef
    var conn: KindConnection<ForFx> = currToken
    var source: Fx<Any?>? = fa as Fx<Any?>
    var asyncBoundary: AsyncBoundary? = rcbRef
    var bFirst: ((Any?) -> Fx<Any?>)? = bFirstRef
    var bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>? = bRestRef
    var hasResult = false
    var result: Any? = null

    while (true) {
      if (conn.isCanceled()) {
        cb(Either.Left(OnCancel.CancellationException))
        return
      }
      when (source?.tag ?: UnknownTag) {
        PureTag -> {
          result = (source as Fx.Pure<Any?>).value
          hasResult = true
        }
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
        ModifyContextTag -> {
          source as Fx.UpdateContext<Any?>
          val modify = source.modify
          val next = source.source

          source = Fx.FlatMap(next, { a ->
            //We need to schedule running the function because at this point we don't know what the correct CC will be to call modify with.
            Fx.Async<Any?>(updateContext = modify) { _, cb ->
              cb(Right(a))
            }
          }, 0)
        }
        ContinueOnTag -> {
          source as Fx.ContinueOn<Any?>

          val nextCC = source.ctx
          val next = source.source

          source = Fx.FlatMap(next, { a ->
            Fx.Async<Any?>(ctx = nextCC) { _, cb ->
              cb(Right(a))
            }
          }, 0)
        }
        LazyTag -> {
          source as Fx.Lazy<Any?>
          try {
            result = source.source(Unit)
            hasResult = true
            source = null
          } catch (t: Throwable) {
            source = Fx.RaiseError(t.nonFatalOrThrow())
          }
        }
        SingleTag -> {
          if (asyncBoundary == null) {
            asyncBoundary = AsyncBoundary(conn, cb)
          }
          //Run the suspend function in the async boundary and return
          asyncBoundary.start(source as Fx.Single<Any?>, ctx, bFirst, bRest)
          return
        }
        DeferTag -> {
          val thunk: () -> FxOf<Any?> = (source as Fx.Defer).thunk
          source = executeSafe { thunk() }
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
            asyncBoundary = AsyncBoundary(conn, cb)
          }

          asyncBoundary.start(source as Fx.Async<Any?>, source.ctx ?: ctx, bFirst, bRest)
          return
        }
        ConnectionSwitchTag -> {
          source as Fx.ConnectionSwitch<Any?>
          val next = source.source
          val modify = source.modify
          val restore = source.restore

          val old = conn
          conn = modify(old)
          source = next as? Fx<Any?>

          if (conn != old) {
            asyncBoundary?.contextSwitch(conn)

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

  @PublishedApi
  @Suppress("ReturnCount")
  internal fun findErrorHandlerInCallStack(bFirst: ((Any?) -> Fx<Any?>)?, bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>?): FxFrame<Any?, Fx<Any?>>? {
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
   * Pops the next bind function from the stack, but filters out [FxFrame.ErrorHandler] references,
   * because we know they won't do anything since no error occurred — an optimization for skipping [handleErrorWith].
   */
  @PublishedApi
  internal fun popNextBind(bFirst: ((Any?) -> Fx<Any?>)?, bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>?): ((Any?) -> Fx<Any?>)? =
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

  /** Specialisation of [FxFrame] to restore the old context regardless of success or failure. */
  //TODO write law in ConcurrentLaws to check if is cancelable after bracket.
  @PublishedApi
  internal class RestoreContext(
    val old: FxConnection,
    val restore: (Any?, Throwable?, FxConnection, FxConnection) -> FxConnection) : FxFrame<Any?, Fx<Any?>> {

    override fun invoke(a: Any?): Fx<Any?> = Fx.ConnectionSwitch(Fx.Pure(a, 0), { current ->
      restore(a, null, old, current)
    })

    override fun recover(e: Throwable): Fx<Any?> = Fx.ConnectionSwitch(Fx.RaiseError(e), { current ->
      restore(null, e, old, current)
    })
  }

  /**
   * An [AsyncBoundary] gets created only once to avoid an allocation / async boundary, per [startCancelable] or [start] invocation and is responsible for two tasks:
   *  - Jumping in -and out of the run loop when awaiting an async result, see [Fx.Single] & [Fx.Async].
   *  - Scheduling a context switch at a certain point in the loop, see [Fx.ContinueOn] & [Fx.UpdateContext].
   *
   * To be able to do this it needs to have following capabilities:
   *   - It needs to save the state of the run loop and restore it when jumping back.
   *   State consist of the first [Fx.FlatMap.fb] [bFirst] and the following [Fx.FlatMap.fb] as an [Platform.ArrayStack] [bRest].
   *
   *   - It needs to act as a callback itself, so it can be called from outside.
   *   So it implements `(Either<Throwable, Any?>) -> Unit` which can model any callback.
   *
   *   - Bridge between `Kotlin`'s suspended world and the normal world.
   *   So we implement `Continuation<Any?>`, which has shape `(Result<Any?>) -> Unit` so we handle it like any other generic callback.
   *
   *   - Trampoline between consecutive async boundaries.
   *   It fills the stack by calling `loop` to jump back to the run loop and needs to be trampolined every [Platform.maxStackDepthSize] frames.
   *
   *   - Switch, and modify to the correct [CoroutineContext]
   *   This is necessary because we need to maintain the correct state within [kotlin.coroutines.coroutineContext] and to do thread switching using [CoroutineContext].
   *
   * **IMPORTANT** this mechanism is essential to [Fx] and its [FxRunLoop] because this allows us to go from `suspend () -> A` to `A`.
   * Using that power we can write the `loop` in such a way that it is not suspended and as a result we have full control over the `Continuation`
   * This means it cannot sneak up on us and throw us out of the loop and thus adds support for pattern promoted by kotlinx. i.e.
   *
   * ```
   * Fx {
   *   suspendCoroutine<A> { cont ->
   *     cont.resumeWithException(RuntimeException("When I occur in a suspended runloop I exit/throw immediately"))
   *   }
   * }
   * ```
   */
  @PublishedApi
  internal class AsyncBoundary(connInit: FxConnection, val cb: (Either<Throwable, Any?>) -> Unit) : (Either<Throwable, Any?>) -> Unit, Continuation<Any?>, () -> Unit {

    //Instance state
    private var conn: FxConnection = connInit
    private var canCall = false
    private var contIndex: Int = 0

    //loop state
    private var bFirst: ((Any?) -> Fx<Any?>)? = null
    private var bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)? = null

    //async result
    private var result: Fx<Any?>? = null

    private inline val shouldTrampoline inline get() = contIndex == Platform.maxStackDepthSize

    fun contextSwitch(conn: FxConnection): Unit {
      this.conn = conn
    }

    fun prepare(ctx: CoroutineContext?,
                updateContext: ((CoroutineContext) -> CoroutineContext)?,
                bFirst: ((Any?) -> Fx<Any?>)?,
                bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)?): Unit {
      _context = updateContext?.invoke(_context) ?: ctx ?: EmptyCoroutineContext //Swap or update the contexts.
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest
    }

    fun start(fx: Fx.Async<Any?>, ctx: CoroutineContext, bFirst: ((Any?) -> Fx<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)?): Unit {
      contIndex++
      _context = fx.updateContext?.invoke(_context) ?: ctx //Swap or update the contexts.
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest

      conn.push(Fx { resumeWith(Result.failure(OnCancel.CancellationException)) })

      // Run the users FFI function provided with the connection for cancellation support and [AsyncBoundary] as a generic callback.
      fx.proc(conn, this)
    }

    fun start(fx: Fx.Single<Any?>, ctx: CoroutineContext, bFirst: ((Any?) -> Fx<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> Fx<Any?>>)?): Unit {
      contIndex++
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest
      _context = ctx

      //Run `suspend () -> A` with `AsyncBoundary` as `Continuation`
      fx.source.startCoroutine(this)
    }

    //NASTY TRICK!!!! Overwrite getter to var mutable backing field.
    // This allows us to reuse this instance across multiple context switches which allows us to stay more lightweight.
    private var _context: CoroutineContext = EmptyCoroutineContext
    override val context: CoroutineContext
      get() = _context

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
        contIndex = 0
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

        loop(requireNotNull(result) { "Fx bug, please contact support! https://arrow-kt.io" }, conn, _context, cb, this, bFirst, bRest)
      }
    }
  }

  /**
   * Evaluates the given `Fx` reference, calling the given callback with the result when completed.
   */
  fun <A> step(source: FxOf<A>): Fx<A> {
    var current: Current? = source
    var bFirst: BindF? = null
    var bRest: CallStack? = null
    var hasResult: Boolean = false
    var result: Any? = null

    while (true) {
      when ((current as? Fx<A>)?.tag ?: UnknownTag) {
        PureTag -> {
          result = (current as Fx.Pure<Any?>).value
          hasResult = true
          //current = null ??? see LazyTag
        }
        RaiseErrorTag -> {
          val errorHandler: FxFrame<Any?, Fx<Any?>>? = findErrorHandlerInCallStack(bFirst, bRest)
          when (errorHandler) {
            // Return case for unhandled errors
            null -> return current as Fx<A>
            else -> {
              (current as Fx.RaiseError<Any?>)
              val exception: Throwable = current.error
              current = executeSafe { errorHandler.recover(exception) }
              bFirst = null
            }
          }
        }
        DeferTag -> {
          //TODO check if passing thunk directly is more efficient than `{ thunk() }`
          current = executeSafe((current as Fx.Defer<Any?>).thunk)
        }
        LazyTag -> {
          (current as Fx.Lazy<Any?>)
          try {
            result = current.source(Unit)
            hasResult = true
            current = null
          } catch (t: Throwable) {
            current = Fx.RaiseError(t.nonFatalOrThrow())
          }
        }
        FlatMapTag -> {
          (current as Fx.FlatMap<Any?, Any?>)
          if (bFirst != null) {
            if (bRest == null) bRest = Platform.ArrayStack()
            bRest.push(bFirst)
          }

          bFirst = current.fb as BindF
          current = current.source
        }
        MapTag -> {
          (current as Fx.Map<Any?, Any?>)

          if (bFirst != null) {
            if (bRest == null) {
              bRest = Platform.ArrayStack()
            }
            bRest.push(bFirst)
          }

          bFirst = current //Fx.Map implements (A) -> Fx<B>
          current = current.source
        }
        ModifyContextTag -> {
          current as Fx.UpdateContext<Any?>
          val modify = current.modify
          val next = current.source

          current = Fx.FlatMap(next, { a ->
            //We need to schedule running the function because at this point we don't know what the correct CC will be to call modify with.
            Fx.Async<Any?>(updateContext = modify) { _, cb ->
              cb(Right(a))
            }
          }, 0)
        }
        ContinueOnTag -> {
          current as Fx.ContinueOn<Any?>

          val nextCC = current.ctx
          val next = current.source

          current = Fx.FlatMap(next, { a ->
            Fx.Async<Any?>(ctx = nextCC) { _, cb ->
              cb(Right(a))
            }
          }, 0)
        }
        SingleTag -> {
          return FxRunLoop.suspendInAsync(current as Fx.Single<A>, bFirst, bRest)
        }
        AsyncTag -> {
          // Return case for Async operations
          return suspendInAsync(current as Fx.Async<A>, bFirst, bRest, current.proc)
        }
        else -> throw FxImpossibleBugs.FxStep
      }

      if (hasResult) {

        val nextBind: BindF? = popNextBind(bFirst, bRest)

        // Return case when no there are no more binds left
        if (nextBind == null) {
          return sanitizedCurrentFx(current, result)
        } else {
          current = executeSafe { nextBind(result) }
          hasResult = false
          result = null
          bFirst = null
        }
      }

    }
  }

  private fun <A> sanitizedCurrentFx(current: Current?, unboxed: Any?): Fx<A> =
    (current ?: Fx.Pure(unboxed, 0)) as Fx<A>

  private fun <A> suspendInAsync(
    currentIO: Fx.Async<A>,
    bFirst: BindF?,
    bRest: CallStack?,
    register: FxProc<Any?>): Fx<A> =
  // Hitting an async boundary means we have to stop, however
  // if we had previous `flatMap` operations then we need to resume
    // the loop with the collected stack
    when {
      bFirst != null || (bRest != null && bRest.isNotEmpty()) ->
        Fx.async { conn, cb ->
          val rcb = FxRunLoop.AsyncBoundary(conn, cb as (Either<Throwable, Any?>) -> Unit)
          rcb.prepare(currentIO.ctx, currentIO.updateContext, bFirst, bRest)
          register(conn, rcb)
        }
      else -> currentIO
    }

  private fun <A> suspendInAsync(
    currentIO: Fx.Single<A>,
    bFirst: BindF?,
    bRest: CallStack?): Fx<A> =
  // Hitting an async boundary means we have to stop, however
  // if we had previous `flatMap` operations then we need to resume
    // the loop with the collected stack
    Fx.async { conn, cb ->
      val rcb = FxRunLoop.AsyncBoundary(conn, cb as (Either<Throwable, Any?>) -> Unit)
      rcb.prepare(null, null, bFirst, bRest)
      currentIO.source.startCoroutine(rcb)
    }

}

private typealias Current = FxOf<Any?>
private typealias BindF = (Any?) -> Fx<Any?>
private typealias CallStack = Platform.ArrayStack<BindF>
