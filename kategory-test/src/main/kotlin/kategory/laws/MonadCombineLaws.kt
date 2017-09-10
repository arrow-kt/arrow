package kategory

object MonadCombineLaws {

    inline fun <reified F> laws(MCF: MonadCombine<F> = monadCombine(),
                                crossinline cf: (Int) -> HK<F, Int>,
                                crossinline cff: (Int) -> HK<F, (Int) -> Int>,
                                EQ: Eq<HK<F, Int>>,
                                EQF: Eq<HK<F, (Int) -> Int>>): List<Law> =
            MonadFilterLaws.laws(MCF, cf, EQ) + AlternativeLaws.laws(MCF, cf, cff, EQ, EQF)
}
