package arrow.test.laws

import arrow.Kind
import arrow.core.None
import arrow.core.Some
import arrow.core.extensions.eq
import arrow.test.generators.GenK
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
  ): List<Law> {
    val GEN = GENK.genK(Gen.int())
    val genBool = GENK.genK(Gen.bool())
    val EQ = EQK.liftEq(Int.eq())
    val EQ_NESTED = EQK.liftEq(EQ)

    return TraverseLaws.laws(TF, GENK, EQK) +
      listOf(
        Law("TraverseFilter Laws: Identity") { TF.identityTraverseFilter(GEN, GA, EQ_NESTED) },
        Law("TraverseFilter Laws: filterA consistent with TraverseFilter") { TF.filterAconsistentWithTraverseFilter(GEN, genBool, GA, EQ_NESTED) }
      )
  }

  fun <F> TraverseFilter<F>.identityTraverseFilter(GEN: Gen<Kind<F, Int>>, GA: Applicative<F>, EQ: Eq<Kind<F, Kind<F, Int>>> = Eq.any()) =
    forAll(GEN) { fa: Kind<F, Int> ->
      fa.traverseFilter(GA) { GA.just(Some(it)) }.equalUnderTheLaw(GA.just(fa), EQ)
    }

  fun <F> TraverseFilter<F>.filterAconsistentWithTraverseFilter(
    genInt: Gen<Kind<F, Int>>,
    genBool: Gen<Kind<F, Boolean>>,
    GA: Applicative<F>,
    EQ: Eq<Kind<F, Kind<F, Int>>>
  ) = run {
    forAll(genInt, Gen.functionAToB<Int, Kind<F, Boolean>>(genBool)) { fa: Kind<F, Int>, f: (Int) -> Kind<F, Boolean> ->
      fa.filterA(f, GA).equalUnderTheLaw(fa.traverseFilter(GA) { a -> f(a).map { b: Boolean -> if (b) Some(a) else None } }, EQ)
    }
  }
}
