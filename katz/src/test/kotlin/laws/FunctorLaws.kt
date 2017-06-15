package katz

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorLaws {

    inline fun <reified F> laws(functor: Functor<F> = functor<F>()): List<Law> =
            listOf(
                    Law("Functor Laws: Covariant Identity", { covariantIdentity(functor) }),
                    Law("Functor: Covariant Composition", { covariantComposition(functor) })
            )

    inline fun <reified F> covariantIdentity(functor: Functor<F> = functor<F>()): Unit =
            forAll(genApplicative<F, Int>(Gen.int()), { fa: HK<F, Int> ->
                functor.map(fa, ::identity) == fa
            })

    inline fun <reified F> covariantComposition(functor: Functor<F> = functor<F>()): Unit =
            forAll(
                    genApplicative<F, Int>(Gen.int()),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    { fa: HK<F, Int>, f, g ->
                        functor.map(functor.map(fa, f), g) == functor.map(fa, f andThen g)
                    }
            )

}



