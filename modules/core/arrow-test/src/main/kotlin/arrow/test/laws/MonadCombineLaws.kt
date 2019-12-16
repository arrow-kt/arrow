package arrow.test.laws

import arrow.Kind
import arrow.typeclasses.EqK
import arrow.typeclasses.MonadCombine

object MonadCombineLaws {

  fun <F> laws(
    MCF: MonadCombine<F>,
    cf: (Int) -> Kind<F, Int>,
    cff: (Int) -> Kind<F, (Int) -> Int>,
    EQK: EqK<F>
  ): List<Law> =
    MonadFilterLaws.laws(MCF, cf, EQK) + AlternativeLaws.laws(MCF, cf, cff, EQK)
}
