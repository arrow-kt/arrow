package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.Kind
import arrow.mtl.MonadCombine
import arrow.mtl.monadCombine

object MonadCombineLaws {

    inline fun <reified F> laws(MCF: MonadCombine<F> = monadCombine(),
                                crossinline cf: (Int) -> Kind<F, Int>,
                                crossinline cff: (Int) -> Kind<F, (Int) -> Int>,
                                EQ: Eq<Kind<F, Int>>): List<Law> =
            MonadFilterLaws.laws(MCF, cf, EQ) + AlternativeLaws.laws(MCF, cf, cff, EQ)
}
