package arrow.fx.extensions

import arrow.Kind
import arrow.core.Either
import arrow.fx.CancelToken
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IODispatchers
import arrow.fx.IOOf
import arrow.fx.OnCancel
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.fix
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.ConcurrentEffect
import arrow.fx.typeclasses.Dispatchers
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.Effect
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.fx.Timer
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.typeclasses.ConcurrentSyntax
import arrow.fx.typeclasses.Environment
import arrow.fx.typeclasses.UnsafeRun
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Apply
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.unsafe
import kotlin.coroutines.CoroutineContext
import arrow.fx.handleErrorWith as ioHandleErrorWith
import arrow.fx.handleError as ioHandleError

@extension
interface IOFunctor : Functor<ForIO> {
  override fun <A, B> IOOf<Throwable, A>.map(f: (A) -> B): IO<Throwable, B> =
    fix().map(f)
}

@extension
interface IOApply : Apply<ForIO> {
  override fun <A, B> IOOf<Throwable, A>.map(f: (A) -> B): IO<Throwable, B> =
    fix().map(f)

  override fun <A, B> IOOf<Throwable, A>.ap(ff: IOOf<Throwable, (A) -> B>): IO<Throwable, B> =
    fix().ap(ff)
}

@extension
interface IOApplicative : Applicative<ForIO> {
  override fun <A, B> IOOf<Throwable, A>.map(f: (A) -> B): IO<Throwable, B> =
    fix().map(f)

  override fun <A> just(a: A): IO<Throwable, A> =
    IO.just(a)

  override fun <A, B> IOOf<Throwable, A>.ap(ff: IOOf<Throwable, (A) -> B>): IO<Throwable, B> =
    fix().ap(ff)
}

@extension
interface IOMonad : Monad<ForIO> {
  override fun <A, B> IOOf<Throwable, A>.flatMap(f: (A) -> IOOf<Throwable, B>): IO<Throwable, B> =
    fix().flatMap(f)

  override fun <A, B> IOOf<Throwable, A>.map(f: (A) -> B): IO<Throwable, B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IOOf<Throwable, Either<A, B>>): IO<Throwable, B> =
    IO.tailRecM(a, f)

  override fun <A> just(a: A): IO<Throwable, A> =
    IO.just(a)
}

@extension
interface IOApplicativeError : ApplicativeError<ForIO, Throwable>, IOApplicative {
  override fun <A> IOOf<Throwable, A>.attempt(): IO<Throwable, Either<Throwable, A>> =
    fix().attempt()

  override fun <A> IOOf<Throwable, A>.handleErrorWith(f: (Throwable) -> IOOf<Throwable, A>): IO<Throwable, A> =
    ioHandleErrorWith(f)

  override fun <A> IOOf<Throwable, A>.handleError(f: (Throwable) -> A): IO<Throwable, A> =
    ioHandleError(f)

  override fun <A, B> IOOf<Throwable, A>.redeem(fe: (Throwable) -> B, fb: (A) -> B): IO<Throwable, B> =
    fix().redeem(fe, fb)

  override fun <A> raiseError(e: Throwable): IO<Throwable, A> =
    IO.raiseError(e)
}

@extension
interface IOMonadError : MonadError<ForIO, Throwable>, IOApplicativeError, IOMonad {

  override fun <A> just(a: A): IO<Throwable, A> = IO.just(a)

  override fun <A, B> IOOf<Throwable, A>.ap(ff: IOOf<Throwable, (A) -> B>): IO<Throwable, B> =
    fix().ap(ff)

  override fun <A, B> IOOf<Throwable, A>.map(f: (A) -> B): IO<Throwable, B> =
    fix().map(f)

  override fun <A> IOOf<Throwable, A>.attempt(): IO<Throwable, Either<Throwable, A>> =
    fix().attempt()

  override fun <A> IOOf<Throwable, A>.handleErrorWith(f: (Throwable) -> IOOf<Throwable, A>): IO<Throwable, A> =
    ioHandleErrorWith(f)

  override fun <A, B> IOOf<Throwable, A>.redeemWith(fe: (Throwable) -> IOOf<Throwable, B>, fb: (A) -> IOOf<Throwable, B>): IO<Throwable, B> =
    fix().redeemWith(fe, fb)

  override fun <A> raiseError(e: Throwable): IO<Throwable, A> =
    IO.raiseError(e)
}

@extension
interface IOMonadThrow : MonadThrow<ForIO>, IOMonadError

@extension
interface IOBracket : Bracket<ForIO, Throwable>, IOMonadThrow {
  override fun <A, B> IOOf<Throwable, A>.bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Throwable, Unit>, use: (A) -> IOOf<Throwable, B>): IO<Throwable, B> =
    fix().bracketCase(release, use)

  override fun <A, B> IOOf<Throwable, A>.bracket(release: (A) -> IOOf<Throwable, Unit>, use: (A) -> IOOf<Throwable, B>): IO<Throwable, B> =
    fix().bracket(release, use)

  override fun <A> IOOf<Throwable, A>.guarantee(finalizer: IOOf<Throwable, Unit>): IO<Throwable, A> =
    fix().guarantee(finalizer)

  override fun <A> IOOf<Throwable, A>.guaranteeCase(finalizer: (ExitCase<Throwable>) -> IOOf<Throwable, Unit>): IO<Throwable, A> =
    fix().guaranteeCase(finalizer)
}

