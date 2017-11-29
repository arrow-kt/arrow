package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SemigroupLaws {

    inline fun <reified F> laws(SG: Semigroup<F>, noinline f: (Int) -> F, EQ: Eq<F>): List<Law> =
            listOf(Law("Semigroup: associativity", { semigroupAssociative(SG, f, EQ) }))

    inline fun <reified F> semigroupAssociative(SG: Semigroup<F>, crossinline f: (Int) -> F, EQ: Eq<F>): Unit =
            forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
                val fa = f(a)
                val fb = f(b)
                val fc = f(c)
                SG.combine(SG.combine(fa, fb), fc).equalUnderTheLaw(SG.combine(fa, SG.combine(fb, fc)), EQ)
            }

}
