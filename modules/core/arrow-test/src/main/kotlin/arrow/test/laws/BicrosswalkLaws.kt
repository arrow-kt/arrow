package arrow.test.laws

import arrow.Kind
import arrow.Kind2
import arrow.core.ListK
import arrow.core.extensions.listk.align.align
import arrow.core.extensions.listk.eqK.eqK
import arrow.typeclasses.Align
import arrow.typeclasses.Bicrosswalk
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object BicrosswalkLaws {

  fun <T> laws(
    BCW: Bicrosswalk<T>,
    gen: Gen<Kind2<T, Int, Int>>,
    EQ: Eq<Kind2<T, Int, Int>>
  ): List<Law> = listOf(
    Law("Bicrosswalk Laws: Law #1") {
      BCW.law1(ListK.align(), gen, buildEq(ListK.eqK(), EQ))
    },
    Law("Bicrosswalk Laws: Law #2") {
      BCW.law2(ListK.align(), gen, buildEq(ListK.eqK(), EQ))
    }
  )

  private fun <F, A> buildEq(EQK: EqK<F>, EQ: Eq<A>): Eq<Kind<F, A>> =
    Eq { a, b ->
      EQK.run { a.eqK(b, EQ) }
    }

  fun <T, F, A, B, C, D> Bicrosswalk<T>.law1(
    ALIGN: Align<F>,
    G: Gen<Kind2<T, A, B>>,
    EQ: Eq<Kind<F, Kind2<T, C, D>>>
  ) = forAll(G) { a: Kind2<T, A, B> ->

    val ls = bicrosswalk(ALIGN, { ALIGN.empty<C>() }, { ALIGN.empty<D>() }, a)
    val rs = ALIGN.empty<Kind2<T, C, D>>()

    ls.equalUnderTheLaw(rs, EQ)
  }

  fun <T, F, A, B, C, D> Bicrosswalk<T>.law2(
    ALIGN: Align<F>,
    G: Gen<Kind2<T, A, B>>,
    EQ: Eq<Kind<F, Kind2<T, C, D>>>
  ) = forAll(G) { a: Kind2<T, A, B> ->

    val fa: (A) -> Kind<F, C> = { ALIGN.empty() }
    val fb: (B) -> Kind<F, D> = { ALIGN.empty() }

    val ls = bicrosswalk(ALIGN, fa, fb, a)
    val rs = bisequenceL(ALIGN, a.bimap(fa, fb))

    ls.equalUnderTheLaw(rs, EQ)
  }
}
