package arrow.test.laws

import arrow.Kind
import arrow.core.ListK
import arrow.core.extensions.listk.align.align
import arrow.core.extensions.listk.eq.eq
import arrow.typeclasses.Align
import arrow.typeclasses.Crosswalk
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object CrosswalkLaws {
  fun <T> laws(
    CW: Crosswalk<T>,
    gen: Gen<Kind<T, Int>>,
    EQK: EqK<T>
  ): List<Law> {

    val eq = buildEq(EQK, Eq.any())

    return listOf(
      Law("Crosswalk laws: law #1") {
        CW.law1(ListK.align(), gen, ListK.eq(eq))
      },
      Law("Crosswalk laws: law #2") {
        CW.law2(ListK.align(), gen, ListK.eq(eq))
      }
    )
  }

  private fun <F, A> buildEq(EQK: EqK<F>, EQ: Eq<A>): Eq<Kind<F, A>> =
    Eq { a, b ->
      EQK.run { a.eqK(b, EQ) }
    }

  fun <T, F, A, B> Crosswalk<T>.law1(
    ALIGN: Align<F>,
    G: Gen<Kind<T, A>>,
    EQ: Eq<Kind<F, Kind<T, B>>>
  ) = forAll(G) { a: Kind<T, A> ->
    val constNil: (A) -> Kind<F, B> = { _: A -> ALIGN.empty() }
    val ls: (Kind<T, A>) -> Kind<F, Kind<T, B>> = { ta -> crosswalk(ALIGN, constNil, ta) }
    val rs: (Kind<T, A>) -> Kind<F, Kind<T, B>> = { ta -> ALIGN.run { empty<B>().map { b -> ta.map { b } } } }

    ls(a).equalUnderTheLaw(rs(a), EQ)
  }

  fun <T, F, A> Crosswalk<T>.law2(
    ALIGN: Align<F>,
    G: Gen<Kind<T, A>>,
    EQ: Eq<Kind<F, Kind<T, *>>>
  ) = forAll(G) { a: Kind<T, A> ->

    val f: (A) -> Kind<F, *> = {
      ALIGN.empty<Any>()
    }

    val ls = { ta: Kind<T, A> -> crosswalk(ALIGN, f, ta) }
    val rs = { ta: Kind<T, A> -> sequenceL(ALIGN, ta.map(f)) }

    ls(a).equalUnderTheLaw(rs(a), EQ)
  }
}
