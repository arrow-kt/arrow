package arrow.test.laws

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.extensions.eq
import arrow.core.extensions.listk.align.align
import arrow.core.extensions.listk.eq.eq
import arrow.core.k
import arrow.test.generators.GenK
import arrow.typeclasses.Align
import arrow.typeclasses.Crosswalk
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.abs

object CrosswalkLaws {

  fun <T> laws(
    CW: Crosswalk<T>,
    GENK: GenK<T>,
    EQK: EqK<T>
  ): List<Law> {

    val funGen = object : Gen<(Int) -> Kind<ForListK, String>> {
      override fun constants(): Iterable<(Int) -> ListK<String>> = listOf(
        { int: Int -> listOf("$int").k() },
        { int: Int -> List(abs(int % 100)) { "$it" }.k() })

      override fun random(): Sequence<(Int) -> ListK<String>> = emptySequence()
    }

    return listOf(
      Law("Crosswalk laws: crosswalk an empty structure == an empty structure") {
        CW.crosswalkEmptyIsEmpty(ListK.align(), GENK.genK(Gen.int()), ListK.eq(EQK.liftEq(Eq.any())))
      },
      Law("Crosswalk laws: crosswalk function == fmap function andThen sequenceL") {
        CW.crosswalkFunctionEqualsComposeSequenceAndFunction(ListK.align(), GENK.genK(Gen.int()), funGen, ListK.eq(EQK.liftEq(String.eq())))
      }
    )
  }

  fun <T, F, A, B> Crosswalk<T>.crosswalkEmptyIsEmpty(
    ALIGN: Align<F>,
    G: Gen<Kind<T, A>>,
    EQ: Eq<Kind<F, Kind<T, B>>>
  ) = forAll(G) { a: Kind<T, A> ->

    val ls: (Kind<T, A>) -> Kind<F, Kind<T, B>> = { ta -> crosswalk(ALIGN, ta) { ALIGN.empty<B>() } }
    val rs: (Kind<T, A>) -> Kind<F, Kind<T, B>> = { ALIGN.empty() }

    ls(a).equalUnderTheLaw(rs(a), EQ)
  }

  fun <T, F, A, B> Crosswalk<T>.crosswalkFunctionEqualsComposeSequenceAndFunction(
    ALIGN: Align<F>,
    aGen: Gen<Kind<T, A>>,
    faGen: Gen<(A) -> Kind<F, B>>,
    EQ: Eq<Kind<F, Kind<T, B>>>
  ) = forAll(aGen, faGen) { a: Kind<T, A>, fa: (A) -> Kind<F, B> ->

    val ls = { ta: Kind<T, A> -> crosswalk(ALIGN, ta, fa) }
    val rs = { ta: Kind<T, A> -> sequenceL(ALIGN, ta.map(fa)) }

    ls(a).equalUnderTheLaw(rs(a), EQ)
  }
}
