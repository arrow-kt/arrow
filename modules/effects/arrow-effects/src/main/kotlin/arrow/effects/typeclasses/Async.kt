package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Right
import arrow.core.left
import arrow.effects.*
import arrow.effects.internal.CancelToken
import arrow.effects.internal.asyncIOContinuation
import arrow.typeclasses.MonadContinuation
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/** An asynchronous computation that might fail. **/
typealias Proc<A> = ((Either<Throwable, A>) -> Unit) -> Unit

typealias ProcF<F, A> = ((Either<Throwable, A>) -> Unit) -> Kind<F, Unit>

/**
 * ank_macro_hierarchy(arrow.effects.typeclasses.Async)
 *
 * The context required to run an asynchronous computation that may fail.
 **/
interface Async<F> : MonadDefer<F> {
  fun <A> async(fa: Proc<A>): Kind<F, A>

  /**
   * [async] variant that can suspend side effects in the provided registration function. On this variant, the passed
   * in function is injected with a side-effectful callback for signaling the final result of an asynchronous process.
   * Its returned result needs to be a pure `F[Unit]` that gets evaluated by the runtime.
   *
   * ```kotlin
   *
   * ```
   *
   */
  fun <A> asyncF(k: ProcF<F, A>): Kind<F, A>

  fun <A> Kind<F, A>.continueOn(ctx: CoroutineContext): Kind<F, A> =
    flatMap { a -> continueOn(ctx).map { a } }

  operator fun <A> invoke(ctx: CoroutineContext, f: () -> A): Kind<F, A> =
    shift(ctx).flatMap { delay(f) }

  fun <A> defer(ctx: CoroutineContext, f: () -> Kind<F, A>): Kind<F, A> =
    shift(ctx).flatMap { defer(f) }

  suspend fun <A> MonadContinuation<F, A>.continueOn(ctx: CoroutineContext): Unit =
    shift(ctx).bind()

  fun shift(ctx: CoroutineContext): Kind<F, Unit> = async { cb ->
    val a: suspend () -> Unit = {
      suspendCoroutine { ca: Continuation<Unit> ->
        ca.resumeWith(Result.success(Unit))
      }
    }
    a.startCoroutine(asyncIOContinuation(ctx, cb))
  }

  fun <A> never(): Kind<F, A> =
    async { }

  fun <A> cancellable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<F>): Kind<F, A> = Promise.uncancelable<F, Unit>(this).flatMap { promise ->
    asyncF<A> { cb ->
      val latchF = asyncF<Unit> { cb2 -> promise.get.map { cb2(rightUnit) } }
      val token = k { r ->
        promise.complete(Unit)
        cb(r)
      }

      just(token).bracketCase(use = { latchF }, release = { r, exitCase ->
        when (exitCase) {
          is ExitCase.Cancelled -> r
          else -> just(Unit)
        }
      })
    }
  }

  fun <A> cancellableF(k: ((Either<Throwable, A>) -> Unit) -> Kind<F, CancelToken<F>>, AS: Async<F>): Kind<F, A> =
    asyncF { cb ->
      val state = AtomicReference<(Either<Throwable, Unit>) -> Unit>(null)
      val cb1 = { r: Either<Throwable, A> ->
        try {
          cb(r)
        } finally {
          if (!state.compareAndSet(null, dummy)) {
            val cb2 = state.get()
            state.lazySet(null)
            cb2(rightUnit)
          }
        }
      }

      k(cb1).bracketCase(use = {
        async<Unit> { cb ->
          if (!state.compareAndSet(null, cb)) cb(rightUnit)
        }
      }, release = { token, exitCase ->
        when (exitCase) {
          is ExitCase.Cancelled -> token
          else -> just(Unit)
        }
      })
    }

}

internal val dummy: (Any?) -> Unit = { Unit }
internal val rightUnit = Right(Unit)
