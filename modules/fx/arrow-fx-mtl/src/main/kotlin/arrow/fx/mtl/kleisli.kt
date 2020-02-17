package arrow.fx.mtl

import arrow.Kind
import arrow.core.AndThen
import arrow.core.Either
import arrow.extension
import arrow.fx.IO
import arrow.fx.RacePair
import arrow.fx.RaceTriple
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
import arrow.mtl.Kleisli
import arrow.mtl.KleisliOf
import arrow.mtl.KleisliPartialOf
import arrow.mtl.extensions.KleisliMonad
import arrow.mtl.extensions.KleisliMonadError
import arrow.mtl.fix
import arrow.mtl.run
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface KleisliBracket<F, R, E> : Bracket<KleisliPartialOf<F, R>, E>, KleisliMonadError<F, R, E> {

  fun BF(): Bracket<F, E>

  override fun ME(): MonadError<F, E> = BF()

  override fun <A, B> KleisliOf<F, R, A>.bracketCase(
    release: (A, ExitCase<E>) -> KleisliOf<F, R, Unit>,
    use: (A) -> KleisliOf<F, R, B>
  ): Kleisli<F, R, B> = BF().run {
    Kleisli { r ->
      this@bracketCase.run(r).bracketCase({ a, br ->
        release(a, br).run(r)
      }) { a ->
        use(a).run(r)
      }
    }
  }

  override fun <A> KleisliOf<F, R, A>.uncancelable(): Kleisli<F, R, A> = BF().run {
    Kleisli { r -> this@uncancelable.run(r).uncancelable() }
  }
}

@extension
@undocumented
interface KleisliMonadDefer<F, R> : MonadDefer<KleisliPartialOf<F, R>>, KleisliBracket<F, R, Throwable> {

  fun MDF(): MonadDefer<F>

  override fun BF(): Bracket<F, Throwable> = MDF()

  override fun <A> defer(fa: () -> KleisliOf<F, R, A>): Kleisli<F, R, A> = MDF().run {
    Kleisli { r -> defer { fa().run(r) } }
  }

  override fun <A> KleisliOf<F, R, A>.handleErrorWith(f: (Throwable) -> KleisliOf<F, R, A>): Kleisli<F, R, A> = MDF().run {
    Kleisli { d -> defer { run(d).handleErrorWith { e -> f(e).run(d) } } }
  }

  override fun <A, B> KleisliOf<F, R, A>.flatMap(f: (A) -> KleisliOf<F, R, B>): Kleisli<F, R, B> = MDF().run {
    Kleisli { d -> defer { run(d).flatMap { a -> f(a).run(d) } } }
  }

  override fun <A> KleisliOf<F, R, A>.uncancelable(): Kleisli<F, R, A> = MDF().run {
    Kleisli { d -> defer { run(d).uncancelable() } }
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
    Kleisli(AndThen(fix().run).andThen { it.continueOn(ctx) })
  }
}

fun <F, R> Kleisli.Companion.async(AS: Async<F>): Async<KleisliPartialOf<F, R>> =
  object : KleisliAsync<F, R> {
    override fun ASF(): Async<F> = AS
  }

interface KleisliConcurrent<F, R> : Concurrent<KleisliPartialOf<F, R>>, KleisliAsync<F, R> {

  fun CF(): Concurrent<F>
  override fun ASF(): Async<F> = CF()

  override fun dispatchers(): Dispatchers<KleisliPartialOf<F, R>> =
    CF().dispatchers() as Dispatchers<KleisliPartialOf<F, R>>

  override fun <A> cancelable(k: ((Either<Throwable, A>) -> Unit) -> CancelToken<KleisliPartialOf<F, R>>): Kleisli<F, R, A> = CF().run {
    Kleisli { d -> cancelable { cb -> k(cb).run(d).map { Unit } } }
  }

  override fun <A> KleisliOf<F, R, A>.fork(ctx: CoroutineContext): Kleisli<F, R, Fiber<KleisliPartialOf<F, R>, A>> = CF().run {
    Kleisli { r -> run(r).fork(ctx).map(::fiberT) }
  }

  override fun <A, B> CoroutineContext.racePair(fa: KleisliOf<F, R, A>, fb: KleisliOf<F, R, B>): Kleisli<F, R, RacePair<KleisliPartialOf<F, R>, A, B>> = CF().run {
    Kleisli { r ->
      racePair(fa.run(r), fb.run(r)).map { res: RacePair<F, A, B> ->
        when (res) {
          is RacePair.First -> RacePair.First(res.winner, fiberT(res.fiberB))
          is RacePair.Second -> RacePair.Second(fiberT(res.fiberA), res.winner)
        }
      }
    }
  }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: KleisliOf<F, R, A>, fb: KleisliOf<F, R, B>, fc: KleisliOf<F, R, C>): Kleisli<F, R, RaceTriple<KleisliPartialOf<F, R>, A, B, C>> = CF().run {
    Kleisli { r ->
      raceTriple(fa.run(r), fb.run(r), fc.run(r)).map { res: RaceTriple<F, A, B, C> ->
        when (res) {
          is RaceTriple.First -> RaceTriple.First(res.winner, fiberT(res.fiberB), fiberT(res.fiberC))
          is RaceTriple.Second -> RaceTriple.Second(fiberT(res.fiberA), res.winner, fiberT(res.fiberC))
          is RaceTriple.Third -> RaceTriple.Third(fiberT(res.fiberA), fiberT(res.fiberB), res.winner)
        }
      }
    }
  }

  fun <A> fiberT(fiber: Fiber<F, A>): Fiber<KleisliPartialOf<F, R>, A> =
    Fiber(Kleisli.liftF(fiber.join()), Kleisli.liftF(fiber.cancel()))
}

fun <F, R> Kleisli.Companion.concurrent(CF: Concurrent<F>): Concurrent<KleisliPartialOf<F, R>> =
  object : KleisliConcurrent<F, R> {
    override fun CF(): Concurrent<F> = CF
  }

fun <F, R> Kleisli.Companion.timer(CF: Concurrent<F>): Timer<KleisliPartialOf<F, R>> =
  Timer(concurrent<F, R>(CF))

@extension
interface KleisliMonadIO<F, R> : MonadIO<KleisliPartialOf<F, R>>, KleisliMonad<F, R> {
  fun FIO(): MonadIO<F>
  override fun MF(): Monad<F> = FIO()
  override fun <A> IO<A>.liftIO(): Kind<KleisliPartialOf<F, R>, A> = FIO().run {
    Kleisli.liftF(liftIO())
  }
}
