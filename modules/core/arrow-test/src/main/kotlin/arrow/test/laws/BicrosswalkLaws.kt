package arrow.test.laws

import arrow.Kind
import arrow.Kind2
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.extensions.listk.align.align
import arrow.core.extensions.listk.eqK.eqK
import arrow.core.k
import arrow.typeclasses.Align
import arrow.typeclasses.Bicrosswalk
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.abs

object BicrosswalkLaws {

  fun <T> laws(
    BCW: Bicrosswalk<T>,
    gen: Gen<Kind2<T, Int, Int>>,
    EQ: Eq<Kind2<T, String, String>>
  ): List<Law> {

    val funGen = object : Gen<(Int) -> Kind<ForListK, String>> {
      override fun constants(): Iterable<(Int) -> ListK<String>> = listOf(
        { _: Int -> ListK.empty<String>() }, { _: Int -> ListK.just("value") }
      )

      override fun random(): Sequence<(Int) -> ListK<String>> = generateSequence({ int: Int -> List(abs(int % 1000)) { "$it" }.k() }) { it }
    }

    return listOf(
      Law("Bicrosswalk Laws: bicrosswalk an empty structure == an empty structure") {
        BCW.bicrosswalkEmpty(ListK.align(), gen, buildEq(ListK.eqK(), EQ))
      },
      Law("Bicrosswalk Laws: bicrosswalk function == fmap function andThen sequenceL") {
        BCW.bicrosswalkSequencelEquality(ListK.align(), gen, funGen, funGen, buildEq(ListK.eqK(), EQ))
      }
    )
  }

  private fun <F, A> buildEq(EQK: EqK<F>, EQ: Eq<A>): Eq<Kind<F, A>> =
    Eq { a, b ->
      EQK.run { a.eqK(b, EQ) }
    }

  fun <T, F, A, B, C, D> Bicrosswalk<T>.bicrosswalkEmpty(
    ALIGN: Align<F>,
    G: Gen<Kind2<T, A, B>>,
    EQ: Eq<Kind<F, Kind2<T, C, D>>>
  ) = forAll(G) { a: Kind2<T, A, B> ->

    val ls = bicrosswalk(ALIGN, a, { ALIGN.empty<C>() }, { ALIGN.empty<D>() })
    val rs = ALIGN.empty<Kind2<T, C, D>>()

    ls.equalUnderTheLaw(rs, EQ)
  }

  fun <T, F, A, B, C, D> Bicrosswalk<T>.bicrosswalkSequencelEquality(
    ALIGN: Align<F>,
    G: Gen<Kind2<T, A, B>>,
    faGen: Gen<(A) -> Kind<F, C>>,
    fbGen: Gen<(B) -> Kind<F, D>>,
    EQ: Eq<Kind<F, Kind2<T, C, D>>>
  ) {
    forAll(G, faGen, fbGen) { a: Kind2<T, A, B>, fa: (A) -> Kind<F, C>, fb: (B) -> Kind<F, D> ->

      val ls = bicrosswalk(ALIGN, a, fa, fb)
      val rs = bisequenceL(ALIGN, a.bimap(fa, fb))

      ls.equalUnderTheLaw(rs, EQ)
    }
  }
}
