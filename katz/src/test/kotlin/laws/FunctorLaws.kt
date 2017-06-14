package katz

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

inline fun <reified F> functorLaws(): List<Law> =
        listOf(
               Law("Functor Laws: Covariant Identity", { covariantIdentity<F>() }),
               Law("Functor: Covariant Composition", {  covariantComposition<F>() })
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




