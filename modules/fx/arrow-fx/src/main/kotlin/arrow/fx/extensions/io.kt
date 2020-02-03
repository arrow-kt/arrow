package arrow.fx.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.extension
import arrow.fx.IO
import arrow.fx.IOPartialOf
import arrow.fx.IOResult
import arrow.fx.typeclasses.ExitCase2
import arrow.fx.IODispatchers
import arrow.fx.IOOf
import arrow.fx.MVar
import arrow.fx.OnCancel
import arrow.fx.Promise
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Semaphore
import arrow.fx.Timer
import arrow.fx.extensions.io.dispatchers.dispatchers
import arrow.fx.extensions.io.concurrent.concurrent
import arrow.fx.fix
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.ConcurrentSyntax
import arrow.fx.typeclasses.Dispatchers
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.Environment
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.MonadIO
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.fx.typeclasses.UnsafeCancellableRun
import arrow.fx.typeclasses.UnsafeRun
import arrow.fx.unsafeRunAsync
import arrow.fx.unsafeRunAsyncCancellable
import arrow.fx.unsafeRunSync
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Apply
import arrow.typeclasses.Continuation
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.stateStack
import arrow.typeclasses.suspended.BindSyntax
import arrow.unsafe
import java.lang.AssertionError
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import arrow.fx.ap as Ap
import arrow.fx.flatMap as FlatMap
import arrow.fx.handleErrorWith as HandleErrorWith
import arrow.fx.redeemWith as RedeemWith
import arrow.fx.bracketCase as BracketCase
import arrow.fx.bracket as Bracket
import arrow.fx.fork as Fork
import arrow.fx.guarantee as Guarantee
import arrow.fx.guaranteeCase as GuaranteeCase
import arrow.fx.onCancel as OnCancel

@extension
interface IOFunctor<E> : Functor<IOPartialOf<E>> {
  override fun <A, B> IOOf<E, A>.map(f: (A) -> B): IO<E, B> =
    fix().map(f)
}

@extension
interface IOApply<E> : Apply<IOPartialOf<E>> {
  override fun <A, B> IOOf<E, A>.ap(ff: IOOf<E, (A) -> B>): IO<E, B> =
    Ap(ff)

  override fun <A, B> IOOf<E, A>.lazyAp(ff: () -> IOOf<E, (A) -> B>): IO<E, B> =
    FlatMap { a -> ff().map { f -> f(a) } }

  override fun <A, B> IOOf<E, A>.map(f: (A) -> B): IO<E, B> =
    fix().map(f)
}

@extension
interface IOApplicative<E> : Applicative<IOPartialOf<E>> {
  override fun <A> just(a: A): IO<E, A> =
    IO.just(a)

  override fun <A, B> IOOf<E, A>.ap(ff: IOOf<E, (A) -> B>): IO<E, B> =
    Ap(ff)

  override fun <A, B> IOOf<E, A>.lazyAp(ff: () -> IOOf<E, (A) -> B>): IO<E, B> =
    FlatMap { a -> ff().map { f -> f(a) } }

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

