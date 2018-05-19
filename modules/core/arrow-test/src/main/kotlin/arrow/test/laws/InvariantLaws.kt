package arrow.test.laws

import arrow.Kind
import arrow.core.andThen
import arrow.core.identity
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Invariant
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object InvariantLaws {

  fun <F> laws(AP: Applicative<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
    listOf(
      Law("Invariant Laws: Invariant Identity", { AP.invariantIdentity(AP::just, EQ) }),
      Law("Invariant Laws: Invariant Composition", { AP.invariantComposition(AP::just, EQ) })
    )

  fun <F> laws(FI: Invariant<F>, f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
    listOf(
      Law("Invariant Laws: Invariant Identity", { FI.invariantIdentity(f, EQ) }),
      Law("Invariant Laws: Invariant Composition", { FI.invariantComposition(f, EQ) })
    )

  fun <F> Invariant<F>.invariantIdentity(f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(genConstructor(Gen.int(), f), { fa: Kind<F, Int> ->
      fa.imap(::identity, ::identity).equalUnderTheLaw(fa, EQ)
    })

  fun <F> Invariant<F>.invariantComposition(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      genConstructor(Gen.int(), ff),
      genFunctionAToB<Int, Int>(Gen.int()),
      genFunctionAToB<Int, Int>(Gen.int()),
      genFunctionAToB<Int, Int>(Gen.int()),
      genFunctionAToB<Int, Int>(Gen.int()),
      { fa: Kind<F, Int>, f, g, h, i ->
        fa.imap(f, g).imap(h, i).equalUnderTheLaw(fa.imap(h andThen f, i andThen g), EQ)
      }
    )
}