package arrow.core.test.laws

import arrow.KindDeprecation
import arrow.core.test.generators.GenK
import arrow.typeclasses.Apply
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.MonadCombine

@Deprecated(KindDeprecation)
object MonadCombineLaws {

  fun <F> laws(
    MCF: MonadCombine<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadFilterLaws.laws(MCF, GENK, EQK) + AlternativeLaws.laws(MCF, GENK, EQK)

  fun <F> laws(
    MCF: MonadCombine<F>,
    FF: Functor<F>,
    AP: Apply<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadFilterLaws.laws(MCF, FF, AP, GENK, EQK) + AlternativeLaws.laws(MCF, GENK, EQK)
}