  override fun <A, B> IOOf<E, A>.lazyAp(ff: () -> IOOf<E, (A) -> B>): IO<E, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }
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

  override fun <A, B> IOOf<E, A>.lazyAp(ff: () -> IOOf<E, (A) -> B>): IO<E, B> =
    FlatMap { a -> ff().map { f -> f(a) } }
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

  override fun <A, B> IOOf<E, A>.lazyAp(ff: () -> IOOf<E, (A) -> B>): IO<E, B> =
    FlatMap { a -> ff().map { f -> f(a) } }
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
          is Either.Left -> IO.just(Unit) // Short-circuit
        }
      }, use = {
        when (it) {
          is Either.Right -> use(it.b).map { b -> Right(b) } // Resource acquired
          is Either.Left -> IO.just(it) // Short-circuit
        }
      }).flatMap { res ->
        when (res) { // Lift Either back into IO
          is Either.Right -> IO.just(res.b)
          is Either.Left -> IO.raiseError<E, B>(res.a)
        }
      }

  override fun <A, B> IOOf<E, A>.bracket(release: (A) -> IOOf<E, Unit>, use: (A) -> IOOf<E, B>): IO<E, B> =
    Bracket<E, A, B>(release, use)

  override fun <A> IOOf<E, A>.guarantee(finalizer: IOOf<E, Unit>): IO<E, A> =
    Guarantee(finalizer)

  override fun <A> IOOf<E, A>.guaranteeCase(finalizer: (ExitCase<Throwable>) -> IOOf<E, Unit>): IO<E, A> {
    val redeemed: IO<E, Either<E, A>> = RedeemWith({ t -> IO.raiseException<Either<E, A>>(t) }, { e -> IO.just(Left(e)) }, { a -> IO.just(Right(a)) }) // Capture `E` into `Either`
    return redeemed.GuaranteeCase { case ->
      when (case) {
        ExitCase2.Completed -> finalizer(ExitCase.Completed)
        ExitCase2.Canceled -> finalizer(ExitCase.Canceled)
        is ExitCase2.Exception -> finalizer(ExitCase.Error(case.exception))
        is ExitCase2.Error -> throw AssertionError("Unreachable") // E is `Nothing`.
      }
    }.flatMap { res: Either<E, A> ->
      when (res) { // Lift Either back into IO
        is Either.Right -> IO.just(res.b)
        is Either.Left -> IO.raiseError<E, A>(res.a)
      }
    }
  }

  override fun <A> IOOf<E, A>.onCancel(finalizer: IOOf<E, Unit>): IO<E, A> =
    OnCancel(finalizer)
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

interface IOConcurrent<EE> : Concurrent<IOPartialOf<EE>>, IOAsync<EE> {
  override fun <A> Kind<IOPartialOf<EE>, A>.fork(ctx: CoroutineContext): IO<EE, Fiber<IOPartialOf<EE>, A>> =
    Fork(ctx)

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<IOPartialOf<EE>>): IO<EE, A> =
    IO.cancelable { cb ->
      k { result ->
        when (result) {
          is Either.Left -> cb(IOResult.Exception(result.a))
          is Either.Right -> cb(IOResult.Success(result.b))
        }
      }
    }

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> IOOf<EE, CancelToken<IOPartialOf<EE>>>): IO<EE, A> =
    IO.cancelableF { cb ->
      k { result ->
        when (result) {
          is Either.Left -> cb(IOResult.Exception(result.a))
          is Either.Right -> cb(IOResult.Success(result.b))
        }
      }
    }

  override fun <A, B> CoroutineContext.racePair(fa: Kind<IOPartialOf<EE>, A>, fb: Kind<IOPartialOf<EE>, B>): IO<EE, RacePair<IOPartialOf<EE>, A, B>> =
    IO.racePair(this, fa, fb)

  override fun <A, B, C> CoroutineContext.raceTriple(fa: Kind<IOPartialOf<EE>, A>, fb: Kind<IOPartialOf<EE>, B>, fc: Kind<IOPartialOf<EE>, C>): IO<EE, RaceTriple<IOPartialOf<EE>, A, B, C>> =
    IO.raceTriple(this, fa, fb, fc)

  override fun <A, B, C> CoroutineContext.parMapN(fa: Kind<IOPartialOf<EE>, A>, fb: Kind<IOPartialOf<EE>, B>, f: (A, B) -> C): IO<EE, C> =
    IO.parMapN(this@parMapN, fa, fb, f)

  override fun <A, B, C, D> CoroutineContext.parMapN(fa: Kind<IOPartialOf<EE>, A>, fb: Kind<IOPartialOf<EE>, B>, fc: Kind<IOPartialOf<EE>, C>, f: (A, B, C) -> D): IO<EE, D> =
    IO.parMapN(this@parMapN, fa, fb, fc, f)
}

