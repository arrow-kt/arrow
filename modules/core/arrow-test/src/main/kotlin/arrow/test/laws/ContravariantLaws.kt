package arrow.test.laws

import arrow.Kind
import arrow.core.compose
import arrow.test.generators.functionAToB
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ContravariantLaws {

  fun <F> laws(CF: Contravariant<F>, G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): List<Law> =
    InvariantLaws.laws(CF, G, EQ) + listOf(
      Law("Contravariant Laws: Contravariant Identity") { CF.identity(G, EQ) },
      Law("Contravariant Laws: Contravariant Composition") { CF.composition(G, EQ) }
    )

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
