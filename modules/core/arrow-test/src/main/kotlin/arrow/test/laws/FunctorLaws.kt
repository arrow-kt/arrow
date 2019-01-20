package arrow.test.laws

import arrow.Kind
import arrow.core.andThen
import arrow.core.identity
import arrow.test.generators.functionAToB
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorLaws {

  fun <F> laws(AP: Applicative<F>, EQ: Eq<Kind<F, Int>>): List<Law> =
    InvariantLaws.laws(AP, AP::just, EQ) + listOf(
      Law("Functor Laws: Covariant Identity") { AP.covariantIdentity(AP::just, EQ) },
      Law("Functor Laws: Covariant Composition") { AP.covariantComposition(AP::just, EQ) }
    )

  fun <F> laws(FF: Functor<F>, f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
    InvariantLaws.laws(FF, f, EQ) + listOf(
      Law("Functor Laws: Covariant Identity") { FF.covariantIdentity(f, EQ) },
      Law("Functor Laws: Covariant Composition") { FF.covariantComposition(f, EQ) }
    )

  fun <F> Functor<F>.covariantIdentity(f: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int().map(f)) { fa: Kind<F, Int> ->
      fa.map(::identity).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Functor<F>.covariantComposition(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      Gen.int().map(ff),
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind<F, Int>, f, g ->
      fa.map(f).map(g).equalUnderTheLaw(fa.map(f andThen g), EQ)
    }
}
