package arrow.test.laws

import arrow.Kind
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import arrow.core.some
import arrow.test.generators.functionAToB
import arrow.test.generators.intSmall
import arrow.test.generators.option
import arrow.typeclasses.Eq
import arrow.typeclasses.FunctorFilter
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorFilterLaws {
  fun <F> laws(FFF: FunctorFilter<F>, cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
    FunctorLaws.laws(FFF, cf, EQ) + listOf(
      Law("Functor Filter: filterMap composition") { FFF.filterMapComposition(cf, EQ) },
      Law("Functor Filter: filterMap map consistency") { FFF.filterMapMapConsistency(cf, EQ) },
      Law("Functor Filter: flattenOption filterMap consistency") { FFF.flattenOptionConsistentWithfilterMap(cf, EQ) },
      Law("Functor Filter: filter filterMap consistency") { FFF.filterConsistentWithfilterMap(cf, EQ) }
    )

  fun <F> FunctorFilter<F>.filterMapComposition(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      Gen.int().map(ff),
      Gen.functionAToB<Int, Option<Int>>(Gen.option(Gen.intSmall())),
      Gen.functionAToB<Int, Option<Int>>(Gen.option(Gen.intSmall()))
    ) { fa: Kind<F, Int>, f, g ->
      fa.filterMap(f).filterMap(g).equalUnderTheLaw(fa.filterMap { a -> f(a).flatMap(g) }, EQ)
    }

  fun <F> FunctorFilter<F>.filterMapMapConsistency(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      Gen.int().map(ff),
      Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind<F, Int>, f ->
      fa.filterMap { Some(f(it)) }.equalUnderTheLaw(fa.map(f), EQ)
    }

  fun <F> FunctorFilter<F>.flattenOptionConsistentWithfilterMap(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      Gen.int().map(ff)
    ) { fa: Kind<F, Int> ->
      fa.map { it.some() }.flattenOption().equalUnderTheLaw(fa.filterMap { Some(identity(it)) }, EQ)
    }

  fun <F> FunctorFilter<F>.filterConsistentWithfilterMap(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      Gen.int().map(ff),
      Gen.functionAToB<Int, Boolean>(Gen.bool())
    ) { fa: Kind<F, Int>, f ->
      fa.filter(f).equalUnderTheLaw(fa.filterMap { if (f(it)) Some(it) else None }, EQ)
    }
}
