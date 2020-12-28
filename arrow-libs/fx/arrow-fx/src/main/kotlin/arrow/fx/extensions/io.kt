package arrow.fx.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.identity
import arrow.extension
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IODeprecation
import arrow.fx.IODispatchers
import arrow.fx.IOOf
import arrow.fx.OnCancel
import arrow.fx.Race2
import arrow.fx.Race3
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Timer
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.fix
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.CancelToken
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
import arrow.fx.typeclasses.MonadIO
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.fx.typeclasses.UnsafeCancellableRun
import arrow.fx.typeclasses.UnsafeRun
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Apply
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.unsafe
import kotlin.coroutines.CoroutineContext
import arrow.fx.handleError as ioHandleError
import arrow.fx.handleErrorWith as ioHandleErrorWith

@extension
@Deprecated(IODeprecation)
interface IOFunctor : Functor<ForIO> {
  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)
}

@extension
@Deprecated(IODeprecation)
interface IOApply : Apply<ForIO> {
  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A, B> IOOf<A>.ap(ff: IOOf<(A) -> B>): IO<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForIO, A>.apEval(ff: Eval<Kind<ForIO, (A) -> B>>): Eval<Kind<ForIO, B>> =
    Eval.now(fix().ap(IO.defer { ff.value() }))
}

@extension
@Deprecated(IODeprecation)
interface IOApplicative : Applicative<ForIO> {
  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A> just(a: A): IO<A> =
    IO.just(a)

  override fun <A, B> IOOf<A>.ap(ff: IOOf<(A) -> B>): IO<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForIO, A>.apEval(ff: Eval<Kind<ForIO, (A) -> B>>): Eval<Kind<ForIO, B>> =
    Eval.now(fix().ap(IO.defer { ff.value() }))
}

@extension
@Deprecated(IODeprecation)
interface IOMonad : Monad<ForIO> {
  override fun <A, B> IOOf<A>.flatMap(f: (A) -> IOOf<B>): IO<B> =
    fix().flatMap(f)

  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IOOf<Either<A, B>>): IO<B> =
    IO.tailRecM(a, f)

  override fun <A> just(a: A): IO<A> =
    IO.just(a)

  override fun <A, B> Kind<ForIO, A>.apEval(ff: Eval<Kind<ForIO, (A) -> B>>): Eval<Kind<ForIO, B>> =
    Eval.now(fix().ap(IO.defer { ff.value() }))
}

@extension
@Deprecated(IODeprecation)
interface IOApplicativeError : ApplicativeError<ForIO, Throwable>, IOApplicative {
  override fun <A> IOOf<A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> IOOf<A>.handleErrorWith(f: (Throwable) -> IOOf<A>): IO<A> =
    ioHandleErrorWith(f)

  override fun <A> IOOf<A>.handleError(f: (Throwable) -> A): IO<A> =
    ioHandleError(f)

  override fun <A, B> IOOf<A>.redeem(fe: (Throwable) -> B, fb: (A) -> B): IO<B> =
    fix().redeem(fe, fb)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)
}

@extension
@Deprecated(IODeprecation)
interface IOMonadError : MonadError<ForIO, Throwable>, IOApplicativeError, IOMonad {

  override fun <A> just(a: A): IO<A> = IO.just(a)

  override fun <A, B> IOOf<A>.ap(ff: IOOf<(A) -> B>): IO<B> =
    fix().ap(ff)

  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A> IOOf<A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> IOOf<A>.handleErrorWith(f: (Throwable) -> IOOf<A>): IO<A> =
    ioHandleErrorWith(f)

  override fun <A, B> IOOf<A>.redeemWith(fe: (Throwable) -> IOOf<B>, fb: (A) -> IOOf<B>): IO<B> =
    fix().redeemWith(fe, fb)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)

  override fun <A, B> Kind<ForIO, A>.apEval(ff: Eval<Kind<ForIO, (A) -> B>>): Eval<Kind<ForIO, B>> =
    Eval.now(fix().ap(IO.defer { ff.value() }))
}

@extension
@Deprecated(IODeprecation)
interface IOMonadThrow : MonadThrow<ForIO>, IOMonadError

