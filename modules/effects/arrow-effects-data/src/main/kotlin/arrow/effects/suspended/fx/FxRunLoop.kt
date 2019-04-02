package arrow.effects.suspended.fx

import arrow.core.Either
import arrow.core.Left
import arrow.core.NonFatal
import arrow.core.Right
import arrow.effects.*
import arrow.effects.internal.Platform
import kotlin.coroutines.*

@Suppress("UNCHECKED_CAST")
object FxRunLoop {

  suspend inline operator fun <A> invoke(source: FxOf<A>): A =
    loop(source, coroutineContext[CancelContext] ?: NonCancelable)() as A

  @JvmStatic inline fun <A> start(source: FxOf<A>,
                                  ctx: CoroutineContext = EmptyCoroutineContext,
                                  crossinline cb: (Either<Throwable, A>) -> Unit): Unit =
    loop(source, NonCancelable)
      .startCoroutine(Continuation(ctx) { r ->
        r.fold({ cb(Right(it as A)) }, { cb(Left(it)) })
      })

  @JvmStatic inline fun <A> startCancelable(fa: FxOf<A>,
                                            token: CancelContext,
                                            ctx: CoroutineContext = EmptyCoroutineContext,
                                            crossinline cb: (Either<Throwable, A>) -> Unit): Unit {
    loop(fa, token)
      .startCoroutine(Continuation(ctx + token) { r ->
        r.fold({ cb(Right(it as A)) }, { cb(Left(it)) })
      })
  }

  @PublishedApi
  @JvmStatic internal fun <A> loop(fa: FxOf<A>, currToken: CancelContext): suspend () -> Any? = suspend {
    val token: CancelContext = currToken
    var source: Fx<Any?>? = fa as Fx<Any?>
    var bFirst: ((Any?) -> Fx<Any?>)? = null
    var bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>? = null
    var hasResult = false
    var result: Any? = null

    while (true) {
      val isCancelled = token.connection.isCanceled()
      if (isCancelled) throw OnCancel.CancellationException
      val tag = source?.tag ?: UnknownTag
      when (tag) {
        RaiseErrorTag -> {
          val errorHandler: FxFrame<Any?, Fx<Any?>>? = findErrorHandlerInCallStack(bFirst, bRest)
          when (errorHandler) {
            null -> throw (source as Fx.RaiseError<Any?>).error //An alternative to throwing would be nice..
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
          result = (source as Fx.Single<Any?>).source.invoke() //Stack safe since wraps single stack-safe suspend function.
          hasResult = true
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
        ConnectionSwitchTag -> {
          source as Fx.ConnectionSwitch<Any?>
          val next = source.source
          val modify = source.modify
          val restore = source.restore

          val old = token.connection
          token.connection = modify(old)
          source = next as? Fx<Any?>
          if (token.connection != old) {
            //We don't have this yet... RestartCallback is used to eliminate the need of Async boundary allocations.
//            rcb.contextSwitch(conn)
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
          break
        } else {
          source = executeSafe { nextBind(result) }
          hasResult = false
          result = null
          bFirst = null
        }
      }
    }

    result
  }

  private fun findErrorHandlerInCallStack(bFirst: ((Any?) -> Fx<Any?>)?, bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>?): FxFrame<Any?, Fx<Any?>>? {
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

  @JvmStatic private inline fun executeSafe(crossinline f: () -> FxOf<Any?>): Fx<Any?> =
    try {
      f().fix()
    } catch (e: Throwable) {
      if (NonFatal(e)) {
        Fx.RaiseError(e)
      } else {
        throw e
      }
    }

  /**
   * Pops the next bind function from the stack,
   * but filters out `IOFrame.ErrorHandler` references, because we know they won't do anything — an optimization for `handleError`.
   */
  @JvmStatic private fun popNextBind(
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

  private class RestoreContext(
    val old: FxConnection,
    val restore: (Any?, Throwable?, FxConnection, FxConnection) -> FxConnection) : IOFrame<Any?, Fx<Any?>> {

    override fun invoke(a: Any?): Fx<Any?> = Fx.ConnectionSwitch(Fx.Pure(a), { current ->
      restore(a, null, old, current)
    }, null)

    override fun recover(e: Throwable): Fx<Any?> =
      Fx.ConnectionSwitch(Fx.RaiseError(e), { current ->
        restore(null, e, old, current)
      }, null)
  }

}