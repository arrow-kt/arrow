package arrow.fx.mtl

import arrow.core.Tuple2
import arrow.fx.Ref
import arrow.mtl.WriterT
import arrow.mtl.WriterTOf
import arrow.mtl.WriterTPartialOf
import arrow.mtl.extensions.WriterTMonadThrow
import arrow.mtl.value
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.extension
import arrow.fx.RacePair
import arrow.fx.RaceTriple
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.Dispatchers
import arrow.fx.typeclasses.Fiber
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

@extension
@undocumented
interface WriterTConcurrent<F, W> : Concurrent<WriterTPartialOf<F, W>>, WriterTAsync<F, W> {

  fun CF(): Concurrent<F>

  override fun MM(): Monoid<W>

  override fun AS(): Async<F> = CF()

  override fun dispatchers(): Dispatchers<WriterTPartialOf<F, W>> =
    CF().dispatchers() as Dispatchers<WriterTPartialOf<F, W>>

  override fun <A> WriterTOf<F, W, A>.fork(ctx: CoroutineContext): WriterT<F, W, Fiber<WriterTPartialOf<F, W>, A>> = CF().run {
    WriterT(value().fork(ctx).map {
      Tuple2(MM().empty(), fiberT(it))
    })
  }

  override fun <A, B> CoroutineContext.racePair(fa: WriterTOf<F, W, A>, fb: WriterTOf<F, W, B>): WriterT<F, W, RacePair<WriterTPartialOf<F, W>, A, B>> = CF().run {
    WriterT(racePair(fa.value(), fb.value()).map {
      when (it) {
        is RacePair.First -> Tuple2(it.winner.a, RacePair.First(it.winner.b, fiberT(it.fiberB)))
        is RacePair.Second -> Tuple2(it.winner.a, RacePair.Second(fiberT(it.fiberA), it.winner.b))
      }
    })
  }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: WriterTOf<F, W, A>, fb: WriterTOf<F, W, B>, fc: WriterTOf<F, W, C>): WriterT<F, W, RaceTriple<WriterTPartialOf<F, W>, A, B, C>> = CF().run {
    WriterT(raceTriple(fa.value(), fb.value(), fc.value()).map {
      when (it) {
        is RaceTriple.First -> Tuple2(it.winner.a, RaceTriple.First(it.winner.b, fiberT(it.fiberB), fiberT(it.fiberC)))
        is RaceTriple.Second -> Tuple2(it.winner.a, RaceTriple.Second(fiberT(it.fiberA), it.winner.b, fiberT(it.fiberC)))
        is RaceTriple.Third -> Tuple2(it.winner.a, RaceTriple.Third(fiberT(it.fiberA), fiberT(it.fiberB), it.winner.b))
      }
    })
  }

  fun <A> fiberT(fiber: Fiber<F, Tuple2<W, A>>): Fiber<WriterTPartialOf<F, W>, A> =
    Fiber(WriterT(fiber.join()), WriterT.liftF(fiber.cancel(), MM(), CF()))
}
