package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonoidLaws {

    inline fun <reified F> laws(M: Monoid<F>, noinline f: (Int) -> F, EQ: Eq<F>): List<Law> =
            listOf(
                    Law("Monoid Laws: Left identity", { monoidLeftIdentity(M, f, EQ) }),
                    Law("Monoid Laws: Right identity", { monoidRightIdentity(M, f, EQ) })
            )

    inline fun <reified F> monoidLeftIdentity(M: Monoid<F>, crossinline f: (Int) -> F, EQ: Eq<F>): Unit =
            forAll(Gen.int()) {
                val fa = f(it)
                M.combine(M.empty(), fa).equalUnderTheLaw(fa, EQ)
            }

    inline fun <reified F> monoidRightIdentity(M: Monoid<F>, crossinline f: (Int) -> F, EQ: Eq<F>): Unit =
            forAll(Gen.int()) {
                val fa = f(it)
                M.combine(fa, M.empty()).equalUnderTheLaw(fa, EQ)
            }

}
