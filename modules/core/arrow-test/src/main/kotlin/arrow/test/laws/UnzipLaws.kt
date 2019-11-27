package arrow.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.toT
import arrow.test.generators.GenK
import arrow.test.generators.tuple2
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Unzip
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object UnzipLaws {
  fun <F> laws(
    UNZIP: Unzip<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    SemialignLaws.laws(UNZIP, buildGen(GENK, Gen.int()), EQK) + unzipLaws(UNZIP, GENK, EQK)

  fun <F> laws(
    UNZIP: Unzip<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    FOLD: Foldable<F>
  ) = SemialignLaws.laws(UNZIP, buildGen(GENK, Gen.int()), EQK, FOLD) + unzipLaws(UNZIP, GENK, EQK)

  private fun <F> unzipLaws(
    UNZIP: Unzip<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> = listOf(
    Law("zip is inverse of unzip") {
      UNZIP.zipIsInverseOfUnzip(
        buildGen(GENK, Gen.tuple2(Gen.int(), Gen.int())),
        buildEq(EQK, Tuple2.eq(Int.eq(), Int.eq()))
      )
    },
    Law("unzip is inverse of zip") {
      val intEq = buildEq(EQK, Int.eq())

      UNZIP.unipIsInverseOfZip(
        buildGen(GENK, Gen.int()),
        Tuple2.eq(intEq, intEq)
      )
    }
  )

  private fun <F, A> buildEq(EQK: EqK<F>, EQ: Eq<A>): Eq<Kind<F, A>> =
    Eq { a, b ->
      EQK.run { a.eqK(b, EQ) }
    }

  private fun <F, A> buildGen(LG: GenK<F>, gen: Gen<A>) =
    LG.run {
      genK(gen)
    }

  fun <F, A, B> Unzip<F>.zipIsInverseOfUnzip(
    G: Gen<Kind<F, Tuple2<A, B>>>,
    EQ: Eq<Kind<F, Tuple2<A, B>>>
  ) =
    forAll(G) { xs: Kind<F, Tuple2<A, B>> ->
      val (ls, rs) = xs.unzip()
      ls.zip(rs).equalUnderTheLaw(xs, EQ)
    }

  fun <F, A> Unzip<F>.unipIsInverseOfZip(
    G: Gen<Kind<F, A>>,
    EQ: Eq<Tuple2<Kind<F, A>, Kind<F, A>>>
  ) =
    forAll(G) { xs: Kind<F, A> ->
      val ls = (xs.zip(xs)).unzip()
      val rs = xs toT xs
      ls.equalUnderTheLaw(rs, EQ)
    }
}
