package arrow.effects.typeclasses.suspended

import arrow.typeclasses.Applicative

interface ApplicativeSyntax<F> : FunctorSyntax<F>, Applicative<F>