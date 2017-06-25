package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorLaws {

    inline fun <reified F> laws(AP: Applicative<F> = applicative<F>(), EQ: Eq<HK<F, Int>>): List<Law> =
            listOf(
                    Law("Functor Laws: Covariant Identity", { covariantIdentity(AP, EQ) }),
                    Law("Functor: Covariant Composition", { covariantComposition(AP, EQ) })
            )

    inline fun <reified F> covariantIdentity(AP: Applicative<F> = applicative<F>(), EQ: Eq<HK<F, Int>> = Eq()): Unit =
            forAll(genApplicative(Gen.int(), AP), { fa: HK<F, Int> ->
                AP.map(fa, ::identity).equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> covariantComposition(AP: Applicative<F> = applicative<F>(), EQ: Eq<HK<F, Int>> = Eq()): Unit =
            forAll(
                    genApplicative(Gen.int(), AP),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    { fa: HK<F, Int>, f, g ->
                        AP.map(AP.map(fa, f), g).equalUnderTheLaw(AP.map(fa, f andThen g), EQ)
                    }
            )

}



