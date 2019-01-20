package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.Effect

interface EffectSyntax<F> : Effect<F>, AsyncSyntax<F>