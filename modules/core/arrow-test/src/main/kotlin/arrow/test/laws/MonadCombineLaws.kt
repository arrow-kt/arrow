package arrow.test.laws

import arrow.Kind
import arrow.mtl.typeclasses.MonadCombine
import arrow.typeclasses.Eq

object MonadCombineLaws {

  fun <F> laws(MCF: MonadCombine<F>,
               cf: (Int) -> Kind<F, Int>,
               cff: (Int) -> Kind<F, (Int) -> Int>,
               EQ: Eq<Kind<F, Int>>): List<Law> =
    MonadFilterLaws.laws(MCF, cf, EQ) + AlternativeLaws.laws(MCF, cf, cff, EQ)
}
