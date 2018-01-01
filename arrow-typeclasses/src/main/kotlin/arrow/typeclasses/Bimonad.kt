package arrow.typeclasses

import arrow.*

@typeclass
interface Bimonad<F> : Monad<F>, Comonad<F>, Typeclass