package arrow.test.laws

import arrow.Kind
import arrow.typeclasses.Divisible
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object DivisibleLaws {
  fun <F> laws(
    DF: Divisible<F>,
    cf: (Int) -> Kind<F, Int>,
    EQ: Eq<Kind<F, Int>>
  ): List<Law> = DivideLaws.laws(DF, cf, EQ) + listOf(
    Law("Divisible laws: Left identity") { DF.leftIdentity(cf, EQ) },
    Law("Divisible laws: Right identity") { DF.rightIdentity(cf, EQ) }
  )

  fun <F> Divisible<F>.leftIdentity(
    cf: (Int) -> Kind<F, Int>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int().map(cf)) { fa ->
      divide<Int, Int, Int>(fa, conquer()) { DivideLaws.delta(it) }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Divisible<F>.rightIdentity(
    cf: (Int) -> Kind<F, Int>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int().map(cf)) { fa ->
      divide<Int, Int, Int>(conquer(), fa) { DivideLaws.delta(it) }.equalUnderTheLaw(fa, EQ)
    }
}
