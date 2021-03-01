package arrow.core.test.laws

import arrow.typeclasses.Semigroup
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SemigroupLaws {

  fun <F> laws(SG: Semigroup<F>, G: Gen<F>, f: (F, F) -> Boolean = { a, b -> a?.equals(b) == true }): List<Law> =
    listOf(Law("Semigroup: associativity") { SG.semigroupAssociative(G, f) })

  fun <F> Semigroup<F>.semigroupAssociative(G: Gen<F>, f: (F, F) -> Boolean): Unit =
    forAll(G, G, G) { A, B, C ->
      A.combine(B).combine(C).equalUnderTheLaw(A.combine(B.combine(C)), f)
    }
}
