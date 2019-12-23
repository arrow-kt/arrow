package arrow.test.laws

import arrow.Kind
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.eq
import arrow.core.identity
import arrow.core.some
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.test.generators.intSmall
import arrow.test.generators.option
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.FunctorFilter
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorFilterLaws {

  fun <F> laws(FFF: FunctorFilter<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val GEN = GENK.genK(Gen.int())
    val EQ = EQK.liftEq(Int.eq())

    return FunctorLaws.laws(FFF, GENK, EQK) + listOf(
        Law("Functor Filter: filterMap composition") { FFF.filterMapComposition(GEN, EQ) },
        Law("Functor Filter: filterMap map consistency") { FFF.filterMapMapConsistency(GEN, EQ) },
        Law("Functor Filter: flattenOption filterMap consistency") { FFF.flattenOptionConsistentWithfilterMap(GEN, EQ) },
        Law("Functor Filter: filter filterMap consistency") { FFF.filterConsistentWithfilterMap(GEN, EQ) }
      )
  }

  fun <F> FunctorFilter<F>.filterMapComposition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G,
      Gen.functionAToB<Int, Option<Int>>(Gen.option(Gen.intSmall())),
      Gen.functionAToB<Int, Option<Int>>(Gen.option(Gen.intSmall()))
    ) { fa: Kind<F, Int>, f, g ->
      fa.filterMap(f).filterMap(g).equalUnderTheLaw(fa.filterMap { a -> f(a).flatMap(g) }, EQ)
    }

  fun <F> FunctorFilter<F>.filterMapMapConsistency(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G,
      Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind<F, Int>, f ->
      fa.filterMap { Some(f(it)) }.equalUnderTheLaw(fa.map(f), EQ)
    }

  fun <F> FunctorFilter<F>.flattenOptionConsistentWithfilterMap(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G
    ) { fa: Kind<F, Int> ->
      fa.map { it.some() }.flattenOption().equalUnderTheLaw(fa.filterMap { Some(identity(it)) }, EQ)
    }

  fun <F> FunctorFilter<F>.filterConsistentWithfilterMap(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G,
      Gen.functionAToB<Int, Boolean>(Gen.bool())
    ) { fa: Kind<F, Int>, f ->
      fa.filter(f).equalUnderTheLaw(fa.filterMap { if (f(it)) Some(it) else None }, EQ)
    }
}
