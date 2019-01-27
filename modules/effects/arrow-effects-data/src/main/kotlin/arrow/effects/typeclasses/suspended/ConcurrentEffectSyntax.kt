package arrow.effects.typeclasses.suspended

import arrow.effects.typeclasses.ConcurrentEffect

interface ConcurrentEffectSyntax<F> : ConcurrentEffect<F>, EffectSyntax<F>