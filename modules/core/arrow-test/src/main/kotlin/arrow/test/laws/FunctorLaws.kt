package arrow.test.laws

import arrow.Kind
import arrow.core.andThen
import arrow.core.extensions.eq
import arrow.core.identity
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FunctorLaws {

  fun <F> laws(FF: Functor<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val G1 = GENK.genK(Gen.int())
    val EQ = EQK.liftEq(Int.eq())

    return InvariantLaws.laws(FF, GENK, EQK) + listOf(
        Law("Functor Laws: Covariant Identity") { FF.covariantIdentity(G1, EQ) },
        Law("Functor Laws: Covariant Composition") { FF.covariantComposition(G1, EQ) }
      )
  }

  fun <F> Functor<F>.covariantIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      fa.map(::identity).equalUnderTheLaw(fa, EQ)
    }

  fun <F> Functor<F>.covariantComposition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G,
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind<F, Int>, f, g ->
      fa.map(f).map(g).equalUnderTheLaw(fa.map(f andThen g), EQ)
    }
}
