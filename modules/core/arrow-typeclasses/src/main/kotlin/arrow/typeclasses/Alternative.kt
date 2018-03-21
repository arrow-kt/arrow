package arrow.typeclasses

interface Alternative<F> : Applicative<F>, MonoidK<F>