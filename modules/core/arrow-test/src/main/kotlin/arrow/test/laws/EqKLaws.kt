package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.test.generators.GenK
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object EqKLaws {

  fun <F> laws(
    EQK: EqK<F>,
    GENK: GenK<F>
  ): List<Law> =
    GENK.genK(Gen.int()).let { gen ->
      listOf(
        Law("EqK Laws: reflexivity") { EQK.eqkReflexivity(gen) },
        Law("EqK Laws: symmetry") { EQK.eqKSymmetry(gen) },
        Law("EqK Laws: transitivity") { EQK.eqKTransitivity(gen) }
      )
    }

  fun <F> EqK<F>.eqkReflexivity(G: Gen<Kind<F, Int>>) = forAll(G) { x: Kind<F, Int> ->
    x.eqK(x, Int.eq())
  }

  fun <F> EqK<F>.eqKSymmetry(G: Gen<Kind<F, Int>>) = forAll(G, G) { x: Kind<F, Int>, y: Kind<F, Int> ->
    x.eqK(y, Int.eq()) == y.eqK(x, Int.eq())
  }

  fun <F> EqK<F>.eqKTransitivity(G: Gen<Kind<F, Int>>) = forAll(G, G, G) { x: Kind<F, Int>, y: Kind<F, Int>, z: Kind<F, Int> ->
    !(x.eqK(y, Int.eq()) && y.eqK(z, Int.eq())) || x.eqK(z, Int.eq())
  }
}
