package arrow.fx.rx2.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2

import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Timer
import arrow.fx.rx2.ForSingleK
import arrow.fx.rx2.SingleK
import arrow.fx.rx2.SingleKOf
import arrow.fx.rx2.extensions.singlek.async.async
import arrow.fx.rx2.fix
import arrow.fx.rx2.k
import arrow.fx.rx2.value
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.ConcurrentEffect
import arrow.fx.typeclasses.Dispatchers
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Effect
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.extension
import arrow.fx.rx2.asScheduler
import arrow.fx.rx2.extensions.singlek.dispatchers.dispatchers
import arrow.fx.rx2.unsafeRunAsync
import arrow.fx.rx2.unsafeRunSync
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.ConcurrentSyntax
import arrow.fx.typeclasses.UnsafeRun
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import arrow.unsafe
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import io.reactivex.disposables.Disposable as RxDisposable
import arrow.fx.rx2.handleErrorWith as singleHandleErrorWith

@extension
interface SingleKFunctor : Functor<ForSingleK> {
  override fun <A, B> SingleKOf<A>.map(f: (A) -> B): SingleK<B> =
    fix().map(f)
}

@extension
interface SingleKApplicative : Applicative<ForSingleK> {
  override fun <A, B> SingleKOf<A>.ap(ff: SingleKOf<(A) -> B>): SingleK<B> =
    fix().ap(ff)

  override fun <A, B> SingleKOf<A>.map(f: (A) -> B): SingleK<B> =
    fix().map(f)

  override fun <A> just(a: A): SingleK<A> =
    SingleK.just(a)

  override fun <A, B> Kind<ForSingleK, A>.lazyAp(ff: () -> Kind<ForSingleK, (A) -> B>): Kind<ForSingleK, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }
}

@extension
interface SingleKMonad : Monad<ForSingleK>, SingleKApplicative {
  override fun <A, B> SingleKOf<A>.ap(ff: SingleKOf<(A) -> B>): SingleK<B> =
    fix().ap(ff)

  override fun <A, B> SingleKOf<A>.flatMap(f: (A) -> SingleKOf<B>): SingleK<B> =
    fix().flatMap(f)

  override fun <A, B> SingleKOf<A>.map(f: (A) -> B): SingleK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> SingleKOf<Either<A, B>>): SingleK<B> =
    SingleK.tailRecM(a, f)

  override fun <A, B> Kind<ForSingleK, A>.lazyAp(ff: () -> Kind<ForSingleK, (A) -> B>): Kind<ForSingleK, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }
}

@extension
interface SingleKApplicativeError :
  ApplicativeError<ForSingleK, Throwable>,
  SingleKApplicative {
  override fun <A> raiseError(e: Throwable): SingleK<A> =
    SingleK.raiseError(e)

  override fun <A> SingleKOf<A>.handleErrorWith(f: (Throwable) -> SingleKOf<A>): SingleK<A> =
    fix().singleHandleErrorWith { f(it).fix() }
}

@extension
interface SingleKMonadError :
  MonadError<ForSingleK, Throwable>,
  SingleKMonad {
  override fun <A> raiseError(e: Throwable): SingleK<A> =
    SingleK.raiseError(e)

  override fun <A> SingleKOf<A>.handleErrorWith(f: (Throwable) -> SingleKOf<A>): SingleK<A> =
    fix().singleHandleErrorWith { f(it).fix() }
}

@extension
interface SingleKMonadThrow : MonadThrow<ForSingleK>, SingleKMonadError

@extension
interface SingleKBracket : Bracket<ForSingleK, Throwable>, SingleKMonadThrow {
  override fun <A, B> SingleKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> SingleKOf<Unit>, use: (A) -> SingleKOf<B>): SingleK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface SingleKMonadDefer : MonadDefer<ForSingleK>, SingleKBracket {
  override fun <A> defer(fa: () -> SingleKOf<A>): SingleK<A> =
    SingleK.defer(fa)
}

@extension
interface SingleKAsync :
  Async<ForSingleK>,
  SingleKMonadDefer {
  override fun <A> async(fa: Proc<A>): SingleK<A> =
    SingleK.async(fa)

  override fun <A> asyncF(k: ProcF<ForSingleK, A>): SingleK<A> =
    SingleK.asyncF(k)

  override fun <A> SingleKOf<A>.continueOn(ctx: CoroutineContext): SingleK<A> =
    fix().continueOn(ctx)
}

