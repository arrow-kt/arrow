package arrow.test.laws

import arrow.Kind
import arrow.mtl.typeclasses.MonadFilter
import arrow.mtl.typeclasses.bindingFilter
import arrow.test.generators.genApplicative
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadFilterLaws {

    inline fun <F> laws(MF: MonadFilter<F>, noinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
            MonadLaws.laws(MF, EQ) + FunctorFilterLaws.laws(MF, cf, EQ) + listOf(
                    Law("MonadFilter Laws: Left Empty", { MF.monadFilterLeftEmpty(EQ) }),
                    Law("MonadFilter Laws: Right Empty", { MF.monadFilterRightEmpty(EQ) }),
                    Law("MonadFilter Laws: Consistency", { MF.monadFilterConsistency(cf, EQ) }),
                    Law("MonadFilter Laws: Comprehension Guards", { MF.monadFilterEmptyComprehensions(EQ) }),
                    Law("MonadFilter Laws: Comprehension bindWithFilter Guards", { MF.monadFilterBindWithFilterComprehensions(EQ) }))

    fun <F> MonadFilter<F>.monadFilterLeftEmpty(EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB(genApplicative(Gen.int(), this)), { f: (Int) -> Kind<F, Int> ->
                empty<Int>().flatMap(f).equalUnderTheLaw(empty(), EQ)
            })

    fun <F> MonadFilter<F>.monadFilterRightEmpty(EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genApplicative(Gen.int(), this), { fa: Kind<F, Int> ->
                fa.flatMap({ empty<Int>() }).equalUnderTheLaw(empty(), EQ)
            })

    fun <F> MonadFilter<F>.monadFilterConsistency(cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB(Gen.bool()), genConstructor(Gen.int(), cf), { f: (Int) -> Boolean, fa: Kind<F, Int> ->
                fa.filter(f).equalUnderTheLaw(fa.flatMap({ a -> if (f(a)) just(a) else empty() }), EQ)
            })

    fun <F> MonadFilter<F>.monadFilterEmptyComprehensions(EQ: Eq<Kind<F, Int>>): Unit =
            forAll(Gen.bool(), Gen.int(), { guard: Boolean, n: Int ->
                bindingFilter {
                    continueIf(guard)
                    n
                }.equalUnderTheLaw(if (!guard) empty() else just(n), EQ)
            })

    fun <F> MonadFilter<F>.monadFilterBindWithFilterComprehensions(EQ: Eq<Kind<F, Int>>): Unit =
            forAll(Gen.bool(), Gen.int(), { guard: Boolean, n: Int ->
                bindingFilter {
                    val x = this@monadFilterBindWithFilterComprehensions.just(n).bindWithFilter { _ -> guard }
                    x
                }.equalUnderTheLaw(if (!guard) empty() else just(n), EQ)
            })

}
