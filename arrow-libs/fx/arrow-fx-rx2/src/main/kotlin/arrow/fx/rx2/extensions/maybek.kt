package arrow.fx.rx2.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.extension
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Timer
import arrow.fx.internal.AtomicBooleanW
import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeKOf
import arrow.fx.rx2.asScheduler
import arrow.fx.rx2.extensions.maybek.dispatchers.dispatchers
import arrow.fx.rx2.fix
import arrow.fx.rx2.k
import arrow.fx.rx2.unsafeRunAsync
import arrow.fx.rx2.unsafeRunSync
import arrow.fx.rx2.value
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.ConcurrentSyntax
import arrow.fx.typeclasses.Dispatchers
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Effect
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.fx.typeclasses.UnsafeRun
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadFilter
import arrow.typeclasses.MonadThrow
import arrow.unsafe
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import arrow.fx.rx2.handleErrorWith as maybeHandleErrorWith
import io.reactivex.disposables.Disposable as RxDisposable

@extension
interface MaybeKFunctor : Functor<ForMaybeK> {
  override fun <A, B> MaybeKOf<A>.map(f: (A) -> B): MaybeK<B> =
    fix().map(f)
}

@extension
interface MaybeKApplicative : Applicative<ForMaybeK> {
  override fun <A, B> MaybeKOf<A>.ap(ff: MaybeKOf<(A) -> B>): MaybeK<B> =
    fix().ap(ff)

  override fun <A, B> MaybeKOf<A>.map(f: (A) -> B): MaybeK<B> =
    fix().map(f)

  override fun <A> just(a: A): MaybeK<A> =
    MaybeK.just(a)

  override fun <A, B> Kind<ForMaybeK, A>.apEval(ff: Eval<Kind<ForMaybeK, (A) -> B>>): Eval<Kind<ForMaybeK, B>> =
    Eval.now(fix().ap(MaybeK.defer { ff.value() }))
}

@extension
interface MaybeKMonad : Monad<ForMaybeK>, MaybeKApplicative {
  override fun <A, B> MaybeKOf<A>.ap(ff: MaybeKOf<(A) -> B>): MaybeK<B> =
    fix().ap(ff)

  override fun <A, B> MaybeKOf<A>.flatMap(f: (A) -> MaybeKOf<B>): MaybeK<B> =
    fix().flatMap(f)

  override fun <A, B> MaybeKOf<A>.map(f: (A) -> B): MaybeK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> MaybeKOf<Either<A, B>>): MaybeK<B> =
    MaybeK.tailRecM(a, f)

  override fun <A, B> Kind<ForMaybeK, A>.apEval(ff: Eval<Kind<ForMaybeK, (A) -> B>>): Eval<Kind<ForMaybeK, B>> =
    Eval.now(fix().ap(MaybeK.defer { ff.value() }))
}

@extension
interface MaybeKFoldable : Foldable<ForMaybeK> {

  override fun <A, B> MaybeKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> MaybeKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> MaybeKOf<A>.isEmpty(): Boolean =
    fix().isEmpty()

  override fun <A> MaybeKOf<A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A> MaybeKOf<A>.forAll(p: (A) -> Boolean): Boolean =
    fix().forall(p)

  override fun <A> MaybeKOf<A>.nonEmpty(): Boolean =
    fix().nonEmpty()
}

