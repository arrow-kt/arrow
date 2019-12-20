package arrow.fx.rx2.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Option
import arrow.core.Tuple2

import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.rx2.FlowableK
import arrow.fx.rx2.FlowableKOf
import arrow.fx.rx2.ForFlowableK
import arrow.fx.rx2.extensions.flowablek.async.async
import arrow.fx.rx2.extensions.flowablek.effect.effect
import arrow.fx.rx2.extensions.flowablek.monad.monad
import arrow.fx.rx2.extensions.flowablek.monadDefer.monadDefer
import arrow.fx.rx2.extensions.flowablek.monadError.monadError
import arrow.fx.rx2.fix
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.ConcurrentEffect
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Effect
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.fx.Timer
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.Fiber
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.Traverse
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import io.reactivex.functions.BiFunction
import arrow.fx.rx2.asScheduler
import arrow.fx.rx2.extensions.flowablek.dispatchers.dispatchers
import arrow.fx.rx2.k
import arrow.fx.rx2.value
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.ConcurrentSyntax
import arrow.fx.typeclasses.Dispatchers
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.MonadFilter
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import io.reactivex.disposables.Disposable as RxDisposable
import arrow.fx.rx2.handleErrorWith as flowableHandleErrorWith

@extension
interface FlowableKFunctor : Functor<ForFlowableK> {
  override fun <A, B> FlowableKOf<A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)
}

@extension
interface FlowableKApplicative : Applicative<ForFlowableK> {
  override fun <A, B> FlowableKOf<A>.ap(ff: FlowableKOf<(A) -> B>): FlowableK<B> =
    fix().ap(ff)

  override fun <A, B> FlowableKOf<A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)

  override fun <A> just(a: A): FlowableK<A> =
    FlowableK.just(a)

  override fun <A, B> Kind<ForFlowableK, A>.lazyAp(ff: () -> Kind<ForFlowableK, (A) -> B>): Kind<ForFlowableK, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }
}

@extension
interface FlowableKMonad : Monad<ForFlowableK>, FlowableKApplicative {
  override fun <A, B> FlowableKOf<A>.ap(ff: FlowableKOf<(A) -> B>): FlowableK<B> =
    fix().ap(ff)

  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().concatMap(f)

  override fun <A, B> FlowableKOf<A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> FlowableKOf<Either<A, B>>): FlowableK<B> =
    FlowableK.tailRecM(a, f)

  override fun <A, B> Kind<ForFlowableK, A>.lazyAp(ff: () -> Kind<ForFlowableK, (A) -> B>): Kind<ForFlowableK, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }
}

@extension
interface FlowableKFoldable : Foldable<ForFlowableK> {
  override fun <A, B> FlowableKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> FlowableKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface FlowableKTraverse : Traverse<ForFlowableK> {
  override fun <A, B> FlowableKOf<A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)

