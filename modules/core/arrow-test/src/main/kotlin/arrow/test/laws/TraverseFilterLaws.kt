package arrow.test.laws

import arrow.Kind
import arrow.core.None
import arrow.core.Some
import arrow.core.extensions.eq
import arrow.test.generators.GenK
import arrow.test.generators.applicative
import arrow.test.generators.functionAToB
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.TraverseFilter
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object TraverseFilterLaws {

  fun <F> laws(
    TF: TraverseFilter<F>,
    GA: Applicative<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    laws(TF, GA, GENK.genK(Gen.int()), EQK)

  @Deprecated("should be internal, use GENK one")
  // FIXME(paco): TraverseLaws cannot receive AP::just due to a crash caused by the inliner. Check in TraverseLaws why.
  fun <F> laws(
    TF: TraverseFilter<F>,
    GA: Applicative<F>,
    GEN: Gen<Kind<F, Int>>,
    EQK: EqK<F>
  ): List<Law> {
    val EQ = EQK.liftEq(Int.eq())
    val EQ_NESTED = EQK.liftEq(EQ)

    return TraverseLaws.laws(TF, GEN, EQK) +
      listOf(
        Law("TraverseFilter Laws: Identity") { TF.identityTraverseFilter(GEN, GA, EQ_NESTED) },
        Law("TraverseFilter Laws: filterA consistent with TraverseFilter") { TF.filterAconsistentWithTraverseFilter(GEN, GA, EQ_NESTED) }
      )
  }

  fun <F> TraverseFilter<F>.identityTraverseFilter(GEN: Gen<Kind<F, Int>>, GA: Applicative<F>, EQ: Eq<Kind<F, Kind<F, Int>>> = Eq.any()) =
    forAll(GEN) { fa: Kind<F, Int> ->
      fa.traverseFilter(GA) { GA.just(Some(it)) }.equalUnderTheLaw(GA.just(fa), EQ)
    }

  fun <F> TraverseFilter<F>.filterAconsistentWithTraverseFilter(GEN: Gen<Kind<F, Int>>, GA: Applicative<F>, EQ: Eq<Kind<F, Kind<F, Int>>> = Eq.any()) = run {
    forAll(GEN, Gen.functionAToB<Int, Kind<F, Boolean>>(Gen.bool().applicative(GA))) { fa: Kind<F, Int>, f: (Int) -> Kind<F, Boolean> ->
      fa.filterA(f, GA).equalUnderTheLaw(fa.traverseFilter(GA) { a -> f(a).map { b: Boolean -> if (b) Some(a) else None } }, EQ)
    }
  }
}
