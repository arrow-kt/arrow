package arrow.test.laws

import io.kotlintest.properties.forAll
import arrow.*
import arrow.core.Eval
import arrow.core.Option
import arrow.instances.IntMonoid
import arrow.instances.LongMonoid
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAAToA
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntSmall

object ReducibleLaws {
    inline fun <reified F> laws(RF: Reducible<F> = reducible(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Int>, EQOptionInt: Eq<Option<Int>>, EQLong: Eq<Long>): List<Law> =
            FoldableLaws.laws(RF, cf, EQ) + listOf(
                    Law("Reducible Laws: reduceLeftTo consistent with reduceMap", { reduceLeftToConsistentWithReduceMap(RF, cf, EQ) }),
                    Law("Reducible Laws: reduceRightTo consistent with reduceMap", { reduceRightToConsistentWithReduceMap(RF, cf, EQ) }),
                    Law("Reducible Laws: reduceRightTo consistent with reduceRightToOption", { reduceRightToConsistentWithReduceRightToOption(RF, cf, EQOptionInt) }),
                    Law("Reducible Laws: reduceRight consistent with reduceRightOption", { reduceRightConsistentWithReduceRightOption(RF, cf, EQOptionInt) }),
                    Law("Reducible Laws: reduce reduce left consistent", { reduceReduceLeftConsistent(RF, cf, EQ) }),
                    Law("Reducible Laws: size consistent", { sizeConsistent(RF, cf, EQLong) })
            )

    inline fun <reified F> reduceLeftToConsistentWithReduceMap(RF: Reducible<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: HK<F, Int> ->
                RF.reduceMap(fa, f, IntMonoid).equalUnderTheLaw(RF.reduceLeftTo(fa, f, { b, a -> IntMonoid.combine(b, f(a)) }), EQ)
            })

    inline fun <reified F> reduceRightToConsistentWithReduceMap(RF: Reducible<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: HK<F, Int> ->
                RF.reduceMap(fa, f, IntMonoid).equalUnderTheLaw(RF.reduceRightTo(fa, f, { a, eb -> eb.map({ IntMonoid.combine(f(a), it) }) }).value(), EQ)
            })

    inline fun <reified F> reduceRightToConsistentWithReduceRightToOption(RF: Reducible<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Option<Int>>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: HK<F, Int> ->
                RF.reduceRightToOption(fa, f, { a, eb -> eb.map({ IntMonoid.combine(f(a), it) }) }).value()
                        .equalUnderTheLaw(RF.reduceRightTo(fa, f, { a, eb -> eb.map({ IntMonoid.combine(f(a), it) }) }).map({ Option(it) }).value(), EQ)
            })

    inline fun <reified F> reduceRightConsistentWithReduceRightOption(RF: Reducible<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Option<Int>>) =
            forAll(genFunctionAAToA(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int, Int) -> Int, fa: HK<F, Int> ->
                RF.reduceRight(fa, { a1, e2 -> Eval.Now(f(a1, e2.value())) }).map({ Option(it) }).value()
                        .equalUnderTheLaw(RF.reduceRightOption(fa, { a1, e2 -> Eval.Now(f(a1, e2.value())) }).value(), EQ)
            })

    inline fun <reified F> reduceReduceLeftConsistent(RF: Reducible<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Int>) =
            forAll(genConstructor(genIntSmall(), cf), { fa: HK<F, Int> ->
                RF.reduce(fa, IntMonoid).equalUnderTheLaw(RF.reduceLeft(fa, { a1, a2 -> IntMonoid.combine(a1, a2) }), EQ)
            })

    inline fun <reified F> sizeConsistent(RF: Reducible<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Long>) =
            forAll(genConstructor(genIntSmall(), cf), { fa: HK<F, Int> ->
                RF.size(LongMonoid, fa).equalUnderTheLaw(RF.reduceMap(fa, { 1L }, LongMonoid), EQ)
            })
}
