package arrow.effects.typeclasses.suspended

import arrow.typeclasses.Functor

interface FunctorSyntax<F> : Functor<F>, SuspendToKindSyntax<F>