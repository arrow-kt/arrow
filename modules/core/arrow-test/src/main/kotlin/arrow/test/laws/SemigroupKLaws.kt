package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.test.generators.GenK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.SemigroupK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SemigroupKLaws {

  fun <F> laws(SGK: SemigroupK<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> =
    listOf(Law("SemigroupK: associativity") { SGK.semigroupKAssociative(GENK.genK(Gen.int()), EQK.liftEq(Int.eq())) })

  fun <F> SemigroupK<F>.semigroupKAssociative(GEN: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GEN, GEN, GEN) { a, b, c ->
      a.combineK(b).combineK(c).equalUnderTheLaw(a.combineK(b.combineK(c)), EQ)
    }
}
