package arrow.fx.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
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
import arrow.mtl.EitherT
import arrow.mtl.EitherTOf
import arrow.mtl.EitherTPartialOf
import arrow.mtl.extensions.EitherTMonadThrow
import arrow.mtl.value
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Monad
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface EitherTBracket<F, L> : Bracket<EitherTPartialOf<F, L>, Throwable>, EitherTMonadThrow<F, L> {

  fun MDF(): MonadDefer<F>

  override fun MF(): Monad<F> = MDF()

  override fun AE(): ApplicativeError<F, Throwable> = MDF()

  override fun <A, B> EitherTOf<F, L, A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> EitherTOf<F, L, Unit>,
    use: (A) -> EitherTOf<F, L, B>
  ): EitherT<F, L, B> = MDF().run {
    EitherT.liftF<F, L, Ref<F, Option<L>>>(this, Ref(None)).flatMap { ref ->
      EitherT(value().bracketCase(use = { either ->
        when (either) {
          is Either.Right -> use(either.b).value()
          is Either.Left -> just(either)
        }
      }, release = { either, exitCase ->
        when (either) {
          is Either.Right -> when (exitCase) {
            is ExitCase.Completed -> release(either.b, ExitCase.Completed).value().flatMap {
              it.fold({ l ->
                ref.set(Some(l))
              }, {
                just(Unit)
              })
            }
            else -> release(either.b, exitCase).value().unit()
          }
          is Either.Left -> just(Unit)
        }
      }).flatMap { either ->
        when (either) {
          is Either.Right -> ref.get().map {
            it.fold({ either }, { left -> Left(left) })
          }
          is Either.Left -> just(either)
        }
      })
    }
  }
}

@extension
@undocumented
interface EitherTMonadDefer<F, L> : MonadDefer<EitherTPartialOf<F, L>>, EitherTBracket<F, L> {

  override fun MDF(): MonadDefer<F>

  override fun <A> defer(fa: () -> EitherTOf<F, L, A>): EitherT<F, L, A> =
    EitherT(MDF().defer { fa().value() })
}

@extension
@undocumented
interface EitherTAsync<F, L> : Async<EitherTPartialOf<F, L>>, EitherTMonadDefer<F, L> {

  fun ASF(): Async<F>

  override fun MDF(): MonadDefer<F> = ASF()

  override fun <A> async(fa: Proc<A>): EitherT<F, L, A> = ASF().run {
    EitherT.liftF(this, async(fa))
  }

  override fun <A> asyncF(k: ProcF<EitherTPartialOf<F, L>, A>): EitherT<F, L, A> = ASF().run {
    EitherT.liftF(this, asyncF { cb -> k(cb).value().unit() })
  }

  override fun <A> EitherTOf<F, L, A>.continueOn(ctx: CoroutineContext): EitherT<F, L, A> = ASF().run {
    EitherT(value().continueOn(ctx))
  }
}

interface EitherTConcurrent<F, L> : Concurrent<EitherTPartialOf<F, L>>, EitherTAsync<F, L> {

  fun CF(): Concurrent<F>

  override fun ASF(): Async<F> = CF()

  override fun dispatchers(): Dispatchers<EitherTPartialOf<F, L>> =
    CF().dispatchers() as Dispatchers<EitherTPartialOf<F, L>>

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<EitherTPartialOf<F, L>>): EitherT<F, L, A> = CF().run {
    EitherT.liftF(this, cancelable { cb -> k(cb).value().map { Unit } })
  }

  override fun <A> EitherTOf<F, L, A>.fork(ctx: CoroutineContext): EitherT<F, L, Fiber<EitherTPartialOf<F, L>, A>> = CF().run {
    EitherT.liftF(this, value().fork(ctx).map(::fiberT))
  }

  override fun <A, B> CoroutineContext.racePair(fa: EitherTOf<F, L, A>, fb: EitherTOf<F, L, B>): EitherT<F, L, RacePair<EitherTPartialOf<F, L>, A, B>> = CF().run {
    val racePair: Kind<F, Either<L, RacePair<EitherTPartialOf<F, L>, A, B>>> =
      racePair(fa.value(), fb.value()).flatMap { res: RacePair<F, Either<L, A>, Either<L, B>> ->
        when (res) {
          is RacePair.First -> when (val winner = res.winner) {
            is Either.Left -> res.fiberB.cancel().map { Left(winner.a) }
            is Either.Right -> just(Right(RacePair.First(winner.b, fiberT(res.fiberB))))
          }
          is RacePair.Second -> when (val winner = res.winner) {
            is Either.Left -> res.fiberA.cancel().map { Left(winner.a) }
            is Either.Right -> just(Right(RacePair.Second(fiberT(res.fiberA), winner.b)))
          }
        }
      }
    EitherT(racePair)
  }

  override fun <A, B, C> CoroutineContext.raceTriple(
    fa: EitherTOf<F, L, A>,
    fb: EitherTOf<F, L, B>,
    fc: EitherTOf<F, L, C>
  ): EitherT<F, L, RaceTriple<EitherTPartialOf<F, L>, A, B, C>> = CF().run {
    val raceTriple: Kind<F, Either<L, RaceTriple<EitherTPartialOf<F, L>, A, B, C>>> =
      raceTriple(fa.value(), fb.value(), fc.value()).flatMap { res: RaceTriple<F, Either<L, A>, Either<L, B>, Either<L, C>> ->
        when (res) {
          is RaceTriple.First -> when (val winner = res.winner) {
            is Either.Left -> tupled(res.fiberB.cancel(), res.fiberC.cancel()).map { Left(winner.a) }
            is Either.Right -> just(Right(RaceTriple.First(winner.b, fiberT(res.fiberB), fiberT(res.fiberC))))
          }
          is RaceTriple.Second -> when (val winner = res.winner) {
            is Either.Left -> tupled(res.fiberA.cancel(), res.fiberC.cancel()).map { Left(winner.a) }
            is Either.Right -> just(Right(RaceTriple.Second(fiberT(res.fiberA), winner.b, fiberT(res.fiberC))))
          }
          is RaceTriple.Third -> when (val winner = res.winner) {
            is Either.Left -> tupled(res.fiberA.cancel(), res.fiberB.cancel()).map { Left(winner.a) }
            is Either.Right -> just(Right(RaceTriple.Third(fiberT(res.fiberA), fiberT(res.fiberB), winner.b)))
          }
        }
      }
    EitherT(raceTriple)
  }

  fun <A> fiberT(fiber: Fiber<F, Either<L, A>>): Fiber<EitherTPartialOf<F, L>, A> =
    Fiber(EitherT(fiber.join()), EitherT.liftF(ASF(), fiber.cancel()))
}

fun <F, L> EitherT.Companion.concurrent(CF: Concurrent<F>): Concurrent<EitherTPartialOf<F, L>> =
  object : EitherTConcurrent<F, L> {
    override fun CF(): Concurrent<F> = CF
  }
