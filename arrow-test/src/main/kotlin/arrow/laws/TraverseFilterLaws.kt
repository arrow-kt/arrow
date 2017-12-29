package arrow

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.core.Some
import arrow.core.None

object TraverseFilterLaws {

    //FIXME(paco): TraverseLaws cannot receive AP::pure due to a crash caused by the inliner. Check in TraverseLaws why.
    inline fun <reified F> laws(TF: TraverseFilter<F> = traverseFilter<F>(), GA: Applicative<F> = applicative<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>, EQ_NESTED: Eq<HK<F, HK<F, Int>>> = Eq.any()): List<Law> =
            TraverseLaws.laws(TF, GA, cf, EQ) + listOf(
                    Law("TraverseFilter Laws: Identity", { identityTraverseFilter(TF, GA, EQ_NESTED) }),
                    Law("TraverseFilter Laws: filterA consistent with TraverseFilter", { filterAconsistentWithTraverseFilter(TF, GA, EQ_NESTED) })
            )

    inline fun <reified F> identityTraverseFilter(FT: TraverseFilter<F>, GA: Applicative<F> = applicative<F>(), EQ: Eq<HK<F, HK<F, Int>>> = Eq.any()) =
            forAll(genApplicative(genIntSmall(), GA), { fa: HK<F, Int> ->
                FT.traverseFilter(fa, { Some(it).pure(GA) }, GA).equalUnderTheLaw(GA.pure(fa), EQ)
            })

    inline fun <reified F> filterAconsistentWithTraverseFilter(FT: TraverseFilter<F>, GA: Applicative<F> = applicative<F>(), EQ: Eq<HK<F, HK<F, Int>>> = Eq.any()) =
            forAll(genApplicative(genIntSmall(), GA), genFunctionAToB(genApplicative(Gen.bool(), GA)), { fa: HK<F, Int>, f: (Int) -> HK<F, Boolean> ->
                FT.filterA(fa, f, GA).equalUnderTheLaw(fa.traverseFilter(FT, GA) { a -> f(a).map(FT) { b: Boolean -> if (b) Some(a) else None } }, EQ)
            })
}