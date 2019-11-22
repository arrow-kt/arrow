package arrow.test.laws

import arrow.Kind
import arrow.core.Ior
import arrow.core.extensions.eq
import arrow.core.extensions.ior.eq.eq
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
    gen: Gen<Kind<F, Int>>,
    EQK: EqK<F>
  ): List<Law> =
    SemialignLaws.laws(A, gen, EQK) +
      listOf(
        Law("Align Laws: align right empty") { A.alignRightEmpty(gen, buildEq(EQK, iorEq)) },
        Law("Align Laws: align left empty") { A.alignLeftEmpty(gen, buildEq(EQK, iorEq)) }
      )

  fun <F> laws(
    A: Align<F>,
    gen: Gen<Kind<F, Int>>,
    EQK: EqK<F>,
    FOLD: Foldable<F>
  ): List<Law> = SemialignLaws.laws(A, gen, EQK, FOLD) +
    laws(A, gen, EQK)

  private fun <F, A> buildEq(EQK: EqK<F>, EQ: Eq<A>): Eq<Kind<F, A>> =
    Eq { a, b ->
      EQK.run { a.eqK(b, EQ) }
    }

  fun <F, A> Align<F>.alignRightEmpty(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Ior<A, A>>>) =
    forAll(G) { a ->
      align(a, empty<A>()).equalUnderTheLaw(a.map { Ior.Left(it) }, EQ)
    }

  fun <F, A> Align<F>.alignLeftEmpty(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, Ior<A, A>>>) =
    forAll(G) { a ->
      align(empty<A>(), a).equalUnderTheLaw(a.map { Ior.Right(it) }, EQ)
    }
}
