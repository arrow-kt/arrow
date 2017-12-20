package arrow

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object SemigroupKLaws {

    inline fun <reified F> laws(SGK: SemigroupK<F>, AP: Applicative<F>, EQ: Eq<HK<F, Int>>): List<Law> =
            listOf(Law("SemigroupK: associativity", { semigroupKAssociative(SGK, AP::pure, EQ) }))

    inline fun <reified F> laws(SGK: SemigroupK<F>, crossinline f: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): List<Law> =
            listOf(Law("SemigroupK: associativity", { semigroupKAssociative(SGK, f, EQ) }))

    inline fun <reified F> semigroupKAssociative(SGK: SemigroupK<F>, crossinline f: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), f), genConstructor(Gen.int(), f), genConstructor(Gen.int(), f), { a, b, c ->
                SGK.combineK(SGK.combineK(a, b), c).equalUnderTheLaw(SGK.combineK(a, SGK.combineK(b, c)), EQ)
            })
}