@extension
@Deprecated(IODeprecation)
interface IOBracket : Bracket<ForIO, Throwable>, IOMonadThrow {
  override fun <A, B> IOOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
    fix().bracketCase(release, use)

  override fun <A, B> IOOf<A>.bracket(release: (A) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
    fix().bracket(release, use)

  override fun <A> IOOf<A>.guarantee(finalizer: IOOf<Unit>): IO<A> =
    fix().guarantee(finalizer)

  override fun <A> IOOf<A>.guaranteeCase(finalizer: (ExitCase<Throwable>) -> IOOf<Unit>): IO<A> =
    fix().guaranteeCase(finalizer)
}

@extension
@Deprecated(IODeprecation)
interface IOMonadDefer : MonadDefer<ForIO>, IOBracket {
  override fun <A> defer(fa: () -> IOOf<A>): IO<A> =
    IO.defer(fa)

  override fun lazy(): IO<Unit> = IO.lazy
}

@extension
@Deprecated(IODeprecation)
interface IOAsync : Async<ForIO>, IOMonadDefer {
  override fun <A> async(fa: Proc<A>): IO<A> =
    IO.async(fa)

  override fun <A> asyncF(k: ProcF<ForIO, A>): IO<A> =
    IO.asyncF(k)

  override fun <A> IOOf<A>.continueOn(ctx: CoroutineContext): IO<A> =
    fix().continueOn(ctx)

  override fun <A> effect(ctx: CoroutineContext, f: suspend () -> A): IO<A> =
    IO.effect(ctx, f)

  override fun <A> effect(f: suspend () -> A): IO<A> =
    IO.effect(f)
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
@Deprecated(IODeprecation)
interface IOConcurrent : Concurrent<ForIO>, IOAsync {
  override fun <A> Kind<ForIO, A>.fork(coroutineContext: CoroutineContext): IO<Fiber<ForIO, A>> =
    fix().fork(coroutineContext)

  override fun <A> cancellable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForIO>): Kind<ForIO, A> =
    IO.cancellable(k)

  override fun <A> cancellableF(k: ((Either<Throwable, A>) -> Unit) -> IOOf<CancelToken<ForIO>>): IO<A> =
    IO.cancellableF(k)

  override fun <A, B> CoroutineContext.racePair(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>): IO<RacePair<ForIO, A, B>> =
    IO.racePair(this, fa, fb)

  override fun <A, B, C> CoroutineContext.raceTriple(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>, fc: Kind<ForIO, C>): IO<RaceTriple<ForIO, A, B, C>> =
    IO.raceTriple(this, fa, fb, fc)

  override fun <A, B> parTupledN(ctx: CoroutineContext, fa: Kind<ForIO, A>, fb: Kind<ForIO, B>): IO<Tuple2<A, B>> =
    IO.parTupledN(ctx, fa, fb)

  override fun <A, B, C> parTupledN(ctx: CoroutineContext, fa: Kind<ForIO, A>, fb: Kind<ForIO, B>, fc: Kind<ForIO, C>): IO<Tuple3<A, B, C>> =
    IO.parTupledN(ctx, fa, fb, fc)

  override fun <A, B> CoroutineContext.raceN(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>): IO<Race2<A, B>> =
    IO.raceN(this@raceN, fa, fb)

