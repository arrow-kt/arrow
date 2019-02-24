package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.toT
import arrow.typeclasses.Divide
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object DivideLaws {

  fun <F> laws(
    DF: Divide<F>,
    cf: (Int) -> Kind<F, Int>,
    EQ: Eq<Kind<F, Int>>
  ): List<Law> = ContravariantLaws.laws(DF, cf, EQ) + listOf(
    Law("Divide laws: Associative") { DF.associative(cf, EQ) }
  )

  fun <A> delta(a: A): Tuple2<A, A> = a toT a

  fun <F> Divide<F>.associative(
    cf: (Int) -> Kind<F, Int>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(Gen.int().map(cf)) { fa ->
      val a = divide<Int, Int, Int>(
        fa,
        divide(fa, fa) { delta(it) }
      ) { delta(it) }
      val b = divide<Int, Int, Int>(
        divide(fa, fa) { delta(it) },
        fa
      ) { delta(it) }

      a.equalUnderTheLaw(b, EQ)
    }
}