fun <EE> IO.Companion.concurrent(dispatchers: Dispatchers<IOPartialOf<EE>>): Concurrent<IOPartialOf<EE>> = object : IOConcurrent<EE> {
  override fun dispatchers(): Dispatchers<IOPartialOf<EE>> = dispatchers
}

fun <EE> IO.Companion.timer(CF: Concurrent<IOPartialOf<EE>>): Timer<IOPartialOf<EE>> =
  Timer(CF)

@extension
interface IOSemigroup<E, A> : Semigroup<IO<E, A>> {

  fun SG(): Semigroup<A>

  override fun IO<E, A>.combine(b: IO<E, A>): IO<E, A> =
    FlatMap { a1: A -> b.map { a2: A -> SG().run { a1.combine(a2) } } }
}

@extension
interface IOMonoid<E, A> : Monoid<IO<E, A>>, IOSemigroup<E, A> {

  override fun SG(): Semigroup<A> = SM()

  fun SM(): Monoid<A>

  override fun empty(): IO<E, A> = IO.just(SM().empty())
}

interface IOUnsafeRun : UnsafeRun<IOPartialOf<Nothing>> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<IOPartialOf<Nothing>, A>): A =
    fa().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<IOPartialOf<Nothing>, A>, cb: (Either<Throwable, A>) -> Unit): Unit =
    fa().unsafeRunAsync(cb)
}

interface IOMonadIO : MonadIO<IOPartialOf<Nothing>>, IOMonad<Nothing> {
  override fun <A> IO<Nothing, A>.liftIO(): IO<Nothing, A> = this
}

private val MonadIO: MonadIO<IOPartialOf<Nothing>> =
  object : IOMonadIO {}

fun IO.Companion.monadIO(): MonadIO<IOPartialOf<Nothing>> =
  MonadIO

private val UnsafeRun: IOUnsafeRun =
  object : IOUnsafeRun {}

fun IO.Companion.unsafeRun(): UnsafeRun<IOPartialOf<Nothing>> =
  UnsafeRun

fun <A> unsafe.runBlocking(fa: () -> IOOf<Nothing, A>): A = invoke {
  UnsafeRun.run { runBlocking(fa) }
}

fun <A> unsafe.runNonBlocking(fa: () -> Kind<IOPartialOf<Nothing>, A>, cb: (Either<Throwable, A>) -> Unit): Unit = invoke {
  UnsafeRun.run { runNonBlocking(fa, cb) }
}

interface IOUnsafeCancellableRun : UnsafeCancellableRun<IOPartialOf<Nothing>>, IOUnsafeRun {
  override suspend fun <A> unsafe.runNonBlockingCancellable(onCancel: OnCancel, fa: () -> Kind<IOPartialOf<Nothing>, A>, cb: (Either<Throwable, A>) -> Unit): Disposable =
    fa().unsafeRunAsyncCancellable(onCancel, cb)
}

private val UnsafeCancellableRun: IOUnsafeCancellableRun =
  object : IOUnsafeCancellableRun {}

fun IO.Companion.unsafeCancellableRun(): UnsafeCancellableRun<IOPartialOf<Nothing>> =
  UnsafeCancellableRun

