package arrow.effects.instances

import arrow.Kind
import arrow.data.Kleisli
import arrow.data.KleisliPartialOf
import arrow.data.fix
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.ExitCase
import arrow.extension
import arrow.instances.KleisliMonadErrorInstance
import arrow.typeclasses.MonadError

@extension
interface KleisliBracketInstance<F, R, E> : Bracket<KleisliPartialOf<F, R>, E>, KleisliMonadErrorInstance<F, R, E> {

  fun BF(): Bracket<F, E>

  override fun ME(): MonadError<F, E> = BF()

  override fun <A, B> Kind<KleisliPartialOf<F, R>, A>.bracketCase(
    release: (A, ExitCase<E>) -> Kind<KleisliPartialOf<F, R>, Unit>,
    use: (A) -> Kind<KleisliPartialOf<F, R>, B>
  ): Kleisli<F, R, B> =
    BF().run {
      Kleisli { r ->
        this@bracketCase.fix().run(r).bracketCase({ a, br ->
          release(a, br).fix().run(r)
        }) { a ->
          use(a).fix().run(r)
        }
      }
    }

  override fun <A> Kind<KleisliPartialOf<F, R>, A>.uncancelable(): Kleisli<F, R, A> =
    Kleisli { r -> BF().run { this@uncancelable.fix().run(r).uncancelable() } }
}
