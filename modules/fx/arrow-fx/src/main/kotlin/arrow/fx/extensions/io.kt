package arrow.fx.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.identity
import arrow.extension
import arrow.fx.IO
import arrow.fx.IOPartialOf
import arrow.fx.IOResult
import arrow.fx.typeclasses.ExitCase2
import arrow.fx.IODispatchers
import arrow.fx.IOOf
import arrow.fx.OnCancel
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Timer
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.fix
import arrow.fx.flatMapLeft
import arrow.fx.mapError
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
import arrow.unsafe
import java.lang.AssertionError
import kotlin.coroutines.CoroutineContext
import arrow.fx.ap as Ap
import arrow.fx.flatMap as FlatMap
import arrow.fx.handleErrorWith as HandleErrorWith
import arrow.fx.redeemWith as RedeemWith
import arrow.fx.bracketCase as BracketCase

@extension
interface IOFunctor<E> : Functor<IOPartialOf<E>> {
  override fun <A, B> IOOf<E, A>.map(f: (A) -> B): IO<E, B> =
    fix().map(f)
}

@extension
interface IOApply<E> : Apply<IOPartialOf<E>>, IOFunctor<E> {
  override fun <A, B> IOOf<E, A>.ap(ff: IOOf<E, (A) -> B>): IO<E, B> =
    Ap(ff)
}

@extension
interface IOApplicative<E> : Applicative<IOPartialOf<E>>, IOApply<E> {
  override fun <A> just(a: A): IO<E, A> =
    IO.just(a)

  override fun <A, B> IOOf<E, A>.map(f: (A) -> B): IO<E, B> =
    fix().map(f)
}

@extension
interface IOMonad<E> : Monad<IOPartialOf<E>>, IOApplicative<E> {
  override fun <A, B> IOOf<E, A>.flatMap(f: (A) -> IOOf<E, B>): IO<E, B> =
    FlatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IOOf<E, Either<A, B>>): IO<E, B> =
    IO.tailRecM(a, f)

  override fun <A, B> IOOf<E, A>.map(f: (A) -> B): IO<E, B> =
    fix().map(f)

  override fun <A, B> IOOf<E, A>.ap(ff: IOOf<E, (A) -> B>): IO<E, B> =
    Ap(ff)
}

@extension
interface IOApplicativeError<E> : ApplicativeError<IOPartialOf<E>, Throwable>, IOApplicative<E> {
  override fun <A> IOOf<E, A>.attempt(): IO<E, Either<Throwable, A>> =
    fix().attempt()

  override fun <A> IOOf<E, A>.handleErrorWith(f: (Throwable) -> IOOf<E, A>): IO<E, A> =
    HandleErrorWith(f, { e -> IO.raiseError(e) })

  override fun <A> IOOf<E, A>.handleError(f: (Throwable) -> A): IO<E, A> =
    HandleErrorWith({ t -> IO.just(f(t)) }, { e -> IO.raiseError<E, A>(e) })

  override fun <A, B> IOOf<E, A>.redeem(fe: (Throwable) -> B, fb: (A) -> B): IO<E, B> =
    RedeemWith({ t -> IO.just(fe(t)) }, { e -> IO.raiseError<E, B>(e) }, { a -> IO.just(fb(a)) })

  override fun <A> raiseError(e: Throwable): IO<E, A> =
    IO.raiseException(e)
}

@extension
interface IOMonadError<E> : MonadError<IOPartialOf<E>, Throwable>, IOApplicativeError<E>, IOMonad<E> {

  override fun <A> just(a: A): IO<Nothing, A> = IO.just(a)

  override fun <A, B> IOOf<E, A>.ap(ff: IOOf<E, (A) -> B>): IO<E, B> =
    Ap(ff)

  override fun <A, B> IOOf<E, A>.map(f: (A) -> B): IO<E, B> =
    fix().map(f)

  override fun <A> IOOf<E, A>.attempt(): IO<E, Either<Throwable, A>> =
    fix().attempt()

  override fun <A> IOOf<E, A>.handleErrorWith(f: (Throwable) -> IOOf<E, A>): IO<E, A> =
    HandleErrorWith(f, { e -> IO.raiseError(e) })

  override fun <A, B> IOOf<E, A>.redeemWith(fe: (Throwable) -> IOOf<E, B>, fb: (A) -> IOOf<E, B>): IO<E, B> =
    RedeemWith({ t -> fe(t) }, { e -> IO.raiseError(e) }, { a -> fb(a) })

  override fun <A> raiseError(e: Throwable): IO<Nothing, A> =
    IO.raiseException(e)
}

@extension
interface IOMonadThrow<E> : MonadThrow<IOPartialOf<E>>, IOMonadError<E>