@extension
interface MaybeKApplicativeError :
  ApplicativeError<ForMaybeK, Throwable>,
  MaybeKApplicative {
  override fun <A> raiseError(e: Throwable): MaybeK<A> =
    MaybeK.raiseError(e)

  override fun <A> MaybeKOf<A>.handleErrorWith(f: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
    fix().maybeHandleErrorWith { f(it).fix() }
}

@extension
interface MaybeKMonadError :
  MonadError<ForMaybeK, Throwable>,
  MaybeKMonad {
  override fun <A> raiseError(e: Throwable): MaybeK<A> =
    MaybeK.raiseError(e)

  override fun <A> MaybeKOf<A>.handleErrorWith(f: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
    fix().maybeHandleErrorWith { f(it).fix() }
}

@extension
interface MaybeKMonadThrow : MonadThrow<ForMaybeK>, MaybeKMonadError

@extension
interface MaybeKBracket : Bracket<ForMaybeK, Throwable>, MaybeKMonadThrow {
  override fun <A, B> MaybeKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> MaybeKOf<Unit>, use: (A) -> MaybeKOf<B>): MaybeK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface MaybeKMonadDefer : MonadDefer<ForMaybeK>, MaybeKBracket {
  override fun <A> defer(fa: () -> MaybeKOf<A>): MaybeK<A> =
    MaybeK.defer(fa)
}

@extension
interface MaybeKAsync : Async<ForMaybeK>, MaybeKMonadDefer {
  override fun <A> async(fa: Proc<A>): MaybeK<A> =
    MaybeK.async(fa)

  override fun <A> asyncF(k: ProcF<ForMaybeK, A>): MaybeK<A> =
    MaybeK.asyncF(k)

  override fun <A> MaybeKOf<A>.continueOn(ctx: CoroutineContext): MaybeK<A> =
    fix().continueOn(ctx)
}

@extension
interface MaybeKEffect :
  Effect<ForMaybeK>,
  MaybeKAsync {
  override fun <A> MaybeKOf<A>.runAsync(cb: (Either<Throwable, A>) -> MaybeKOf<Unit>): MaybeK<Unit> =
    fix().runAsync(cb)
}

interface MaybeKConcurrent : Concurrent<ForMaybeK>, MaybeKAsync {
  override fun <A> Kind<ForMaybeK, A>.fork(ctx: CoroutineContext): MaybeK<Fiber<ForMaybeK, A>> =
    ctx.asScheduler().let { scheduler ->
      Maybe.create<Fiber<ForMaybeK, A>> { emitter ->
        if (!emitter.isDisposed) {
          val s: ReplaySubject<A> = ReplaySubject.create()
          val conn: RxDisposable = value().subscribeOn(scheduler).subscribe(s::onNext, s::onError)
          emitter.onSuccess(Fiber(s.firstElement().k(), MaybeK {
            conn.dispose()
          }))
        }
      }.k()
    }

  override fun <A, B> parTupledN(ctx: CoroutineContext, fa: MaybeKOf<A>, fb: MaybeKOf<B>): MaybeK<Tuple2<A, B>> =
    fa.value().zipWith(fb.value(), tupled2()).subscribeOn(ctx.asScheduler()).k()

  override fun <A, B, C> parTupledN(ctx: CoroutineContext, fa: MaybeKOf<A>, fb: MaybeKOf<B>, fc: MaybeKOf<C>): MaybeK<Tuple3<A, B, C>> =
    Maybe.zip(fa.value(), fb.value(), fc.value(), tupled3()).subscribeOn(ctx.asScheduler()).k()

  override fun <A> cancellable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForMaybeK>): MaybeK<A> =
    MaybeK.cancellable(k)

  override fun <A> cancellableF(k: ((Either<Throwable, A>) -> Unit) -> MaybeKOf<CancelToken<ForMaybeK>>): MaybeK<A> =
    MaybeK.cancellableF(k)

  override fun <A, B> CoroutineContext.racePair(fa: MaybeKOf<A>, fb: MaybeKOf<B>): MaybeK<RacePair<ForMaybeK, A, B>> =
    asScheduler().let { scheduler ->
      Maybe.create<RacePair<ForMaybeK, A, B>> { emitter ->
        val sa = ReplaySubject.create<A>()
        val sb = ReplaySubject.create<B>()
        val dda = fa.value().subscribe(sa::onNext, sa::onError)
        val ddb = fb.value().subscribe(sb::onNext, sb::onError)

        val shouldDisposeSa = AtomicBooleanW(true)
        val shouldDisposeSb = AtomicBooleanW(true)
        emitter.setCancellable {
          if (shouldDisposeSa.value) dda.dispose()
          if (shouldDisposeSb.value) ddb.dispose()
        }

        val ffa = Fiber(sa.firstElement().k(), MaybeK { dda.dispose() })
        val ffb = Fiber(sb.firstElement().k(), MaybeK { ddb.dispose() })
        sa.subscribe({
          shouldDisposeSb.value = false
          emitter.onSuccess(RacePair.First(it, ffb))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
        sb.subscribe({
          shouldDisposeSa.value = false
          emitter.onSuccess(RacePair.Second(ffa, it))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
      }.subscribeOn(scheduler).observeOn(Schedulers.trampoline()).k()
    }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: MaybeKOf<A>, fb: MaybeKOf<B>, fc: MaybeKOf<C>): MaybeK<RaceTriple<ForMaybeK, A, B, C>> =
    asScheduler().let { scheduler ->
      Maybe.create<RaceTriple<ForMaybeK, A, B, C>> { emitter ->
        val sa = ReplaySubject.create<A>()
        val sb = ReplaySubject.create<B>()
        val sc = ReplaySubject.create<C>()
        val dda = fa.value().subscribe(sa::onNext, sa::onError, sa::onComplete)
        val ddb = fb.value().subscribe(sb::onNext, sb::onError, sb::onComplete)
        val ddc = fc.value().subscribe(sc::onNext, sc::onError, sc::onComplete)
        val shouldDisposeSa = AtomicBooleanW(true)
        val shouldDisposeSb = AtomicBooleanW(true)
        val shouldDisposeSc = AtomicBooleanW(true)

        emitter.setCancellable {
          if (shouldDisposeSa.value) dda.dispose()
          if (shouldDisposeSb.value) ddb.dispose()
          if (shouldDisposeSc.value) ddc.dispose()
        }
        val ffa = Fiber(sa.firstElement().k(), MaybeK { dda.dispose() })
        val ffb = Fiber(sb.firstElement().k(), MaybeK { ddb.dispose() })
        val ffc = Fiber(sc.firstElement().k(), MaybeK { ddc.dispose() })
        sa.subscribe({
          shouldDisposeSb.value = false
          shouldDisposeSc.value = false
          emitter.onSuccess(RaceTriple.First(it, ffb, ffc))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
        sb.subscribe({
          shouldDisposeSa.value = false
          shouldDisposeSc.value = false
          emitter.onSuccess(RaceTriple.Second(ffa, it, ffc))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
        sc.subscribe({
          shouldDisposeSa.value = false
          shouldDisposeSb.value = false
          emitter.onSuccess(RaceTriple.Third(ffa, ffb, it))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
      }.subscribeOn(scheduler).observeOn(Schedulers.trampoline()).k()
    }
}

fun MaybeK.Companion.concurrent(dispatchers: Dispatchers<ForMaybeK> = MaybeK.dispatchers()): Concurrent<ForMaybeK> = object : MaybeKConcurrent {
  override fun dispatchers(): Dispatchers<ForMaybeK> = dispatchers
}

@extension
interface MaybeKUnsafeRun : UnsafeRun<ForMaybeK> {
  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<ForMaybeK, A>): A = fa().fix().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<ForMaybeK, A>, cb: (Either<Throwable, A>) -> Unit): Unit =
    fa().fix().unsafeRunAsync(cb)
}

@extension
interface MaybeKDispatchers : Dispatchers<ForMaybeK> {
  override fun default(): CoroutineContext =
    ComputationScheduler

  override fun io(): CoroutineContext =
    IOScheduler
}

@extension
interface MaybeKTimer : Timer<ForMaybeK> {
  override fun sleep(duration: Duration): MaybeK<Unit> =
    MaybeK(Maybe.timer(duration.nanoseconds, TimeUnit.NANOSECONDS)
      .map { Unit })
}

@extension
interface MaybeKFunctorFilter : FunctorFilter<ForMaybeK>, MaybeKFunctor {
  override fun <A, B> Kind<ForMaybeK, A>.filterMap(f: (A) -> Option<B>): Kind<ForMaybeK, B> =
    fix().filterMap(f)
}

@extension
interface MaybeKMonadFilter : MonadFilter<ForMaybeK>, MaybeKMonad {
  override fun <A> empty(): MaybeK<A> =
    Maybe.empty<A>().k()

  override fun <A, B> Kind<ForMaybeK, A>.filterMap(f: (A) -> Option<B>): MaybeK<B> =
    fix().filterMap(f)
}

fun <A> MaybeK.Companion.fx(c: suspend ConcurrentSyntax<ForMaybeK>.() -> A): MaybeK<A> =
  MaybeK.concurrent().fx.concurrent(c).fix()
