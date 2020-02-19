package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Semigroup
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SemigroupLaws {

  fun <F> laws(SG: Semigroup<F>, G: Gen<F>, EQ: Eq<F>): List<Law> =
    listOf(Law("Semigroup: associativity") { SG.semigroupAssociative(G, EQ) })

  fun <F> Semigroup<F>.semigroupAssociative(G: Gen<F>, EQ: Eq<F>): Unit =
    forAll(G, G, G) { A, B, C ->
      A.combine(B).combine(C).equalUnderTheLaw(A.combine(B.combine(C)), EQ)
    }
}
