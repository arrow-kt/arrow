package arrow.test.laws

import arrow.Kind
import arrow.typeclasses.Applicative
import arrow.typeclasses.MonadCombine
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor

object MonadCombineLaws {

  fun <F> laws(
    MCF: MonadCombine<F>,
    FF: Functor<F>,
    AP: Applicative<F>,
    cf: (Int) -> Kind<F, Int>,
    cff: (Int) -> Kind<F, (Int) -> Int>,
    EQ: Eq<Kind<F, Int>>
  ): List<Law> =
    MonadFilterLaws.laws(MCF, FF, AP, cf, EQ) + AlternativeLaws.laws(MCF, cf, cff, EQ)
}
