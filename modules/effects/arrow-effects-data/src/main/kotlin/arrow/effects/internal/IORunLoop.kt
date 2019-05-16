package arrow.effects.internal

import arrow.core.Either
import arrow.core.handleErrorWith
import arrow.core.nonFatalOrThrow
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.IOConnection
import arrow.effects.IONonCancelable
import arrow.effects.IOOf
import arrow.effects.KindConnection
import arrow.effects.OnCancel
import arrow.effects.fix
import arrow.effects.internal.IORunLoop.startCancelable
import java.lang.RuntimeException
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * This is the internal API for all methods that run the effect,
 * this includes all unsafe/safe run methods, [IO.not], fibers and races.
 */
@Suppress("UNCHECKED_CAST")
// @PublishedApi
object IORunLoop {

  /** Internal API for [IO.not] */
  suspend operator fun <A> invoke(source: IOOf<A>): A = suspendCoroutine { cont ->
    start(source) {
      it.fold(cont::resumeWithException, cont::resume)
    }
  }

  fun <A> start(source: IOOf<A>, ctx: CoroutineContext = EmptyCoroutineContext, cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(source, IONonCancelable, ctx, cb as (Either<Throwable, Any?>) -> Unit, null, null, null)

  fun <A> startCancelable(
    fa: IOOf<A>,
    token: KindConnection<ForIO>,
    ctx: CoroutineContext = EmptyCoroutineContext,
    cb: (Either<Throwable, A>) -> Unit
  ): Unit =
    loop(fa, token, ctx, cb as (Either<Throwable, Any?>) -> Unit, null, null, null)

  /**
   * This is the **only** main entry point to running an [IO] value.
   *
   * @param currToken [IOConnection] is an important detail because misuse can result in hard to debug code.
   * When started by [start] or [startCancelable] it's decided whether the loop will be cancelable or not.
   * Once a job is started uncancelable it can never become cancelable again,
   * only a cancelable job can become uncancelable temporary or permanently behind a certain point.
   * This is done using [IO.ConnectionSwitch].
   *
   * @param ctxRef [CoroutineContext] that is visible throughout the suspended program `IO { coroutineContext }`.
   * This is very important to keep compatibility with kotlins vision of [CoroutineContext] and [suspend].
   * [this](https://github.com/Kotlin/kotlin-coroutines-examples/blob/master/examples/context/auth-example.kt) has to work across any async boundary.
   *
   * @param cb the callback that will be called with the result as [Either].
   * @param rcbRef [AsyncBoundary] helper class instance that is shared across async boundaries
   * @param bFirstRef first [IO.FlatMap] on the stack to restore to the state
   * @param bRestRef remaining [IO.FlatMap] stack to restore to the state
   */
  @Suppress("CollapsibleIfStatements", "ReturnCount")
  private fun loop(
    fa: IOOf<Any?>,
    currToken: IOConnection,
    ctxRef: CoroutineContext,
    cb: (Either<Throwable, Any?>) -> Unit,
    rcbRef: AsyncBoundary?,
    bFirstRef: ((Any?) -> IO<Any?>)?,
    bRestRef: Platform.ArrayStack<(Any?) -> IO<Any?>>?
  ) {

    // Once a loop is started it context doesn't change. You can only modify the context through `startCoroutine`.
    val ctx: CoroutineContext = ctxRef
    var conn: KindConnection<ForIO> = currToken
    var source: IO<Any?>? = fa as IO<Any?>
    var asyncBoundary: AsyncBoundary? = rcbRef
    var bFirst: ((Any?) -> IO<Any?>)? = bFirstRef
    var bRest: Platform.ArrayStack<(Any?) -> IO<Any?>>? = bRestRef
    var hasResult = false
    var result: Any? = null

    while (true) {
      if (conn.isCanceled()) {
        cb(Either.Left(OnCancel.CancellationException))
        return
      }
      when (source) {
        is IO.Pure -> {
          result = source.value
          hasResult = true
        }
        is IO.RaiseError -> {
          when (val errorHandler: IOFrame<Any?, IO<Any?>>? = findErrorHandlerInCallStack(bFirst, bRest)) {
            null -> {
              cb(Either.Left(source.error))
              return
            }
            else -> {
              val error = source.error
              source = executeSafe { errorHandler.recover(error) }
              bFirst = null
            }
          }
        }
        is IO.UpdateContext -> {
          val modify = source.modify
          val prev = source.source

          source = IO.FlatMap(prev, { a ->
            // We need to schedule running the function because at this point we don't know what the correct CC will be to call modify with.
            IO.AsyncUpdateContext(IO.Pure<Any?>(a), modify)
          }, 0)
        }
        is IO.ContinueOn -> {
          val nextCC = source.ctx
          val prev = source.source

          source = IO.FlatMap(prev, { a ->
            IO.AsyncContinueOn(IO.Pure<Any?>(a), nextCC)
          }, 0)
        }
        is IO.Lazy -> {
          try {
            result = source.source(Unit)
            hasResult = true
            source = null
          } catch (t: Throwable) {
            source = IO.RaiseError(t.nonFatalOrThrow())
          }
        }
        is IO.Single -> {
          if (asyncBoundary == null) {
            asyncBoundary = AsyncBoundary(conn, cb)
          }
          // Run the suspend function in the async boundary and return
          asyncBoundary.start(source, ctx, bFirst, bRest)
          return
        }
        is IO.Defer -> {
          source = executeSafe(source.thunk)
        }
        is IO.Map<*, *> -> {
          if (bFirst != null) {
            if (bRest == null) {
              bRest = Platform.ArrayStack()
            }
            bRest.push(bFirst)
          }
          bFirst = source as ((Any?) -> IO<Any?>)?
          source = (source as IO.Map<Any?, Any?>).source.fix()
        }
        is IO.FlatMap<*, *> -> {
          if (bFirst != null) {
            if (bRest == null) bRest = Platform.ArrayStack()
            bRest.push(bFirst)
          }
          source as IO.FlatMap<Any?, Any?>
          bFirst = source.fb as ((Any?) -> IO<Any?>)?
          source = source.source.fix()
        }
        is IO.Async -> {
          if (asyncBoundary == null) {
            asyncBoundary = AsyncBoundary(conn, cb)
          }

          asyncBoundary.start(source, bFirst, bRest)
          return
        }
        is IO.ConnectionSwitch -> {
          val next = source.source
          val modify = source.modify
          val restore = source.restore

          val old = conn
          conn = modify(old)
          source = next as? IO<Any?>

          if (conn != old) {
            asyncBoundary?.contextSwitch(conn)

            if (restore != null) {
              source = IO.FlatMap(next, RestoreContext(old, restore))
            }
          }
        }
        is IO.AsyncContinueOn -> {
          if (asyncBoundary == null) {
            asyncBoundary = AsyncBoundary(conn, cb)
          }

          asyncBoundary.start(source, bFirst, bRest)
          return
        }
        is IO.AsyncUpdateContext -> {
          if (asyncBoundary == null) {
            asyncBoundary = AsyncBoundary(conn, cb)
          }

          asyncBoundary.start(source, bFirst, bRest)
          return
        }
        null -> {
          source = IO.RaiseError(RuntimeException("IORunLoop is running a null fx"))
        }
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
  internal fun findErrorHandlerInCallStack(bFirst: ((Any?) -> IO<Any?>)?, bRest: Platform.ArrayStack<(Any?) -> IO<Any?>>?): IOFrame<Any?, IO<Any?>>? {
    if (bFirst != null && bFirst is IOFrame) {
      return bFirst
    } else if (bRest == null) {
      return null
    }

    var result: IOFrame<Any?, IO<Any?>>? = null
    var cursor: ((Any?) -> IO<Any?>)? = bFirst

    @Suppress("LoopWithTooManyJumpStatements")
    do {
      if (cursor != null && cursor is IOFrame) {
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

  private inline fun executeSafe(crossinline f: () -> IOOf<Any?>): IO<Any?> =
    try {
      f().fix()
    } catch (e: Throwable) {
      IO.RaiseError(e.nonFatalOrThrow())
    }

  /**
   * Pops the next bind function from the stack, but filters out [IOFrame.ErrorHandler] references,
   * because we know they won't do anything since no error occurred — an optimization for skipping [handleErrorWith].
   */
  @PublishedApi
  internal fun popNextBind(bFirst: ((Any?) -> IO<Any?>)?, bRest: Platform.ArrayStack<(Any?) -> IO<Any?>>?): ((Any?) -> IO<Any?>)? =
    when {
      bFirst != null && bFirst !is IOFrame.Companion.ErrorHandler -> bFirst
      bRest != null -> {
        var cursor: ((Any?) -> IO<Any?>)? = null
        while (cursor == null && bRest.isNotEmpty()) {
          val ref = bRest.pop()
          if (ref !is IOFrame.Companion.ErrorHandler) cursor = ref
        }
        cursor
      }
      else -> null
    }

  /** Specialisation of [IOFrame] to restore the old context regardless of success or failure. */
  // TODO write law in ConcurrentLaws to check if is cancelable after bracket.
  @PublishedApi
  internal class RestoreContext(
    val old: IOConnection,
    val restore: (Any?, Throwable?, IOConnection, IOConnection) -> IOConnection
  ) : IOFrame<Any?, IO<Any?>> {

    override fun invoke(a: Any?): IO<Any?> = IO.ConnectionSwitch(IO.Pure(a), { current ->
      restore(a, null, old, current)
    })

    override fun recover(e: Throwable): IO<Any?> = IO.ConnectionSwitch(IO.RaiseError(e), { current ->
      restore(null, e, old, current)
    })
  }

  /**
   * An [AsyncBoundary] gets created only once to avoid an allocation / async boundary, per [startCancelable] or [start] invocation and is responsible for two tasks:
   *  - Jumping in -and out of the run loop when awaiting an async result, see [IO.Single] & [IO.Async].
   *  - Scheduling a context switch at a certain point in the loop, see [IO.ContinueOn] & [IO.UpdateContext].
   *
   * To be able to do this it needs to have following capabilities:
   *   - It needs to save the state of the run loop and restore it when jumping back.
   *   State consist of the first [IO.FlatMap.fb] [bFirst] and the following [IO.FlatMap.fb] as an [Platform.ArrayStack] [bRest].
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
   * **IMPORTANT** this mechanism is essential to [IO] and its [IORunLoop] because this allows us to go from `suspend () -> A` to `A`.
   * Using that power we can write the `loop` in such a way that it is not suspended and as a result we have full control over the `Continuation`
   * This means it cannot sneak up on us and throw us out of the loop and thus adds support for pattern promoted by kotlinx. i.e.
   *
   * ```
   * IO {
   *   suspendCoroutine<A> { cont ->
   *     cont.resumeWithException(RuntimeException("When I occur in a suspended runloop I exit/throw immediately"))
   *   }
   * }
   * ```
   */
  @PublishedApi
  internal class AsyncBoundary(connInit: IOConnection, val cb: (Either<Throwable, Any?>) -> Unit) : (Either<Throwable, Any?>) -> Unit, Continuation<Any?>, () -> Unit {

    // Instance state
    private var conn: IOConnection = connInit
    private var canCall = false
    private var contIndex: Int = 0

    // loop state
    private var bFirst: ((Any?) -> IO<Any?>)? = null
    private var bRest: (Platform.ArrayStack<(Any?) -> IO<Any?>>)? = null

    // async result
    private var result: IO<Any?>? = null

    private var contextSwitch = false

    private inline val shouldTrampoline inline get() = contIndex == Platform.maxStackDepthSize

    fun contextSwitch(conn: IOConnection) {
      this.conn = conn
    }

    fun start(fx: IO.Async<Any?>, bFirst: ((Any?) -> IO<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> IO<Any?>>)?) {
      contIndex++
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest

      conn.push(IO { resumeWith(Result.failure(OnCancel.CancellationException)) })

      // Run the users FFI function provided with the connection for cancellation support and [AsyncBoundary] as a generic callback.
      fx.proc(conn, this)
    }

    fun start(fx: IO.AsyncContinueOn<Any?>, bFirst: ((Any?) -> IO<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> IO<Any?>>)?) {
      contIndex++
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest

      this._context = fx.ctx
      this.result = fx.source
      this.contextSwitch = true
      suspend { Unit }.startCoroutine(this)
    }

    fun start(fx: IO.AsyncUpdateContext<Any?>, bFirst: ((Any?) -> IO<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> IO<Any?>>)?) {
      contIndex++
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest

      this._context = fx.f(this._context)
      this.result = fx.source
      this.contextSwitch = true
      suspend { Unit }.startCoroutine(this)
    }

    fun start(fx: IO.Single<Any?>, ctx: CoroutineContext, bFirst: ((Any?) -> IO<Any?>)?, bRest: (Platform.ArrayStack<(Any?) -> IO<Any?>>)?) {
      contIndex++
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest
      _context = ctx

      // Run `suspend () -> A` with `AsyncBoundary` as `Continuation`
      fx.source.startCoroutine(this)
    }

    // NASTY TRICK!!!! Overwrite getter to var mutable backing field.
    // This allows us to reuse this instance across multiple context switches which allows us to stay more lightweight.
    private var _context: CoroutineContext = EmptyCoroutineContext
    override val context: CoroutineContext
      get() = _context

    override fun resumeWith(result: Result<Any?>) {
      if (contextSwitch) {
        // result already set
        contextSwitch = false
      } else {
        this.result = result.fold(
          onSuccess = { IO.Pure(it) },
          onFailure = { IO.RaiseError(it) }
        )
      }

      if (shouldTrampoline) {
        contIndex = 1
        Platform.trampoline(this)
      } else invoke()
    }

    override operator fun invoke(either: Either<Throwable, Any?>) {
      result = when (either) {
        is Either.Left -> IO.RaiseError(either.a)
        is Either.Right -> IO.Pure(either.b)
      }

      if (shouldTrampoline) {
        contIndex = 0
        Platform.trampoline(this)
      } else invoke()
    }

    override fun invoke() {
      if (canCall) {
        canCall = false
        val bFirst = bFirst
        val bRest = bRest
        this.bFirst = null // We need to clear the state so GC can cleanup if it wants to.
        this.bRest = null

        loop(requireNotNull(result) { "IO bug, please contact support! https://arrow-kt.io" }, conn, _context, cb, this, bFirst, bRest)
      }
    }
  }

  /**
   * Evaluates the given `IO` reference, calling the given callback with the result when completed.
   */
  @Suppress("ReturnCount")
  fun <A> step(source: IOOf<A>): IO<A> {
    var current: Current? = source as IO<Any?>
    var bFirst: BindF? = null
    var bRest: CallStack? = null
    var hasResult = false
    var result: Any? = null

    while (true) {
      when (current) {
        is IO.Pure -> {
          result = current.value
          hasResult = true
          // current = null ??? see LazyTag
        }
        is IO.RaiseError -> {
          when (val errorHandler: IOFrame<Any?, IO<Any?>>? = findErrorHandlerInCallStack(bFirst, bRest)) {
            // Return case for unhandled errors
            null -> return current
            else -> {
              val exception: Throwable = current.error
              current = executeSafe { errorHandler.recover(exception) }
              bFirst = null
            }
          }
        }
        is IO.Defer -> {
          // TODO check if passing thunk executeSafe(thunk) directly is more efficient than `{ thunk() }`
          current = executeSafe(current.thunk)
        }
        is IO.Lazy -> {
          try {
            result = current.source(Unit)
            hasResult = true
            current = null
          } catch (t: Throwable) {
            current = IO.RaiseError(t.nonFatalOrThrow())
          }
        }
        is IO.FlatMap<*, *> -> {
          (current as IO.FlatMap<Any?, Any?>)
          if (bFirst != null) {
            if (bRest == null) bRest = Platform.ArrayStack()
            bRest.push(bFirst)
          }

          bFirst = current.fb as BindF
          current = current.source as IO<Any?> // TODO: this is properly expensive...
        }
        is IO.Map<*, *> -> {
          (current as IO.Map<Any?, Any?>)

          if (bFirst != null) {
            if (bRest == null) {
              bRest = Platform.ArrayStack()
            }
            bRest.push(bFirst)
          }

          bFirst = current // IO.Map implements (A) -> IO<B>
          current = current.source as IO<Any?> // TODO: this is properly expensive...
        }
        is IO.UpdateContext -> {
          val modify = current.modify
          val next = current.source

          current = IO.FlatMap(next, { a ->
            // We need to schedule running the function because at this point we don't know what the correct CC will be to call modify with.
            IO.AsyncUpdateContext(IO.Pure<Any?>(a), modify)
          }, 0)
        }
        is IO.ContinueOn -> {
          val nextCC = current.ctx
          val next = current.source

          current = IO.FlatMap(next, { a ->
            IO.AsyncContinueOn(IO.Pure<Any?>(a), nextCC)
          }, 0)
        }
        is IO.Single -> return suspendInSingle(current, bFirst, bRest) as IO<A>
        is IO.Async -> return suspendInAsync(current, bFirst, bRest) as IO<A>
        is IO.AsyncContinueOn -> return suspendContinueOn(current, bFirst, bRest) as IO<A>
        is IO.AsyncUpdateContext -> return suspendInAsyncUpdateContext(current, bFirst, bRest) as IO<A>
        is IO.ConnectionSwitch -> return suspendAnyInAsync(current, bFirst, bRest) as IO<A>
        null -> return IO.RaiseError(ArrowInternalException)
      }

      if (hasResult) {

        val nextBind: BindF? = popNextBind(bFirst, bRest)

        // Return case when no there are no more binds left
        if (nextBind == null) {
          return sanitizedCurrentIO(current, result)
        } else {
          current = executeSafe { nextBind(result) }
          hasResult = false
          result = null
          bFirst = null
        }
      }
    }
  }

  private fun <A> sanitizedCurrentIO(current: Current?, unboxed: Any?): IO<A> =
    (current ?: IO.Pure(unboxed)) as IO<A>

  private val suspendAnyInAsync: (currentIO: IO<Any?>, bFirst: BindF?, bRest: CallStack?) -> IO<Any?> = { source, bFirst, bRest ->
    if (bFirst != null || (bRest != null && !bRest.isEmpty())) IO.Async { conn, cb ->
      loop(source, conn, EmptyCoroutineContext, cb, null, bFirst, bRest)
    }
    else source
  }

  private val suspendInAsync: (currentIO: IO.Async<Any?>, bFirst: BindF?, bRest: CallStack?) -> IO<Any?> = { source, bFirst, bRest ->
    // Hitting an async boundary means we have to stop, however if we had previous `flatMap` operations then we need to resume the loop with the collected stack
    if (bFirst != null || (bRest != null && bRest.isNotEmpty())) IO.async { conn, cb ->
      AsyncBoundary(conn, cb).start(source, bFirst, bRest)
    }
    else source
  }

  private val suspendContinueOn: (IO.AsyncContinueOn<Any?>, BindF?, CallStack?) -> IO<Any?> = { source, bFirst, bRest ->
    if (bFirst != null || (bRest != null && bRest.isNotEmpty())) IO.async { conn, cb ->
      AsyncBoundary(conn, cb).start(source, bFirst, bRest)
    }
    else source
  }

  private val suspendInAsyncUpdateContext: (IO.AsyncUpdateContext<Any?>, BindF?, CallStack?) -> IO<Any?> = { source, bFirst, bRest ->
    if (bFirst != null || (bRest != null && bRest.isNotEmpty())) IO.async { conn, cb ->
      AsyncBoundary(conn, cb).start(source, bFirst, bRest)
    }
    else source
  }

  private val suspendInSingle: (IO.Single<Any?>, BindF?, CallStack?) -> IO<Any?> = { source, bFirst, bRest ->
    if (bFirst != null || (bRest != null && bRest.isNotEmpty()))
      IO.async { conn, cb ->
        AsyncBoundary(conn, cb).start(source, EmptyCoroutineContext, bFirst, bRest)
      }
    else source
  }
}

private typealias Current = IO<Any?>
private typealias BindF = (Any?) -> IO<Any?>
private typealias CallStack = Platform.ArrayStack<BindF>
