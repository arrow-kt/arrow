package arrow.test.laws

import arrow.Kind
import arrow.test.generators.genConstructor
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.SemigroupK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SemigroupKLaws {

    inline fun <F> laws(SGK: SemigroupK<F>, AP: Applicative<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
            listOf(Law("SemigroupK: associativity", { SGK.semigroupKAssociative(AP::pure, EQ) }))

    inline fun <F> laws(SGK: SemigroupK<F>, noinline f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
            listOf(Law("SemigroupK: associativity", { SGK.semigroupKAssociative(f, EQ) }))

    fun <F> SemigroupK<F>.semigroupKAssociative(f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), f), genConstructor(Gen.int(), f), genConstructor(Gen.int(), f), { a, b, c ->
                combineK(combineK(a, b), c).equalUnderTheLaw(combineK(a, combineK(b, c)), EQ)
            })
}
