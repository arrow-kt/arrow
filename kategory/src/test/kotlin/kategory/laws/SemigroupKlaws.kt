package kategory.laws

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.*
import kategory.typeclasses.SemigroupK

object SemigroupKLaws {

    inline fun <reified F> laws(SGK: SemigroupK<F>, EQ: Eq<HK<F, Int>>): List<Law> =
            listOf(Law("associativity", { semigroupKAssociative(SGK, EQ) }))

    inline fun <reified F> semigroupKAssociative(SGK: SemigroupK<F>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genApplicative<F, Int>(Gen.int()), genApplicative<F, Int>(Gen.int()), genApplicative<F, Int>(Gen.int()), { a, b, c ->
                SGK.combineK(SGK.combineK(a, b), c).equalUnderTheLaw(SGK.combineK(a, SGK.combineK(b, c)), EQ)
            })
}
