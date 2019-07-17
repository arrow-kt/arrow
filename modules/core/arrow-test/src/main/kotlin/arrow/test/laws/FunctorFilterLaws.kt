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
      Law("Functor Filter: mapFilter composition") { FFF.mapFilterComposition(cf, EQ) },
      Law("Functor Filter: mapFilter map consistency") { FFF.mapFilterMapConsistency(cf, EQ) },
      Law("Functor Filter: flattenOption mapFilter consistency") { FFF.flattenOptionConsistentWithMapFilter(cf, EQ) },
      Law("Functor Filter: filter mapFilter consistency") { FFF.filterConsistentWithMapFilter(cf, EQ) }
    )

  fun <F> FunctorFilter<F>.mapFilterComposition(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      Gen.int().map(ff),
      Gen.functionAToB<Int, Option<Int>>(Gen.option(Gen.intSmall())),
      Gen.functionAToB<Int, Option<Int>>(Gen.option(Gen.intSmall()))
    ) { fa: Kind<F, Int>, f, g ->
      fa.mapFilter(f).mapFilter(g).equalUnderTheLaw(fa.mapFilter { a -> f(a).flatMap(g) }, EQ)
    }

  fun <F> FunctorFilter<F>.mapFilterMapConsistency(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      Gen.int().map(ff),
      Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind<F, Int>, f ->
      fa.mapFilter { Some(f(it)) }.equalUnderTheLaw(fa.map(f), EQ)
    }

  fun <F> FunctorFilter<F>.flattenOptionConsistentWithMapFilter(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      Gen.int().map(ff)
    ) { fa: Kind<F, Int> ->
      fa.map { it.some() }.flattenOption().equalUnderTheLaw(fa.mapFilter { Some(identity(it)) }, EQ)
    }

  fun <F> FunctorFilter<F>.filterConsistentWithMapFilter(ff: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      Gen.int().map(ff),
      Gen.functionAToB<Int, Boolean>(Gen.bool())
    ) { fa: Kind<F, Int>, f ->
      fa.filter(f).equalUnderTheLaw(fa.mapFilter { if (f(it)) Some(it) else None }, EQ)
    }
}
