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
import arrow.fx.reaktive.ForMaybeK
import arrow.fx.reaktive.MaybeK
import arrow.fx.reaktive.MaybeKOf
import arrow.fx.reaktive.connectAndGetDisposable
import arrow.fx.reaktive.extensions.maybek.dispatchers.dispatchers
import arrow.fx.reaktive.fix
import arrow.fx.reaktive.k
import arrow.fx.reaktive.unsafeRunAsync
import arrow.fx.reaktive.unsafeRunSync
import arrow.fx.reaktive.value
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
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.maybe.map
import com.badoo.reaktive.maybe.maybe
import com.badoo.reaktive.maybe.maybeOfEmpty
import com.badoo.reaktive.maybe.maybeTimer
import com.badoo.reaktive.maybe.subscribeOn
import com.badoo.reaktive.maybe.zipWith
import com.badoo.reaktive.observable.ConnectableObservable
import com.badoo.reaktive.observable.firstOrComplete
import com.badoo.reaktive.observable.replay
import com.badoo.reaktive.scheduler.computationScheduler
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import arrow.fx.reaktive.handleErrorWith as maybeHandleErrorWith
import com.badoo.reaktive.disposable.Disposable as RxDisposable

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

  override fun <A, B> Kind<ForMaybeK, A>.lazyAp(ff: () -> Kind<ForMaybeK, (A) -> B>): Kind<ForMaybeK, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }
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

  override fun <A, B> Kind<ForMaybeK, A>.lazyAp(ff: () -> Kind<ForMaybeK, (A) -> B>): Kind<ForMaybeK, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }
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

private val fiberFactory: FiberFactory<ForMaybeK> =
  object : FiberFactory<ForMaybeK> {
    override fun <A> invoke(join: Maybe<A>, disposable: RxDisposable): Fiber<ForMaybeK, A> =
      Fiber(join = join.k(), cancel = MaybeK(disposable::dispose))
  }

interface MaybeKConcurrent : Concurrent<ForMaybeK>, MaybeKAsync {
  override fun <A> Kind<ForMaybeK, A>.fork(ctx: CoroutineContext): MaybeK<Fiber<ForMaybeK, A>> =
    ctx.asScheduler().let { scheduler ->
      maybe<Fiber<ForMaybeK, A>> { emitter ->
        if (!emitter.isDisposed) {
          val observable: ConnectableObservable<A> = value().subscribeOn(scheduler).asObservable().replay(bufferSize = 1)
          val disposable = observable.connectAndGetDisposable()
          val fiber = Fiber(observable.firstOrComplete().k(), MaybeK(disposable::dispose))
          if (!emitter.isDisposed) {
            // Race condition here: the chain can be disposed before the fiber reaches its listener
            emitter.onSuccess(fiber)
          } else {
            disposable.dispose()
          }
        }
      }.k()
    }

  override fun <A, B, C> CoroutineContext.parMapN(fa: MaybeKOf<A>, fb: MaybeKOf<B>, f: (A, B) -> C): MaybeK<C> =
    MaybeK(fa.value().zipWith(fb.value(), f).subscribeOn(asScheduler()))

  override fun <A, B, C, D> CoroutineContext.parMapN(fa: MaybeKOf<A>, fb: MaybeKOf<B>, fc: MaybeKOf<C>, f: (A, B, C) -> D): MaybeK<D> =
    MaybeK(
      fa
        .value()
        .zipWith(fb.value().zipWith(fc.value()) { b, c -> Tuple2(b, c) }) { a: A, tuple: Tuple2<B, C> ->
          f(a, tuple.a, tuple.b)
        }
        .subscribeOn(asScheduler())
    )

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForMaybeK>): MaybeK<A> =
    MaybeK.cancelable(k)

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> MaybeKOf<CancelToken<ForMaybeK>>): MaybeK<A> =
    MaybeK.cancelableF(k)

  override fun <A, B> CoroutineContext.racePair(fa: Kind<ForMaybeK, A>, fb: Kind<ForMaybeK, B>): Kind<ForMaybeK, RacePair<ForMaybeK, A, B>> =
    racePair(fa.value(), fb.value(), fiberFactory).k()

  override fun <A, B, C> CoroutineContext.raceTriple(fa: MaybeKOf<A>, fb: MaybeKOf<B>, fc: MaybeKOf<C>): MaybeK<RaceTriple<ForMaybeK, A, B, C>> =
    raceTriple(fa.value(), fb.value(), fc.value(), fiberFactory).k()
}

fun MaybeK.Companion.concurrent(dispatchers: Dispatchers<ForMaybeK> = MaybeK.dispatchers()): Concurrent<ForMaybeK> = object : MaybeKConcurrent {
  override fun dispatchers(): Dispatchers<ForMaybeK> = dispatchers
}

@extension
interface MaybeKUnsafeRun : UnsafeRun<ForMaybeK> {
  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<ForMaybeK, A>): A = fa().fix().unsafeRunSync() as A

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<ForMaybeK, A>, cb: (Either<Throwable, A>) -> Unit): Unit =
    fa().fix().unsafeRunAsync(cb)
}

@extension
interface MaybeKDispatchers : Dispatchers<ForMaybeK> {
  override fun default(): CoroutineContext =
    arrow.fx.reaktive.extensions.computationSchedulerContext

  override fun io(): CoroutineContext =
    ioSchedulerContext
}

@extension
interface MaybeKTimer : Timer<ForMaybeK> {
  override fun sleep(duration: Duration): MaybeK<Unit> =
    MaybeK(maybeTimer(TimeUnit.NANOSECONDS.toMillis(duration.nanoseconds), computationScheduler)
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
    maybeOfEmpty<A>().k()

  override fun <A, B> Kind<ForMaybeK, A>.filterMap(f: (A) -> Option<B>): MaybeK<B> =
    fix().filterMap(f)
}

fun <A> MaybeK.Companion.fx(c: suspend ConcurrentSyntax<ForMaybeK>.() -> A): MaybeK<A> =
  MaybeK.concurrent().fx.concurrent(c).fix()
