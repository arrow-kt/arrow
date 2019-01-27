package arrow.typeclasses.suspended

import arrow.typeclasses.Functor

interface FunctorSyntax<F> : Functor<F>, BindSyntax<F>