@extension
interface IOBracket<E> : Bracket<IOPartialOf<E>, Throwable>, IOMonadThrow<E> {
  override fun <A, B> IOOf<E, A>.bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<E, Unit>, use: (A) -> IOOf<E, B>): IO<E, B> =
    // Capture `E` into `Either`
    RedeemWith({ t -> IO.raiseException<Either<E, A>>(t) }, { e -> IO.just(Left(e)) }, { a -> IO.just(Right(a)) })
      .BracketCase<Either<E, A>, E, Either<E, B>>(release = { a, ex ->
        when (a) {
          is Either.Right -> { // Release resource.
            when (ex) {
              ExitCase2.Completed -> release(a.b, ExitCase.Completed).fix()
              ExitCase2.Canceled -> release(a.b, ExitCase.Canceled).fix()
              is ExitCase2.Exception -> release(a.b, ExitCase.Error(ex.exception)).fix()
              is ExitCase2.Error -> throw AssertionError("Unreachable") // E is `Nothing`.
            }
          }
          is Either.Left -> IO.just(Unit) //Short-circuit
        }
      }, use = {
        when (it) {
          is Either.Right -> use(it.b).map { b -> Right(b) } // Resource acquired
          is Either.Left -> IO.just(it) //Short-circuit
        }
      }).flatMap { res ->
        when (res) {  //Lift Either back into IO
          is Either.Right -> IO.just(res.b)
          is Either.Left -> IO.raiseError<E, B>(res.a)
        }
      }
}

@extension
interface IOMonadDefer<E> : MonadDefer<IOPartialOf<E>>, IOBracket<E> {
  override fun <A> defer(fa: () -> IOOf<E, A>): IO<E, A> =
    IO.defer(fa)

  override fun lazy(): IO<Nothing, Unit> = IO.lazy
}

@extension
interface IOAsync<E> : Async<IOPartialOf<E>>, IOMonadDefer<E> {
  override fun <A> async(fa: Proc<A>): IO<E, A> =
    IO.async { cb ->
      fa { result ->
        when (result) {
          is Either.Left -> cb(IOResult.Exception(result.a))
          is Either.Right -> cb(IOResult.Success(result.b))
        }
      }
    }

  override fun <A> asyncF(k: ProcF<IOPartialOf<E>, A>): IO<E, A> =
    IO.asyncF { cb ->
      k { result ->
        when (result) {
          is Either.Left -> cb(IOResult.Exception(result.a))
          is Either.Right -> cb(IOResult.Success(result.b))
        }
      }
    }

  override fun <A> IOOf<E, A>.continueOn(ctx: CoroutineContext): IO<E, A> =
    fix().continueOn(ctx)

  override fun <A> effect(ctx: CoroutineContext, f: suspend () -> A): IO<Nothing, A> =
    IO.effect(ctx, f)

  override fun <A> effect(f: suspend () -> A): IO<Nothing, A> =
    IO.effect(f)
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
interface IOConcurrent : Concurrent<IOPartialOf<E>>, IOAsync {
  override fun <A> Kind<IOPartialOf<E>, A>.fork(coroutineContext: CoroutineContext): IO<Nothing, Fiber<IOPartialOf<E>, A>> =
    fix().fork(coroutineContext)

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<IOPartialOf<E>>): Kind<IOPartialOf<E>, A> =
    IO.cancelable(k)

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> IOOf<CancelToken<IOPartialOf<E>>>): IO<Nothing, A> =
    IO.cancelableF(k)

  override fun <A, B> CoroutineContext.racePair(fa: Kind<IOPartialOf<E>, A>, fb: Kind<IOPartialOf<E>, B>): IO<Nothing, RacePair<IOPartialOf<E>, A, B>> =
    IO.racePair(this, fa, fb)

  override fun <A, B, C> CoroutineContext.raceTriple(fa: Kind<IOPartialOf<E>, A>, fb: Kind<IOPartialOf<E>, B>, fc: Kind<IOPartialOf<E>, C>): IO<Nothing, RaceTriple<IOPartialOf<E>, A, B, C>> =
    IO.raceTriple(this, fa, fb, fc)

  override fun <A, B, C> CoroutineContext.parMapN(fa: Kind<IOPartialOf<E>, A>, fb: Kind<IOPartialOf<E>, B>, f: (A, B) -> C): Kind<IOPartialOf<E>, C> =
    IO.parMapN(this@parMapN, fa, fb, f)

  override fun <A, B, C, D> CoroutineContext.parMapN(fa: Kind<IOPartialOf<E>, A>, fb: Kind<IOPartialOf<E>, B>, fc: Kind<IOPartialOf<E>, C>, f: (A, B, C) -> D): Kind<IOPartialOf<E>, D> =
    IO.parMapN(this@parMapN, fa, fb, fc, f)
}

