package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.experimental.CoroutineContext

@extension
interface MonoKFunctorInstance : Functor<ForMonoK> {
  override fun <A, B> Kind<ForMonoK, A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)
}

@extension
interface MonoKApplicativeInstance : Applicative<ForMonoK> {
  override fun <A, B> MonoKOf<A>.ap(ff: MonoKOf<(A) -> B>): MonoK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForMonoK, A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)

  override fun <A> just(a: A): MonoK<A> =
    MonoK.just(a)
}

@extension
interface MonoKMonadInstance : Monad<ForMonoK> {
  override fun <A, B> MonoKOf<A>.ap(ff: MonoKOf<(A) -> B>): MonoK<B> =
    fix().ap(ff)

  override fun <A, B> MonoKOf<A>.flatMap(f: (A) -> Kind<ForMonoK, B>): MonoK<B> =
    fix().flatMap(f)

  override fun <A, B> MonoKOf<A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, MonoKOf<Either<A, B>>>): MonoK<B> =
    MonoK.tailRecM(a, f)

  override fun <A> just(a: A): MonoK<A> =
    MonoK.just(a)
}

@extension
interface MonoKApplicativeErrorInstance :
  MonoKApplicativeInstance,
  ApplicativeError<ForMonoK, Throwable> {
  override fun <A> raiseError(e: Throwable): MonoK<A> =
    MonoK.raiseError(e)

  override fun <A> MonoKOf<A>.handleErrorWith(f: (Throwable) -> MonoKOf<A>): MonoK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface MonoKMonadErrorInstance :
  MonoKMonadInstance,
  MonadError<ForMonoK, Throwable> {
  override fun <A> raiseError(e: Throwable): MonoK<A> =
    MonoK.raiseError(e)

  override fun <A> MonoKOf<A>.handleErrorWith(f: (Throwable) -> MonoKOf<A>): MonoK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface MonoKMonadDeferInstance :
  MonoKMonadErrorInstance,
  MonadDefer<ForMonoK> {
  override fun <A> defer(fa: () -> MonoKOf<A>): MonoK<A> =
    MonoK.defer(fa)
}

@extension
interface MonoKAsyncInstance :
  MonoKMonadDeferInstance,
  Async<ForMonoK> {
  override fun <A> async(fa: Proc<A>): MonoK<A> =
    MonoK.async(fa)

  override fun <A> MonoKOf<A>.continueOn(ctx: CoroutineContext): MonoK<A> =
    fix().continueOn(ctx)
}

@extension
interface MonoKEffectInstance :
  MonoKAsyncInstance,
  Effect<ForMonoK> {
  override fun <A> MonoKOf<A>.runAsync(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Unit> =
    fix().runAsync(cb)
}

@extension
interface MonoKConcurrentEffectInstance :
  MonoKEffectInstance,
  ConcurrentEffect<ForMonoK> {
  override fun <A> Kind<ForMonoK, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Disposable> =
    fix().runAsyncCancellable(cb)
}

object MonoKContext : MonoKConcurrentEffectInstance

infix fun <A> ForMonoK.Companion.extensions(f: MonoKContext.() -> A): A =
  f(MonoKContext)
