package kategory.laws

import io.kotlintest.properties.forAll
import kategory.*

object TraverseFilterLaws {

    inline fun <reified F> laws(TF: TraverseFilter<F> = traverseFilter<F>(), GA: Applicative<F> = applicative<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): List<Law> =
            TraverseLaws.laws(TF, GA, cf, EQ) + FunctorLaws.laws(GA, cf, EQ) + listOf(
                    Law("TraverseFilter Laws: Identity", { identityTraverseFilter(TF, GA, cf, Eq.any()) })
            )

    inline fun <reified F> identityTraverseFilter(FT: TraverseFilter<F>, GA: Applicative<F> = applicative<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, HK<F, Int>>> = Eq.any()) =
            forAll(
                    genConstructor(genIntSmall(), cf),
                    { fa: HK<F, Int> ->
                        val traverseFilter: HK<F, HK<F, Int>> = FT.traverseFilter(fa, { n: Int -> n.some().pure(GA) }, GA)
                        val b: HK<F, HK<F, Int>> = GA.pure(fa)
                        traverseFilter.equalUnderTheLaw(b, EQ)
                    })
}