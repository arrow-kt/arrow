package arrow.effects.typeclasses.suspended

import arrow.effects.typeclasses.Effect

interface EffectSyntax<F> : Effect<F>, AsyncSyntax<F>