fun IO.Companion.concurrent(dispatchers: Dispatchers<IOPartialOf<E>>): Concurrent<IOPartialOf<E>> = object : IOConcurrent {
  override fun dispatchers(): Dispatchers<IOPartialOf<E>> = dispatchers
}

fun IO.Companion.timer(CF: Concurrent<IOPartialOf<E>>): Timer<IOPartialOf<E>> =
  Timer(CF)

@extension
interface IOEffect<E> : Effect<IOPartialOf<E>>, IOAsync<E> {
  override fun <A> IOOf<E, A>.runAsync(cb: (Either<Throwable, A>) -> IOOf<E, Unit>): IO<Nothing, Unit> =
    fix().runAsync(cb)
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
interface IOConcurrentEffect<E> : ConcurrentEffect<IOPartialOf<E>>, IOEffect, IOConcurrent {

  override fun <A> IOOf<E, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Nothing, Disposable> =
    fix().runAsyncCancellable(OnCancel.ThrowCancellationException, cb)
}

fun IO.Companion.concurrentEffect(dispatchers: Dispatchers<IOPartialOf<E>>): ConcurrentEffect<IOPartialOf<E>> = object : IOConcurrentEffect {
  override fun dispatchers(): Dispatchers<IOPartialOf<E>> = dispatchers
}

@extension
interface IOSemigroup<A> : Semigroup<IO<Nothing, A>> {

  fun SG(): Semigroup<A>

  override fun IO<Nothing, A>.combine(b: IO<Nothing, A>): IO<Nothing, A> =
    FlatMap { a1: A -> b.map { a2: A -> SG().run { a1.combine(a2) } } }
}

@extension
interface IOMonoid<A> : Monoid<IO<Nothing, A>>, IOSemigroup<A> {

  override fun SG(): Semigroup<A> = SM()

  fun SM(): Monoid<A>

  override fun empty(): IO<Nothing, A> = IO.just(SM().empty())
}

@extension
interface IOUnsafeRun : UnsafeRun<IOPartialOf<E>> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<IOPartialOf<E>, A>): A = fa().fix().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<IOPartialOf<E>, A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().unsafeRunAsync(cb)
}

@extension
interface IOUnsafeCancellableRun : UnsafeCancellableRun<IOPartialOf<E>> {
  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<IOPartialOf<E>, A>): A = fa().fix().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<IOPartialOf<E>, A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().unsafeRunAsync(cb)

  override suspend fun <A> unsafe.runNonBlockingCancellable(onCancel: OnCancel, fa: () -> Kind<IOPartialOf<E>, A>, cb: (Either<Throwable, A>) -> Unit): Disposable =
    fa().fix().unsafeRunAsyncCancellable(onCancel, cb)
}

@extension
interface IODispatchers<E> : Dispatchers<IOPartialOf<E>> {
  override fun default(): CoroutineContext =
    IODispatchers.CommonPool

  override fun io(): CoroutineContext =
    IODispatchers.IOPool
}

@extension
interface IOEnvironment<E> : Environment<IOPartialOf<E>> {
  override fun dispatchers(): Dispatchers<IOPartialOf<E>> =
    IO.dispatchers()

  override fun handleAsyncError(e: Throwable): IO<Nothing, Unit> =
    IO { println("Found uncaught async exception!"); e.printStackTrace() }
}

@extension
interface IODefaultConcurrent : Concurrent<IOPartialOf<E>>, IOConcurrent {

  override fun dispatchers(): Dispatchers<IOPartialOf<E>> =
    IO.dispatchers()
}

fun <E> IO.Companion.timer(): Timer<IOPartialOf<E>> = Timer(IO.concurrent())

@extension
interface IODefaultConcurrentEffect<E> : ConcurrentEffect<IOPartialOf<E>>, IOConcurrentEffect, IODefaultConcurrent

fun <A> IO.Companion.fx(c: suspend ConcurrentSyntax<IOPartialOf<E>>.() -> A): IO<Nothing, A> =
  defer { IO.concurrent().fx.concurrent(c).fix() }

/**
 * converts this Either to an IO. The resulting IO will evaluate to this Eithers
 * Right value or alternatively to the result of applying the specified function to this Left value.
 */
fun <E, A> Either<E, A>.toIO(f: (E) -> Throwable): IO<Nothing, A> =
  fold({ IO.raiseException(f(it)) }, { IO.just(it) })

/**
 * converts this Either to an IO. The resulting IO will evaluate to this Eithers
 * Right value or Left exception.
 */
fun <A> Either<Throwable, A>.toIO(): IO<Nothing, A> =
  toIO(::identity)
