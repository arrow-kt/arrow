package arrow.test.laws

import arrow.Kind
import arrow.core.Eval
import arrow.core.Id
import arrow.core.monad
import arrow.core.value
import arrow.instances.IntMonoidInstance
import arrow.test.concurrency.SideEffect
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genIntPredicate
import arrow.test.generators.genIntSmall
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FoldableLaws {
    inline fun <F> laws(FF: Foldable<F>, noinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>): List<Law> =
            listOf(
                    Law("Foldable Laws: Left fold consistent with foldMap", { FF.leftFoldConsistentWithFoldMap(cf, EQ) }),
                    Law("Foldable Laws: Right fold consistent with foldMap", { FF.rightFoldConsistentWithFoldMap(cf, EQ) }),
                    Law("Foldable Laws: Exists is consistent with find", { FF.existsConsistentWithFind(cf) }),
                    Law("Foldable Laws: Exists is lazy", { FF.existsIsLazy(cf, EQ) }),
                    Law("Foldable Laws: ForAll is lazy", { FF.forAllIsLazy(cf, EQ) }),
                    Law("Foldable Laws: ForAll consistent with exists", { FF.forallConsistentWithExists(cf) }),
                    Law("Foldable Laws: ForAll returns true if isEmpty", { FF.forallReturnsTrueIfEmpty(cf) }),
                    Law("Foldable Laws: FoldM for Id is equivalent to fold left", { FF.foldMIdIsFoldL(cf, EQ) })
            )

    fun <F> Foldable<F>.leftFoldConsistentWithFoldMap(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: Kind<F, Int> ->
                with(IntMonoidInstance) {
                    fa.foldMap(this, f).equalUnderTheLaw(foldLeft(fa, empty(), { acc, a -> acc.combine(f(a)) }), EQ)
                }
            })

    fun <F> Foldable<F>.rightFoldConsistentWithFoldMap(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: Kind<F, Int> ->
                with(IntMonoidInstance) {
                    fa.foldMap(this, f).equalUnderTheLaw(foldRight(fa, Eval.later { empty() }, { a, lb: Eval<Int> -> lb.map { f(a).combine(it) } }).value(), EQ)
                }
            })

    fun <F> Foldable<F>.existsConsistentWithFind(cf: (Int) -> Kind<F, Int>) =
            forAll(genIntPredicate(), genConstructor(Gen.int(), cf), { f: (Int) -> Boolean, fa: Kind<F, Int> ->
                exists(fa, f).equalUnderTheLaw(find(fa, f).fold({ false }, { true }), Eq.any())
            })

    fun <F> Foldable<F>.existsIsLazy(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
            forAll(genConstructor(Gen.int(), cf), { fa: Kind<F, Int> ->
                val sideEffect = SideEffect()
                exists(fa, { _ ->
                    sideEffect.increment()
                    true
                })
                val expected = if (isEmpty(fa)) 0 else 1
                sideEffect.counter.equalUnderTheLaw(expected, EQ)
            })

    fun <F> Foldable<F>.forAllIsLazy(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
            forAll(genConstructor(Gen.int(), cf), { fa: Kind<F, Int> ->
                val sideEffect = SideEffect()
                forall(fa, { _ ->
                    sideEffect.increment()
                    true
                })
                val expected = if (isEmpty(fa)) 0 else 1
                sideEffect.counter.equalUnderTheLaw(expected, EQ)
            })

    fun <F> Foldable<F>.forallConsistentWithExists(cf: (Int) -> Kind<F, Int>) =
            forAll(genIntPredicate(), genConstructor(Gen.int(), cf), { f: (Int) -> Boolean, fa: Kind<F, Int> ->
                if (forall(fa, f)) {
                    val negationExists = exists(fa, { a -> !(f(a)) })
                    // if p is true for all elements, then there cannot be an element for which
                    // it does not hold.
                    !negationExists &&
                            // if p is true for all elements, then either there must be no elements
                            // or there must exist an element for which it is true.
                            (isEmpty(fa) || exists(fa, f))
                } else true
            })

    fun <F> Foldable<F>.forallReturnsTrueIfEmpty(cf: (Int) -> Kind<F, Int>) =
            forAll(genIntPredicate(), genConstructor(Gen.int(), cf), { f: (Int) -> Boolean, fa: Kind<F, Int> ->
                !isEmpty(fa) || forall(fa, f)
            })

    fun <F> Foldable<F>.foldMIdIsFoldL(cf: (Int) -> Kind<F, Int>, EQ: Eq<Int>) =
            forAll(genFunctionAToB<Int, Int>(genIntSmall()), genConstructor(genIntSmall(), cf), { f: (Int) -> Int, fa: Kind<F, Int> ->
                with(IntMonoidInstance) {
                    val foldL: Int = foldLeft(fa, empty(), { acc, a -> acc.combine(f(a)) })
                    val foldM: Int = Id.monad().foldM(fa, empty(), { acc, a -> Id(acc.combine(f(a))) }).value()
                    foldM.equalUnderTheLaw(foldL, EQ)
                }
            })
}