  override fun <A, B, C> CoroutineContext.raceN(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>, fc: Kind<ForIO, C>): IO<Race3<A, B, C>> =
    IO.raceN(this@raceN, fa, fb, fc)
}

fun IO.Companion.concurrent(dispatchers: Dispatchers<ForIO>): Concurrent<ForIO> = object : IOConcurrent {
  override fun dispatchers(): Dispatchers<ForIO> = dispatchers
}

fun IO.Companion.timer(CF: Concurrent<ForIO>): Timer<ForIO> =
  Timer(CF)

@extension
@Deprecated(IODeprecation)
interface IOEffect : Effect<ForIO>, IOAsync {
  override fun <A> IOOf<A>.runAsync(cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Unit> =
    fix().runAsync(cb)
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
@Deprecated(IODeprecation)
interface IOConcurrentEffect : ConcurrentEffect<ForIO>, IOEffect, IOConcurrent {

  override fun <A> IOOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Disposable> =
    fix().runAsyncCancellable(OnCancel.ThrowCancellationException, cb)
}

fun IO.Companion.concurrentEffect(dispatchers: Dispatchers<ForIO>): ConcurrentEffect<ForIO> = object : IOConcurrentEffect {
  override fun dispatchers(): Dispatchers<ForIO> = dispatchers
}

@extension
@Deprecated(IODeprecation)
interface IOSemigroup<A> : Semigroup<IO<A>> {

  fun SG(): Semigroup<A>

  override fun IO<A>.combine(b: IO<A>): IO<A> =
    flatMap { a1: A -> b.map { a2: A -> SG().run { a1.combine(a2) } } }
}

@extension
@Deprecated(IODeprecation)
interface IOMonoid<A> : Monoid<IO<A>>, IOSemigroup<A> {
  override fun SG(): Monoid<A>

  override fun empty(): IO<A> = IO.just(SG().empty())
}

@extension
@Deprecated(IODeprecation)
interface IOMonadIO : MonadIO<ForIO>, IOMonad {
  override fun <A> IO<A>.liftIO(): Kind<ForIO, A> = this
}

@extension
@Deprecated(IODeprecation)
interface IOUnsafeRun : UnsafeRun<ForIO> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<ForIO, A>): A = fa().fix().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<ForIO, A>, cb: (Either<Throwable, A>) -> Unit): Unit =
    fa().fix().unsafeRunAsync(cb)
}

@extension
@Deprecated(IODeprecation)
interface IOUnsafeCancellableRun : UnsafeCancellableRun<ForIO> {
  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<ForIO, A>): A = fa().fix().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<ForIO, A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().unsafeRunAsync(cb)

  override suspend fun <A> unsafe.runNonBlockingCancellable(onCancel: OnCancel, fa: () -> Kind<ForIO, A>, cb: (Either<Throwable, A>) -> Unit): Disposable =
    fa().fix().unsafeRunAsyncCancellable(onCancel, cb)
}

@extension
@Deprecated(IODeprecation)
interface IODispatchers : Dispatchers<ForIO> {
  override fun default(): CoroutineContext =
    IODispatchers.CommonPool

  override fun io(): CoroutineContext =
    IODispatchers.IOPool
}

@extension
@Deprecated(IODeprecation)
interface IOEnvironment : Environment<ForIO> {
  override fun dispatchers(): Dispatchers<ForIO> =
    IO.dispatchers()

  override fun handleAsyncError(e: Throwable): IO<Unit> =
    IO { println("Found uncaught async exception!"); e.printStackTrace() }
}

@extension
@Deprecated(IODeprecation)
interface IODefaultConcurrent : Concurrent<ForIO>, IOConcurrent {

  override fun dispatchers(): Dispatchers<ForIO> =
    IO.dispatchers()
}

fun IO.Companion.timer(): Timer<ForIO> = Timer(IO.concurrent())

@extension
@Deprecated(IODeprecation)
interface IODefaultConcurrentEffect : ConcurrentEffect<ForIO>, IOConcurrentEffect, IODefaultConcurrent

fun <A> IO.Companion.fx(c: suspend ConcurrentSyntax<ForIO>.() -> A): IO<A> =
  IO.concurrent().fx.concurrent(c).fix()

/**
 * converts this Either to an IO. The resulting IO will evaluate to this Eithers
 * Right value or alternatively to the result of applying the specified function to this Left value.
 */
fun <E, A> Either<E, A>.toIO(f: (E) -> Throwable): IO<A> =
  fold({ IO.raiseError(f(it)) }, { IO.just(it) })

/**
 * converts this Either to an IO. The resulting IO will evaluate to this Eithers
 * Right value or Left exception.
 */
fun <A> Either<Throwable, A>.toIO(): IO<A> =
  toIO(::identity)

@extension
@Deprecated(IODeprecation)
interface IOSemigroupK : SemigroupK<ForIO> {
  override fun <A> Kind<ForIO, A>.combineK(y: Kind<ForIO, A>): Kind<ForIO, A> =
    (this.fix() to y.fix()).let { (l, r) ->
      l.ioHandleErrorWith { r }
    }
}
