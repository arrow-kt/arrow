package kategory

class EvalEq : Eq<HK<Eval.F, Int>> {
    override fun eqv(a: HK<Eval.F, Int>, b: HK<Eval.F, Int>): Boolean =
            a.ev().value() == b.ev().value()
}