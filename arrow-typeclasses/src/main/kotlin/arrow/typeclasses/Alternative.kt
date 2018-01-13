package arrow.typeclasses

import arrow.*

@typeclass
interface Alternative<F> : Applicative<F>, MonoidK<F>, TC