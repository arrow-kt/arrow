package arrow.effects.rx2.extensions

import arrow.core.Either
import arrow.effects.CancelToken
import arrow.effects.RacePair
import arrow.effects.RaceTriple
import arrow.effects.Timer
import arrow.effects.rx2.CoroutineContextRx2Scheduler.asScheduler
import arrow.effects.rx2.ForSingleK
import arrow.effects.rx2.SingleK
import arrow.effects.rx2.SingleKOf
import arrow.effects.rx2.extensions.singlek.async.async
import arrow.effects.rx2.fix
import arrow.effects.rx2.k
import arrow.effects.rx2.value
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.AsyncSyntax
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.ConcurrentEffect
import arrow.effects.typeclasses.ConnectedProcF
import arrow.effects.typeclasses.Dispatchers
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.Duration
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.Fiber
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.Proc
import arrow.effects.typeclasses.ProcF
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

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
}

@extension
interface SingleKMonad : Monad<ForSingleK> {
  override fun <A, B> SingleKOf<A>.ap(ff: SingleKOf<(A) -> B>): SingleK<B> =
    fix().ap(ff)

  override fun <A, B> SingleKOf<A>.flatMap(f: (A) -> SingleKOf<B>): SingleK<B> =
    fix().flatMap(f)

  override fun <A, B> SingleKOf<A>.map(f: (A) -> B): SingleK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: Function1<A, SingleKOf<Either<A, B>>>): SingleK<B> =
    SingleK.tailRecM(a, f)

  override fun <A> just(a: A): SingleK<A> =
    SingleK.just(a)
}

@extension
interface SingleKApplicativeError :
  ApplicativeError<ForSingleK, Throwable>,
  SingleKApplicative {
  override fun <A> raiseError(e: Throwable): SingleK<A> =
    SingleK.raiseError(e)

  override fun <A> SingleKOf<A>.handleErrorWith(f: (Throwable) -> SingleKOf<A>): SingleK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface SingleKMonadError :
  MonadError<ForSingleK, Throwable>,
  SingleKMonad {
  override fun <A> raiseError(e: Throwable): SingleK<A> =
    SingleK.raiseError(e)

  override fun <A> SingleKOf<A>.handleErrorWith(f: (Throwable) -> SingleKOf<A>): SingleK<A> =
    fix().handleErrorWith { f(it).fix() }
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
    SingleK.async { _, cb -> fa(cb) }

  override fun <A> asyncF(k: ProcF<ForSingleK, A>): SingleK<A> =
    SingleK.asyncF { _, cb -> k(cb) }

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
  override fun <A> CoroutineContext.startFiber(kind: SingleKOf<A>): SingleK<Fiber<ForSingleK, A>> =
    asScheduler().let { scheduler ->
      Single.create<Fiber<ForSingleK, A>> { emitter ->
        if (!emitter.isDisposed) {
          val s: ReplaySubject<A> = ReplaySubject.create()
          val conn: io.reactivex.disposables.Disposable = kind.value().subscribeOn(scheduler).subscribe(s::onNext, s::onError)
          emitter.onSuccess(Fiber(s.firstOrError().k(), SingleK {
            conn.dispose()
          }))
        }
      }.k()
    }

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> SingleKOf<CancelToken<ForSingleK>>): SingleK<A> =
    SingleK.asyncF { kindConnection, function ->
      k(function).map { kindConnection.push(it) }
    }

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
        }, emitter::onError)
        sb.subscribe({
          emitter.onSuccess(RacePair.Second(ffa, it))
        }, emitter::onError)
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
        }, emitter::onError)
        sb.subscribe({
          emitter.onSuccess(RaceTriple.Second(ffa, it, ffc))
        }, emitter::onError)
        sc.subscribe({
          emitter.onSuccess(RaceTriple.Third(ffa, ffb, it))
        }, emitter::onError)
      }.subscribeOn(scheduler).observeOn(Schedulers.trampoline()).k()
    }
}

fun SingleK.Companion.concurrent(dispatchers: Dispatchers<ForSingleK>): Concurrent<ForSingleK> = object : SingleKConcurrent {
  override fun dispatchers(): Dispatchers<ForSingleK> = dispatchers
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

// TODO SingleK does not yet have a Concurrent instance
fun <A> SingleK.Companion.fx(c: suspend AsyncSyntax<ForSingleK>.() -> A): SingleK<A> =
  SingleK.async().fx.async(c).fix()
