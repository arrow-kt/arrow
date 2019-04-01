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

  private suspend inline fun runLoop(fa: Fx<Any?>): Any? {
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
      val tag = source?.tag ?: UnknownTag
      when (tag) {
        RaiseErrorTag -> {
          throw (source as Fx.RaiseError<Any?>).error
        } //An alternative to throwing would be nice..
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
        UnknownTag -> source = Fx.RaiseError(NullPointerException("Looping on null Fx")) //Improve message
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
    when {
      bFirst != null /*&& bFirst !is IOFrame.Companion.ErrorHandler*/ -> bFirst
      bRest != null -> {
        var cursor: ((Any?) -> Fx<Any?>)? = null
        while (cursor == null && bRest.isNotEmpty()) {
          val ref = bRest.pop()
          /*if (ref !is IOFrame.Companion.ErrorHandler) */cursor = ref
        }
        cursor
      }
      else -> null
    }

}