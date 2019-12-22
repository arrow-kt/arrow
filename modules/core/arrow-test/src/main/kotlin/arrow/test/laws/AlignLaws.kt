package arrow.test.laws

import arrow.Kind
import arrow.core.Ior
import arrow.core.extensions.eq
import arrow.core.extensions.ior.eq.eq
import arrow.test.generators.GenK
import arrow.typeclasses.Align
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object AlignLaws {

  val iorEq = Ior.eq(Int.eq(), Int.eq())

  fun <F> laws(
    A: Align<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    SemialignLaws.laws(A, GENK, EQK) + alignLaws(A, GENK, EQK)

  fun <F> laws(
    A: Align<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    FOLD: Foldable<F>
  ): List<Law> = SemialignLaws.laws(A, GENK, EQK, FOLD) +
    alignLaws(A, GENK, EQK)

  private fun <F> alignLaws(
    A: Align<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ) = listOf(
    Law("Align Laws: align right empty") { A.alignRightEmpty(GENK.genK(Gen.int()), EQK.liftEq(iorEq)) },
    Law("Align Laws: align left empty") { A.alignLeftEmpty(GENK.genK(Gen.int()), EQK.liftEq(iorEq)) }
  )

  fun <F, A> Align<F>.alignRightEmpty(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Ior<A, A>>>) =
    forAll(G) { a ->
      align(a, empty<A>()).equalUnderTheLaw(a.map { Ior.Left(it) }, EQ)
    }

  fun <F, A> Align<F>.alignLeftEmpty(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Ior<A, A>>>) =
    forAll(G) { a ->
      align(empty<A>(), a).equalUnderTheLaw(a.map { Ior.Right(it) }, EQ)
    }
}
