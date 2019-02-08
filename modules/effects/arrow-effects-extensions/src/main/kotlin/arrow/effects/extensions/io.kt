package arrow.effects.extensions

import arrow.Kind
import arrow.core.Either
import arrow.effects.*
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import arrow.unsafe
import kotlin.coroutines.CoroutineContext
import arrow.effects.ap as ioAp
import arrow.effects.handleErrorWith as ioHandleErrorWith
import arrow.effects.startFiber as ioStart

@extension
interface IOFunctor : Functor<ForIO> {
  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)
}

@extension
interface IOApplicative : Applicative<ForIO> {
  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A> just(a: A): IO<A> =
    IO.just(a)

  override fun <A, B> IOOf<A>.ap(ff: IOOf<(A) -> B>): IO<B> =
    ioAp(ff)
}

@extension
interface IOMonad : Monad<ForIO> {
  override fun <A, B> IOOf<A>.flatMap(f: (A) -> IOOf<B>): IO<B> =
    fix().flatMap(f)

  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IOOf<Either<A, B>>): IO<B> =
    IO.tailRecM(a, f)

  override fun <A> just(a: A): IO<A> =
    IO.just(a)
}

@extension
interface IOApplicativeError : ApplicativeError<ForIO, Throwable>, IOApplicative {
  override fun <A> IOOf<A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> IOOf<A>.handleErrorWith(f: (Throwable) -> IOOf<A>): IO<A> =
    ioHandleErrorWith(f)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)
}

@extension
interface IOMonadError : MonadError<ForIO, Throwable>, IOApplicativeError, IOMonad {

  override fun <A> just(a: A): IO<A> = IO.just(a)

  override fun <A, B> IOOf<A>.ap(ff: IOOf<(A) -> B>): IO<B> =
    ioAp(ff)

  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A> IOOf<A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> IOOf<A>.handleErrorWith(f: (Throwable) -> IOOf<A>): IO<A> =
    ioHandleErrorWith(f)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)
}

@extension
interface IOMonadThrow : MonadThrow<ForIO>, IOMonadError

@extension
interface IOBracket : Bracket<ForIO, Throwable>, IOMonadThrow {
  override fun <A, B> IOOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
    fix().bracketCase({ a, e -> release(a, e) }, { a -> use(a) })

  override fun <A, B> IOOf<A>.bracket(release: (A) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
    fix().bracket({ a -> release(a) }, { a -> use(a) })

  override fun <A> IOOf<A>.guarantee(finalizer: IOOf<Unit>): IO<A> =
    fix().guarantee(finalizer)

  override fun <A> IOOf<A>.guaranteeCase(finalizer: (ExitCase<Throwable>) -> IOOf<Unit>): IO<A> =
    fix().guaranteeCase { e -> finalizer(e) }
}

@extension
interface IOMonadDefer : MonadDefer<ForIO>, IOBracket {
  override fun <A> defer(fa: () -> IOOf<A>): IO<A> =
    IO.defer(fa)

  override fun lazy(): IO<Unit> = IO.lazy
}

@extension
interface IOAsync : Async<ForIO>, IOMonadDefer {
  override fun <A> async(fa: Proc<A>): IO<A> =
    IO.async(fa.toIOProc())

  override fun <A> asyncF(k: ProcF<ForIO, A>): IO<A> =
    IO.asyncF(k.toIOProcF())

  override fun <A> IOOf<A>.continueOn(ctx: CoroutineContext): IO<A> =
    fix().continueOn(ctx)
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
interface IOConcurrent : Concurrent<ForIO>, IOAsync {

  override fun <A> CoroutineContext.startFiber(fa: IOOf<A>): IO<Fiber<ForIO, A>> =
    fa.ioStart(this)

  override fun <A> asyncF(fa: ConnectedProcF<ForIO, A>): IO<A> =
    IO.asyncF(fa)

  override fun <A> async(fa: ConnectedProc<ForIO, A>): IO<A> =
    IO.async(fa)

  override fun <A> asyncF(k: ProcF<ForIO, A>): IO<A> =
    IO.asyncF { _, cb -> k(cb) }

  override fun <A> async(fa: Proc<A>): IO<A> =
    IO.async { _, cb -> fa(cb) }

  override fun <A, B> CoroutineContext.racePair(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>): IO<RacePair<ForIO, A, B>> =
    IO.racePair(this, fa, fb)

  override fun <A, B, C> CoroutineContext.raceTriple(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>, fc: Kind<ForIO, C>): IO<RaceTriple<ForIO, A, B, C>> =
    IO.raceTriple(this, fa, fb, fc)

}

fun IO.Companion.concurrent(dispatchers: Dispatchers<ForIO>): Concurrent<ForIO> = object : IOConcurrent {
  override fun dispatchers(): Dispatchers<ForIO> = dispatchers
}

@extension
interface IOEffect : Effect<ForIO>, IOAsync {
  override fun <A> IOOf<A>.runAsync(cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Unit> =
    fix().runAsync(cb)
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
interface IOConcurrentEffect : ConcurrentEffect<ForIO>, IOEffect, IOConcurrent {

  override fun <A> IOOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Disposable> =
    fix().runAsyncCancellable(OnCancel.ThrowCancellationException, cb)
}

fun IO.Companion.concurrentEffect(dispatchers: Dispatchers<ForIO>): ConcurrentEffect<ForIO> = object : IOConcurrentEffect {
  override fun dispatchers(): Dispatchers<ForIO> = dispatchers
}

@extension
interface IOSemigroup<A> : Semigroup<IO<A>> {

  fun SG(): Semigroup<A>

  override fun IO<A>.combine(b: IO<A>): IO<A> =
    flatMap { a1: A -> b.map { a2: A -> SG().run { a1.combine(a2) } } }
}

@extension
interface IOMonoid<A> : Monoid<IO<A>>, IOSemigroup<A> {

  override fun SG(): Semigroup<A> = SM()

  fun SM(): Monoid<A>

  override fun empty(): IO<A> = IO.just(SM().empty())

}

@extension
interface IOUnsafeRun : UnsafeRun<ForIO> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<ForIO, A>): A = fa().fix().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<ForIO, A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().unsafeRunAsync(cb)

}
