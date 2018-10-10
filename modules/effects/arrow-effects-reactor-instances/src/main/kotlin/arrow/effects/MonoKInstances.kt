package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.*
import arrow.instance
import arrow.typeclasses.*
import kotlin.coroutines.experimental.CoroutineContext

@instance
interface MonoKFunctorInstance : Functor<ForMonoK> {
  override fun <A, B> Kind<ForMonoK, A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)
}

@instance
interface MonoKApplicativeInstance : Applicative<ForMonoK> {
  override fun <A, B> MonoKOf<A>.ap(ff: MonoKOf<(A) -> B>): MonoK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForMonoK, A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)

  override fun <A> just(a: A): MonoK<A> =
    MonoK.just(a)
}

@instance
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

@instance
interface MonoKApplicativeErrorInstance :
  ApplicativeError<ForMonoK, Throwable>,
  MonoKApplicativeInstance {
  override fun <A> raiseError(e: Throwable): MonoK<A> =
    MonoK.raiseError(e)

  override fun <A> MonoKOf<A>.handleErrorWith(f: (Throwable) -> MonoKOf<A>): MonoK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@instance
interface MonoKMonadErrorInstance :
  MonadError<ForMonoK, Throwable>,
  MonoKMonadInstance {
  override fun <A> raiseError(e: Throwable): MonoK<A> =
    MonoK.raiseError(e)

  override fun <A> MonoKOf<A>.handleErrorWith(f: (Throwable) -> MonoKOf<A>): MonoK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@instance
interface MonoKMonadDeferInstance :
  MonadDefer<ForMonoK>,
  MonoKMonadErrorInstance {
  override fun <A> defer(fa: () -> MonoKOf<A>): MonoK<A> =
    MonoK.defer(fa)
}

@instance
interface MonoKAsyncInstance :
  Async<ForMonoK>,
  MonoKMonadDeferInstance {
  override fun <A> async(fa: Proc<A>): MonoK<A> =
    MonoK.async(fa)

  override fun <A> MonoKOf<A>.continueOn(ctx: CoroutineContext): MonoK<A> =
    fix().continueOn(ctx)
}

@instance
interface MonoKEffectInstance :
  Effect<ForMonoK>,
  MonoKAsyncInstance {
  override fun <A> MonoKOf<A>.runAsync(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Unit> =
    fix().runAsync(cb)
}

@instance
interface MonoKConcurrentEffectInstance :
  ConcurrentEffect<ForMonoK>,
  MonoKEffectInstance {
  override fun <A> Kind<ForMonoK, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Disposable> =
    fix().runAsyncCancellable(cb)
}

object MonoKContext : MonoKConcurrentEffectInstance

infix fun <A> ForMonoK.Companion.extensions(f: MonoKContext.() -> A): A =
  f(MonoKContext)
