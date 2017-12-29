package arrow

object MonoidLaws {

    inline fun <reified F> laws(M: Monoid<F>, A: F, EQ: Eq<F>): List<Law> =
            listOf(
                    Law("Monoid Laws: Left identity", { monoidLeftIdentity(M, A, EQ) }),
                    Law("Monoid Laws: Right identity", { monoidRightIdentity(M, A, EQ) })
            )

    inline fun <reified F> monoidLeftIdentity(M: Monoid<F>, A: F, EQ: Eq<F>): Boolean =
            M.combine(M.empty(), A).equalUnderTheLaw(A, EQ)

    inline fun <reified F> monoidRightIdentity(M: Monoid<F>, A: F, EQ: Eq<F>): Boolean =
            M.combine(A, M.empty()).equalUnderTheLaw(A, EQ)

}
