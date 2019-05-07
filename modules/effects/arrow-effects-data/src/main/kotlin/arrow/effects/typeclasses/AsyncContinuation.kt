package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.typeclasses.MonadContinuation
import arrow.typeclasses.MonadThrowContinuation
import arrow.typeclasses.stateStack
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

typealias Disposable = () -> Unit

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class AsyncContinuation<F, A>(val SC: Async<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadThrowContinuation<F, A>(SC), Async<F> by SC, BindSyntax<F> {

  override val fx: PartiallyAppliedAsyncFx<F> = SC.fx
  override fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> = fx.monad(c)
  override fun <B> bindingCatch(c: suspend MonadThrowContinuation<F, *>.() -> B): Kind<F, B> = fx.monadThrow(c)

  override fun returnedMonad(): Kind<F, A> = returnedMonad

  suspend fun <B> bindDefer(f: () -> B): B =
    delay(f).bind()

  suspend fun <B> bindDeferIn(context: CoroutineContext, f: () -> B): B =
    defer { bindingCatch { bindIn(context, f) } }.bind()

  suspend fun <B> bindDelayOrRaise(f: () -> Either<Throwable, B>): B =
          delayOrRaise(f).bind()

  @Deprecated("Use bindDelayOrRaise instead",
          ReplaceWith("bindDelayOrRaise(f)", "arrow.effects.typeclasses.MonadDeferCancellableContinuations"))
  suspend fun <B> bindDeferUnsafe(f: () -> Either<Throwable, B>): B =
          delayOrRaise(f).bind()

  override suspend fun <B> bind(m: () -> Kind<F, B>): B = suspendCoroutineUninterceptedOrReturn { c ->
    val labelHere = c.stateStack // save the whole coroutine stack labels
    returnedMonad = m().flatMap { x: B ->
      c.stateStack = labelHere
      c.resumeWith(Result.success(x))
      returnedMonad
    }
    COROUTINE_SUSPENDED
  }

  override suspend fun <B> bindIn(context: CoroutineContext, m: () -> B): B = suspendCoroutineUninterceptedOrReturn { c ->
    val labelHere = c.stateStack // save the whole coroutine stack labels
    val monadCreation: suspend () -> Kind<F, A> = {
      val datatype = try {
        just(m())
      } catch (t: Throwable) {
        t.raiseNonFatal<B>()
      }
      datatype.flatMap { xx: B ->
        c.stateStack = labelHere
        c.resumeWith(Result.success(xx))
        returnedMonad
      }
    }
    val completion = bindingInContextContinuation(context)
    returnedMonad = just(Unit).flatMap {
      monadCreation.startCoroutine(completion)
      val error = completion.await()
      if (error != null) {
        throw error
      }
      returnedMonad
    }
    COROUTINE_SUSPENDED
  }
}
