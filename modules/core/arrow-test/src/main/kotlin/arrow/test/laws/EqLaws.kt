package arrow.test.laws

import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object EqLaws {

  fun <F> laws(
    EQ: Eq<F>,
    GEN: Gen<F>
  ): List<Law> =
    listOf(
      Law("Eq Laws: reflexivity") { EQ.eqReflexivity(GEN) },
      Law("Eq Laws: symmetry") { EQ.eqSymmetry(GEN) },
      Law("Eq Laws: transitivity") { EQ.eqTransitivity(GEN) }
    )

  fun <F> Eq<F>.eqReflexivity(G: Gen<F>) =
    forAll(G) { x ->
      x.eqv(x)
    }

  fun <F> Eq<F>.eqSymmetry(G: Gen<F>) =
    forAll(G, G) { x, y ->
      x.eqv(y) == y.eqv(x)
    }

  fun <F> Eq<F>.eqTransitivity(G: Gen<F>) =
    forAll(G, G, G) { x, y, z ->
      !(x.eqv(y) && y.eqv(z)) || x.eqv(z)
    }
}
