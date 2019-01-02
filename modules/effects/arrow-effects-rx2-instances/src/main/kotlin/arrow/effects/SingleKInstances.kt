package arrow.effects

import arrow.core.Either
import arrow.core.Tuple2
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext

@extension
interface SingleKFunctorInstance : Functor<ForSingleK> {
  override fun <A, B> SingleKOf<A>.map(f: (A) -> B): SingleK<B> =
    fix().map(f)
}

@extension
interface SingleKApplicativeInstance : Applicative<ForSingleK> {
  override fun <A, B> SingleKOf<A>.ap(ff: SingleKOf<(A) -> B>): SingleK<B> =
    fix().ap(ff)

  override fun <A, B> SingleKOf<A>.map(f: (A) -> B): SingleK<B> =
    fix().map(f)

  override fun <A> just(a: A): SingleK<A> =
    SingleK.just(a)
}

@extension
interface SingleKMonadInstance : Monad<ForSingleK> {
  override fun <A, B> SingleKOf<A>.ap(ff: SingleKOf<(A) -> B>): SingleK<B> =
    fix().ap(ff)

  override fun <A, B> SingleKOf<A>.flatMap(f: (A) -> SingleKOf<B>): SingleK<B> =
    fix().flatMap(f)

  override fun <A, B> SingleKOf<A>.map(f: (A) -> B): SingleK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: Function1<A, SingleKOf<Either<A, B>>>): SingleK<B> =
    SingleK.tailRecM(a, f)

  override fun <A> just(a: A): SingleK<A> =
    SingleK.just(a)
}

@extension
interface SingleKApplicativeErrorInstance :
  ApplicativeError<ForSingleK, Throwable>,
  SingleKApplicativeInstance {
  override fun <A> raiseError(e: Throwable): SingleK<A> =
    SingleK.raiseError(e)

  override fun <A> SingleKOf<A>.handleErrorWith(f: (Throwable) -> SingleKOf<A>): SingleK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface SingleKMonadErrorInstance :
  MonadError<ForSingleK, Throwable>,
  SingleKMonadInstance {
  override fun <A> raiseError(e: Throwable): SingleK<A> =
    SingleK.raiseError(e)

  override fun <A> SingleKOf<A>.handleErrorWith(f: (Throwable) -> SingleKOf<A>): SingleK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface SingleKMonadThrowInstance : MonadThrow<ForSingleK>, SingleKMonadErrorInstance

@extension
interface SingleKBracketInstance : Bracket<ForSingleK, Throwable>, SingleKMonadThrowInstance {
  override fun <A, B> SingleKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> SingleKOf<Unit>, use: (A) -> SingleKOf<B>): SingleK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface SingleKMonadDeferInstance : MonadDefer<ForSingleK>, SingleKBracketInstance {
  override fun <A> defer(fa: () -> SingleKOf<A>): SingleK<A> =
    SingleK.defer(fa)
}

@extension
interface SingleKAsyncInstance :
  Async<ForSingleK>,
  SingleKMonadDeferInstance {
  override fun <A> async(fa: Proc<A>): SingleK<A> =
    SingleK.async { _, cb -> fa(cb) }

  override fun <A> asyncF(k: ProcF<ForSingleK, A>): SingleK<A> =
    SingleK.asyncF { _, cb -> k(cb) }

  override fun <A> SingleKOf<A>.continueOn(ctx: CoroutineContext): SingleK<A> =
    fix().continueOn(ctx)
}

@extension
interface SingleKConcurrentInstance : Concurrent<ForSingleK>, SingleKAsyncInstance {

  override fun <A> SingleKOf<A>.startF(ctx: CoroutineContext): SingleK<Fiber<ForSingleK, A>> =
    fix().startF(ctx)

  override fun <A> asyncF(k: ConnectedProcF<ForSingleK, A>): SingleK<A> =
    SingleK.asyncF(k)

  override fun <A> async(fa: ConnectedProc<ForSingleK, A>): SingleK<A> =
    SingleK.async(fa)

  override fun <A> asyncF(k: ProcF<ForSingleK, A>): SingleK<A> =
    SingleK.asyncF { _, cb -> k(cb) }

  override fun <A> async(fa: Proc<A>): SingleK<A> =
    SingleK.async { _, cb -> fa(cb) }

  override fun <A, B> racePair(ctx: CoroutineContext, fa: SingleKOf<A>, fb: SingleKOf<B>): SingleK<Either<Tuple2<A, Fiber<ForSingleK, B>>, Tuple2<Fiber<ForSingleK, A>, B>>> =
    SingleK.racePair2(ctx, fa, fb)

//  override fun <A, B> raceN(ctx: CoroutineContext, fa: Kind<ForSingleK, A>, fb: Kind<ForSingleK, B>): SingleK<Either<A, B>> {
//    val scheduler = ctx.asScheduler()
//    return Single.ambArray(
//      fa.value()
//        .observeOn(scheduler)
//        .subscribeOn(scheduler)
//        .map(::Left),
//      fb.value()
//        .observeOn(scheduler)
//        .subscribeOn(scheduler)
//        .map(::Right)
//    ).k()
//  }

}

@extension
interface SingleKEffectInstance :
  Effect<ForSingleK>,
  SingleKAsyncInstance {
  override fun <A> SingleKOf<A>.runAsync(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Unit> =
    fix().runAsync(cb)
}

@extension
interface SingleKConcurrentEffectInstance : ConcurrentEffect<ForSingleK>, SingleKConcurrentInstance, SingleKEffectInstance {
  override fun <A> SingleKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Disposable> =
    fix().runAsyncCancellable(cb)
}

object SingleKContext : SingleKConcurrentEffectInstance

@Deprecated(ExtensionsDSLDeprecated)
infix fun <A> ForSingleK.Companion.extensions(f: SingleKContext.() -> A): A =
  f(SingleKContext)
