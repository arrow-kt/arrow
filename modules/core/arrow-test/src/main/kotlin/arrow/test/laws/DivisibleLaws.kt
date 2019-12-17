package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.typeclasses.Divisible
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object DivisibleLaws {
  fun <F> laws(
    DF: Divisible<F>,
    G: Gen<Kind<F, Int>>,
    EQK: EqK<F>
  ): List<Law> {
    val EQ = EQK.liftEq(Int.eq())

    return DivideLaws.laws(DF, G, EQK) + listOf(
      Law("Divisible laws: Left identity") { DF.leftIdentity(G, EQ) },
      Law("Divisible laws: Right identity") { DF.rightIdentity(G, EQ) }
    )
  }

  fun <F> Divisible<F>.leftIdentity(
    G: Gen<Kind<F, Int>>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(G) { fa ->
      divide<Int, Int, Int>(fa, conquer()) { DivideLaws.delta(it) }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Divisible<F>.rightIdentity(
    G: Gen<Kind<F, Int>>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(G) { fa ->
      divide<Int, Int, Int>(conquer(), fa) { DivideLaws.delta(it) }.equalUnderTheLaw(fa, EQ)
    }
}
