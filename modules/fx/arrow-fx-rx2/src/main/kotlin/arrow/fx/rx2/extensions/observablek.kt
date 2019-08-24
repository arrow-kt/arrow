package arrow.fx.rx2.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.fx.CancelToken
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Timer
import arrow.fx.rx2.CoroutineContextRx2Scheduler.asScheduler
import arrow.fx.rx2.ForObservableK
import arrow.fx.rx2.ObservableK
import arrow.fx.rx2.ObservableKOf
import arrow.fx.rx2.extensions.observablek.async.async
import arrow.fx.rx2.extensions.observablek.monad.monad
import arrow.fx.rx2.extensions.observablek.monadError.monadError
import arrow.fx.rx2.fix
import arrow.fx.rx2.k
import arrow.fx.rx2.value
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.AsyncSyntax
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
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.Traverse
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import io.reactivex.disposables.Disposable as RxDisposable

@extension
interface ObservableKFunctor : Functor<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)
}

@extension
interface ObservableKApplicative : Applicative<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.ap(ff: ObservableKOf<(A) -> B>): ObservableK<B> =
    fix().ap(ff)

  override fun <A, B> ObservableKOf<A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)

  override fun <A> just(a: A): ObservableK<A> =
    ObservableK.just(a)
}

@extension
interface ObservableKMonad : Monad<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.ap(ff: ObservableKOf<(A) -> B>): ObservableK<B> =
    fix().ap(ff)

  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().concatMap(f)

  override fun <A, B> ObservableKOf<A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> ObservableKOf<Either<A, B>>): ObservableK<B> =
    ObservableK.tailRecM(a, f)

  override fun <A> just(a: A): ObservableK<A> =
    ObservableK.just(a)
}

