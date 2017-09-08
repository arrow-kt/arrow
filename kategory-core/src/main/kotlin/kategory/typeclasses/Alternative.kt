package kategory.typeclasses

import kategory.Applicative
import kategory.MonoidK
import kategory.Typeclass

interface Alternative<F> : Applicative<F>, MonoidK<F>, Typeclass
