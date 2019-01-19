package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.Effect

interface EffectSyntax<F> : Effect<F>, AsyncSyntax<F> {

  private suspend fun <A> effects(fb: Effect<F>.() -> Kind<F, A>): A =
    run<Effect<F>, Kind<F, A>> { fb(this) }.bind()

}