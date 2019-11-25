package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.test.generators.LiftGen
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Repeat
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object RepeatLaws {
  fun <F> laws(
    RP: Repeat<F>,
    GEN: LiftGen<F>,
    EQK: EqK<F>
  ): List<Law> =
    ZipLaws.laws(RP, GEN, EQK) + repeatLaws(RP, GEN, EQK)

  fun <F> laws(
    RP: Repeat<F>,
    GEN: LiftGen<F>,
    EQK: EqK<F>,
    FOLD: Foldable<F>
  ) = ZipLaws.laws(RP, GEN, EQK, FOLD) + repeatLaws(RP, GEN, EQK)

  private fun <F, A> buildEq(EQK: EqK<F>, EQ: Eq<A>): Eq<Kind<F, A>> =
    Eq { a, b ->
      EQK.run { a.eqK(b, EQ) }
    }

  private fun <F, A> buildGen(LG: LiftGen<F>, gen: Gen<A>) =
    LG.run {
      liftGen(gen)
    }

  private fun <F> repeatLaws(
    RP: Repeat<F>,
    GEN: LiftGen<F>,
    EQK: EqK<F>
  ): List<Law> =
    listOf(
      Law("RepeatLaws: zip with rhs repeat is neutral to the lhs") {
        RP.zipWithRhsRepeatIsNeutralToTheLhs(buildGen(GEN, Gen.int()), buildEq(EQK, Int.eq()))
      },
      Law("RepeatLaws: zip with lhs repeat is neutral to the rhs") {
        RP.zipWithLhsRepeatIsNeutralToTheRhs(buildGen(GEN, Gen.int()), buildEq(EQK, Int.eq()))
      }
    )

  fun <F, A> Repeat<F>.zipWithRhsRepeatIsNeutralToTheLhs(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>) =
    forAll(G) { a: Kind<F, A> ->
      a.zip(repeat("foo")).map { it.a }.equalUnderTheLaw(a, EQ)
    }

  fun <F, A> Repeat<F>.zipWithLhsRepeatIsNeutralToTheRhs(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>) =
    forAll(G) { a: Kind<F, A> ->
      repeat("foo").zip(a).map { it.b }.equalUnderTheLaw(a, EQ)
    }
}
