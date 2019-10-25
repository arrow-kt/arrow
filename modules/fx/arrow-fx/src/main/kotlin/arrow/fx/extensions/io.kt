package arrow.fx.extensions

import arrow.Kind
import arrow.core.Either
import arrow.extension
import arrow.fx.CancelToken
import arrow.fx.IO
import arrow.fx.IODispatchers
import arrow.fx.IOOf
import arrow.fx.IOPartialOf
import arrow.fx.OnCancel
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Timer
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.fix
import arrow.fx.flatMap
import arrow.fx.runAsyncCancellable
import arrow.fx.startFiber
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.ConcurrentEffect
import arrow.fx.typeclasses.ConcurrentSyntax
import arrow.fx.typeclasses.Dispatchers
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.Effect
import arrow.fx.typeclasses.Environment
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.ProcE
import arrow.fx.typeclasses.ProcEF
import arrow.fx.typeclasses.UnsafeRun
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Apply
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.unsafe
import kotlin.coroutines.CoroutineContext
import arrow.fx.ap as ioAp
import arrow.fx.bracket as ioBracket
import arrow.fx.bracketCase as ioBracketCase
import arrow.fx.flatMap as ioFlatMap
import arrow.fx.guarantee as ioGuarantee
import arrow.fx.guaranteeCase as ioGuaranteeCase
import arrow.fx.handleError as ioHandleError
import arrow.fx.handleErrorWith as ioHandleErrorWith
import arrow.fx.redeemWith as ioRedeemWith

@extension
interface IOFunctor<E> : Functor<IOPartialOf<E>> {
  override fun <A, B> IOOf<E, A>.map(f: (A) -> B): IO<E, B> =
    fix().map(f)
}

@extension
interface IOApply<E> : Apply<IOPartialOf<E>> {
  override fun <A, B> IOOf<E, A>.map(f: (A) -> B): IO<E, B> =
    fix().map(f)

  override fun <A, B> IOOf<E, A>.ap(ff: IOOf<E, (A) -> B>): IO<E, B> =
    fix().ioAp(ff)
}

@extension
interface IOApplicative<E> : Applicative<IOPartialOf<E>> {
  override fun <A, B> IOOf<E, A>.map(f: (A) -> B): IO<E, B> =
    fix().map(f)

  override fun <A> just(a: A): IO<E, A> =
    IO.just(a)

  override fun <A, B> IOOf<E, A>.ap(ff: IOOf<E, (A) -> B>): IO<E, B> =
    fix().ioAp(ff)
}

@extension
interface IOMonad<E> : Monad<IOPartialOf<E>> {
  override fun <A, B> IOOf<E, A>.flatMap(f: (A) -> IOOf<E, B>): IO<E, B> =
    fix().ioFlatMap(f)

  override fun <A, B> IOOf<E, A>.map(f: (A) -> B): IO<E, B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IOOf<E, Either<A, B>>): IO<E, B> =
    IO.tailRecM(a, f)

  override fun <A> just(a: A): IO<E, A> =
    IO.just(a)
}

@extension
interface IOApplicativeError<E> : ApplicativeError<IOPartialOf<E>, E>, IOApplicative<E> {
  override fun <A> IOOf<E, A>.attempt(): IO<E, Either<E, A>> =
    fix().attempt()

  override fun <A> IOOf<E, A>.handleErrorWith(f: (E) -> IOOf<E, A>): IO<E, A> =
    ioHandleErrorWith(f)

  override fun <A> IOOf<E, A>.handleError(f: (E) -> A): IO<E, A> =
    ioHandleError(f)

  override fun <A, B> IOOf<E, A>.redeem(fe: (E) -> B, fb: (A) -> B): IO<E, B> =
    fix().redeem(fe, fb)

  override fun <A> raiseError(e: E): IO<E, A> =
    IO.raiseError(e)
}

@extension
interface IOMonadError<E> : MonadError<IOPartialOf<E>, E>, IOApplicativeError<E>, IOMonad<E> {

  override fun <A> just(a: A): IO<E, A> = IO.just(a)

  override fun <A, B> IOOf<E, A>.ap(ff: IOOf<E, (A) -> B>): IO<E, B> =
    fix().ioAp(ff)

  override fun <A, B> IOOf<E, A>.map(f: (A) -> B): IO<E, B> =
    fix().map(f)