@extension
interface SingleKEffect :
  Effect<ForSingleK>,
  SingleKAsync {
  override fun <A> SingleKOf<A>.runAsync(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Unit> =
    fix().runAsync(cb)
}

interface SingleKConcurrent : Concurrent<ForSingleK>, SingleKAsync {
  override fun <A> Kind<ForSingleK, A>.fork(coroutineContext: CoroutineContext): SingleK<Fiber<ForSingleK, A>> =
    coroutineContext.asScheduler().let { scheduler ->
      Single.create<Fiber<ForSingleK, A>> { emitter ->
        if (!emitter.isDisposed) {
          val s: ReplaySubject<A> = ReplaySubject.create()
          val conn: RxDisposable = value().subscribeOn(scheduler).subscribe(s::onNext, s::onError)
          emitter.onSuccess(Fiber(s.firstOrError().k(), SingleK {
            conn.dispose()
          }))
        }
      }.k()
    }

  override fun <A, B, C> CoroutineContext.parMapN(fa: SingleKOf<A>, fb: SingleKOf<B>, f: (A, B) -> C): SingleK<C> =
    SingleK(fa.value().zipWith(fb.value(), BiFunction(f)).subscribeOn(asScheduler()))

  override fun <A, B, C, D> CoroutineContext.parMapN(fa: SingleKOf<A>, fb: SingleKOf<B>, fc: SingleKOf<C>, f: (A, B, C) -> D): SingleK<D> =
    SingleK(fa.value().zipWith(fb.value().zipWith(fc.value(), BiFunction<B, C, Tuple2<B, C>> { b, c -> Tuple2(b, c) }), BiFunction { a: A, tuple: Tuple2<B, C> ->
      f(a, tuple.a, tuple.b)
    }).subscribeOn(asScheduler()))

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForSingleK>): SingleK<A> =
    SingleK.cancelable(k)

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> SingleKOf<CancelToken<ForSingleK>>): SingleK<A> =
    SingleK.cancelableF(k)

  override fun <A, B> CoroutineContext.racePair(fa: SingleKOf<A>, fb: SingleKOf<B>): SingleK<RacePair<ForSingleK, A, B>> =
    asScheduler().let { scheduler ->
      Single.create<RacePair<ForSingleK, A, B>> { emitter ->
        val sa = ReplaySubject.create<A>()
        val sb = ReplaySubject.create<B>()
        val dda = fa.value().subscribe(sa::onNext, sa::onError)
        val ddb = fb.value().subscribe(sb::onNext, sb::onError)
        emitter.setCancellable { dda.dispose(); ddb.dispose() }
        val ffa = Fiber(sa.firstOrError().k(), SingleK { dda.dispose() })
        val ffb = Fiber(sb.firstOrError().k(), SingleK { ddb.dispose() })
        sa.subscribe({
          emitter.onSuccess(RacePair.First(it, ffb))
        }, { e -> emitter.tryOnError(e) })
        sb.subscribe({
          emitter.onSuccess(RacePair.Second(ffa, it))
        }, { e -> emitter.tryOnError(e) })
      }.subscribeOn(scheduler).observeOn(Schedulers.trampoline()).k()
    }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: SingleKOf<A>, fb: SingleKOf<B>, fc: SingleKOf<C>): SingleK<RaceTriple<ForSingleK, A, B, C>> =
    asScheduler().let { scheduler ->
      Single.create<RaceTriple<ForSingleK, A, B, C>> { emitter ->
        val sa = ReplaySubject.create<A>()
        val sb = ReplaySubject.create<B>()
        val sc = ReplaySubject.create<C>()
        val dda = fa.value().subscribe(sa::onNext, sa::onError)
        val ddb = fb.value().subscribe(sb::onNext, sb::onError)
        val ddc = fc.value().subscribe(sc::onNext, sc::onError)
        emitter.setCancellable { dda.dispose(); ddb.dispose(); ddc.dispose() }
        val ffa = Fiber(sa.firstOrError().k(), SingleK { dda.dispose() })
        val ffb = Fiber(sb.firstOrError().k(), SingleK { ddb.dispose() })
        val ffc = Fiber(sc.firstOrError().k(), SingleK { ddc.dispose() })
        sa.subscribe({
          emitter.onSuccess(RaceTriple.First(it, ffb, ffc))
        }, { e -> emitter.tryOnError(e) })
        sb.subscribe({
          emitter.onSuccess(RaceTriple.Second(ffa, it, ffc))
        }, { e -> emitter.tryOnError(e) })
        sc.subscribe({
          emitter.onSuccess(RaceTriple.Third(ffa, ffb, it))
        }, { e -> emitter.tryOnError(e) })
      }.subscribeOn(scheduler).observeOn(Schedulers.trampoline()).k()
    }
}

fun SingleK.Companion.concurrent(dispatchers: Dispatchers<ForSingleK> = SingleK.dispatchers()): Concurrent<ForSingleK> = object : SingleKConcurrent {
  override fun dispatchers(): Dispatchers<ForSingleK> = dispatchers
}

@extension
interface SingleKDispatchers : Dispatchers<ForSingleK> {
  override fun default(): CoroutineContext =
    ComputationScheduler

  override fun io(): CoroutineContext =
    IOScheduler
}

@extension
interface SingleKConcurrentEffect : ConcurrentEffect<ForSingleK>, SingleKEffect {
  override fun <A> SingleKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Disposable> =
    fix().runAsyncCancellable(cb)
}

@extension
interface SingleKTimer : Timer<ForSingleK> {
  override fun sleep(duration: Duration): SingleK<Unit> =
    SingleK(Single.timer(duration.nanoseconds, TimeUnit.NANOSECONDS)
      .map { Unit })
}

@extension
interface SingleKUnsafeRun : UnsafeRun<ForSingleK> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<ForSingleK, A>): A = fa().fix().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<ForSingleK, A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().unsafeRunAsync(cb)
}

fun <A> SingleK.Companion.fx(c: suspend ConcurrentSyntax<ForSingleK>.() -> A): SingleK<A> =
  defer { SingleK.concurrent().fx.concurrent(c).fix() }
