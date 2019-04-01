package arrow.effects.suspended.fx

import arrow.Kind
import arrow.core.Either
import arrow.core.NonFatal
import arrow.effects.KindConnection
import arrow.effects.OnCancel
import arrow.effects.internal.Platform
import arrow.typeclasses.Applicative
import kotlin.coroutines.coroutineContext

internal val FxAP: Applicative<ForFx> = object : Applicative<ForFx> {
  override fun <A> just(a: A): Fx<A> = Fx.just(a)
  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> = fix().ap(ff)
}

object FxRunLoop {

  operator fun <A> invoke(fa: FxOf<A>): suspend () -> A = {
    val conn: KindConnection<ForFx> = coroutineContext[CancelToken]?.connection ?: KindConnection.uncancelable(FxAP)
    runLoop(fa as Fx<Any?>, conn) as A
  }

  suspend fun <A> start(fa: FxOf<A>, conn: KindConnection<ForFx> = KindConnection.uncancelable(FxAP)): A =
    runLoop(fa as Fx<Any?>, conn) as A

  private suspend fun runLoop(fa: Fx<Any?>, conn: KindConnection<ForFx>): Any? {
    var source: Fx<Any?>? = fa
    var bFirst: ((Any?) -> Fx<Any?>)? = null
    var bRest: Platform.ArrayStack<(Any?) -> Fx<Any?>>? = null

    var hasResult = false
    var result: Any? = null

    while (true) {
      val isCancelled = conn?.isCanceled() ?: false
//      println("I am checking isCancelled: $isCancelled")
      if (isCancelled) throw OnCancel.CancellationException

      when (source) {
        is Fx.RaiseError -> throw source.error
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