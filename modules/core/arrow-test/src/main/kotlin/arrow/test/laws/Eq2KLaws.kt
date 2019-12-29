package arrow.test.laws

import arrow.Kind2
import arrow.core.extensions.eq
import arrow.test.generators.Gen2K
import arrow.typeclasses.Eq2K
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object Eq2KLaws {

  fun <F> laws(
    EQK: Eq2K<F>,
    GENK: Gen2K<F>
  ): List<Law> {
    val G = GENK.genK(Gen.int(), Gen.int())

    return listOf(
      Law("Eq2K Laws: reflexivity") { EQK.eqkReflexivity(G) },
      Law("Eq2K Laws: symmetry") { EQK.eqKSymmetry(G) },
      Law("Eq2K Laws: transitivity") { EQK.eqKTransitivity(G) }
    )
  }

  fun <F> Eq2K<F>.eqkReflexivity(G: Gen<Kind2<F, Int, Int>>) =
    forAll(G) { x ->
      x.eqK(x, Int.eq(), Int.eq())
    }

  fun <F> Eq2K<F>.eqKSymmetry(G: Gen<Kind2<F, Int, Int>>) =
    forAll(G, G) { x, y ->
      x.eqK(y, Int.eq(), Int.eq()) == y.eqK(x, Int.eq(), Int.eq())
    }

  fun <F> Eq2K<F>.eqKTransitivity(G: Gen<Kind2<F, Int, Int>>) =
    forAll(G, G, G) { x, y, z ->
      !(x.eqK(y, Int.eq(), Int.eq()) && y.eqK(z, Int.eq(), Int.eq())) || x.eqK(z, Int.eq(), Int.eq())
    }
}
