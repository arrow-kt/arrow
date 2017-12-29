package arrow.test.laws

import arrow.Eq
import arrow.Semigroup

object SemigroupLaws {

    inline fun <reified F> laws(SG: Semigroup<F>, A: F, B: F, C:F, EQ: Eq<F>): List<Law> =
            listOf(Law("Semigroup: associativity", { semigroupAssociative(SG, A, B, C, EQ) }))

    inline fun <reified F> semigroupAssociative(SG: Semigroup<F>, A: F, B: F, C: F, EQ: Eq<F>): Boolean =
            SG.combine(SG.combine(A, B), C).equalUnderTheLaw(SG.combine(A, SG.combine(B, C)), EQ)

}
