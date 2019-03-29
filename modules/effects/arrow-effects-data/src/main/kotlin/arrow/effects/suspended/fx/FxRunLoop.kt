package arrow.effects.suspended.fx

import arrow.core.NonFatal
import arrow.effects.KindConnection
import arrow.effects.internal.Platform
import java.util.concurrent.CancellationException
import kotlin.coroutines.coroutineContext

object FxRunLoop {

  operator fun <A> invoke(fa: Fx<A>): suspend () -> A = {
    runLoop(fa as Fx<Any?>) as A
  }

  private suspend fun runLoop(fa: Fx<Any?>): Any? {
    var source: Fx<Any?>? = fa
    var bFirst: ((Any?) -> Fx<Any?>)? = null
    var bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>? = null

    var hasResult = false
    var result: Any? = null

    val conn: KindConnection<ForFx>? = coroutineContext[CancelToken]?.connection

    while (true) {
      val isCancelled = conn?.isCanceled() ?: false
//      println("I am checking isCancelled: $isCancelled")
      if (isCancelled) throw CancellationException()

      when (source) {
        is Fx.RaiseError -> throw source.error //An alternative to throwing would be nice..
        is Fx.Pure -> {
          result = source.value
          hasResult = true
        }
        is Fx.Single -> {
          result = source.source.invoke() //Stack safe since wraps single stack-safe suspend function.
          hasResult = true
        }
        is Fx.Map<*, *> -> {
          if (bFirst != null) {
            if (bRest == null) {
              bRest = Platform.ArrayStack()
            }
            bRest.push(bFirst)
          }
          bFirst = source as ((Any?) -> Fx<Any?>)?
          source = source.source.fix()
        }
        is Fx.FlatMap<*, *> -> {
          if (bFirst != null) {
            if (bRest == null) bRest = Platform.ArrayStack()
            bRest.push(bFirst)
          }
          bFirst = source.fb as ((Any?) -> Fx<Any?>)?
          source = source.source.fix()
        }
        null -> source = Fx.RaiseError(NullPointerException("Looping on null Fx")) //Improve message
      }

      if (hasResult) {
        val nextBind = popNextBind(bFirst, bRest)

        if (nextBind == null) {
          return result
        } else {
          source = executeSafe { nextBind(result) }
          hasResult = false
          result = null
          bFirst = null
        }
      }
    }
  }

  private inline fun executeSafe(crossinline f: () -> FxOf<Any?>): Fx<Any?> =
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
  private fun popNextBind(bFirst: ((Any?) -> Fx<Any?>)?, bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>?): ((Any?) -> Fx<Any?>)? =
    if ((bFirst != null) /*&& bFirst !is IOFrame.Companion.ErrorHandler*/)
      bFirst
    else if (bRest != null) {
      var cursor: ((Any?) -> Fx<Any?>)? = null
      while (cursor == null && bRest.isNotEmpty()) {
        val ref = bRest.pop()
        /*if (ref !is IOFrame.Companion.ErrorHandler) */cursor = ref
      }
      cursor
    } else {
      null
    }

}