fun <A> unsafe.runNonBlockingCancellable(onCancel: OnCancel, fa: () -> Kind<IOPartialOf<Nothing>, A>, cb: (Either<Throwable, A>) -> Unit): Disposable =
  invoke {
    UnsafeCancellableRun.run {
      runNonBlockingCancellable(onCancel, fa, cb)
    }
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
interface IODefaultConcurrent<EE> : Concurrent<IOPartialOf<EE>>, IOConcurrent<EE> {

  override fun dispatchers(): Dispatchers<IOPartialOf<EE>> =
    IO.dispatchers()
}

fun <E> IO.Companion.timer(): Timer<IOPartialOf<E>> = Timer(IO.concurrent())

fun <E, A> IO.Companion.fx(c: suspend IOSyntax<E>.() -> A): IO<E, A> =
  defer {
    val continuation = IOContinuation<E, A>()
    val wrapReturn: suspend IOContinuation<E, *>.() -> IO<E, A> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    continuation.returnedMonad().fix()
  }

@JvmName("fxIO")
fun <A> IO.Companion.fx(c: suspend ConcurrentSyntax<IOPartialOf<Nothing>>.() -> A): IO<Nothing, A> =
  IO.concurrent<Nothing>().fx.concurrent(c).fix()

/**
 * converts this Either to an IO. The resulting IO will evaluate to this Eithers
 * Right value or alternatively to the result of applying the specified function to this Left value.
 */
fun <A> Either<Throwable, A>.toIOException(): IO<Nothing, A> =
  fold({ IO.raiseException(it) }, { IO.just(it) })

/**
 * converts this Either to an IO. The resulting IO will evaluate to this Eithers
 * Right value or Left error value
 */
fun <E, A> Either<E, A>.toIO(): IO<E, A> =
  fold({ IO.raiseError(it) }, { IO.just(it) })

interface IOSyntax<E> : BindSyntax<IOPartialOf<E>> {
  suspend fun continueOn(ctx: CoroutineContext): Unit =
    IO.unit.continueOn(ctx).bind()

  fun <A> Iterable<IOOf<E, A>>.parSequence(ctx: CoroutineContext): IO<E, List<A>> =
    IO.concurrent<E>().run { parSequence(ctx).fix() }

  fun <A> Iterable<IOOf<E, A>>.parSequence(): IO<E, List<A>> =
    parSequence(IO.dispatchers<E>().default())

  fun <A, B> Iterable<A>.parTraverse(ctx: CoroutineContext, f: (A) -> IOOf<E, B>): IO<E, List<B>> =
    IO.concurrent<E>().run { parTraverse(ctx, f) }.fix()

  fun <A, B> Iterable<A>.parTraverse(f: (A) -> IOOf<E, B>): IO<E, List<B>> =
    parTraverse(IO.dispatchers<E>().default(), f)

  fun <A> Promise(): IO<Nothing, Promise<IOPartialOf<Nothing>, A>> =
    Promise<IOPartialOf<Nothing>, A>(IO.concurrent<Nothing>()).fix()

  fun Semaphore(n: Long): IO<Nothing, Semaphore<IOPartialOf<Nothing>>> =
    Semaphore(n, IO.concurrent<Nothing>()).fix()

  fun <A> MVar(a: A): IO<Nothing, MVar<IOPartialOf<Nothing>, A>> =
    MVar(a, IO.concurrent<Nothing>()).fix()

  /**
   * Create an empty [MVar] or mutable variable structure to be used for thread-safe sharing.
   *
   * @see MVar
   * @see [MVar] for more usage details.
   */
  fun <A> MVar(): IO<Nothing, MVar<IOPartialOf<Nothing>, A>> =
    MVar.empty<IOPartialOf<Nothing>, A>(IO.concurrent<Nothing>()).fix()
}

open class IOContinuation<E, A>(override val context: CoroutineContext = EmptyCoroutineContext) : Continuation<IO<E, A>>, IOSyntax<E> {

  override fun resume(value: IO<E, A>) {
    returnedMonad = value
  }

  @Suppress("UNCHECKED_CAST")
  override fun resumeWithException(exception: Throwable) {
    throw exception
  }

  protected lateinit var returnedMonad: IO<E, A>

  open fun returnedMonad(): IO<E, A> = returnedMonad

  override suspend fun <B> IOOf<E, B>.bind(): B =
    suspendCoroutineUninterceptedOrReturn { c ->
      val labelHere = c.stateStack // save the whole coroutine stack labels
      returnedMonad = this.FlatMap { x: B ->
        c.stateStack = labelHere
        c.resume(x)
        returnedMonad
      }
      COROUTINE_SUSPENDED
    }
}
