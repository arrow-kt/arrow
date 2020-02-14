package arrow.fx.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.extension
import arrow.fx.IO
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Ref
import arrow.fx.Timer
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.Dispatchers
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.MonadIO
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.mtl.WriterT
import arrow.mtl.WriterTOf
import arrow.mtl.WriterTPartialOf
import arrow.mtl.extensions.WriterTMonad
import arrow.mtl.extensions.WriterTMonadThrow
import arrow.mtl.value
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface WriterTBracket<F, W> : Bracket<WriterTPartialOf<F, W>, Throwable>, WriterTMonadThrow<F, W> {

  fun MD(): MonadDefer<F>

  override fun MM(): Monoid<W>

  override fun ME(): MonadError<F, Throwable> = MD()

  override fun <A, B> WriterTOf<F, W, A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> WriterTOf<F, W, Unit>,
    use: (A) -> WriterTOf<F, W, B>
  ): WriterT<F, W, B> = MM().run {
    MD().run {
      WriterT(Ref(this, empty()).flatMap { ref ->
        value().bracketCase(use = { wa ->
          WriterT(wa.just()).flatMap(use).value()
        }, release = { wa, exitCase ->
          val r = release(wa.b, exitCase).value()
          when (exitCase) {
            is ExitCase.Completed -> r.flatMap { (l, _) -> ref.set(l) }
            else -> r.unit()
          }
        }).flatMap { (w, b) ->
          ref.get().map { ww -> Tuple2(w.combine(ww), b) }
        }
      })
    }
  }
}

@extension
@undocumented
interface WriterTMonadDefer<F, W> : MonadDefer<WriterTPartialOf<F, W>>, WriterTBracket<F, W> {

  override fun MD(): MonadDefer<F>

  override fun MM(): Monoid<W>

  override fun <A> defer(fa: () -> WriterTOf<F, W, A>): WriterTOf<F, W, A> =
    WriterT(MD().defer { fa().value() })
}

@extension
@undocumented
interface WriterTAsync<F, W> : Async<WriterTPartialOf<F, W>>, WriterTMonadDefer<F, W> {

  fun AS(): Async<F>

  override fun MM(): Monoid<W>

  override fun MD(): MonadDefer<F> = AS()

  override fun <A> async(fa: Proc<A>): WriterT<F, W, A> = AS().run {
    WriterT.liftF(async(fa), MM(), this)
  }

  override fun <A> asyncF(k: ProcF<WriterTPartialOf<F, W>, A>): WriterTOf<F, W, A> = AS().run {
    WriterT.liftF(asyncF { cb -> k(cb).value().unit() }, MM(), this)
  }

  override fun <A> WriterTOf<F, W, A>.continueOn(ctx: CoroutineContext): WriterT<F, W, A> = AS().run {
    WriterT(value().continueOn(ctx))
  }
}

interface WriterTConcurrent<F, W> : Concurrent<WriterTPartialOf<F, W>>, WriterTAsync<F, W> {

  fun CF(): Concurrent<F>

  override fun MM(): Monoid<W>

  override fun AS(): Async<F> = CF()

  override fun dispatchers(): Dispatchers<WriterTPartialOf<F, W>> =
    CF().dispatchers() as Dispatchers<WriterTPartialOf<F, W>>

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<WriterTPartialOf<F, W>>): WriterT<F, W, A> = CF().run {
    WriterT.liftF(cancelable { cb -> k(cb).value().unit() }, MM(), this)
  }

  override fun <A> WriterTOf<F, W, A>.fork(ctx: CoroutineContext): WriterT<F, W, Fiber<WriterTPartialOf<F, W>, A>> = CF().run {
    val fork: Kind<F, Tuple2<W, Fiber<WriterTPartialOf<F, W>, A>>> = value().fork(ctx).map { fiber: Fiber<F, Tuple2<W, A>> ->
      Tuple2(MM().empty(), fiberT(fiber))
    }
    WriterT(fork)
  }

  override fun <A, B> CoroutineContext.racePair(fa: WriterTOf<F, W, A>, fb: WriterTOf<F, W, B>): WriterT<F, W, RacePair<WriterTPartialOf<F, W>, A, B>> = CF().run {
    val racePair: Kind<F, Tuple2<W, RacePair<WriterTPartialOf<F, W>, A, B>>> = racePair(fa.value(), fb.value()).map { res: RacePair<F, Tuple2<W, A>, Tuple2<W, B>> ->
      when (res) {
        is RacePair.First -> Tuple2(res.winner.a, RacePair.First(res.winner.b, fiberT(res.fiberB)))
        is RacePair.Second -> Tuple2(res.winner.a, RacePair.Second(fiberT(res.fiberA), res.winner.b))
      }
    }
    WriterT(racePair)
  }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: WriterTOf<F, W, A>, fb: WriterTOf<F, W, B>, fc: WriterTOf<F, W, C>): WriterT<F, W, RaceTriple<WriterTPartialOf<F, W>, A, B, C>> = CF().run {
    val raceTriple: Kind<F, Tuple2<W, RaceTriple<WriterTPartialOf<F, W>, A, B, C>>> = raceTriple(fa.value(), fb.value(), fc.value()).map { res: RaceTriple<F, Tuple2<W, A>, Tuple2<W, B>, Tuple2<W, C>> ->
      when (res) {
        is RaceTriple.First -> Tuple2(res.winner.a, RaceTriple.First(res.winner.b, fiberT(res.fiberB), fiberT(res.fiberC)))
        is RaceTriple.Second -> Tuple2(res.winner.a, RaceTriple.Second(fiberT(res.fiberA), res.winner.b, fiberT(res.fiberC)))
        is RaceTriple.Third -> Tuple2(res.winner.a, RaceTriple.Third(fiberT(res.fiberA), fiberT(res.fiberB), res.winner.b))
      }
    }
    WriterT(raceTriple)
  }

  fun <A> fiberT(fiber: Fiber<F, Tuple2<W, A>>): Fiber<WriterTPartialOf<F, W>, A> =
    Fiber(WriterT(fiber.join()), WriterT.liftF(fiber.cancel(), MM(), CF()))
}

fun <F, W> WriterT.Companion.concurrent(CF: Concurrent<F>, MM: Monoid<W>): Concurrent<WriterTPartialOf<F, W>> =
  object : WriterTConcurrent<F, W> {
    override fun CF(): Concurrent<F> = CF
    override fun MM(): Monoid<W> = MM
  }

fun <F, W> WriterT.Companion.timer(CF: Concurrent<F>, MM: Monoid<W>): Timer<WriterTPartialOf<F, W>> =
  Timer(concurrent(CF, MM))

@extension
interface WriterTMonadIO<F, W> : MonadIO<WriterTPartialOf<F, W>>, WriterTMonad<F, W> {
  fun FIO(): MonadIO<F>
  override fun MF(): Monad<F> = FIO()
  override fun MM(): Monoid<W>
  override fun <A> IO<A>.liftIO(): Kind<WriterTPartialOf<F, W>, A> = FIO().run {
    WriterT.liftF(liftIO(), MM(), this)
  }
}
