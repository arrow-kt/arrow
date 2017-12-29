package arrow.test.laws

import arrow.*
import arrow.core.Eval
import arrow.core.Id
import arrow.core.value
import arrow.instances.IntMonoid
import arrow.instances.monad
import arrow.test.concurrency.SideEffect
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntPredicate
import arrow.test.generators.genIntSmall
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FoldableLaws {
    inline fun <reified F> laws(FF: Foldable<F> = foldable<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Int>): List<Law> =
            listOf(
                    Law("Foldable Laws: Left fold consistent with foldMap", { leftFoldConsistentWithFoldMap(FF, cf, EQ) }),
                    Law("Foldable Laws: Right fold consistent with foldMap", { rightFoldConsistentWithFoldMap(FF, cf, EQ) }),
                    Law("Foldable Laws: Exists is consistent with find", { existsConsistentWithFind(FF, cf) }),
                    Law("Foldable Laws: Exists is lazy", { existsIsLazy(FF, cf, EQ) }),
                    Law("Foldable Laws: ForAll is lazy", { forAllIsLazy(FF, cf, EQ) }),
                    Law("Foldable Laws: ForAll consistent with exists", { forallConsistentWithExists(FF, cf) }),
                    Law("Foldable Laws: ForAll returns true if isEmpty", { forallReturnsTrueIfEmpty(FF, cf) }),
                    Law("Foldable Laws: FoldM for Id is equivalent to fold left", { foldMIdIsFoldL(FF, cf, EQ) })
            )

    inline fun <reified F> leftFoldConsistentWithFoldMap(FF: Foldable<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: HK<F, Int> ->
                FF.foldMap(IntMonoid, fa, f).equalUnderTheLaw(FF.foldLeft(fa, IntMonoid.empty(), { acc, a -> IntMonoid.combine(acc, f(a)) }), EQ)
            })

    inline fun <reified F> rightFoldConsistentWithFoldMap(FF: Foldable<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: HK<F, Int> ->
                FF.foldMap(IntMonoid, fa, f).equalUnderTheLaw(FF.foldRight(fa, Eval.later { IntMonoid.empty() }, { a, lb: Eval<Int> -> lb.map { IntMonoid.combine(f(a), it) } }).value(), EQ)
            })

    inline fun <reified F> existsConsistentWithFind(FF: Foldable<F>, crossinline cf: (Int) -> HK<F, Int>) =
            forAll(genIntPredicate(), genConstructor(Gen.int(), cf), { f: (Int) -> Boolean, fa: HK<F, Int> ->
                FF.exists(fa, f).equalUnderTheLaw(FF.find(fa, f).fold({ false }, { true }), Eq.any())
            })

    inline fun <reified F> existsIsLazy(FF: Foldable<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Int>) =
            forAll(genConstructor(Gen.int(), cf), { fa: HK<F, Int> ->
                val sideEffect = SideEffect()
                FF.exists(fa, { _ ->
                    sideEffect.increment()
                    true
                })
                val expected = if (FF.isEmpty(fa)) 0 else 1
                sideEffect.counter.equalUnderTheLaw(expected, EQ)
            })

    inline fun <reified F> forAllIsLazy(FF: Foldable<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Int>) =
            forAll(genConstructor(Gen.int(), cf), { fa: HK<F, Int> ->
                val sideEffect = SideEffect()
                FF.forall(fa, { _ ->
                    sideEffect.increment()
                    true
                })
                val expected = if (FF.isEmpty(fa)) 0 else 1
                sideEffect.counter.equalUnderTheLaw(expected, EQ)
            })

    inline fun <reified F> forallConsistentWithExists(FF: Foldable<F>, crossinline cf: (Int) -> HK<F, Int>) =
            forAll(genIntPredicate(), genConstructor(Gen.int(), cf), { f: (Int) -> Boolean, fa: HK<F, Int> ->
                if (FF.forall(fa, f)) {
                    val negationExists = FF.exists(fa, { a -> !(f(a)) })
                    // if p is true for all elements, then there cannot be an element for which
                    // it does not hold.
                    !negationExists &&
                            // if p is true for all elements, then either there must be no elements
                            // or there must exist an element for which it is true.
                            (FF.isEmpty(fa) || FF.exists(fa, f))
                } else true
            })

    inline fun <reified F> forallReturnsTrueIfEmpty(FF: Foldable<F>, crossinline cf: (Int) -> HK<F, Int>) =
            forAll(genIntPredicate(), genConstructor(Gen.int(), cf), { f: (Int) -> Boolean, fa: HK<F, Int> ->
                !FF.isEmpty(fa) || FF.forall(fa, f)
            })

    inline fun <reified F> foldMIdIsFoldL(FF: Foldable<F>, crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: HK<F, Int> ->
                val foldL: Int = FF.foldLeft(fa, IntMonoid.empty(), { acc, a -> IntMonoid.combine(acc, f(a)) })
                val foldM: Int = FF.foldM(fa, IntMonoid.empty(), { acc, a -> Id(IntMonoid.combine(acc, f(a))) }, Id.monad()).value()
                foldM.equalUnderTheLaw(foldL, EQ)
            })
}