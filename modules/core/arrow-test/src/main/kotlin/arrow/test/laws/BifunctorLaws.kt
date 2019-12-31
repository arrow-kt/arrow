package arrow.test.laws

import arrow.Kind2
import arrow.core.andThen
import arrow.core.extensions.eq
import arrow.test.generators.GenK2
import arrow.test.generators.functionAToB
import arrow.typeclasses.Bifunctor
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK2
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BifunctorLaws {

  fun <F> laws(BF: Bifunctor<F>, GENK: GenK2<F>, EQK: EqK2<F>): List<Law> {
    val G = GENK.genK(Gen.int(), Gen.int())
    val EQ = EQK.liftEq(Int.eq(), Int.eq())

    return listOf(
      Law("Bifunctor Laws: Identity") { BF.identity(G, EQ) },
      Law("Bifunctor Laws: Composition") { BF.composition(G, EQ) }
    )
  }

  fun <F> Bifunctor<F>.identity(G: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(G) { fa: Kind2<F, Int, Int> ->
      fa.bimap({ it }, { it }).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Bifunctor<F>.composition(G: Gen<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): Unit =
    forAll(
      G,
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind2<F, Int, Int>, ff, g, x, y ->
      fa.bimap(ff, g).bimap(x, y).equalUnderTheLaw(fa.bimap(ff andThen x, g andThen y), EQ)
    }
}
