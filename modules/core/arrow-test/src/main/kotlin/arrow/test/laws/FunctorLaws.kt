package arrow.test.laws

import arrow.Kind
import arrow.core.andThen
import arrow.core.identity
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorLaws {

    inline fun <F> laws(AP: Applicative<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
            listOf(
                    Law("Functor Laws: Covariant Identity", { AP.covariantIdentity(AP::pure, EQ) }),
                    Law("Functor Laws: Covariant Composition", { AP.covariantComposition(AP::pure, EQ) })
            )

    fun <F> laws(FF: Functor<F>, f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
            listOf(
                    Law("Functor Laws: Covariant Identity", { FF.covariantIdentity(f, EQ) }),
                    Law("Functor Laws: Covariant Composition", { FF.covariantComposition(f, EQ) })
            )

    fun <F> Functor<F>.covariantIdentity(f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), f), { fa: Kind<F, Int> ->
                fa.map(::identity).equalUnderTheLaw(fa, EQ)
            })

    fun <F> Functor<F>.covariantComposition(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(
                    genConstructor(Gen.int(), ff),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    genFunctionAToB<Int, Int>(Gen.int()),
                    { fa: Kind<F, Int>, f, g ->
                        fa.map(f).map(g).equalUnderTheLaw(fa.map(f andThen g), EQ)
                    }
            )
}
