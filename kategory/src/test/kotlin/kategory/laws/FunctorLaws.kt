package kategory

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorLaws {

    inline fun <reified F> laws(AP: Applicative<F> = applicative<F>()): List<Law> =
            listOf(
                    Law("Functor Laws: Covariant Identity", { covariantIdentity(AP) }),
                    Law("Functor: Covariant Composition", { covariantComposition(AP) })
            )

    inline fun <reified F> covariantIdentity(AP: Applicative<F> = applicative<F>()): Unit =
            forAll(genApplicative(Gen.int(), AP), { fa: HK<F, Int> ->
                AP.map(fa, ::identity) == fa
            })

    inline fun <reified F> covariantComposition(AP: Applicative<F> = applicative<F>()): Unit =
            forAll(
                    genApplicative(Gen.int(), AP),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    { fa: HK<F, Int>, f, g ->
                        AP.map(AP.map(fa, f), g) == AP.map(fa, f andThen g)
                    }
            )

}



