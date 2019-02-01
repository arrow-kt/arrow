package arrow.test.laws

import arrow.Kind
import arrow.core.Option
import arrow.core.Some
import arrow.mtl.typeclasses.FunctorFilter
import arrow.test.generators.*
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorFilterLaws {

  fun <F> laws(FFF: FunctorFilter<F>, cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
    FunctorLaws.laws(FFF, cf, EQ) + listOf(
      Law("Functor Filter: mapFilter composition") { FFF.mapFilterComposition(cf, EQ) },
      Law("Functor Filter: mapFilter map consistency") { FFF.mapFilterMapConsistency(cf, EQ) }
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
}
