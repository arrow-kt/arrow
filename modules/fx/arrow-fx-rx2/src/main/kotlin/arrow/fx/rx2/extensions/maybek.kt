package arrow.fx.rx2.extensions

import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.fx.CancelToken
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Timer
import arrow.fx.rx2.CoroutineContextRx2Scheduler.asScheduler
import arrow.fx.rx2.ForMaybeK
import arrow.fx.rx2.MaybeK
import arrow.fx.rx2.MaybeKOf
import arrow.fx.rx2.extensions.maybek.async.async
import arrow.fx.rx2.fix
import arrow.fx.rx2.k
import arrow.fx.rx2.value
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.AsyncSyntax
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.Dispatchers
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
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

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
}

@extension
interface MaybeKMonad : Monad<ForMaybeK> {
  override fun <A, B> MaybeKOf<A>.ap(ff: MaybeKOf<(A) -> B>): MaybeK<B> =
    fix().ap(ff)

  override fun <A, B> MaybeKOf<A>.flatMap(f: (A) -> MaybeKOf<B>): MaybeK<B> =
    fix().flatMap(f)

  override fun <A, B> MaybeKOf<A>.map(f: (A) -> B): MaybeK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> MaybeKOf<Either<A, B>>): MaybeK<B> =
    MaybeK.tailRecM(a, f)

  override fun <A> just(a: A): MaybeK<A> =
    MaybeK.just(a)
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
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface MaybeKMonadError :
  MonadError<ForMaybeK, Throwable>,
  MaybeKMonad {
  override fun <A> raiseError(e: Throwable): MaybeK<A> =
    MaybeK.raiseError(e)

  override fun <A> MaybeKOf<A>.handleErrorWith(f: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface MaybeKBracket : Bracket<ForMaybeK, Throwable>, MaybeKMonadError {
  override fun <A, B> MaybeKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> MaybeKOf<Unit>, use: (A) -> MaybeKOf<B>): MaybeK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface MaybeKMonadDefer : MonadDefer<ForMaybeK, Throwable>, MaybeKBracket {
  override fun <A> defer(fa: () -> MaybeKOf<A>): MaybeK<A> =
    MaybeK.defer(fa)
}

@extension
interface MaybeKAsync : Async<ForMaybeK, Throwable>, MaybeKMonadDefer {
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

interface MaybeKConcurrent : Concurrent<ForMaybeK, Throwable>, MaybeKAsync {
  override fun <A> CoroutineContext.startFiber(kind: MaybeKOf<A>): MaybeK<Fiber<ForMaybeK, A>> =
    asScheduler().let { scheduler ->
      Maybe.create<Fiber<ForMaybeK, A>> { emitter ->
        if (!emitter.isDisposed) {
          val s: ReplaySubject<A> = ReplaySubject.create()
          val conn: Disposable = kind.value().subscribeOn(scheduler).subscribe(s::onNext, s::onError)
          emitter.onSuccess(Fiber(s.firstElement().k(), MaybeK {
            conn.dispose()
          }))
        }
      }.k()
    }

  override fun <A, B, C> CoroutineContext.parMapN(fa: MaybeKOf<A>, fb: MaybeKOf<B>, f: (A, B) -> C): MaybeK<C> =
    MaybeK(fa.value().zipWith(fb.value(), BiFunction(f)).subscribeOn(asScheduler()))

  override fun <A, B, C, D> CoroutineContext.parMapN(fa: MaybeKOf<A>, fb: MaybeKOf<B>, fc: MaybeKOf<C>, f: (A, B, C) -> D): MaybeK<D> =
    MaybeK(fa.value().zipWith(fb.value().zipWith(fc.value(), BiFunction<B, C, Tuple2<B, C>> { b, c -> Tuple2(b, c) }), BiFunction { a: A, tuple: Tuple2<B, C> ->
      f(a, tuple.a, tuple.b)
    }).subscribeOn(asScheduler()))

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<ForMaybeK>): MaybeK<A> =
    MaybeK.cancelable(k)

  override fun <A> cancelableF(k: ((Either<Throwable, A>) -> Unit) -> MaybeKOf<CancelToken<ForMaybeK>>): MaybeK<A> =
    MaybeK.cancelableF(k)

  override fun <A, B> CoroutineContext.racePair(fa: MaybeKOf<A>, fb: MaybeKOf<B>): MaybeK<RacePair<ForMaybeK, A, B>> =
    asScheduler().let { scheduler ->
      Maybe.create<RacePair<ForMaybeK, A, B>> { emitter ->
        val sa = ReplaySubject.create<A>()
        val sb = ReplaySubject.create<B>()
        val dda = fa.value().subscribe(sa::onNext, sa::onError)
        val ddb = fb.value().subscribe(sb::onNext, sb::onError)
        emitter.setCancellable { dda.dispose(); ddb.dispose() }
        val ffa = Fiber(sa.firstElement().k(), MaybeK { dda.dispose() })
        val ffb = Fiber(sb.firstElement().k(), MaybeK { ddb.dispose() })
        sa.subscribe({
          emitter.onSuccess(RacePair.First(it, ffb))
        }, emitter::onError, emitter::onComplete)
        sb.subscribe({
          emitter.onSuccess(RacePair.Second(ffa, it))
        }, emitter::onError, emitter::onComplete)
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
        emitter.setCancellable { dda.dispose(); ddb.dispose(); ddc.dispose() }
        val ffa = Fiber(sa.firstElement().k(), MaybeK { dda.dispose() })
        val ffb = Fiber(sb.firstElement().k(), MaybeK { ddb.dispose() })
        val ffc = Fiber(sc.firstElement().k(), MaybeK { ddc.dispose() })
        sa.subscribe({
          emitter.onSuccess(RaceTriple.First(it, ffb, ffc))
        }, emitter::onError, emitter::onComplete)
        sb.subscribe({
          emitter.onSuccess(RaceTriple.Second(ffa, it, ffc))
        }, emitter::onError, emitter::onComplete)
        sc.subscribe({
          emitter.onSuccess(RaceTriple.Third(ffa, ffb, it))
        }, emitter::onError, emitter::onComplete)
      }.subscribeOn(scheduler).observeOn(Schedulers.trampoline()).k()
    }
}

fun MaybeK.Companion.concurrent(dispatchers: Dispatchers<ForMaybeK>): Concurrent<ForMaybeK, Throwable> = object : MaybeKConcurrent {
  override fun dispatchers(): Dispatchers<ForMaybeK> = dispatchers
}

@extension
interface MaybeKTimer : Timer<ForMaybeK> {
  override fun sleep(duration: Duration): MaybeK<Unit> =
    MaybeK(Maybe.timer(duration.nanoseconds, TimeUnit.NANOSECONDS)
      .map { Unit })
}

// TODO MaybeK does not yet have a Concurrent instance
fun <A> MaybeK.Companion.fx(c: suspend AsyncSyntax<ForMaybeK, Throwable>.() -> A): MaybeK<A> =
  MaybeK.async().fx.async(c).fix()
