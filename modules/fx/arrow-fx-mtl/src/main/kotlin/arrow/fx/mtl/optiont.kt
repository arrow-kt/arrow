package arrow.fx.mtl

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.mtl.OptionT
import arrow.mtl.OptionTOf
import arrow.mtl.OptionTPartialOf
import arrow.mtl.extensions.OptionTMonadError
import arrow.mtl.value
import arrow.fx.Ref
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
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface OptionTBracket<F> : Bracket<OptionTPartialOf<F>, Throwable>, OptionTMonadError<F, Throwable> {

  fun MD(): MonadDefer<F>

  override fun ME(): MonadError<F, Throwable> = MD()

  override fun <A, B> OptionTOf<F, A>.bracketCase(release: (A, ExitCase<Throwable>) -> OptionTOf<F, Unit>, use: (A) -> OptionTOf<F, B>): OptionT<F, B> = MD().run {
    OptionT(Ref(this, false).flatMap { ref ->
      value().bracketCase(use = {
        it.fold(
          { just(None) },
          { a -> use(a).value() }
        )
      }, release = { option, exitCase ->
        option.fold(
          { just(Unit) },
          { a ->
            when (exitCase) {
              is ExitCase.Completed -> release(a, exitCase).value().flatMap {
                it.fold({ ref.set(true) }, { just(Unit) })
              }
              else -> release(a, exitCase).value().unit()
            }
          }
        )
      }).flatMap { option ->
        option.fold(
          { just(None) },
          { ref.get().map { b -> if (b) None else option } }
        )
      }
    })
  }
}

@extension
@undocumented
interface OptionTMonadDefer<F> : MonadDefer<OptionTPartialOf<F>>, OptionTBracket<F> {

  override fun MD(): MonadDefer<F>

  override fun <A> defer(fa: () -> OptionTOf<F, A>): OptionT<F, A> =
    OptionT(MD().defer { fa().value() })
}

@extension
@undocumented
interface OptionTAsync<F> : Async<OptionTPartialOf<F>>, OptionTMonadDefer<F> {

  fun AS(): Async<F>

  override fun MD(): MonadDefer<F> = AS()

  override fun <A> async(fa: Proc<A>): OptionT<F, A> = AS().run {
    OptionT.liftF(this, async(fa))
  }

  override fun <A> asyncF(k: ProcF<OptionTPartialOf<F>, A>): OptionT<F, A> = AS().run {
    OptionT.liftF(this, asyncF { cb -> k(cb).value().unit() })
  }

  override fun <A> OptionTOf<F, A>.continueOn(ctx: CoroutineContext): OptionT<F, A> = AS().run {
    OptionT(value().continueOn(ctx))
  }
}

@extension
@undocumented
interface OptionTConcurrent<F> : Concurrent<OptionTPartialOf<F>>, OptionTAsync<F> {
  fun CF(): Concurrent<F>
  override fun AS(): Async<F> = CF()

  override fun dispatchers(): Dispatchers<OptionTPartialOf<F>> =
    CF().dispatchers() as Dispatchers<OptionTPartialOf<F>>

  override fun <A> OptionTOf<F, A>.fork(ctx: CoroutineContext): OptionT<F, Fiber<OptionTPartialOf<F>, A>> = CF().run {
    OptionT.liftF(this, value().fork(ctx).map(::fiberT))
  }

  override fun <A, B> CoroutineContext.racePair(fa: OptionTOf<F, A>, fb: OptionTOf<F, B>): OptionT<F, RacePair<OptionTPartialOf<F>, A, B>> = CF().run {
    OptionT(racePair(fa.value(), fb.value()).flatMap {
      when (it) {
        is RacePair.First -> when (val winner = it.winner) {
          None -> it.fiberB.cancel().map { None }
          is Some -> just(Some(RacePair.First(winner.t, fiberT(it.fiberB))))
        }
        is RacePair.Second -> when (val winner = it.winner) {
          is None -> it.fiberA.cancel().map { None }
          is Some -> just(Some(RacePair.Second(fiberT(it.fiberA), winner.t)))
        }
      }
    })
  }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: OptionTOf<F, A>, fb: OptionTOf<F, B>, fc: OptionTOf<F, C>): OptionT<F, RaceTriple<OptionTPartialOf<F>, A, B, C>> = CF().run {
    OptionT(raceTriple(fa.value(), fb.value(), fc.value()).flatMap {
      when (it) {
        is RaceTriple.First -> when (val winner = it.winner) {
          None -> tupled(it.fiberB.cancel(), it.fiberC.cancel()).map { None }
          is Some -> just(Some(RaceTriple.First(winner.t, fiberT(it.fiberB), fiberT(it.fiberC))))
        }
        is RaceTriple.Second -> when (val winner = it.winner) {
          is None -> tupled(it.fiberA.cancel(), it.fiberC.cancel()).map { None }
          is Some -> just(Some(RaceTriple.Second(fiberT(it.fiberA), winner.t, fiberT(it.fiberC))))
        }
        is RaceTriple.Third -> when (val winner = it.winner) {
          is None -> it.fiberA.cancel().map { None }
          is Some -> just(Some(RaceTriple.Third(fiberT(it.fiberA), fiberT(it.fiberB), winner.t)))
        }
      }
    })
  }

  fun <A> fiberT(fiber: Fiber<F, Option<A>>): Fiber<OptionTPartialOf<F>, A> = CF().run {
    Fiber(OptionT(fiber.join()), OptionT.liftF(this, fiber.cancel()))
  }
}
