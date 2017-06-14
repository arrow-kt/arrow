package katz

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

inline fun <reified F, reified A, reified B, reified C> functorLaws(): List<Law> =
        listOf(
                Law("Functor: Covariant Identity", { covariantIdentity<F, A>() }) ,
                Law("Functor: Covariant Composition", {  covariantComposition<F, A, B, C>()})
        )

inline fun <reified F, reified A> covariantIdentity(functor: Functor<F> = functor<F>()): Unit =
        forAll(genMonad(), { fa: HK<F, A> ->
            functor.map(fa, ::identity) == fa
        })

inline fun <reified F, reified A, reified B, reified C> covariantComposition(functor: Functor<F> = functor<F>()): Unit =
        forAll(
                genMonad(),
                genFunctionAToB<A, B>(Gen.default()),
                genFunctionAToB<B, C>(Gen.default()),
                { fa: HK<F, A>, f, g ->
                    functor.map(functor.map(fa, f), g) == functor.map(fa, f andThen g)
                }
        )




