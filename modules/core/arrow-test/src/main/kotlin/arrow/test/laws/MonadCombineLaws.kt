package arrow.test.laws

import arrow.Kind
import arrow.mtl.typeclasses.MonadCombine
import arrow.typeclasses.Eq

object MonadCombineLaws {

    inline fun <F> laws(MCF: MonadCombine<F>,
                        noinline cf: (Int) -> Kind<F, Int>,
                        noinline cff: (Int) -> Kind<F, (Int) -> Int>,
                        EQ: Eq<Kind<F, Int>>): List<Law> =
            MonadFilterLaws.laws(MCF, cf, EQ) + AlternativeLaws.laws(MCF, cf, cff, EQ)
}
