package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.toT
import arrow.test.generators.GenK
import arrow.typeclasses.Divide
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object DivideLaws {

  fun <F> laws(
    DF: Divide<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> {
    val G = GENK.genK(Gen.int())

    return ContravariantLaws.laws(DF, GENK, EQK) + listOf(
      Law("Divide laws: Associative") { DF.associative(G, EQK.liftEq(Int.eq())) }
    )
  }

  fun <A> delta(a: A): Tuple2<A, A> = a toT a

  fun <F> Divide<F>.associative(
    G: Gen<Kind<F, Int>>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(G) { fa ->
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
