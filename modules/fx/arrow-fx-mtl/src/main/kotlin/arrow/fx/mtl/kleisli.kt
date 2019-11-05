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
import arrow.typeclasses.MonadError
import arrow.undocumented
import kotlin.coroutines.CoroutineContext

@extension
@undocumented
interface KleisliBracket<R, F, E> : Bracket<KleisliPartialOf<R, F>, E>, KleisliMonadError<R, F, E> {

  fun BF(): Bracket<F, E>

  override fun ME(): MonadError<F, E> = BF()

  override fun <A, B> Kind<KleisliPartialOf<R, F>, A>.bracketCase(
    release: (A, ExitCase<E>) -> Kind<KleisliPartialOf<R, F>, Unit>,
    use: (A) -> Kind<KleisliPartialOf<R, F>, B>
  ): Kleisli<R, F, B> =
    BF().run {
      Kleisli { r ->
        this@bracketCase.run(r).bracketCase({ a, br ->
          release(a, br).run(r)
        }) { a ->
          use(a).run(r)
        }
      }
    }

  override fun <A> Kind<KleisliPartialOf<R, F>, A>.uncancelable(): Kleisli<R, F, A> =
    Kleisli { r -> BF().run { this@uncancelable.run(r).uncancelable() } }
}

// TODO fix stack safety issue. AsyncLaws#stack safety over repeated attempts fails.
internal interface KleisliMonadDefer<R, F> : MonadDefer<KleisliPartialOf<R, F>>, KleisliBracket<R, F, Throwable> {

  fun MDF(): MonadDefer<F>

  override fun BF(): Bracket<F, Throwable> = MDF()

  override fun <A> defer(fa: () -> KleisliOf<R, F, A>): Kleisli<R, F, A> = MDF().run {
    Kleisli { r -> defer { fa().run(r) } }
  }
}

internal interface KleisliAsync<R, F> : Async<KleisliPartialOf<R, F>>, KleisliMonadDefer<R, F> {

  fun ASF(): Async<F>

  override fun MDF(): MonadDefer<F> = ASF()

  override fun <A> async(fa: Proc<A>): Kleisli<R, F, A> =
    Kleisli.liftF(ASF().async(fa))

  override fun <A> asyncF(k: ProcF<KleisliPartialOf<R, F>, A>): Kleisli<R, F, A> =
    Kleisli { r -> ASF().asyncF { cb -> k(cb).run(r) } }

  override fun <A> KleisliOf<R, F, A>.continueOn(ctx: CoroutineContext): Kleisli<R, F, A> = ASF().run {
    Kleisli { r -> run(r).continueOn(ctx) }
  }
}
