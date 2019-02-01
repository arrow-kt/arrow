package arrow.test.laws

import arrow.Kind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.SemigroupK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SemigroupKLaws {

  fun <F> laws(SGK: SemigroupK<F>, AP: Applicative<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
    listOf(Law("SemigroupK: associativity") { SGK.semigroupKAssociative(AP::just, EQ) })

  fun <F> laws(SGK: SemigroupK<F>, f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
    listOf(Law("SemigroupK: associativity") { SGK.semigroupKAssociative(f, EQ) })

  fun <F> SemigroupK<F>.semigroupKAssociative(f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int().map(f), Gen.int().map(f), Gen.int().map(f)) { a, b, c ->
      a.combineK(b).combineK(c).equalUnderTheLaw(a.combineK(b.combineK(c)), EQ)
    }
}
