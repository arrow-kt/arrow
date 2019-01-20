package arrow.effects.extensions

import arrow.core.Either
import arrow.core.*
import arrow.effects.*
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext
import arrow.effects.ap as ioAp
import arrow.effects.handleErrorWith as ioHandleErrorWith
import arrow.effects.startF as ioStart

@extension
interface IOFunctor<E> : Functor<BIOPartialOf<E>> {
  override fun <A, B> BIOOf<E, A>.map(f: (A) -> B): BIO<E, B> =
    fix().map(f)
}

@extension
interface IOBifunctor<E> : Bifunctor<ForBIO> {
  override fun <A, B, C, D> BiOOf<A, B>.bimap(fl: (A) -> C, fr: (B) -> D): BIOOf<C, D> =
    fix().bimap(fl, fr)
}

@extension
interface IOApplicative<E> : Applicative<BIOPartialOf<E>> {
  override fun <A, B> BIOOf<E, A>.map(f: (A) -> B): BIO<E, B> =
    fix().map(f)

  override fun <A> just(a: A): BIO<E, A> =
    IO.just(a)

  override fun <A, B> BIOOf<E, A>.ap(ff: BIOOf<E, (A) -> B>): BIO<E, B> =
    ioAp(ff)
}

@extension
interface IOMonad<E> : Monad<BIOPartialOf<E>> {
  override fun <A, B> BIOOf<E, A>.flatMap(f: (A) -> BIOOf<E, B>): BIO<E, B> =
    fix().flatMap(f)

  override fun <A, B> BIOOf<E, A>.map(f: (A) -> B): BIO<E, B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> BIOOf<E, Either<A, B>>): BIO<E, B> =
    IO.tailRecM(a, f)

  override fun <A> just(a: A): BIO<E, A> =
    IO.just(a)
}

@extension
interface IOApplicativeError<E>: ApplicativeError<ForIO, E>, IOApplicative<E> {
  override fun <A> BIOOf<E, A>.attempt(): BIO<E, Either<Throwable, A>> =
    fix().attempt()

  override fun <A> BIOOf<E, A>.handleErrorWith(f: (Throwable) -> BIOOf<E, A>): BIO<E, A> =
    ioHandleErrorWith(f)

  override fun <A> raiseError(e: Throwable): BIO<E, A> =
    IO.raiseError(e)
}

@extension
interface IOMonadError<E>: MonadError<ForIO, E>, IOApplicativeError<E>, IOMonad<E> {

  override fun <A> just(a: A): BIO<E, A> = IO.just(a)

  override fun <A, B> BIOOf<E, A>.ap(ff: BIOOf<E, (A) -> B>): BIO<E, B> =
    ioAp(ff)

  override fun <A, B> BIOOf<E, A>.map(f: (A) -> B): BIO<E, B> =
    fix().map(f)

  override fun <A> BIOOf<E, A>.attempt(): BIO<E, Either<Throwable, A>> =
    fix().attempt()

  override fun <A> BIOOf<E, A>.handleErrorWith(f: (Throwable) -> BIOOf<E, A>): BIO<E, A> =
    ioHandleErrorWith(f)

  override fun <A> raiseError(e: Throwable): BIO<E, A> =
    IO.raiseError(e)
}

@extension
interface IOMonadThrow : MonadThrow<BIOPartialOf<Throwable>>, IOMonadError

@extension
interface IOBracket: Bracket<ForIO, Throwable>, IOMonadThrow {
  override fun <A, B> BIOOf<E, A>.bracketCase(release: (A, ExitCase<Throwable>) -> BIOOf<E, Unit>, use: (A) -> BIOOf<E, B>): BIO<E, B> =
    fix().bracketCase({ a, e -> release(a, e) }, { a -> use(a) })

  override fun <A, B> BIOOf<E, A>.bracket(release: (A) -> BIOOf<E, Unit>, use: (A) -> BIOOf<E, B>): BIO<E, B> =
    fix().bracket({ a -> release(a) }, { a -> use(a) })