@extension
interface IOMonadDefer : MonadDefer<ForIO>, IOBracket {
  override fun <A> defer(fa: () -> IOOf<Throwable, A>): IO<Throwable, A> =
    IO.defer(fa)

  override fun lazy(): IO<Throwable, Unit> = IO.lazy
}

@extension
interface IOAsync : Async<ForIO>, IOMonadDefer {
  override fun <A> async(fa: Proc<A>): IO<Throwable, A> =
    IO.async(fa)

  override fun <A> asyncF(k: ProcF<ForIO, A>): IO<Throwable, A> =
    IO.asyncF(k)

  override fun <A> IOOf<Throwable, A>.continueOn(ctx: CoroutineContext): IO<Throwable, A> =
    fix().continueOn(ctx)

  override fun <A> effect(ctx: CoroutineContext, f: suspend () -> A): IO<Throwable, A> =
    IO.effect(ctx, f)

  override fun <A> effect(f: suspend () -> A): IO<Throwable, A> =
    IO.effect(f)
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
interface IOConcurrent : Concurrent<ForIO>, IOAsync {
  override fun <A> CoroutineContext.startFiber(kind: IOOf<Throwable, A>): IO<Throwable, Fiber<ForIO, A>> =
    kind.fix().startFiber(this)

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForIO>): Kind<ForIO, A> =
    IO.cancelable(k)

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> IOOf<Throwable, CancelToken<ForIO>>): IO<Throwable, A> =
    IO.cancelableF(k)

  override fun <A, B> CoroutineContext.racePair(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>): IO<Throwable, RacePair<ForIO, A, B>> =
    IO.racePair(this, fa, fb)

  override fun <A, B, C> CoroutineContext.raceTriple(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>, fc: Kind<ForIO, C>): IO<Throwable, RaceTriple<ForIO, A, B, C>> =
    IO.raceTriple(this, fa, fb, fc)

  override fun <A, B, C> CoroutineContext.parMapN(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>, f: (A, B) -> C): Kind<ForIO, C> =
    IO.parMapN(this@parMapN, fa, fb, f)

  override fun <A, B, C, D> CoroutineContext.parMapN(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>, fc: Kind<ForIO, C>, f: (A, B, C) -> D): Kind<ForIO, D> =
    IO.parMapN(this@parMapN, fa, fb, fc, f)
}

fun IO.Companion.concurrent(dispatchers: Dispatchers<ForIO>): Concurrent<ForIO> = object : IOConcurrent {
  override fun dispatchers(): Dispatchers<ForIO> = dispatchers
}

fun IO.Companion.timer(CF: Concurrent<ForIO>): Timer<ForIO> =
  Timer(CF)

@extension
interface IOEffect : Effect<ForIO>, IOAsync {
  override fun <A> IOOf<Throwable, A>.runAsync(cb: (Either<Throwable, A>) -> IOOf<Throwable, Unit>): IO<Throwable, Unit> =
    fix().runAsync(cb)
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
interface IOConcurrentEffect : ConcurrentEffect<ForIO>, IOEffect, IOConcurrent {

  override fun <A> IOOf<Throwable, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> IOOf<Throwable, Unit>): IO<Throwable, Disposable> =
    fix().runAsyncCancellable(OnCancel.ThrowCancellationException, cb)
}

fun IO.Companion.concurrentEffect(dispatchers: Dispatchers<ForIO>): ConcurrentEffect<ForIO> = object : IOConcurrentEffect {
  override fun dispatchers(): Dispatchers<ForIO> = dispatchers
}

@extension
interface IOSemigroup<A> : Semigroup<IO<Throwable, A>> {

  fun SG(): Semigroup<A>

  override fun IO<Throwable, A>.combine(b: IO<Throwable, A>): IO<Throwable, A> =
    flatMap { a1: A -> b.map { a2: A -> SG().run { a1.combine(a2) } } }
}

@extension
interface IOMonoid<A> : Monoid<IO<Throwable, A>>, IOSemigroup<A> {

  override fun SG(): Semigroup<A> = SM()

  fun SM(): Monoid<A>

  override fun empty(): IO<Throwable, A> = IO.just(SM().empty())
}

@extension
interface IOUnsafeRun : UnsafeRun<ForIO> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<ForIO, A>): A = fa().fix().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<ForIO, A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().unsafeRunAsync(cb)
}

@extension
interface IODispatchers : Dispatchers<ForIO> {
  override fun default(): CoroutineContext =
    IODispatchers.CommonPool
}

@extension
interface IOEnvironment : Environment<ForIO> {
  override fun dispatchers(): Dispatchers<ForIO> =
    IO.dispatchers()

  override fun handleAsyncError(e: Throwable): IO<Throwable, Unit> =
    IO { println("Found uncaught async exception!"); e.printStackTrace() }
}

@extension
interface IODefaultConcurrent : Concurrent<ForIO>, IOConcurrent {

  override fun dispatchers(): Dispatchers<ForIO> =
    IO.dispatchers()
}

fun IO.Companion.timer(): Timer<ForIO> = Timer(IO.concurrent())

@extension
interface IODefaultConcurrentEffect : ConcurrentEffect<ForIO>, IOConcurrentEffect, IODefaultConcurrent

fun <A> IO.Companion.fx(c: suspend ConcurrentSyntax<ForIO>.() -> A): IO<Throwable, A> =
  IO.concurrent().fx.concurrent(c).fix()
