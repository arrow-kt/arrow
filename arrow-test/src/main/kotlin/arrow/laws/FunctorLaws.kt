package arrow

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorLaws {

    inline fun <reified F> laws(AP: Applicative<F> = applicative<F>(), EQ: Eq<HK<F, Int>>): List<Law> =
            listOf(
                    Law("Functor Laws: Covariant Identity", { covariantIdentity(AP, AP::pure, EQ) }),
                    Law("Functor Laws: Covariant Composition", { covariantComposition(AP, AP::pure, EQ) })
            )

    inline fun <reified F> laws(FF: Functor<F> = functor<F>(), crossinline f: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): List<Law> =
            listOf(
                    Law("Functor Laws: Covariant Identity", { covariantIdentity(FF, f, EQ) }),
                    Law("Functor Laws: Covariant Composition", { covariantComposition(FF, f, EQ) })
            )

    inline fun <reified F> covariantIdentity(FF: Functor<F> = functor<F>(), crossinline f: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), f), { fa: HK<F, Int> ->
                FF.map(fa, ::identity).equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> covariantComposition(FF: Functor<F> = functor<F>(), crossinline ff: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(
                    genConstructor(Gen.int(), ff),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    { fa: HK<F, Int>, f, g ->
                        FF.map(FF.map(fa, f), g).equalUnderTheLaw(FF.map(fa, f andThen g), EQ)
                    }
            )

}



