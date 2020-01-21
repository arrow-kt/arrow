package arrow.fx.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.extension
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.Ref
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.Dispatchers
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.mtl.WriterT
import arrow.mtl.WriterTOf
import arrow.mtl.WriterTPartialOf
import arrow.mtl.extensions.WriterTMonadThrow
import arrow.mtl.value
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface WriterTBracket<W, F> : Bracket<WriterTPartialOf<W, F>, Throwable>, WriterTMonadThrow<W, F> {

  fun MD(): MonadDefer<F>

  override fun MM(): Monoid<W>

  override fun ME(): MonadError<F, Throwable> = MD()

  override fun <A, B> WriterTOf<W, F, A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> WriterTOf<W, F, Unit>,
    use: (A) -> WriterTOf<W, F, B>
  ): WriterT<W, F, B> = MM().run {
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
interface WriterTMonadDefer<W, F> : MonadDefer<WriterTPartialOf<W, F>>, WriterTBracket<W, F> {

  override fun MD(): MonadDefer<F>

  override fun MM(): Monoid<W>

  override fun <A> defer(fa: () -> WriterTOf<W, F, A>): WriterTOf<W, F, A> =
    WriterT(MD().defer { fa().value() })
}

@extension
@undocumented
interface WriterTAsync<W, F> : Async<WriterTPartialOf<W, F>>, WriterTMonadDefer<W, F> {

  fun AS(): Async<F>

  override fun MM(): Monoid<W>

  override fun MD(): MonadDefer<F> = AS()

  override fun <A> async(fa: Proc<A>): WriterT<W, F, A> = AS().run {
    WriterT.liftF(async(fa), MM(), this)
  }

  override fun <A> asyncF(k: ProcF<WriterTPartialOf<W, F>, A>): WriterTOf<W, F, A> = AS().run {
    WriterT.liftF(asyncF { cb -> k(cb).value().unit() }, MM(), this)
  }

  override fun <A> WriterTOf<W, F, A>.continueOn(ctx: CoroutineContext): WriterT<W, F, A> = AS().run {
    WriterT(value().continueOn(ctx))
  }
}

interface WriterTConcurrent<W, F> : Concurrent<WriterTPartialOf<W, F>>, WriterTAsync<W, F> {

  fun CF(): Concurrent<F>

  override fun MM(): Monoid<W>

  override fun AS(): Async<F> = CF()

  override fun dispatchers(): Dispatchers<WriterTPartialOf<W, F>> =
    CF().dispatchers() as Dispatchers<WriterTPartialOf<W, F>>

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<WriterTPartialOf<W, F>>): WriterT<W, F, A> = CF().run {
    WriterT.liftF(cancelable { cb -> k(cb).value().unit() }, MM(), this)
  }

  override fun <A> WriterTOf<W, F, A>.fork(ctx: CoroutineContext): WriterT<W, F, Fiber<WriterTPartialOf<W, F>, A>> = CF().run {
    val fork: Kind<F, Tuple2<W, Fiber<WriterTPartialOf<W, F>, A>>> = value().fork(ctx).map { fiber: Fiber<F, Tuple2<W, A>> ->
      Tuple2(MM().empty(), fiberT(fiber))
    }
    WriterT(fork)
  }

  override fun <A, B> CoroutineContext.racePair(fa: WriterTOf<W, F, A>, fb: WriterTOf<W, F, B>): WriterT<W, F, RacePair<WriterTPartialOf<W, F>, A, B>> = CF().run {
    val racePair: Kind<F, Tuple2<W, RacePair<WriterTPartialOf<W, F>, A, B>>> = racePair(fa.value(), fb.value()).map { res: RacePair<F, Tuple2<W, A>, Tuple2<W, B>> ->
      when (res) {
        is RacePair.First -> Tuple2(res.winner.a, RacePair.First(res.winner.b, fiberT(res.fiberB)))
        is RacePair.Second -> Tuple2(res.winner.a, RacePair.Second(fiberT(res.fiberA), res.winner.b))
      }
    }
    WriterT(racePair)
  }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: WriterTOf<W, F, A>, fb: WriterTOf<W, F, B>, fc: WriterTOf<W, F, C>): WriterT<W, F, RaceTriple<WriterTPartialOf<W, F>, A, B, C>> = CF().run {
    val raceTriple: Kind<F, Tuple2<W, RaceTriple<WriterTPartialOf<W, F>, A, B, C>>> = raceTriple(fa.value(), fb.value(), fc.value()).map { res: RaceTriple<F, Tuple2<W, A>, Tuple2<W, B>, Tuple2<W, C>> ->
      when (res) {
        is RaceTriple.First -> Tuple2(res.winner.a, RaceTriple.First(res.winner.b, fiberT(res.fiberB), fiberT(res.fiberC)))
        is RaceTriple.Second -> Tuple2(res.winner.a, RaceTriple.Second(fiberT(res.fiberA), res.winner.b, fiberT(res.fiberC)))
        is RaceTriple.Third -> Tuple2(res.winner.a, RaceTriple.Third(fiberT(res.fiberA), fiberT(res.fiberB), res.winner.b))
      }
    }
    WriterT(raceTriple)
  }

  fun <A> fiberT(fiber: Fiber<F, Tuple2<W, A>>): Fiber<WriterTPartialOf<W, F>, A> =
    Fiber(WriterT(fiber.join()), WriterT.liftF(fiber.cancel(), MM(), CF()))
}

fun <W, F> WriterT.Companion.concurrent(CF: Concurrent<F>, MM: Monoid<W>): Concurrent<WriterTPartialOf<W, F>> =
  object : WriterTConcurrent<W, F> {
    override fun CF(): Concurrent<F> = CF
    override fun MM(): Monoid<W> = MM
  }
