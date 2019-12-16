package arrow.fx.mtl

import arrow.Kind
import arrow.mtl.Kleisli
import arrow.mtl.KleisliOf
import arrow.mtl.KleisliPartialOf
import arrow.mtl.extensions.KleisliMonadError
import arrow.mtl.run
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
import arrow.mtl.fix
import arrow.typeclasses.MonadError
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface KleisliBracket<F, R, E> : Bracket<KleisliPartialOf<F, R>, E>, KleisliMonadError<F, R, E> {

  fun BF(): Bracket<F, E>

  override fun ME(): MonadError<F, E> = BF()

  override fun <A, B> Kind<KleisliPartialOf<F, R>, A>.bracketCase(
    release: (A, ExitCase<E>) -> Kind<KleisliPartialOf<F, R>, Unit>,
    use: (A) -> Kind<KleisliPartialOf<F, R>, B>
  ): Kleisli<F, R, B> =
    BF().run {
      Kleisli { r ->
        this@bracketCase.run(r).bracketCase({ a, br ->
          release(a, br).run(r)
        }) { a ->
          use(a).run(r)
        }
      }
    }

  override fun <A> Kind<KleisliPartialOf<F, R>, A>.uncancelable(): Kleisli<F, R, A> =
    Kleisli { r -> BF().run { this@uncancelable.run(r).uncancelable() } }
}

@extension
@undocumented
interface KleisliMonadDefer<F, R> : MonadDefer<KleisliPartialOf<F, R>>, KleisliBracket<F, R, Throwable> {

  fun MDF(): MonadDefer<F>

  override fun BF(): Bracket<F, Throwable> = MDF()

  override fun <A> defer(fa: () -> KleisliOf<F, R, A>): Kleisli<F, R, A> = MDF().run {
    Kleisli { r -> defer { fa().run(r) } }
  }
}

fun <F, R> Kleisli.Companion.monadDefer(MD: MonadDefer<F>): MonadDefer<KleisliPartialOf<F, R>> =
  object : KleisliMonadDefer<F, R> {
    override fun MDF(): MonadDefer<F> = MD
  }

@extension
@undocumented
interface KleisliAsync<F, R> : Async<KleisliPartialOf<F, R>>, KleisliMonadDefer<F, R> {

  fun ASF(): Async<F>

  override fun MDF(): MonadDefer<F> = ASF()

  override fun <A> async(fa: Proc<A>): Kleisli<F, R, A> =
    Kleisli.liftF(ASF().async(fa))

  override fun <A> asyncF(k: ProcF<KleisliPartialOf<F, R>, A>): Kleisli<F, R, A> =
    Kleisli { r -> ASF().asyncF { cb -> k(cb).run(r) } }

  override fun <A> KleisliOf<F, R, A>.continueOn(ctx: CoroutineContext): Kleisli<F, R, A> = ASF().run {
    Kleisli { r -> run(r).continueOn(ctx) }
  }
}

fun <F, R> Kleisli.Companion.async(AS: Async<F>): Async<KleisliPartialOf<F, R>> =
  object : KleisliAsync<F, R> {
    override fun ASF(): Async<F> = AS
  }

@extension
@undocumented
interface KleisliConcurrent<F, R> : Concurrent<KleisliPartialOf<F, R>>, KleisliAsync<F, R> {

  fun CF(): Concurrent<F>
  override fun ASF(): Async<F> = CF()

  override fun dispatchers(): Dispatchers<KleisliPartialOf<F, R>> =
    CF().dispatchers() as Dispatchers<KleisliPartialOf<F, R>>

  override fun <A> KleisliOf<F, R, A>.fork(ctx: CoroutineContext): Kleisli<F, R, Fiber<KleisliPartialOf<F, R>, A>> = CF().run {
    Kleisli { r -> fix().run(r).fork(ctx).map(::fiberT) }
  }

  override fun <A, B> CoroutineContext.racePair(fa: KleisliOf<F, R, A>, fb: KleisliOf<F, R, B>): Kleisli<F, R, RacePair<KleisliPartialOf<F, R>, A, B>> = CF().run {
    Kleisli { r ->
      racePair(fa.run(r), fb.run(r)).map {
        when (it) {
          is RacePair.First -> RacePair.First(it.winner, fiberT(it.fiberB))
          is RacePair.Second -> RacePair.Second(fiberT(it.fiberA), it.winner)
        }
      }
    }
  }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: KleisliOf<F, R, A>, fb: KleisliOf<F, R, B>, fc: KleisliOf<F, R, C>): Kleisli<F, R, RaceTriple<KleisliPartialOf<F, R>, A, B, C>> = CF().run {
    Kleisli { r ->
      raceTriple(fa.run(r), fb.run(r), fc.run(r)).map {
        when (it) {
          is RaceTriple.First -> RaceTriple.First(it.winner, fiberT(it.fiberB), fiberT(it.fiberC))
          is RaceTriple.Second -> RaceTriple.Second(fiberT(it.fiberA), it.winner, fiberT(it.fiberC))
          is RaceTriple.Third -> RaceTriple.Third(fiberT(it.fiberA), fiberT(it.fiberB), it.winner)
        }
      }
    }
  }
  fun <A> fiberT(fiber: Fiber<F, A>): Fiber<KleisliPartialOf<F, R>, A> =
    Fiber(Kleisli.liftF(fiber.join()), Kleisli.liftF(fiber.cancel()))
}
