package arrow.test.laws

import arrow.Kind
import arrow.core.compose
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ContravariantLaws {

    inline fun <F> laws(CF: Contravariant<F>, noinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
        listOf(
            Law("Contravariant Laws: Contravariant Identity") { CF.identity(cf, EQ) },
            Law("Contravariant Laws: Contravariant Composition") { CF.composition(cf, EQ) }
        )

    fun <F> Contravariant<F>.identity(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
        forAll(genConstructor(Gen.int(), cf)) { fa: Kind<F, Int> ->
            fa.contramap { it: Int -> it }.equalUnderTheLaw(fa, EQ)
        }

    fun <F> Contravariant<F>.composition(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
        forAll(
            genConstructor(Gen.int(), cf),
            genFunctionAToB<Int, Int>(Gen.int()),
            genFunctionAToB<Int, Int>(Gen.int())
        ) { fa: Kind<F, Int>, f, g ->
            fa.contramap(f).contramap(g).equalUnderTheLaw(fa.contramap(f compose g), EQ)
        }

}