  override fun <G, A, B> FlowableKOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, FlowableK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> FlowableKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> FlowableKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface FlowableKApplicativeError :
  ApplicativeError<ForFlowableK, Throwable>,
  FlowableKApplicative {
  override fun <A> raiseError(e: Throwable): FlowableK<A> =
    FlowableK.raiseError(e)

  override fun <A> FlowableKOf<A>.handleErrorWith(f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
    fix().flowableHandleErrorWith { f(it).fix() }
}

@extension
interface FlowableKMonadError :
  MonadError<ForFlowableK, Throwable>,
  FlowableKMonad {
  override fun <A> raiseError(e: Throwable): FlowableK<A> =
    FlowableK.raiseError(e)

  override fun <A> FlowableKOf<A>.handleErrorWith(f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
    fix().flowableHandleErrorWith { f(it).fix() }
}

@extension
interface FlowableKMonadThrow : MonadThrow<ForFlowableK>, FlowableKMonadError

@extension
interface FlowableKBracket : Bracket<ForFlowableK, Throwable>, FlowableKMonadThrow {
  override fun <A, B> FlowableKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FlowableKOf<Unit>, use: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface FlowableKMonadDefer : MonadDefer<ForFlowableK>, FlowableKBracket {
  override fun <A> defer(fa: () -> FlowableKOf<A>): FlowableK<A> =
    FlowableK.defer(fa)

  fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER
}

@extension
interface FlowableKAsync :
  Async<ForFlowableK>,
  FlowableKMonadDefer {
  override fun <A> async(fa: Proc<A>): FlowableK<A> =
    FlowableK.async(fa, BS())

  override fun <A> asyncF(k: ProcF<ForFlowableK, A>): FlowableKOf<A> =
    FlowableK.asyncF(k, BS())

  override fun <A> FlowableKOf<A>.continueOn(ctx: CoroutineContext): FlowableK<A> =
    fix().continueOn(ctx)
}

@extension
interface FlowableKEffect :
  Effect<ForFlowableK>,
  FlowableKAsync {
  override fun <A> FlowableKOf<A>.runAsync(cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Unit> =
    fix().runAsync(cb)
}

interface FlowableKConcurrent : Concurrent<ForFlowableK>, FlowableKAsync {

  override fun <A> Kind<ForFlowableK, A>.fork(coroutineContext: CoroutineContext): FlowableK<Fiber<ForFlowableK, A>> =
    coroutineContext.asScheduler().let { scheduler ->
      Flowable.create<Fiber<ForFlowableK, A>>({ emitter ->
        if (!emitter.isCancelled) {
          val s: ReplaySubject<A> = ReplaySubject.create<A>()
          val conn: RxDisposable = value().subscribeOn(scheduler).subscribe(s::onNext, s::onError)
          emitter.onNext(Fiber(s.toFlowable(BS()).k(), FlowableK {
            conn.dispose()
          }))
        }
      }, BS()).k()
    }

  override fun <A, B, C> CoroutineContext.parMapN(fa: FlowableKOf<A>, fb: FlowableKOf<B>, f: (A, B) -> C): FlowableK<C> =
    FlowableK(fa.value().zipWith(fb.value(), BiFunction(f)).subscribeOn(asScheduler()))

  override fun <A, B, C, D> CoroutineContext.parMapN(fa: FlowableKOf<A>, fb: FlowableKOf<B>, fc: FlowableKOf<C>, f: (A, B, C) -> D): FlowableK<D> =
    FlowableK(fa.value().zipWith(fb.value().zipWith(fc.value(), BiFunction<B, C, Tuple2<B, C>> { b, c -> Tuple2(b, c) }), BiFunction { a: A, tuple: Tuple2<B, C> ->
      f(a, tuple.a, tuple.b)
    }).subscribeOn(asScheduler()))

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForFlowableK>): FlowableK<A> =
    FlowableK.cancelable(k, BS())

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> FlowableKOf<CancelToken<ForFlowableK>>): FlowableK<A> =
    FlowableK.cancelableF(k, BS())

  override fun <A, B> CoroutineContext.racePair(fa: FlowableKOf<A>, fb: FlowableKOf<B>): FlowableK<RacePair<ForFlowableK, A, B>> =
    asScheduler().let { scheduler ->
      Flowable.create<RacePair<ForFlowableK, A, B>>({ emitter ->
        val sa = ReplaySubject.create<A>()
        val sb = ReplaySubject.create<B>()
        val dda = fa.value().subscribe(sa::onNext, sa::onError)
        val ddb = fb.value().subscribe(sb::onNext, sb::onError)
        emitter.setCancellable { dda.dispose(); ddb.dispose() }
        val ffa = Fiber(sa.toFlowable(BS()).k(), FlowableK { dda.dispose() })
        val ffb = Fiber(sb.toFlowable(BS()).k(), FlowableK { ddb.dispose() })
        sa.subscribe({
          emitter.onNext(RacePair.First(it, ffb))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
        sb.subscribe({
          emitter.onNext(RacePair.Second(ffa, it))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
      }, BS()).subscribeOn(scheduler).observeOn(Schedulers.trampoline()).k()
    }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: FlowableKOf<A>, fb: FlowableKOf<B>, fc: FlowableKOf<C>): FlowableK<RaceTriple<ForFlowableK, A, B, C>> =
    asScheduler().let { scheduler ->
      Flowable.create<RaceTriple<ForFlowableK, A, B, C>>({ emitter ->
        val sa = ReplaySubject.create<A>()
        val sb = ReplaySubject.create<B>()
        val sc = ReplaySubject.create<C>()
        val dda = fa.value().subscribe(sa::onNext, sa::onError)
        val ddb = fb.value().subscribe(sb::onNext, sb::onError)
        val ddc = fc.value().subscribe(sc::onNext, sc::onError)
        emitter.setCancellable { dda.dispose(); ddb.dispose(); ddc.dispose() }
        val ffa = Fiber(sa.toFlowable(BS()).k(), FlowableK { dda.dispose() })
        val ffb = Fiber(sb.toFlowable(BS()).k(), FlowableK { ddb.dispose() })
        val ffc = Fiber(sc.toFlowable(BS()).k(), FlowableK { ddc.dispose() })
        sa.subscribe({
          emitter.onNext(RaceTriple.First(it, ffb, ffc))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
        sb.subscribe({
          emitter.onNext(RaceTriple.Second(ffa, it, ffc))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
        sc.subscribe({
          emitter.onNext(RaceTriple.Third(ffa, ffb, it))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
      }, BS()).subscribeOn(scheduler).observeOn(Schedulers.trampoline()).k()
    }
}

fun FlowableK.Companion.concurrent(dispatchers: Dispatchers<ForFlowableK> = FlowableK.dispatchers()): Concurrent<ForFlowableK> = object : FlowableKConcurrent {
  override fun dispatchers(): Dispatchers<ForFlowableK> = dispatchers
}

@extension
interface FlowableKDispatchers : Dispatchers<ForFlowableK> {
  override fun default(): CoroutineContext =
    ComputationScheduler

  override fun io(): CoroutineContext =
    IOScheduler
}

@extension
interface FlowableKConcurrentEffect : ConcurrentEffect<ForFlowableK>, FlowableKEffect {
  override fun <A> FlowableKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Disposable> =
    fix().runAsyncCancellable(cb)
}

fun FlowableK.Companion.monadFlat(): FlowableKMonad = monad()

fun FlowableK.Companion.monadConcat(): FlowableKMonad = object : FlowableKMonad {
  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().concatMap { f(it).fix() }
}

fun FlowableK.Companion.monadSwitch(): FlowableKMonad = object : FlowableKMonad {
  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().switchMap { f(it).fix() }
}

fun FlowableK.Companion.monadErrorFlat(): FlowableKMonadError = monadError()

fun FlowableK.Companion.monadErrorConcat(): FlowableKMonadError = object : FlowableKMonadError {
  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().concatMap { f(it).fix() }
}

fun FlowableK.Companion.monadErrorSwitch(): FlowableKMonadError = object : FlowableKMonadError {
  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().switchMap { f(it).fix() }
}

fun FlowableK.Companion.monadSuspendBuffer(): FlowableKMonadDefer = monadDefer()

fun FlowableK.Companion.monadSuspendDrop(): FlowableKMonadDefer = object : FlowableKMonadDefer {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
}

fun FlowableK.Companion.monadSuspendError(): FlowableKMonadDefer = object : FlowableKMonadDefer {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
}

fun FlowableK.Companion.monadSuspendLatest(): FlowableKMonadDefer = object : FlowableKMonadDefer {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
}

fun FlowableK.Companion.monadSuspendMissing(): FlowableKMonadDefer = object : FlowableKMonadDefer {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
}

fun FlowableK.Companion.asyncBuffer(): FlowableKAsync = async()

fun FlowableK.Companion.asyncDrop(): FlowableKAsync = object : FlowableKAsync {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
}

fun FlowableK.Companion.asyncError(): FlowableKAsync = object : FlowableKAsync {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
}

fun FlowableK.Companion.asyncLatest(): FlowableKAsync = object : FlowableKAsync {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
}

fun FlowableK.Companion.asyncMissing(): FlowableKAsync = object : FlowableKAsync {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
}

fun FlowableK.Companion.effectBuffer(): FlowableKEffect = effect()

fun FlowableK.Companion.effectDrop(): FlowableKEffect = object : FlowableKEffect {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
}

fun FlowableK.Companion.effectError(): FlowableKEffect = object : FlowableKEffect {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
}

fun FlowableK.Companion.effectLatest(): FlowableKEffect = object : FlowableKEffect {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
}

fun FlowableK.Companion.effectMissing(): FlowableKEffect = object : FlowableKEffect {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
}

@extension
interface FlowableKTimer : Timer<ForFlowableK> {
  override fun sleep(duration: Duration): FlowableK<Unit> =
    FlowableK(Flowable.timer(duration.nanoseconds, TimeUnit.NANOSECONDS)
      .map { Unit })
}

@extension
interface FlowableKFunctorFilter : FunctorFilter<ForFlowableK>, FlowableKFunctor {
  override fun <A, B> Kind<ForFlowableK, A>.filterMap(f: (A) -> Option<B>): FlowableK<B> =
    fix().filterMap(f)
}

@extension
interface FlowableKMonadFilter : MonadFilter<ForFlowableK>, FlowableKMonad {
  override fun <A> empty(): FlowableK<A> =
    Flowable.empty<A>().k()

  override fun <A, B> Kind<ForFlowableK, A>.filterMap(f: (A) -> Option<B>): FlowableK<B> =
    fix().filterMap(f)
}
fun <A> FlowableK.Companion.fx(c: suspend ConcurrentSyntax<ForFlowableK>.() -> A): FlowableK<A> =
  defer { FlowableK.concurrent().fx.concurrent(c).fix() }
