package kategory.laws

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.*
import kategory.Option.Some
import kategory.Option.None

object TraverseFilterLaws {

    inline fun <reified F> laws(TF: TraverseFilter<F> = traverseFilter<F>(), GA: Applicative<F> = applicative<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>, EQ_NESTED: Eq<HK<F, HK<F, Int>>> = Eq.any()): List<Law> =
            TraverseLaws.laws(TF, GA, cf, EQ) + listOf(
                    Law("TraverseFilter Laws: Identity", { identityTraverseFilter(TF, GA, cf, EQ_NESTED) }),
                    Law("TraverseFilter Laws: filterA consistent with TraverseFilter", { filterAconsistentWithTraverseFilter(TF, GA, cf, EQ_NESTED) })
            )

    inline fun <reified F> identityTraverseFilter(FT: TraverseFilter<F>, GA: Applicative<F> = applicative<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, HK<F, Int>>> = Eq.any()) =
            forAll(genConstructor(genIntSmall(), cf), { fa: HK<F, Int> ->
                FT.traverseFilter(fa, { it.some().pure(GA) }, GA).equalUnderTheLaw(GA.pure(fa), EQ)
            })

    inline fun <reified F> filterAconsistentWithTraverseFilter(FT: TraverseFilter<F>, GA: Applicative<F> = applicative<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, HK<F, Int>>> = Eq.any()) =
            forAll(genConstructor(genIntSmall(), cf), genFunctionAToB<Int, HK<F, Boolean>>(genConstructor(Gen.bool(), { GA.pure(it) })), { fa: HK<F, Int>, f: (Int) -> HK<F, Boolean> ->
                FT.filterA(fa, f, GA).equalUnderTheLaw(fa.traverseFilter(FT, GA) { a -> f(a).map(FT) { b: Boolean -> if (b) Some(a) else None } }, EQ)
            })
}