package arrow.effects

import arrow.core.Continuation
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.effects.internal.IOConnection
import arrow.effects.internal.Platform.ArrayStack
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine

private typealias Current = IOOf<Any?>
private typealias BindF = (Any?) -> IO<Any?>
private typealias CallStack = ArrayStack<BindF>
private typealias Callback = (Either<Throwable, Any?>) -> Unit

@Suppress("UNCHECKED_CAST")
internal object IORunLoop {

  fun <A> start(source: IO<A>, cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(source, IOConnection.uncancelable, cb as Callback, null, null, null)

  /**
   * Evaluates the given `IO` reference, calling the given callback
   * with the result when completed.
   */
  fun <A> startCancelable(source: IO<A>, conn: IOConnection, cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(source, conn, cb as Callback, null, null, null)

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
          currentIO = executeSafe { thunk() }
        }
        is IO.Delay -> {
          try {
            result = currentIO.thunk()
            hasResult = true
            currentIO = null
          } catch (t: Throwable) {
            currentIO = IO.RaiseError(t)
          }
        }
        is IO.Async -> {
          // Return case for Async operations
          return suspendInAsync(currentIO as IO<A>, bFirst, bRest, currentIO.k)
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

          currentIO = IO.async { conn, cc ->
            loop(localCont, conn, cc.asyncCallback(currentCC), null, null, null)
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
        null -> {
          currentIO = IO.RaiseError(NullPointerException("Stepping on null IO"))
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

  private fun <A> suspendInAsync(
    currentIO: IO<A>,
    bFirst: BindF?,
    bRest: CallStack?,
    register: IOProc<Any?>): IO<A> =
  // Hitting an async boundary means we have to stop, however
  // if we had previous `flatMap` operations then we need to resume
  // the loop with the collected stack
    when {
      bFirst != null || (bRest != null && bRest.isNotEmpty()) ->
        IO.Async { conn, cb ->
          val rcb = RestartCallback(conn, cb as Callback)
          rcb.prepare(bFirst, bRest)
          register(conn, rcb)
        }
      else -> currentIO
    }

  private fun loop(
    source: Current,
    cancelable: IOConnection,
    cb: (Either<Throwable, Any?>) -> Unit,
    rcbRef: RestartCallback?,
    bFirstRef: BindF?,
    bRestRef: CallStack?): Unit {
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
            currentIO = IO.RaiseError(t)
          }
        }
        is IO.Async -> {
          if (rcb == null) {
            rcb = RestartCallback(conn, cb)
          }
          rcb.prepare(bFirst, bRest)
          // Return case for Async operations
          currentIO.k(conn, rcb)
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

          currentIO = IO.async { _, callback ->
            loop(localCont, conn, callback.asyncCallback(currentCC), null, null, null)
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
          currentIO = IO.RaiseError(NullPointerException("Looping on null IO"))
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
      IO.RaiseError(e)
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
  private data class RestartCallback(val connInit: IOConnection, val cb: Callback) : Callback {

    private var conn: IOConnection = connInit
    private var canCall = false
    private var bFirst: BindF? = null
    private var bRest: CallStack? = null

    fun contextSwitch(conn: IOConnection): Unit {
      this.conn = conn
    }

    fun prepare(bFirst: BindF?, bRest: CallStack?): Unit {
      canCall = true
      this.bFirst = bFirst
      this.bRest = bRest
    }

    override operator fun invoke(either: Either<Throwable, Any?>): Unit {
      if (canCall) {
        canCall = false
        when (either) {
          is Either.Left -> loop(IO.RaiseError(either.a), conn, cb, this, bFirst, bRest)
          is Either.Right -> loop(IO.Pure(either.b), conn, cb, this, bFirst, bRest)
        }
      }
    }
  }

  private fun <T> ((Either<Throwable, T>) -> Unit).asyncCallback(currentCC: CoroutineContext): (Either<Throwable, T>) -> Unit =
    { result ->
      val func: suspend () -> Unit = { this(result) }

      val normalResume: Continuation<Unit> = object : Continuation<Unit> {
        override val context: CoroutineContext = currentCC

        override fun resume(value: Unit) {
        }

        override fun resumeWithException(exception: Throwable) {
          this@asyncCallback(Either.left(exception))
        }

      }

      func.startCoroutine(normalResume)
    }

  private class RestoreContext(
    val old: IOConnection,
    val restore: (Any?, Throwable?, IOConnection, IOConnection) -> IOConnection) : IOFrame<Any?, IO<Any?>> {

    override fun invoke(a: Any?): IO<Any?> = IO.ContextSwitch(IO.Pure(a), { current -> restore(a, null, old, current) }, null)

    override fun recover(e: Throwable): IO<Any> =
      IO.ContextSwitch(IO.RaiseError(e), { current ->
        restore(null, e, old, current)
      }, null)
  }
}
