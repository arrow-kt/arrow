package arrow.test.laws

import arrow.*
import arrow.data.Cokleisli
import arrow.syntax.comonad.extract
import arrow.syntax.functor.map
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ComonadLaws {

    inline fun <reified F> laws(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): List<Law> =
            FunctorLaws.laws(CM, cf, EQ) + listOf(
                    Law("Comonad Laws: duplicate then extract is identity", { duplicateThenExtractIsId(CM, cf, EQ) }),
                    Law("Comonad Laws: duplicate then map into extract is identity", { duplicateThenMapExtractIsId(CM, cf, EQ) }),
                    Law("Comonad Laws: map and coflatmap are coherent", { mapAndCoflatmapCoherence(CM, cf, EQ) }),
                    Law("Comonad Laws: left identity", { comonadLeftIdentity(CM, cf, EQ) }),
                    Law("Comonad Laws: right identity", { comonadRightIdentity(CM, cf, EQ) }),
                    Law("Comonad Laws: cokleisli left identity", { cokleisliLeftIdentity(CM, cf, EQ) }),
                    Law("Comonad Laws: cokleisli right identity", { cokleisliRightIdentity(CM, cf, EQ) }),
                    Law("Comonad Laws: cobinding", { cobinding(CM, cf, EQ) })
            )

    inline fun <reified F> duplicateThenExtractIsId(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), { fa: HK<F, Int> ->
                CM.duplicate(fa).extract(CM).equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> duplicateThenMapExtractIsId(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), { fa: HK<F, Int> ->
                CM.duplicate(fa).map(CM) { it.extract(CM) }.equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> mapAndCoflatmapCoherence(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genFunctionAToB(Gen.int()), { fa: HK<F, Int>, f: (Int) -> Int ->
                CM.map(fa, f).equalUnderTheLaw(CM.coflatMap(fa, { f(it.extract(CM)) }), EQ)
            })

    inline fun <reified F> comonadLeftIdentity(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), { fa: HK<F, Int> ->
                CM.coflatMap(fa, { it.extract(CM) }).equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> comonadRightIdentity(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genFunctionAToB(genConstructor(Gen.int(), cf)), { fa: HK<F, Int>, f: (HK<F, Int>) -> HK<F, Int> ->
                CM.coflatMap(fa, f).extract(CM).equalUnderTheLaw(f(fa), EQ)
            })

    inline fun <reified F> cokleisliLeftIdentity(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genFunctionAToB(genConstructor(Gen.int(), cf)), { fa: HK<F, Int>, f: (HK<F, Int>) -> HK<F, Int> ->
                Cokleisli(CM, { hk: HK<F, Int> -> CM.extract(hk) }).andThen(Cokleisli(CM, f)).run(fa).equalUnderTheLaw(f(fa), EQ)
            })

    inline fun <reified F> cokleisliRightIdentity(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genFunctionAToB(genConstructor(Gen.int(), cf)), { fa: HK<F, Int>, f: (HK<F, Int>) -> HK<F, Int> ->
                Cokleisli(CM, f).andThen(Cokleisli(CM, { hk: HK<F, HK<F, Int>> -> CM.extract(hk) })).run(fa).equalUnderTheLaw(f(fa), EQ)
            })

    inline fun <reified F> cobinding(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> HK<F, Int>, EQ: Eq<HK<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), { fa: HK<F, Int> ->
                CM.cobinding {
                    val x = fa.extract(CM)
                    val y = extract { CM.map(fa, { it + x }) }
                    CM.map(fa, { x + y })
                }.equalUnderTheLaw(CM.map(fa, { it * 3 }), EQ)
            })
}
