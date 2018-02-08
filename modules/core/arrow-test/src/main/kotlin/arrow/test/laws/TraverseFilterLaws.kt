package arrow.test.laws

import arrow.*
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import arrow.core.*
import arrow.mtl.*
import arrow.syntax.applicative.*
import arrow.syntax.functor.*
import arrow.test.generators.genApplicative
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.applicative

object TraverseFilterLaws {

    //FIXME(paco): TraverseLaws cannot receive AP::pure due to a crash caused by the inliner. Check in TraverseLaws why.
    inline fun <reified F> laws(TF: TraverseFilter<F> = traverseFilter<F>(), GA: Applicative<F> = applicative<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>, EQ_NESTED: Eq<Kind<F, Kind<F, Int>>> = Eq.any()): List<Law> =
            TraverseLaws.laws(TF, GA, cf, EQ) + listOf(
                    Law("TraverseFilter Laws: Identity", { identityTraverseFilter(TF, GA, EQ_NESTED) }),
                    Law("TraverseFilter Laws: filterA consistent with TraverseFilter", { filterAconsistentWithTraverseFilter(TF, GA, EQ_NESTED) })
            )

    inline fun <reified F> identityTraverseFilter(FT: TraverseFilter<F>, GA: Applicative<F> = applicative<F>(), EQ: Eq<Kind<F, Kind<F, Int>>> = Eq.any()) =
            forAll(genApplicative(genIntSmall(), GA), { fa: Kind<F, Int> ->
                FT.traverseFilter(fa, { Some(it).pure(GA) }, GA).equalUnderTheLaw(GA.pure(fa), EQ)
            })

    inline fun <reified F> filterAconsistentWithTraverseFilter(FT: TraverseFilter<F>, GA: Applicative<F> = applicative<F>(), EQ: Eq<Kind<F, Kind<F, Int>>> = Eq.any()) =
            forAll(genApplicative(genIntSmall(), GA), genFunctionAToB(genApplicative(Gen.bool(), GA)), { fa: Kind<F, Int>, f: (Int) -> Kind<F, Boolean> ->
                FT.filterA(fa, f, GA).equalUnderTheLaw(fa.traverseFilter(FT, GA) { a -> f(a).map(FT) { b: Boolean -> if (b) Some(a) else None } }, EQ)
            })
}