package arrow.typeclasses

interface Bimonad<F> : Monad<F>, Comonad<F>