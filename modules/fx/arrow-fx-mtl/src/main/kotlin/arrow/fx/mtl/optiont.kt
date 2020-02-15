package arrow.fx.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
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
import arrow.mtl.OptionT
import arrow.mtl.OptionTOf
import arrow.mtl.OptionTPartialOf
import arrow.mtl.extensions.OptionTMonad
import arrow.mtl.extensions.OptionTMonadError
import arrow.mtl.extensions.optiont.monadTrans.liftT
import arrow.mtl.value
import arrow.typeclasses.Monad
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

interface OptionTConcurrent<F> : Concurrent<OptionTPartialOf<F>>, OptionTAsync<F> {
  fun CF(): Concurrent<F>
  override fun AS(): Async<F> = CF()

  override fun dispatchers(): Dispatchers<OptionTPartialOf<F>> =
    CF().dispatchers() as Dispatchers<OptionTPartialOf<F>>

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<OptionTPartialOf<F>>): OptionT<F, A> = CF().run {
    OptionT.liftF(this, cancelable { cb -> k(cb).value().map { Unit } })
  }

  override fun <A> OptionTOf<F, A>.fork(ctx: CoroutineContext): OptionT<F, Fiber<OptionTPartialOf<F>, A>> = CF().run {
    OptionT.liftF(this, value().fork(ctx).map(::fiberT))
  }

  override fun <A, B> CoroutineContext.racePair(fa: OptionTOf<F, A>, fb: OptionTOf<F, B>): OptionT<F, RacePair<OptionTPartialOf<F>, A, B>> = CF().run {
    val racePair: Kind<F, Option<RacePair<OptionTPartialOf<F>, A, B>>> =
      racePair(fa.value(), fb.value()).flatMap { res: RacePair<F, Option<A>, Option<B>> ->
        when (res) {
          is RacePair.First -> when (val winner = res.winner) {
            None -> res.fiberB.cancel().map { None }
            is Some -> just(Some(RacePair.First(winner.t, fiberT(res.fiberB))))
          }
          is RacePair.Second -> when (val winner = res.winner) {
            is None -> res.fiberA.cancel().map { None }
            is Some -> just(Some(RacePair.Second(fiberT(res.fiberA), winner.t)))
          }
        }
      }
    OptionT(racePair)
  }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: OptionTOf<F, A>, fb: OptionTOf<F, B>, fc: OptionTOf<F, C>): OptionT<F, RaceTriple<OptionTPartialOf<F>, A, B, C>> = CF().run {
    val raceTriple: Kind<F, Option<RaceTriple<OptionTPartialOf<F>, A, B, C>>> =
      raceTriple(fa.value(), fb.value(), fc.value()).flatMap { res: RaceTriple<F, Option<A>, Option<B>, Option<C>> ->
        when (res) {
          is RaceTriple.First -> when (val winner = res.winner) {
            None -> tupledN(res.fiberB.cancel(), res.fiberC.cancel()).map { None }
            is Some -> just(Some(RaceTriple.First(winner.t, fiberT(res.fiberB), fiberT(res.fiberC))))
          }
          is RaceTriple.Second -> when (val winner = res.winner) {
            is None -> tupledN(res.fiberA.cancel(), res.fiberC.cancel()).map { None }
            is Some -> just(Some(RaceTriple.Second(fiberT(res.fiberA), winner.t, fiberT(res.fiberC))))
          }
          is RaceTriple.Third -> when (val winner = res.winner) {
            is None -> res.fiberA.cancel().map { None }
            is Some -> just(Some(RaceTriple.Third(fiberT(res.fiberA), fiberT(res.fiberB), winner.t)))
          }
        }
      }
    OptionT(raceTriple)
  }

  fun <A> fiberT(fiber: Fiber<F, Option<A>>): Fiber<OptionTPartialOf<F>, A> = CF().run {
    Fiber(OptionT(fiber.join()), OptionT.liftF(this, fiber.cancel()))
  }
}

fun <F> OptionT.Companion.concurrent(CF: Concurrent<F>): Concurrent<OptionTPartialOf<F>> =
  object : OptionTConcurrent<F> {
    override fun CF(): Concurrent<F> = CF
  }

fun <F> OptionT.Companion.timer(CF: Concurrent<F>): Timer<OptionTPartialOf<F>> =
  Timer(concurrent(CF))

@extension
interface OptionTMonadIO<F> : MonadIO<OptionTPartialOf<F>>, OptionTMonad<F> {
  fun FIO(): MonadIO<F>
  override fun MF(): Monad<F> = FIO()
  override fun <A> IO<A>.liftIO(): Kind<OptionTPartialOf<F>, A> = FIO().run {
    liftIO().liftT(this)
  }
}
