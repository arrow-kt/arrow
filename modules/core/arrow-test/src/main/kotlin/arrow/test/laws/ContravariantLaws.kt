package arrow.test.laws

import arrow.Kind
import arrow.core.compose
import arrow.core.extensions.eq
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ContravariantLaws {

  fun <F> laws(CF: Contravariant<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val G = GENK.genK(Gen.int())

    return InvariantLaws.laws(CF, GENK, EQK) + contravariantLaws(CF, G, EQK)
  }

  private fun <F> contravariantLaws(CF: Contravariant<F>, G: Gen<Kind<F, Int>>, EQK: EqK<F>): List<Law> {
    val EQ = EQK.liftEq(Int.eq())

    return listOf(
      Law("Contravariant Laws: Contravariant Identity") { CF.identity(G, EQ) },
      Law("Contravariant Laws: Contravariant Composition") { CF.composition(G, EQ) }
    )
  }

  fun <F> Contravariant<F>.identity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      @Suppress("ExplicitItLambdaParameter")
      fa.contramap { it: Int -> it }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Contravariant<F>.composition(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G,
      Gen.functionAToB<Int, Int>(Gen.int()),
      Gen.functionAToB<Int, Int>(Gen.int())
    ) { fa: Kind<F, Int>, f, g ->
      fa.contramap(f).contramap(g).equalUnderTheLaw(fa.contramap(f compose g), EQ)
    }
}
