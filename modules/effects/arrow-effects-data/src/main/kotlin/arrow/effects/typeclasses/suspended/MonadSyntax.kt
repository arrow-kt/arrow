package arrow.effects.typeclasses.suspended

import arrow.typeclasses.Monad

interface MonadSyntax<F> : ApplicativeSyntax<F>, Monad<F>