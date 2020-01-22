package arrow.test.laws

import arrow.Kind2
import arrow.core.extensions.eq
import arrow.test.generators.GenK2
import arrow.typeclasses.EqK2
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object EqK2Laws {

  fun <F> laws(
    EQK: EqK2<F>,
    GENK: GenK2<F>
  ): List<Law> {
    val G = GENK.genK(Gen.int(), Gen.int())

    return listOf(
      Law("EqK2 Laws: reflexivity") { EQK.eqkReflexivity(G) },
      Law("EqK2 Laws: symmetry") { EQK.eqKSymmetry(G) },
      Law("EqK2 Laws: transitivity") { EQK.eqKTransitivity(G) }
    )
  }

  fun <F> EqK2<F>.eqkReflexivity(G: Gen<Kind2<F, Int, Int>>) =
    forAll(G) { x ->
      x.eqK(x, Int.eq(), Int.eq())
    }

  fun <F> EqK2<F>.eqKSymmetry(G: Gen<Kind2<F, Int, Int>>) =
    forAll(G, G) { x, y ->
      x.eqK(y, Int.eq(), Int.eq()) == y.eqK(x, Int.eq(), Int.eq())
    }

  fun <F> EqK2<F>.eqKTransitivity(G: Gen<Kind2<F, Int, Int>>) =
    forAll(G, G, G) { x, y, z ->
      !(x.eqK(y, Int.eq(), Int.eq()) && y.eqK(z, Int.eq(), Int.eq())) || x.eqK(z, Int.eq(), Int.eq())
    }
}
