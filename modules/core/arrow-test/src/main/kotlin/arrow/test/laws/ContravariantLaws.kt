package arrow.test.laws

import arrow.Kind
import arrow.core.andThen
import arrow.core.identity
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ContravariantLaws {

  fun <F> laws(FC: Contravariant<F>, f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
    listOf(
      Law("Contravariant Laws: Contravariant Identity", { FC.contravariantIdentity(f, EQ) }),
      Law("Contravariant Laws: Contravariant Composition", { FC.contravariantComposition(f, EQ) })
    )

  fun <F> Contravariant<F>.contravariantIdentity(f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genConstructor(Gen.int(), f), { fa: Kind<F, Int> ->
      fa.contramap<Int, Int>(::identity).equalUnderTheLaw(fa, EQ)
    })

  fun <F> Contravariant<F>.contravariantComposition(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      genConstructor(Gen.int(), ff),
      genFunctionAToB<Int, Int>(Gen.int()),
      genFunctionAToB<Int, Int>(Gen.int()),
      { fa: Kind<F, Int>, f, g ->
        fa.contramap(f).contramap(g).equalUnderTheLaw(fa.contramap(f andThen g), EQ)
      }
    )
}