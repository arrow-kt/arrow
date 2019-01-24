package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.effects.data.internal.BindingCancellationException
import arrow.effects.typeclasses.suspended.MonadDeferSyntax
import arrow.typeclasses.MonadContinuation
import arrow.typeclasses.MonadErrorContinuation
import arrow.typeclasses.stateStack
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

typealias Disposable = () -> Unit

@RestrictsSuspension
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
open class MonadDeferCancellableContinuation<F, A>(val SC: MonadDefer<F>, override val context: CoroutineContext = EmptyCoroutineContext) :
  MonadErrorContinuation<F, A>(SC), MonadDefer<F> by SC, MonadDeferSyntax<F> {

  protected val cancelled: AtomicBoolean = AtomicBoolean(false)

  fun disposable(): Disposable = { cancelled.set(true) }

  override fun <B> binding(c: suspend MonadContinuation<F, *>.() -> B): Kind<F, B> =
    fx(c)

  override fun returnedMonad(): Kind<F, A> = returnedMonad

  suspend fun <B> bindDefer(f: () -> B): B =
    delay(f).bind()

  suspend fun <B> bindDeferIn(context: CoroutineContext, f: () -> B): B =
    defer { bindingCatch { bindIn(context, f) } }.bind()

  suspend fun <B> bindDeferUnsafe(f: () -> Either<Throwable, B>): B =
    deferUnsafe(f).bind()

  override suspend fun <B> bind(m: () -> Kind<F, B>): B = suspendCoroutineUninterceptedOrReturn { c ->
    val labelHere = c.stateStack // save the whole coroutine stack labels
    returnedMonad = m().flatMap { x: B ->
      c.stateStack = labelHere
      if (cancelled.get()) {
        throw BindingCancellationException()
      }
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
        raiseError<B>(t)
      }
      datatype.flatMap { xx: B ->
        c.stateStack = labelHere
        if (cancelled.get()) {
          throw BindingCancellationException()
        }
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

  override fun <A> fx(f: suspend MonadContinuation<F, *>.() -> A): Kind<F, A> =
    super<MonadDeferSyntax>.fx(f)

  override fun <A> f(fa: suspend () -> A): Kind<F, A> =
    super<MonadDeferSyntax>.f(fa)

}
