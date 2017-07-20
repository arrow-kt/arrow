package kategory

import io.kotlintest.properties.forAll

object TraverseLaws {
    inline fun <reified F> laws(FF: Traverse<F> = traverse<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Any?>): List<Law> =
            FoldableLaws.laws(FF, cf, EQ) + listOf(
                    Law("Traverse Laws: Identity", { identityTraverse(FF, cf, EQ) })
            )

    inline fun <reified F> identityTraverse(FF: Traverse<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Any?>) =
            forAll(genFunctionAToB<Int, HK<Id.F, Int>>(genConstructor(genIntSmall(), ::Id)), genConstructor(genIntSmall(), cf), { f: (Int) -> HK<Id.F, Int>, fa: HK<F, Int> ->
                FF.traverse(fa, f, Id).equalUnderTheLaw(FF.map(fa, f), EQ)
            })
}