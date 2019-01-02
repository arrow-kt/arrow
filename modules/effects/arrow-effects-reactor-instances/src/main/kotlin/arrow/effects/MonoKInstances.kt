package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext

@extension
interface MonoKFunctorInstance : Functor<ForMonoK> {
  override fun <A, B> MonoKOf<A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)
}

@extension
interface MonoKApplicativeInstance : Applicative<ForMonoK>, MonoKFunctorInstance {
  override fun <A, B> MonoKOf<A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)

  override fun <A, B> MonoKOf<A>.ap(ff: MonoKOf<(A) -> B>): MonoK<B> =
    fix().ap(ff)

  override fun <A> just(a: A): MonoK<A> =
    MonoK.just(a)
}

@extension
interface MonoKMonadInstance : Monad<ForMonoK>, MonoKApplicativeInstance {
  override fun <A, B> MonoKOf<A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)

  override fun <A, B> MonoKOf<A>.ap(ff: MonoKOf<(A) -> B>): MonoK<B> =
    fix().ap(ff)

  override fun <A, B> MonoKOf<A>.flatMap(f: (A) -> MonoKOf<B>): MonoK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, MonoKOf<Either<A, B>>>): MonoK<B> =
    MonoK.tailRecM(a, f)
}

@extension
interface MonoKApplicativeErrorInstance : ApplicativeError<ForMonoK, Throwable>, MonoKApplicativeInstance {
  override fun <A> raiseError(e: Throwable): MonoK<A> =
    MonoK.raiseError(e)

  override fun <A> MonoKOf<A>.handleErrorWith(f: (Throwable) -> MonoKOf<A>): MonoK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface MonoKMonadErrorInstance : MonadError<ForMonoK, Throwable>, MonoKMonadInstance, MonoKApplicativeErrorInstance {
  override fun <A, B> MonoKOf<A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)

  override fun <A> raiseError(e: Throwable): MonoK<A> =
    MonoK.raiseError(e)

  override fun <A> MonoKOf<A>.handleErrorWith(f: (Throwable) -> MonoKOf<A>): MonoK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface MonoKMonadThrowInstance : MonadThrow<ForMonoK>, MonoKMonadErrorInstance

@extension
interface MonoKBracketInstance : Bracket<ForMonoK, Throwable>, MonoKMonadThrowInstance {
  override fun <A, B> MonoKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> MonoKOf<Unit>, use: (A) -> MonoKOf<B>): MonoK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface MonoKMonadDeferInstance : MonadDefer<ForMonoK>, MonoKBracketInstance {
  override fun <A> defer(fa: () -> MonoKOf<A>): MonoK<A> =
    MonoK.defer(fa)
}

@extension
interface MonoKAsyncInstance : Async<ForMonoK>, MonoKMonadDeferInstance {
  override fun <A> async(fa: Proc<A>): MonoK<A> =
    MonoK.async { _, cb -> fa(cb) }

  override fun <A> asyncF(k: ProcF<ForMonoK, A>): MonoK<A> =
    MonoK.asyncF { _, cb -> k(cb) }

  override fun <A> MonoKOf<A>.continueOn(ctx: CoroutineContext): MonoK<A> =
    fix().continueOn(ctx)
}

@extension
interface MonoKConcurrentInstance : Concurrent<ForMonoK>, MonoKAsyncInstance {
  override fun <A> Kind<ForMonoK, A>.startF(ctx: CoroutineContext): Kind<ForMonoK, Fiber<ForMonoK, A>> =
    fix().startF(ctx)

  override fun <A> asyncF(k: ConnectedProcF<ForMonoK, A>): MonoK<A> =
    MonoK.asyncF(k)

  override fun <A> async(fa: ConnectedProc<ForMonoK, A>): MonoK<A> =
    MonoK.async(fa)

  override fun <A> asyncF(k: ProcF<ForMonoK, A>): MonoK<A> =
    MonoK.asyncF { _, cb -> k(cb) }

  override fun <A> async(fa: Proc<A>): MonoK<A> =
    MonoK.async { _, cb -> fa(cb) }

  override fun <A, B> racePair(ctx: CoroutineContext, fa: Kind<ForMonoK, A>, fb: Kind<ForMonoK, B>): Kind<ForMonoK, Either<Tuple2<A, Fiber<ForMonoK, B>>, Tuple2<Fiber<ForMonoK, A>, B>>> =
    MonoK.racePair(ctx, fa, fb)

}

@extension
interface MonoKEffectInstance : Effect<ForMonoK>, MonoKAsyncInstance {

  override fun <A> MonoKOf<A>.runAsync(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Unit> =
    fix().runAsync(cb)
}

@extension
interface MonoKConcurrentEffectInstance : ConcurrentEffect<ForMonoK>, MonoKEffectInstance, MonoKConcurrentInstance {
  override fun <A> MonoKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Disposable> =
    fix().runAsyncCancellable(cb)
}

object MonoKContext : MonoKConcurrentEffectInstance

@Deprecated(ExtensionsDSLDeprecated)
infix fun <A> ForMonoK.Companion.extensions(f: MonoKContext.() -> A): A =
  f(MonoKContext)
