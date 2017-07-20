package kategory

import io.kotlintest.properties.forAll

object TraverseLaws {
    inline fun <reified F> laws(FF: Traverse<F> = traverse<F>(), AP: Applicative<F> = applicative<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): List<Law> =
            FoldableLaws.laws(FF, cf, Eq.any()) + FunctorLaws.laws(AP, EQ) + listOf(
                    Law("Traverse Laws: Identity", { identityTraverse(FF, AP, cf, EQ) })
            )

    inline fun <reified F> identityTraverse(FF: Traverse<F>, AP: Applicative<F> = applicative<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>) =
            forAll(genFunctionAToB<Int, HK<Id.F, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> HK<Id.F, Int>, fa: HK<F, Int> ->
                FF.traverse(fa, f, Id).value().equalUnderTheLaw(FF.map(fa, f).map(AP) { it.value() }, EQ)
            })
}