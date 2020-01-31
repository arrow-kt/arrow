package arrow.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.NonFatal
import arrow.core.Right
import arrow.core.nonFatalOrThrow
import arrow.fx.internal.ArrowInternalException
import arrow.fx.internal.Platform
import arrow.fx.internal.Platform.ArrayStack
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

private typealias Current = IOOf<Any?>
private typealias BindF = (Any?) -> IO<Any?>
private typealias CallStack = ArrayStack<BindF>
private typealias Callback = (Either<Throwable, Any?>) -> Unit

@Suppress("UNCHECKED_CAST", "ReturnCount", "ComplexMethod")
internal object IORunLoop {

  fun <A> start(source: IOOf<A>, cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(source, KindConnection.uncancelable, cb as Callback, null, null, null, IOContext(KindConnection.uncancelable))

  /**
   * Evaluates the given `IO` reference, calling the given callback
   * with the result when completed.
   */
  fun <A> startCancelable(source: IOOf<A>, conn: IOConnection, cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(source, conn, cb as Callback, null, null, null, IOContext(conn))

  fun <A> step(source: IO<A>): IO<A> {
    var currentIO: Current? = source
    var bFirst: BindF? = null
    var bRest: CallStack? = null
    var hasResult: Boolean = false
    var result: Any? = null

    do {
      when (currentIO) {
        is IO.Pure -> {
          result = currentIO.a
          hasResult = true
        }
        is IO.RaiseError -> {
          val errorHandler: IOFrame<Any?, IO<Any?>>? = findErrorHandlerInCallStack(bFirst, bRest)
          when (errorHandler) {
            // Return case for unhandled errors
            null -> return currentIO
            else -> {
              val exception: Throwable = currentIO.exception
              currentIO = executeSafe { errorHandler.recover(exception) }
              bFirst = null
            }
          }
        }
        is IO.Suspend -> {
          val thunk: () -> IOOf<Any?> = currentIO.thunk
          currentIO = executeSafe(thunk)
        }
        is IO.Delay -> {
          try {
            result = currentIO.thunk()
            hasResult = true
            currentIO = null
          } catch (t: Throwable) {
            currentIO = IO.RaiseError(t.nonFatalOrThrow())
          }
        }
        is IO.Async -> {
          // Return case for Async operations
          return suspendAsync(currentIO, bFirst, bRest) as IO<A>
        }
        is IO.Effect -> {
          return suspendAsync(currentIO, bFirst, bRest) as IO<A>
        }
        is IO.Bind<*, *> -> {
          if (bFirst != null) {
            if (bRest == null) {
              bRest = ArrayStack()
            }
            bRest.push(bFirst)
          }
          bFirst = currentIO.g as BindF
          currentIO = currentIO.cont
        }
        is IO.ContinueOn -> {
          val currentCC = currentIO.cc
          val localCont = currentIO.cont

          currentIO = IO.Bind(localCont) { a ->
            IO.Effect(currentCC) { a }
          }
        }
        is IO.Map<*, *> -> {
          if (bFirst != null) {
            if (bRest == null) {
              bRest = ArrayStack()
            }
            bRest.push(bFirst)
          }
          bFirst = currentIO as BindF
          currentIO = currentIO.source
        }
        is IO.ContextSwitch -> {
          val localCurrent = currentIO
          return IO.Async { conn, cb ->
            loop(localCurrent, conn, cb as Callback, null, bFirst, bRest, EmptyCoroutineContext)
          }
        }
        null -> {
          currentIO = IO.RaiseError(IORunLoopStepOnNull)
        }
        else -> {
          // Since we don't capture the value of `when` kotlin doesn't enforce exhaustiveness
          currentIO = IO.raiseError(IORunLoopMissingStep)
        }
      }

      if (hasResult) {

        val nextBind: BindF? = popNextBind(bFirst, bRest)

        // Return case when no there are no more binds left
        if (nextBind == null) {
          return sanitizedCurrentIO(currentIO, result)
        } else {
          currentIO = executeSafe { nextBind(result) }
          hasResult = false
          result = null
          bFirst = null
        }
      }
    } while (true)
  }

  private fun <A> sanitizedCurrentIO(currentIO: Current?, unboxed: Any?): IO<A> =
    (currentIO ?: IO.Pure(unboxed)) as IO<A>

  private fun suspendAsync(currentIO: IO<Any?>, bFirst: BindF?, bRest: CallStack?): IO<Any?> =
    // Hitting an async boundary means we have to stop, however if we had previous `flatMap` operations then we need to resume the loop with the collected stack
    if (bFirst != null || (bRest != null && bRest.isNotEmpty())) {
      IO.Async { conn, cb ->
        loop(currentIO, conn, cb, null, bFirst, bRest, EmptyCoroutineContext)
      }
    } else {
      currentIO
    }

  private fun loop(
    source: Current,
    cancelable: IOConnection,
    cb: (Either<Throwable, Any?>) -> Unit,
    rcbRef: RestartCallback?,
    bFirstRef: BindF?,
    bRestRef: CallStack?,
    ctx: CoroutineContext
  ) {
    var currentIO: Current? = source
    var conn: IOConnection = cancelable
    var bFirst: BindF? = bFirstRef
    var bRest: CallStack? = bRestRef
    var rcb: RestartCallback? = rcbRef
    // Values from Pure and Delay are unboxed in this var,
    // for code reuse between Pure and Delay
    var hasResult: Boolean = false
    var result: Any? = null

    do {
      if (conn.isCanceled()) {
        cb(Left(OnCancel.CancellationException))
        return
      }
      when (currentIO) {
        is IO.Pure -> {
          result = currentIO.a
          hasResult = true
        }
        is IO.RaiseError -> {
          val errorHandler: IOFrame<Any?, IO<Any?>>? = findErrorHandlerInCallStack(bFirst, bRest)
          when (errorHandler) {
            // Return case for unhandled errors
            null -> {
              cb(Left(currentIO.exception))
              return
            }
            else -> {
              val exception: Throwable = currentIO.exception
              currentIO = executeSafe { errorHandler.recover(exception) }
              bFirst = null
            }
          }
        }
        is IO.Suspend -> {
          val thunk: () -> IOOf<Any?> = currentIO.thunk
          currentIO = executeSafe { thunk() }
        }
        is IO.Delay -> {
          try {
            result = currentIO.thunk()
            hasResult = true
            currentIO = null
          } catch (t: Throwable) {
            if (NonFatal(t)) {
              currentIO = IO.RaiseError(t)
            } else {
              throw t
            }
          }
        }
        is IO.Async -> {
          if (rcb == null) {
            rcb = RestartCallback(conn, cb)
          }

          // Return case for Async operations
          rcb.start(currentIO, ctx, bFirst, bRest)
          return
        }
        is IO.Effect -> {
          if (rcb == null) {
            rcb = RestartCallback(conn, cb)
          }

          // Return case for Effect operations
          rcb.start(currentIO, ctx, bFirst, bRest)
          return
        }
        is IO.Bind<*, *> -> {
          if (bFirst != null) {
            if (bRest == null) bRest = ArrayStack()
            bRest.push(bFirst)
          }
          bFirst = currentIO.g as BindF
          currentIO = currentIO.cont
        }
        is IO.ContinueOn<*> -> {
          if (bFirst != null) {
            if (bRest == null) bRest = ArrayStack()
            bRest.push(bFirst)
          }
          val localCurrent = currentIO
          val currentCC = localCurrent.cc
          val localCont = currentIO.cont

          bFirst = { c: Any? -> IO.just(c) }

          currentIO = IO.Bind(localCont) { a ->
            IO.Effect(currentCC) { a }
          }
        }
        is IO.Map<*, *> -> {
          if (bFirst != null) {
            if (bRest == null) {
              bRest = ArrayStack()
            }
            bRest.push(bFirst)
          }
          bFirst = currentIO as BindF
          currentIO = currentIO.source
        }
        is IO.ContextSwitch -> {
          val next = currentIO.source
          val modify = currentIO.modify
          val restore = currentIO.restore

          val old = conn
          conn = modify(old)
          currentIO = next
          if (conn != old) {
            rcb?.contextSwitch(conn)
            if (restore != null)
              currentIO = IO.Bind(next, RestoreContext(old, restore))
          }
        }
        null -> {
          currentIO = IO.RaiseError(IORunLoopOnNull)
        }
        else -> {
          // Since we don't capture the value of `when` kotlin doesn't enforce exhaustiveness
          currentIO = IO.RaiseError(IORunLoopMissingLoop)
        }
      }

      if (hasResult) {

        val nextBind: BindF? = popNextBind(bFirst, bRest)

        // Return case when no there are no more binds left
        if (nextBind == null) {
          cb(Right(result))
          return
        } else {
          currentIO = executeSafe { nextBind(result) }
          hasResult = false
          result = null
          bFirst = null
        }
      }
    } while (true)
  }

  private inline fun executeSafe(crossinline f: () -> IOOf<Any?>): IO<Any?> =
    try {
      f().fix()
    } catch (e: Throwable) {
      if (NonFatal(e)) {
        IO.RaiseError(e)
      } else {
        throw e
      }
    }

  /**
   * Pops the next bind function from the stack, but filters out
   * `IOFrame.ErrorHandler` references, because we know they won't do
   * anything — an optimization for `handleError`.
   */
  private fun popNextBind(bFirst: BindF?, bRest: CallStack?): BindF? =
    if ((bFirst != null) && bFirst !is IOFrame.Companion.ErrorHandler)
      bFirst
    else if (bRest != null) {
      var cursor: BindF? = null
      while (cursor == null && bRest.isNotEmpty()) {
        val ref = bRest.pop()
        if (ref !is IOFrame.Companion.ErrorHandler) cursor = ref
      }
      cursor
    } else {
      null
    }

  private fun findErrorHandlerInCallStack(bFirst: BindF?, bRest: CallStack?): IOFrame<Any?, IO<Any?>>? {
    if (bFirst != null && bFirst is IOFrame) {
      return bFirst
    } else if (bRest == null) {
      return null
    }

    var result: IOFrame<Any?, IO<Any?>>? = null
    var cursor: BindF? = bFirst

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

  /**
   * A `RestartCallback` gets created only once, per [startCancelable] (`unsafeRunAsync`) invocation, once an `Async`
   * state is hit, its job being to resume the loop after the boundary, but with the bind call-stack restored.
   */
  private data class RestartCallback(val connInit: IOConnection, val cb: Callback) : Callback, kotlin.coroutines.Continuation<Any?> {

    // Nasty trick to re-use `Continuation` with different CC.
    private var _context: CoroutineContext = EmptyCoroutineContext
    override val context: CoroutineContext
      get() = _context

    private var conn: IOConnection = connInit
    private var canCall = false
    private var bFirst: BindF? = null
    private var bRest: CallStack? = null

    private var contIndex: Int = 0
    private var trampolineAfter: Boolean = false
    private inline val shouldTrampoline inline get() = trampolineAfter || contIndex == Platform.maxStackDepthSize

    private var value: IO<Any?>? = null

    fun contextSwitch(conn: IOConnection) {
      this.conn = conn
    }

    private fun prepare(ctx: CoroutineContext, bFirst: BindF?, bRest: CallStack?) {
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest
      this._context = ctx
      contIndex++
    }

    fun start(async: IO.Async<Any?>, ctx: CoroutineContext, bFirst: BindF?, bRest: CallStack?) {
      prepare(ctx, bFirst, bRest)
      trampolineAfter = async.shouldTrampoline
      async.k(conn, this)
    }

    fun start(effect: IO.Effect<Any?>, ctx: CoroutineContext, bFirst: BindF?, bRest: CallStack?) {
      prepare(effect.ctx ?: ctx, bFirst, bRest)
      effect.effect.startCoroutine(this)
    }

    private fun signal(result: IO<Any?>) {
      // Allow GC to collect
      val bFirst = this.bFirst
      val bRest = this.bRest
      val ctx = this._context
      this.bFirst = null
      this.bRest = null
      this._context = EmptyCoroutineContext

      loop(result, conn, cb, this, bFirst, bRest, ctx)
    }

    override operator fun invoke(either: Either<Throwable, Any?>) {
      if (canCall) {
        canCall = false
        when (either) {
          is Either.Left -> IO.RaiseError(either.a)
          is Either.Right -> IO.Pure(either.b)
        }.let(::forward)
      }
    }

    override fun resumeWith(result: Result<Any?>) {
      if (canCall) {
        canCall = false
        result.fold(
          { a -> IO.Pure(a) },
          { e -> IO.RaiseError(e) }
        ).let(::forward)
      }
    }

    private fun forward(io: IO<Any?>) {
      if (shouldTrampoline) {
        this.value = io
        Platform.trampoline { trampoline() }
      } else {
        signal(io)
      }
    }

    fun trampoline() {
      val v = value
      value = null
      contIndex = 0
      signal(v!!)
    }
  }

  private class RestoreContext(
    val old: IOConnection,
    val restore: (Any?, Throwable?, IOConnection, IOConnection) -> IOConnection
  ) : IOFrame<Any?, IO<Any?>> {

    override fun invoke(a: Any?): IO<Any?> = IO.ContextSwitch(IO.Pure(a), { current -> restore(a, null, old, current) }, null)

    override fun recover(e: Throwable): IO<Any> =
      IO.ContextSwitch(IO.RaiseError(e), { current ->
        restore(null, e, old, current)
      }, null)
  }
}

internal object IORunLoopMissingStep : ArrowInternalException() {
  override fun fillInStackTrace(): Throwable = this
}

internal object IORunLoopStepOnNull : ArrowInternalException() {
  override fun fillInStackTrace(): Throwable = this
}

internal object IORunLoopMissingLoop : ArrowInternalException() {
  override fun fillInStackTrace(): Throwable = this
}

internal object IORunLoopOnNull : ArrowInternalException() {
  override fun fillInStackTrace(): Throwable = this
}