  override fun <A> BIOOf<E, A>.guarantee(finalizer: BIOOf<E, Unit>): BIO<E, A> =
    fix().guarantee(finalizer)

  override fun <A> BIOOf<E, A>.guaranteeCase(finalizer: (ExitCase<Throwable>) -> BIOOf<E, Unit>): BIO<E, A> =
    fix().guaranteeCase { e -> finalizer(e) }
}

@extension
interface IOMonadDefer : MonadDefer<BIOPartialOf<Throwable>>, IOBracket {
  override fun <A> defer(fa: () -> BIOOf<E, A>): BIO<E, A> =
    IO.defer(fa)

  override fun lazy(): BIO<E, Unit> = IO.lazy
}

@extension
interface IOAsync : Async<BIOPartialOf<Throwable>>, IOMonadDefer {
  override fun <A> async(fa: Proc<A>): BIO<E, A> =
    IO.async(fa.toIOProc())

  override fun <A> asyncF(k: ProcF<ForIO, A>): BIO<E, A> =
    IO.asyncF(k.toIOProcF())

  override fun <A> BIOOf<E, A>.continueOn(ctx: CoroutineContext): BIO<E, A> =
    fix().continueOn(ctx)
}

@extension
interface IOConcurrent : Concurrent<BIOPartialOf<Throwable>>, IOAsync {
  override fun <A> BIOOf<E, A>.startF(ctx: CoroutineContext): BIO<E, Fiber<ForIO, A>> =
    ioStart(ctx)

  override fun <A> asyncF(k: ConnectedProcF<ForIO, A>): BIO<E, A> =
    IO.asyncF(k)

  override fun <A> async(fa: ConnectedProc<ForIO, A>): BIO<E, A> =
    IO.async(fa)

  override fun <A> asyncF(k: ProcF<ForIO, A>): BIO<E, A> =
    IO.asyncF { _, cb -> k(cb) }

  override fun <A> async(fa: Proc<A>): BIO<E, A> =
    IO.async { _, cb -> fa(cb) }

  override fun <A, B> racePair(ctx: CoroutineContext, fa: BIOOf<E, A>, fb: BIOOf<E, B>): BIO<E, RacePair<ForIO, A, B>> =
    IO.racePair(ctx, fa, fb)

  override fun <A, B, C> raceTriple(ctx: CoroutineContext, fa: BIOOf<E, A>, fb: BIOOf<E, B>, fc: BIOOf<E, C>): BIO<E, RaceTriple<ForIO, A, B, C>> =
    IO.raceTriple(ctx, fa, fb, fc)

}

@extension
interface IOEffect : Effect<BIOPartialOf<Throwable>>, IOAsync {
  override fun <A> BIOOf<E, A>.runAsync(cb: (Either<Throwable, A>) -> BIOOf<E, Unit>): BIO<E, Unit> =
    fix().runAsync(cb)
}

@extension
interface IOConcurrentEffect : ConcurrentEffect<BIOPartialOf<Throwable>>, IOEffect, IOConcurrent {
  override fun <A> BIOOf<E, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> BIOOf<E, Unit>): BIO<E, Disposable> =
    fix().runAsyncCancellable(OnCancel.ThrowCancellationException, cb)
}

@extension
interface IOSemigroup<E, A> : Semigroup<BIO<E, A>> {

  fun SG(): Semigroup<A>

  override fun BIO<E, A>.combine(b: BIO<E, A>): BIO<E, A> =
    flatMap { a1: A -> b.map { a2: A -> SG().run { a1.combine(a2) } } }
}

@extension
interface IOMonoid<E, A> : Monoid<BIO<E, A>>, IOSemigroup<E, A> {

  override fun SG(): Semigroup<A> = SM()

  fun SM(): Monoid<A>

  override fun empty(): BIO<E, A> = IO.just(SM().empty())

}
