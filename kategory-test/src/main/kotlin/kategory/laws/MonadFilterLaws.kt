package kategory.laws

import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.*

object MonadFilterLaws {

    inline fun <reified F> laws(MF: MonadFilter<F> = monadFilter<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): List<Law> =
            MonadLaws.laws(MF, EQ) + FunctorFilterLaws.laws(MF, cf, EQ) + listOf(
                    Law("MonadFilter Laws: Left Empty", { monadFilterLeftEmpty(MF, EQ) }),
                    Law("MonadFilter Laws: Right Empty", { monadFilterRightEmpty(MF, cf, EQ) }),
                    Law("MonadFilter Laws: Consistency", { monadFilterConsistency(MF, cf, EQ) }))

    inline fun <reified F> monadFilterLeftEmpty(MF: MonadFilter<F> = monadFilter<F>(), EQ: Eq<HK<F, Int>>): Unit =
            forAll(genFunctionAToB(genApplicative(Gen.int(), MF)), { f: (Int) -> HK<F, Int> ->
                MF.empty<Int>().flatMap(MF, f).equalUnderTheLaw(MF.empty(), EQ)
            })

    inline fun <reified F> monadFilterRightEmpty(MF: MonadFilter<F> = monadFilter<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genFunctionAToB(genApplicative(Gen.int(), MF)), genConstructor(Gen.int(), cf), { f: (Int) -> HK<F, Int>, fa: HK<F, Int> ->
                MF.flatMap(fa, { MF.empty<Int>() }).equalUnderTheLaw(MF.empty(), EQ)
            })

    inline fun <reified F> monadFilterConsistency(MF: MonadFilter<F> = monadFilter<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genFunctionAToB(Gen.bool()), genConstructor(Gen.int(), cf), { f: (Int) -> Boolean, fa: HK<F, Int> ->
                MF.filter(fa, f).equalUnderTheLaw(fa.flatMap { a -> if (f(a)) MF.pure(a) else MF.empty() }, EQ)
            })
}
