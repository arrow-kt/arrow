package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object EqKLaws {

  fun <F> laws(
    EQK: EqK<F>,
    EQ: Eq<Kind<F, Int>>,
    gen: Gen<Kind<F, Int>>,
    cf: (Int) -> Kind<F, Int>
  ): List<Law> = listOf(
    Law("EqK Laws: reflexivity") { EQK.eqkReflexivity(cf) },
    Law("EqK Laws: symmetry") { EQK.eqKSymmetry(cf) },
    Law("EqK Laws: transitivity") { EQK.eqKTransitivity(cf) },
    Law("EqK Laws: eqK == eq") { EQK.eqKCanSubstituteEq(gen, EQ) }
  )

  fun <F> EqK<F>.eqkReflexivity(cf: (Int) -> Kind<F, Int>) = forAll { int: Int ->
    cf(int).eqK(cf(int), Int.eq())
  }

  fun <F> EqK<F>.eqKSymmetry(cf: (Int) -> Kind<F, Int>) = forAll { int: Int ->
    val x = cf(int)
    val y = cf(int)

    x.eqK(y, Int.eq()) && y.eqK(x, Int.eq())
  }

  fun <F> EqK<F>.eqKTransitivity(cf: (Int) -> Kind<F, Int>) = forAll { int: Int ->
    val x = cf(int)
    val y = cf(int)
    val z = cf(int)

    !(x.eqK(y, Int.eq()) && y.eqK(z, Int.eq())) || x.eqK(z, Int.eq())
  }

  fun <F> EqK<F>.eqKCanSubstituteEq(
    G: Gen<Kind<F, Int>>,
    EQ: Eq<Kind<F, Int>>
  ) = forAll(G, G) { a, b ->
    a.eqK(b, Int.eq()) == EQ.run { a.eqv(b) }
  }
}