  override fun <A> IOOf<E, A>.attempt(): IO<E, Either<E, A>> =
    fix().attempt()

  override fun <A> IOOf<E, A>.handleErrorWith(f: (E) -> IOOf<E, A>): IO<E, A> =
    ioHandleErrorWith(f)

  override fun <A, B> IOOf<E, A>.redeemWith(fe: (E) -> IOOf<E, B>, fb: (A) -> IOOf<E, B>): IO<E, B> =
    fix().ioRedeemWith(fe, fb)

  override fun <A> raiseError(e: E): IO<E, A> =
    IO.raiseError(e)
}

@extension
interface IOBracket : Bracket<IOPartialOf<Throwable>, Throwable>, IOMonadError<Throwable> {
  override fun <A, B> IOOf<Throwable, A>.bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Throwable, Unit>, use: (A) -> IOOf<Throwable, B>): IO<Throwable, B> =
    fix().ioBracketCase(release, use)

  override fun <A, B> IOOf<Throwable, A>.bracket(release: (A) -> IOOf<Throwable, Unit>, use: (A) -> IOOf<Throwable, B>): IO<Throwable, B> =
    fix().ioBracket(release, use)

  override fun <A> IOOf<Throwable, A>.guarantee(finalizer: IOOf<Throwable, Unit>): IO<Throwable, A> =
    fix().ioGuarantee(finalizer)

  override fun <A> IOOf<Throwable, A>.guaranteeCase(finalizer: (ExitCase<Throwable>) -> IOOf<Throwable, Unit>): IO<Throwable, A> =
    fix().ioGuaranteeCase(finalizer)
}

@extension
interface IOMonadDefer : MonadDefer<IOPartialOf<Throwable>, Throwable>, IOBracket {
  override fun <A> defer(fa: () -> Kind<IOPartialOf<Throwable>, A>): Kind<IOPartialOf<Throwable>, A> =
    IO.defer(fa)

  override fun <A> later(f: () -> A): Kind<IOPartialOf<Throwable>, A> =
    IO.later(f)
}

@extension
interface IOAsync : Async<IOPartialOf<Throwable>, Throwable>, IOMonadDefer {
  override fun <A> async(fa: ProcE<Throwable, A>): Kind<IOPartialOf<Throwable>, A> =
    IO.async(fa)

  override fun <A> asyncF(k: ProcEF<IOPartialOf<Throwable>, Throwable, A>): Kind<IOPartialOf<Throwable>, A> =
    IO.asyncF(k)

  override fun <A> IOOf<Throwable, A>.continueOn(ctx: CoroutineContext): IO<Throwable, A> =
    fix().continueOn(ctx)

  override fun <A> defer(fa: () -> Kind<IOPartialOf<Throwable>, A>): Kind<IOPartialOf<Throwable>, A> =
    IO.defer(fa)

  override fun <A> Async<IOPartialOf<Throwable>, Throwable>.effect(f: suspend () -> A): Kind<IOPartialOf<Throwable>, A> =
    IO.effect(f)

  override fun <A> Async<IOPartialOf<Throwable>, Throwable>.effect(ctx: CoroutineContext, f: suspend () -> A): Kind<IOPartialOf<Throwable>, A> =
    IO.effect(ctx, f)
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
interface IOConcurrent : Concurrent<IOPartialOf<Throwable>, Throwable>, IOAsync {
  override fun <A> CoroutineContext.startFiber(kind: IOOf<Throwable, A>): IO<Throwable, Fiber<IOPartialOf<Throwable>, A>> =
    kind.fix().startFiber(this)

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<IOPartialOf<Throwable>>): IO<Throwable, A> =
    IO.cancelable(k)

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> IOOf<Throwable, CancelToken<IOPartialOf<Throwable>>>): IO<Throwable, A> =
    IO.cancelableF(k)

  override fun <A, B> CoroutineContext.racePair(fa: IOOf<Throwable, A>, fb: IOOf<Throwable, B>): IO<Throwable, RacePair<IOPartialOf<Throwable>, A, B>> =
    IO.racePair(this, fa, fb)

  override fun <A, B, C> CoroutineContext.raceTriple(fa: IOOf<Throwable, A>, fb: IOOf<Throwable, B>, fc: IOOf<Throwable, C>): IO<Throwable, RaceTriple<IOPartialOf<Throwable>, A, B, C>> =
    IO.raceTriple(this, fa, fb, fc)

  override fun <A, B, C> CoroutineContext.parMapN(fa: IOOf<Throwable, A>, fb: IOOf<Throwable, B>, f: (A, B) -> C): IO<Throwable, C> =
    IO.parMapN(this@parMapN, fa, fb, f)

  override fun <A, B, C, D> CoroutineContext.parMapN(fa: IOOf<Throwable, A>, fb: IOOf<Throwable, B>, fc: IOOf<Throwable, C>, f: (A, B, C) -> D): IO<Throwable, D> =
    IO.parMapN(this@parMapN, fa, fb, fc, f)
}

