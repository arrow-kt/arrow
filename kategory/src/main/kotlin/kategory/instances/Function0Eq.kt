package kategory

class Function0Eq : Eq<HK<Function0.F, Int>> {
    override fun eqv(a: HK<Function0.F, Int>, b: HK<Function0.F, Int>): Boolean =
            a.ev()() == b.ev()()
}