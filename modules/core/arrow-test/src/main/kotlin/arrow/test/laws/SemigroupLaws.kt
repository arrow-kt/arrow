package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Semigroup

object SemigroupLaws {

  inline fun <F> laws(SG: Semigroup<F>, A: F, B: F, C: F, EQ: Eq<F>): List<Law> =
    listOf(Law("Semigroup: associativity") { SG.semigroupAssociative(A, B, C, EQ) })

  fun <F> Semigroup<F>.semigroupAssociative(A: F, B: F, C: F, EQ: Eq<F>): Boolean =
    A.combine(B).combine(C).equalUnderTheLaw(A.combine(B.combine(C)), EQ)

}