fun IO.Companion.concurrent(dispatchers: Dispatchers<IOPartialOf<Throwable>>): Concurrent<IOPartialOf<Throwable>, Throwable> = object : IOConcurrent {
  override fun dispatchers(): Dispatchers<IOPartialOf<Throwable>> = dispatchers
}

fun IO.Companion.timer(CF: Concurrent<IOPartialOf<Throwable>, Throwable>): Timer<IOPartialOf<Throwable>> =
  Timer(CF)

@extension
interface IOEffect : Effect<IOPartialOf<Throwable>>, IOAsync {
  override fun <A> IOOf<Throwable, A>.runAsync(cb: (Either<Throwable, A>) -> IOOf<Throwable, Unit>): IO<Throwable, Unit> =
    fix().runAsync(cb)
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
interface IOConcurrentEffect : ConcurrentEffect<IOPartialOf<Throwable>>, IOEffect, IOConcurrent {

  override fun <A> IOOf<Throwable, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> IOOf<Throwable, Unit>): IO<Throwable, Disposable> =
    fix().runAsyncCancellable(OnCancel.ThrowCancellationException, cb)
}

fun IO.Companion.concurrentEffect(dispatchers: Dispatchers<IOPartialOf<Throwable>>): ConcurrentEffect<IOPartialOf<Throwable>> = object : IOConcurrentEffect {
  override fun dispatchers(): Dispatchers<IOPartialOf<Throwable>> = dispatchers
}

@extension
interface IOSemigroup<E, A> : Semigroup<IO<E, A>> {

  fun SG(): Semigroup<A>

  override fun IO<E, A>.combine(b: IO<E, A>): IO<E, A> =
    flatMap { a1: A -> b.map { a2: A -> SG().run { a1.combine(a2) } } }
}

@extension
interface IOMonoid<E, A> : Monoid<IO<E, A>>, IOSemigroup<E, A> {

  override fun SG(): Semigroup<A> = SM()

  fun SM(): Monoid<A>

  override fun empty(): IO<E, A> = IO.just(SM().empty())
}

@extension
interface IOUnsafeRun : UnsafeRun<IOPartialOf<Throwable>> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> IOOf<Throwable, A>): A = fa().fix().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> IOOf<Throwable, A>, cb: (Either<Throwable, A>) -> Unit): Unit =
    fa().fix().unsafeRunAsync(cb)
}

@extension
interface IODispatchers : Dispatchers<IOPartialOf<Throwable>> {
  override fun default(): CoroutineContext =
    IODispatchers.CommonPool
}

@extension
interface IOEnvironment : Environment<IOPartialOf<Throwable>> {
  override fun dispatchers(): Dispatchers<IOPartialOf<Throwable>> =
    IO.dispatchers()

  override fun handleAsyncError(e: Throwable): IO<Throwable, Unit> =
    IO { println("Found uncaught async exception!"); e.printStackTrace() }
}

@extension
interface IODefaultConcurrent : Concurrent<IOPartialOf<Throwable>, Throwable>, IOConcurrent {

  override fun dispatchers(): Dispatchers<IOPartialOf<Throwable>> =
    IO.dispatchers()
}

fun IO.Companion.timer(): Timer<IOPartialOf<Throwable>> = Timer(IO.concurrent())

@extension
interface IODefaultConcurrentEffect : ConcurrentEffect<IOPartialOf<Throwable>>, IOConcurrentEffect, IODefaultConcurrent

fun <A> IO.Companion.fx(c: suspend ConcurrentSyntax<IOPartialOf<Throwable>, Throwable>.() -> A): IO<Throwable, A> =
  IO.concurrent().fx.concurrent(c).fix()
