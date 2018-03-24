package arrow.test.laws

import arrow.Kind
import arrow.core.Eval
import arrow.core.Option
import arrow.instances.IntMonoidInstance
import arrow.instances.LongMonoidInstance
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAAToA
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall
import arrow.typeclasses.Eq
import arrow.typeclasses.Reducible
import io.kotlintest.properties.forAll

object ReducibleLaws {
    inline fun <F> laws(RF: Reducible<F>, noinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>, EQOptionInt: Eq<Option<Int>>, EQLong: Eq<Long>): List<Law> =
            FoldableLaws.laws(RF, cf, EQ) + listOf(
                    Law("Reducible Laws: reduceLeftTo consistent with reduceMap", { RF.reduceLeftToConsistentWithReduceMap(cf, EQ) }),
                    Law("Reducible Laws: reduceRightTo consistent with reduceMap", { RF.reduceRightToConsistentWithReduceMap(cf, EQ) }),
                    Law("Reducible Laws: reduceRightTo consistent with reduceRightToOption", { RF.reduceRightToConsistentWithReduceRightToOption(cf, EQOptionInt) }),
                    Law("Reducible Laws: reduceRight consistent with reduceRightOption", { RF.reduceRightConsistentWithReduceRightOption(cf, EQOptionInt) }),
                    Law("Reducible Laws: reduce reduce left consistent", { RF.reduceReduceLeftConsistent(cf, EQ) }),
                    Law("Reducible Laws: size consistent", { RF.sizeConsistent(cf, EQLong) })
            )

    fun <F> Reducible<F>.reduceLeftToConsistentWithReduceMap(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: Kind<F, Int> ->
                with(IntMonoidInstance) {
                    reduceMap(fa, f).equalUnderTheLaw(fa.reduceLeftTo(f, { b, a -> b.combine(f(a)) }), EQ)
                }
            })

    fun <F> Reducible<F>.reduceRightToConsistentWithReduceMap(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: Kind<F, Int> ->
                with(IntMonoidInstance) {
                    reduceMap(fa, f).equalUnderTheLaw(fa.reduceRightTo(f, { a, eb -> eb.map({ f(a).combine(it) }) }).value(), EQ)
                }
            })

    fun <F> Reducible<F>.reduceRightToConsistentWithReduceRightToOption(cf: (Int) -> Kind<F, Int>, EQ: Eq<Option<Int>>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: Kind<F, Int> ->
                with(IntMonoidInstance) {
                    reduceRightToOption(fa, f, { a, eb -> eb.map({ f(a).combine(it) }) }).value()
                            .equalUnderTheLaw(fa.reduceRightTo(f, { a, eb -> eb.map({ f(a).combine(it) }) }).map({ Option(it) }).value(), EQ)
                }
            })

    fun <F> Reducible<F>.reduceRightConsistentWithReduceRightOption(cf: (Int) -> Kind<F, Int>, EQ: Eq<Option<Int>>) =
            forAll(genFunctionAAToA(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int, Int) -> Int, fa: Kind<F, Int> ->
                fa.reduceRight({ a1, e2 -> Eval.Now(f(a1, e2.value())) }).map({ Option(it) }).value()
                        .equalUnderTheLaw(reduceRightOption(fa, { a1, e2 -> Eval.Now(f(a1, e2.value())) }).value(), EQ)
            })

    fun <F> Reducible<F>.reduceReduceLeftConsistent(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
            forAll(genConstructor(genIntSmall(), cf), { fa: Kind<F, Int> ->
                with(IntMonoidInstance) {
                    reduce(fa).equalUnderTheLaw(fa.reduceLeft({ a1, a2 -> a1.combine(a2) }), EQ)
                }
            })

    fun <F> Reducible<F>.sizeConsistent(cf: (Int) -> Kind<F, Int>, EQ: Eq<Long>) =
            forAll(genConstructor(genIntSmall(), cf), { fa: Kind<F, Int> ->
                with(LongMonoidInstance) {
                    size(fa).equalUnderTheLaw(reduceMap(fa) { 1L }, EQ)
                }
            })
}