@extension
interface ObservableKFoldable : Foldable<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> ObservableKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface ObservableKTraverse : Traverse<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)

  override fun <G, A, B> ObservableKOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ObservableK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> ObservableKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> ObservableKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface ObservableKApplicativeError :
  ApplicativeError<ForObservableK, Throwable>,
  ObservableKApplicative {
  override fun <A> raiseError(e: Throwable): ObservableK<A> =
    ObservableK.raiseError(e)

  override fun <A> ObservableKOf<A>.handleErrorWith(f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface ObservableKMonadError :
  MonadError<ForObservableK, Throwable>,
  ObservableKMonad {
  override fun <A> raiseError(e: Throwable): ObservableK<A> =
    ObservableK.raiseError(e)

  override fun <A> ObservableKOf<A>.handleErrorWith(f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface ObservableKMonadThrow : MonadThrow<ForObservableK>, ObservableKMonadError

@extension
interface ObservableKBracket : Bracket<ForObservableK, Throwable>, ObservableKMonadThrow {
  override fun <A, B> ObservableKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> ObservableKOf<Unit>, use: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface ObservableKMonadDefer : MonadDefer<ForObservableK>, ObservableKBracket {
  override fun <A> defer(fa: () -> ObservableKOf<A>): ObservableK<A> =
    ObservableK.defer(fa)
}

@extension
interface ObservableKAsync : Async<ForObservableK>, ObservableKMonadDefer {
  override fun <A> async(fa: Proc<A>): ObservableK<A> =
    ObservableK.async(fa)

  override fun <A> asyncF(k: ProcF<ForObservableK, A>): ObservableK<A> =
    ObservableK.asyncF(k)

  override fun <A> ObservableKOf<A>.continueOn(ctx: CoroutineContext): ObservableK<A> =
    fix().continueOn(ctx)
}

@extension
interface ObservableKEffect : Effect<ForObservableK>, ObservableKAsync {
  override fun <A> ObservableKOf<A>.runAsync(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Unit> =
    fix().runAsync(cb)
}

interface ObservableKConcurrent : Concurrent<ForObservableK>, ObservableKAsync {
  override fun <A> Kind<ForObservableK, A>.fork(coroutineContext: CoroutineContext): ObservableK<Fiber<ForObservableK, A>> =
    coroutineContext.asScheduler().let { scheduler ->
      Observable.create<Fiber<ForObservableK, A>> { emitter ->
        if (!emitter.isDisposed) {
          val s: ReplaySubject<A> = ReplaySubject.create()
          val conn: RxDisposable = value().subscribeOn(scheduler).subscribe(s::onNext, s::onError)
          emitter.onNext(Fiber(s.k(), ObservableK {
            conn.dispose()
          }))
        }
      }.k()
    }

  override fun <A, B, C> CoroutineContext.parMapN(fa: ObservableKOf<A>, fb: ObservableKOf<B>, f: (A, B) -> C): ObservableK<C> =
    ObservableK(fa.value().zipWith(fb.value(), BiFunction(f)).subscribeOn(asScheduler()))

  override fun <A, B, C, D> CoroutineContext.parMapN(fa: ObservableKOf<A>, fb: ObservableKOf<B>, fc: ObservableKOf<C>, f: (A, B, C) -> D): ObservableK<D> =
    ObservableK(fa.value().zipWith(fb.value().zipWith(fc.value(), BiFunction<B, C, Tuple2<B, C>> { b, c -> Tuple2(b, c) }), BiFunction { a: A, tuple: Tuple2<B, C> ->
      f(a, tuple.a, tuple.b)
    }).subscribeOn(asScheduler()))

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForObservableK>): ObservableKOf<A> =
    ObservableK.cancelable(k)

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> ObservableKOf<CancelToken<ForObservableK>>): ObservableK<A> =
    ObservableK.cancelableF(k)

  override fun <A, B> CoroutineContext.racePair(fa: ObservableKOf<A>, fb: ObservableKOf<B>): ObservableK<RacePair<ForObservableK, A, B>> =
    asScheduler().let { scheduler ->
      Observable.create<RacePair<ForObservableK, A, B>> { emitter ->
        val sa = ReplaySubject.create<A>()
        val sb = ReplaySubject.create<B>()
        val dda = fa.value().subscribe(sa::onNext, sa::onError)
        val ddb = fb.value().subscribe(sb::onNext, sb::onError)
        emitter.setCancellable { dda.dispose(); ddb.dispose() }
        val ffa = Fiber(sa.k(), ObservableK { dda.dispose() })
        val ffb = Fiber(sb.k(), ObservableK { ddb.dispose() })
        sa.subscribe({
          emitter.onNext(RacePair.First(it, ffb))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
        sb.subscribe({
          emitter.onNext(RacePair.Second(ffa, it))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
      }.subscribeOn(scheduler).observeOn(Schedulers.trampoline()).k()
    }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: ObservableKOf<A>, fb: ObservableKOf<B>, fc: ObservableKOf<C>): ObservableK<RaceTriple<ForObservableK, A, B, C>> =
    asScheduler().let { scheduler ->
      Observable.create<RaceTriple<ForObservableK, A, B, C>> { emitter ->
        val sa = ReplaySubject.create<A>()
        val sb = ReplaySubject.create<B>()
        val sc = ReplaySubject.create<C>()
        val dda = fa.value().subscribe(sa::onNext, sa::onError)
        val ddb = fb.value().subscribe(sb::onNext, sb::onError)
        val ddc = fc.value().subscribe(sc::onNext, sc::onError)
        emitter.setCancellable { dda.dispose(); ddb.dispose(); ddc.dispose() }
        val ffa = Fiber(sa.k(), ObservableK { dda.dispose() })
        val ffb = Fiber(sb.k(), ObservableK { ddb.dispose() })
        val ffc = Fiber(sc.k(), ObservableK { ddc.dispose() })
        sa.subscribe({
          emitter.onNext(RaceTriple.First(it, ffb, ffc))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
        sb.subscribe({
          emitter.onNext(RaceTriple.Second(ffa, it, ffc))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
        sc.subscribe({
          emitter.onNext(RaceTriple.Third(ffa, ffb, it))
        }, { e -> emitter.tryOnError(e) }, emitter::onComplete)
      }.subscribeOn(scheduler).observeOn(Schedulers.trampoline()).k()
    }
}

fun ObservableK.Companion.concurrent(dispatchers: Dispatchers<ForObservableK>): Concurrent<ForObservableK> = object : ObservableKConcurrent {
  override fun dispatchers(): Dispatchers<ForObservableK> = dispatchers
}

@extension
interface ObservableKConcurrentEffect : ConcurrentEffect<ForObservableK>, ObservableKEffect {
  override fun <A> ObservableKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Disposable> =
    fix().runAsyncCancellable(cb)
}

fun ObservableK.Companion.monadFlat(): ObservableKMonad = monad()

fun ObservableK.Companion.monadConcat(): ObservableKMonad = object : ObservableKMonad {
  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().concatMap { f(it).fix() }
}

fun ObservableK.Companion.monadSwitch(): ObservableKMonad = object : ObservableKMonadError {
  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().switchMap { f(it).fix() }
}

fun ObservableK.Companion.monadErrorFlat(): ObservableKMonadError = monadError()

fun ObservableK.Companion.monadErrorConcat(): ObservableKMonadError = object : ObservableKMonadError {
  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().concatMap { f(it).fix() }
}

fun ObservableK.Companion.monadErrorSwitch(): ObservableKMonadError = object : ObservableKMonadError {
  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().switchMap { f(it).fix() }
}

// TODO ObservableK does not yet have a Concurrent instance
fun <A> ObservableK.Companion.fx(c: suspend AsyncSyntax<ForObservableK>.() -> A): ObservableK<A> =
  ObservableK.async().fx.async(c).fix()

@extension
interface ObservableKTimer : Timer<ForObservableK> {
  override fun sleep(duration: Duration): ObservableK<Unit> =
    ObservableK(io.reactivex.Observable.timer(duration.nanoseconds, TimeUnit.NANOSECONDS)
      .map { Unit })
}
