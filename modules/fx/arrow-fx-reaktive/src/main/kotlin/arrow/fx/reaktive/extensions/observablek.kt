package arrow.fx.reaktive.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Option
import arrow.core.Tuple2
import arrow.extension
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Timer
import arrow.fx.reaktive.ForObservableK
import arrow.fx.reaktive.ObservableK
import arrow.fx.reaktive.ObservableKOf
import arrow.fx.reaktive.connectAndGetDisposable
import arrow.fx.reaktive.extensions.observablek.dispatchers.dispatchers
import arrow.fx.reaktive.extensions.observablek.monad.monad
import arrow.fx.reaktive.extensions.observablek.monadError.monadError
import arrow.fx.reaktive.fix
import arrow.fx.reaktive.k
import arrow.fx.reaktive.value
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.ConcurrentEffect
import arrow.fx.typeclasses.ConcurrentSyntax
import arrow.fx.typeclasses.Dispatchers
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Effect
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.observable.ConnectableObservable
import com.badoo.reaktive.observable.firstOrComplete
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.observable.observableTimer
import com.badoo.reaktive.observable.replay
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.observable.zipWith
import com.badoo.reaktive.scheduler.computationScheduler
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import arrow.fx.reaktive.handleErrorWith as observableHandleErrorWith
import com.badoo.reaktive.disposable.Disposable as RxDisposable

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

  override fun <A, B> Kind<ForObservableK, A>.lazyAp(ff: () -> Kind<ForObservableK, (A) -> B>): Kind<ForObservableK, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }
}

@extension
interface ObservableKMonad : Monad<ForObservableK>, ObservableKApplicative {
  override fun <A, B> ObservableKOf<A>.ap(ff: ObservableKOf<(A) -> B>): ObservableK<B> =
    fix().ap(ff)

  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().concatMap(f)

  override fun <A, B> ObservableKOf<A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> ObservableKOf<Either<A, B>>): ObservableK<B> =
    ObservableK.tailRecM(a, f)

  override fun <A, B> Kind<ForObservableK, A>.lazyAp(ff: () -> Kind<ForObservableK, (A) -> B>): Kind<ForObservableK, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }
}

@extension
interface ObservableKFoldable : Foldable<ForObservableK> {
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
    fix().observableHandleErrorWith { f(it).fix() }
}

@extension
interface ObservableKMonadError :
  MonadError<ForObservableK, Throwable>,
  ObservableKMonad {
  override fun <A> raiseError(e: Throwable): ObservableK<A> =
    ObservableK.raiseError(e)

  override fun <A> ObservableKOf<A>.handleErrorWith(f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
    fix().observableHandleErrorWith { f(it).fix() }
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

private val fiberFactory =
  object : FiberFactory<ForObservableK> {
    override fun <A> invoke(join: Maybe<A>, disposable: RxDisposable): Fiber<ForObservableK, A> =
      Fiber(join = join.asObservable().k(), cancel = ObservableK(disposable::dispose))
  }

interface ObservableKConcurrent : Concurrent<ForObservableK>, ObservableKAsync {
  override fun <A> Kind<ForObservableK, A>.fork(ctx: CoroutineContext): ObservableK<Fiber<ForObservableK, A>> =
    ctx.asScheduler().let { scheduler ->
      observable<Fiber<ForObservableK, A>> { emitter ->
        if (!emitter.isDisposed) {
          val observable: ConnectableObservable<A> = value().subscribeOn(scheduler).replay()
          val disposable = observable.connectAndGetDisposable()
          val fiber = Fiber(observable.k(), ObservableK(disposable::dispose))
          if (!emitter.isDisposed) {
            // Race condition here: the chain can be disposed before the fiber reaches its listener
            emitter.onNext(fiber)
          } else {
            disposable.dispose()
          }
        }
      }.k()
    }

  override fun <A, B, C> CoroutineContext.parMapN(fa: ObservableKOf<A>, fb: ObservableKOf<B>, f: (A, B) -> C): ObservableK<C> =
    ObservableK(fa.value().zipWith(fb.value(), f).subscribeOn(asScheduler()))

  override fun <A, B, C, D> CoroutineContext.parMapN(fa: ObservableKOf<A>, fb: ObservableKOf<B>, fc: ObservableKOf<C>, f: (A, B, C) -> D): ObservableK<D> =
    ObservableK(
      fa
        .value()
        .zipWith(fb.value().zipWith(fc.value()) { b, c -> Tuple2(b, c) }) { a: A, tuple: Tuple2<B, C> ->
          f(a, tuple.a, tuple.b)
        }
        .subscribeOn(asScheduler())
    )

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForObservableK>): ObservableKOf<A> =
    ObservableK.cancelable(k)

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> ObservableKOf<CancelToken<ForObservableK>>): ObservableK<A> =
    ObservableK.cancelableF(k)

  override fun <A, B> CoroutineContext.racePair(fa: ObservableKOf<A>, fb: ObservableKOf<B>): ObservableK<RacePair<ForObservableK, A, B>> =
    racePair(
      fa.value().firstOrComplete(),
      fb.value().firstOrComplete(),
      fiberFactory
    ).asObservable().k()

  override fun <A, B, C> CoroutineContext.raceTriple(fa: ObservableKOf<A>, fb: ObservableKOf<B>, fc: ObservableKOf<C>): ObservableK<RaceTriple<ForObservableK, A, B, C>> =
    raceTriple(
      fa.value().firstOrComplete(),
      fb.value().firstOrComplete(),
      fc.value().firstOrComplete(),
      fiberFactory
    ).asObservable().k()
}

@extension
interface ObservableKDispatchers : Dispatchers<ForObservableK> {
  override fun default(): CoroutineContext =
    arrow.fx.reaktive.extensions.computationSchedulerContext

  override fun io(): CoroutineContext =
    ioSchedulerContext
}

fun ObservableK.Companion.concurrent(dispatchers: Dispatchers<ForObservableK> = ObservableK.dispatchers()): Concurrent<ForObservableK> = object : ObservableKConcurrent {
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

fun <A> ObservableK.Companion.fx(c: suspend ConcurrentSyntax<ForObservableK>.() -> A): ObservableK<A> =
  ObservableK.concurrent().fx.concurrent(c).fix()

@extension
interface ObservableKTimer : Timer<ForObservableK> {
  override fun sleep(duration: Duration): ObservableK<Unit> =
    ObservableK(observableTimer(TimeUnit.NANOSECONDS.toMillis(duration.nanoseconds), computationScheduler) // TODO: Is computation scheduler ok?
      .map { Unit })
}

@extension
interface ObservableKFunctorFilter : FunctorFilter<ForObservableK>, ObservableKFunctor {
  override fun <A, B> Kind<ForObservableK, A>.filterMap(f: (A) -> Option<B>): ObservableK<B> =
    fix().filterMap(f)
}
