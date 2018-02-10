package arrow.test.laws

import arrow.typeclasses.Eq
import arrow.Kind
import arrow.mtl.MonadFilter
import arrow.mtl.bindingFilter
import arrow.mtl.monadFilter
import arrow.syntax.monad.flatMap
import arrow.test.generators.genApplicative
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadFilterLaws {

    inline fun <reified F> laws(MF: MonadFilter<F> = monadFilter<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
            MonadLaws.laws(MF, EQ) + FunctorFilterLaws.laws(MF, cf, EQ) + listOf(
                    Law("MonadFilter Laws: Left Empty", { monadFilterLeftEmpty(MF, EQ) }),
                    Law("MonadFilter Laws: Right Empty", { monadFilterRightEmpty(MF, EQ) }),
                    Law("MonadFilter Laws: Consistency", { monadFilterConsistency(MF, cf, EQ) }),
                    Law("MonadFilter Laws: Comprehension Guards", { monadFilterEmptyComprehensions(MF, EQ) }),
                    Law("MonadFilter Laws: Comprehension bindWithFilter Guards", { monadFilterBindWithFilterComprehensions(MF, EQ) }))

    inline fun <reified F> monadFilterLeftEmpty(MF: MonadFilter<F> = monadFilter<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB(genApplicative(Gen.int(), MF)), { f: (Int) -> Kind<F, Int> ->
                MF.empty<Int>().flatMap(MF, f).equalUnderTheLaw(MF.empty(), EQ)
            })

    inline fun <reified F> monadFilterRightEmpty(MF: MonadFilter<F> = monadFilter<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genApplicative(Gen.int(), MF), { fa: Kind<F, Int> ->
                MF.flatMap(fa, { MF.empty<Int>() }).equalUnderTheLaw(MF.empty(), EQ)
            })

    inline fun <reified F> monadFilterConsistency(MF: MonadFilter<F> = monadFilter<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genFunctionAToB(Gen.bool()), genConstructor(Gen.int(), cf), { f: (Int) -> Boolean, fa: Kind<F, Int> ->
                MF.filter(fa, f).equalUnderTheLaw(fa.flatMap(MF, { a -> if (f(a)) MF.pure(a) else MF.empty() }), EQ)
            })

    inline fun <reified F> monadFilterEmptyComprehensions(MF: MonadFilter<F> = monadFilter<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(Gen.bool(), Gen.int(), { guard: Boolean, n: Int ->
                MF.bindingFilter {
                    continueIf(guard)
                    yields(n)
                }.equalUnderTheLaw(if (!guard) MF.empty() else MF.pure(n), EQ)
            })

    inline fun <reified F> monadFilterBindWithFilterComprehensions(MF: MonadFilter<F> = monadFilter<F>(), EQ: Eq<Kind<F, Int>>): Unit =
            forAll(Gen.bool(), Gen.int(), { guard: Boolean, n: Int ->
                MF.bindingFilter {
                    val x = MF.pure(n).bindWithFilter { _ -> guard }
                    yields(x)
                }.equalUnderTheLaw(if (!guard) MF.empty() else MF.pure(n), EQ)
            })

}
