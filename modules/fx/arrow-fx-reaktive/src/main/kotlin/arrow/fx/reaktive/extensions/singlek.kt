package arrow.fx.reaktive.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.extension
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Timer
import arrow.fx.reaktive.ForSingleK
import arrow.fx.reaktive.SingleK
import arrow.fx.reaktive.SingleKOf
import arrow.fx.reaktive.connectAndGetDisposable
import arrow.fx.reaktive.extensions.singlek.dispatchers.dispatchers
import arrow.fx.reaktive.fix
import arrow.fx.reaktive.k
import arrow.fx.reaktive.unsafeRunAsync
import arrow.fx.reaktive.unsafeRunSync
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
import arrow.fx.typeclasses.UnsafeRun
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import arrow.unsafe
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asSingleOrError
import com.badoo.reaktive.observable.ConnectableObservable
import com.badoo.reaktive.observable.firstOrError
import com.badoo.reaktive.observable.replay
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.single.asMaybe
import com.badoo.reaktive.single.asObservable
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.single
import com.badoo.reaktive.single.singleTimer
import com.badoo.reaktive.single.subscribeOn
import com.badoo.reaktive.single.zipWith
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import arrow.fx.reaktive.handleErrorWith as singleHandleErrorWith
import com.badoo.reaktive.disposable.Disposable as RxDisposable

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

private val fiberFactory =
  object : FiberFactory<ForSingleK> {
    override fun <A> invoke(join: Maybe<A>, disposable: RxDisposable): Fiber<ForSingleK, A> =
      Fiber(join = join.asSingleOrError().k(), cancel = SingleK(disposable::dispose))
  }

interface SingleKConcurrent : Concurrent<ForSingleK>, SingleKAsync {
  override fun <A> Kind<ForSingleK, A>.fork(ctx: CoroutineContext): SingleK<Fiber<ForSingleK, A>> =
    ctx.asScheduler().let { scheduler ->
      single<Fiber<ForSingleK, A>> { emitter ->
        if (!emitter.isDisposed) {
          val observable: ConnectableObservable<A> = value().subscribeOn(scheduler).asObservable().replay(bufferSize = 1)
          val disposable = observable.connectAndGetDisposable()
          val fiber = Fiber(observable.firstOrError().k(), SingleK(disposable::dispose))
          if (!emitter.isDisposed) {
            // Race condition here: the chain can be disposed before the fiber reaches its listener
            emitter.onSuccess(fiber)
          } else {
            disposable.dispose()
          }
        }
      }.k()
    }

  override fun <A, B, C> CoroutineContext.parMapN(fa: SingleKOf<A>, fb: SingleKOf<B>, f: (A, B) -> C): SingleK<C> =
    SingleK(fa.value().zipWith(fb.value(), f).subscribeOn(asScheduler()))

  override fun <A, B, C, D> CoroutineContext.parMapN(fa: SingleKOf<A>, fb: SingleKOf<B>, fc: SingleKOf<C>, f: (A, B, C) -> D): SingleK<D> =
    SingleK(
      fa
        .value()
        .zipWith(fb.value().zipWith(fc.value()) { b, c -> Tuple2(b, c) }) { a: A, tuple: Tuple2<B, C> ->
          f(a, tuple.a, tuple.b)
        }
        .subscribeOn(asScheduler())
    )

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForSingleK>): SingleK<A> =
    SingleK.cancelable(k)

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> SingleKOf<CancelToken<ForSingleK>>): SingleK<A> =
    SingleK.cancelableF(k)

  override fun <A, B> CoroutineContext.racePair(fa: SingleKOf<A>, fb: SingleKOf<B>): SingleK<RacePair<ForSingleK, A, B>> =
    racePair(
      fa.value().asMaybe(),
      fb.value().asMaybe(),
      fiberFactory
    ).asSingleOrError().k()

  override fun <A, B, C> CoroutineContext.raceTriple(fa: SingleKOf<A>, fb: SingleKOf<B>, fc: SingleKOf<C>): SingleK<RaceTriple<ForSingleK, A, B, C>> =
    raceTriple(
      fa.value().asMaybe(),
      fb.value().asMaybe(),
      fc.value().asMaybe(),
      fiberFactory
    ).asSingleOrError().k()
}

fun SingleK.Companion.concurrent(dispatchers: Dispatchers<ForSingleK> = SingleK.dispatchers()): Concurrent<ForSingleK> = object : SingleKConcurrent {
  override fun dispatchers(): Dispatchers<ForSingleK> = dispatchers
}

@extension
interface SingleKDispatchers : Dispatchers<ForSingleK> {
  override fun default(): CoroutineContext =
    arrow.fx.reaktive.extensions.computationSchedulerContext

  override fun io(): CoroutineContext =
    ioSchedulerContext
}

@extension
interface SingleKConcurrentEffect : ConcurrentEffect<ForSingleK>, SingleKEffect {
  override fun <A> SingleKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> SingleKOf<Unit>): SingleK<Disposable> =
    fix().runAsyncCancellable(cb)
}

@extension
interface SingleKTimer : Timer<ForSingleK> {
  override fun sleep(duration: Duration): SingleK<Unit> =
    SingleK(singleTimer(TimeUnit.NANOSECONDS.toMillis(duration.nanoseconds), computationScheduler)
      .map { Unit })
}

@extension
interface SingleKUnsafeRun : UnsafeRun<ForSingleK> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<ForSingleK, A>): A = fa().fix().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<ForSingleK, A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().unsafeRunAsync(cb)
}

fun <A> SingleK.Companion.fx(c: suspend ConcurrentSyntax<ForSingleK>.() -> A): SingleK<A> =
  SingleK.concurrent().fx.concurrent(c).fix()
