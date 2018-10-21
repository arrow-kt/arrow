package arrow.test.laws

import arrow.Kind2
import arrow.core.andThen
import arrow.test.generators.genDoubleConstructor
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.Bifunctor
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BifunctorLaws {
    fun <F> laws(BF: Bifunctor<F>, f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): List<Law> =
        listOf(
            Law("Bifunctor Laws: Identity") { BF.identity(f, EQ) },
            Law("Bifunctor Laws: Composition") { BF.composition(f, EQ) }
        )

    fun <F> Bifunctor<F>.identity(f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
        forAll(genDoubleConstructor(Gen.int(), f)) { fa: Kind2<F, Int, Int> ->
            fa.bimap({ it }, { it }).equalUnderTheLaw(fa, EQ)
        }

    fun <F> Bifunctor<F>.composition(f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
        forAll(
            genDoubleConstructor(Gen.int(), f),
            genFunctionAToB<Int, Int>(Gen.int()),
            genFunctionAToB<Int, Int>(Gen.int()),
            genFunctionAToB<Int, Int>(Gen.int()),
            genFunctionAToB<Int, Int>(Gen.int())
        ) { fa: Kind2<F, Int, Int>, ff, g, x, y ->
            fa.bimap(ff, g).bimap(x, y).equalUnderTheLaw(fa.bimap(ff andThen x, g andThen y), EQ)
        }

}
