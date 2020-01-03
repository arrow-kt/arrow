package arrow.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.MonadFilter
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadFilterLaws {

  private fun <F> monadFilterLaws(
    MF: MonadFilter<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> {
    val EQ = EQK.liftEq(Int.eq())
    val GEN = GENK.genK(Gen.int())
    val GEN_F = Gen.functionAToB<Int, Kind<F, Int>>(GEN)

    return listOf(
      Law("MonadFilter Laws: Left Empty") { MF.monadFilterLeftEmpty(GEN_F, EQ) },
      Law("MonadFilter Laws: Right Empty") { MF.monadFilterRightEmpty(GEN, EQ) },
      Law("MonadFilter Laws: Consistency") { MF.monadFilterConsistency(GEN, EQ) },
      Law("MonadFilter Laws: Comprehension Guards") { MF.monadFilterEmptyComprehensions(EQ) },
      Law("MonadFilter Laws: Comprehension bindWithFilter Guards") { MF.monadFilterBindWithFilterComprehensions(EQ) })
  }

  fun <F> laws(
    MF: MonadFilter<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadLaws.laws(MF, GENK, EQK) +
      FunctorFilterLaws.laws(MF, GENK, EQK) +
      monadFilterLaws(MF, GENK, EQK)

  fun <F> laws(
    MF: MonadFilter<F>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadLaws.laws(MF, FF, AP, SL, GENK, EQK) +
      FunctorFilterLaws.laws(MF, GENK, EQK) +
      monadFilterLaws(MF, GENK, EQK)

  fun <F, A> MonadFilter<F>.monadFilterLeftEmpty(G: Gen<Function1<A, Kind<F, A>>>, EQ: Eq<Kind<F, A>>): Unit =
    forAll(G) { f: (A) -> Kind<F, A> ->
      empty<A>().flatMap(f).equalUnderTheLaw(empty(), EQ)
    }

  fun <F, A> MonadFilter<F>.monadFilterRightEmpty(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>): Unit =
    forAll(G) { fa: Kind<F, A> ->
      fa.flatMap { empty<A>() }.equalUnderTheLaw(empty(), EQ)
    }

  fun <F, A> MonadFilter<F>.monadFilterConsistency(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>): Unit =
    forAll(Gen.functionAToB<A, Boolean>(Gen.bool()), G) { f: (A) -> Boolean, fa: Kind<F, A> ->
      fa.filter(f).equalUnderTheLaw(fa.flatMap { a -> if (f(a)) just(a) else empty() }, EQ)
    }

  fun <F> MonadFilter<F>.monadFilterEmptyComprehensions(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.bool(), Gen.int()) { guard: Boolean, n: Int ->
      fx.monadFilter {
        continueIf(guard)
        n
      }.equalUnderTheLaw(if (!guard) empty() else just(n), EQ)
    }

  fun <F> MonadFilter<F>.monadFilterBindWithFilterComprehensions(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.bool(), Gen.int()) { guard: Boolean, n: Int ->
      fx.monadFilter {
        val x = just(n).bindWithFilter { _ -> guard }
        x
      }.equalUnderTheLaw(if (!guard) empty() else just(n), EQ)
    }
}
