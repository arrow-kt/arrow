package arrow.test.laws

import arrow.Kind
import arrow.data.Cokleisli
import arrow.syntax.comonad.extractM
import arrow.syntax.functor.map
import arrow.test.generators.genConstructor
import arrow.test.generators.genFunctionAToB
import arrow.typeclasses.Comonad
import arrow.typeclasses.Eq
import arrow.typeclasses.cobinding
import arrow.typeclasses.comonad
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object ComonadLaws {

    inline fun <reified F> laws(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): List<Law> =
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

    inline fun <reified F> duplicateThenExtractIsId(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), { fa: Kind<F, Int> ->
                CM.duplicate(fa).extractM(CM).equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> duplicateThenMapExtractIsId(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), { fa: Kind<F, Int> ->
                CM.duplicate(fa).map(CM) { it.extractM(CM) }.equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> mapAndCoflatmapCoherence(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genFunctionAToB(Gen.int()), { fa: Kind<F, Int>, f: (Int) -> Int ->
                CM.map(fa, f).equalUnderTheLaw(CM.coflatMap(fa, { f(it.extractM(CM)) }), EQ)
            })

    inline fun <reified F> comonadLeftIdentity(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), { fa: Kind<F, Int> ->
                CM.coflatMap(fa, { it.extractM(CM) }).equalUnderTheLaw(fa, EQ)
            })

    inline fun <reified F> comonadRightIdentity(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genFunctionAToB(genConstructor(Gen.int(), cf)), { fa: Kind<F, Int>, f: (Kind<F, Int>) -> Kind<F, Int> ->
                CM.coflatMap(fa, f).extractM(CM).equalUnderTheLaw(f(fa), EQ)
            })

    inline fun <reified F> cokleisliLeftIdentity(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genFunctionAToB(genConstructor(Gen.int(), cf)), { fa: Kind<F, Int>, f: (Kind<F, Int>) -> Kind<F, Int> ->
                Cokleisli(CM, { hk: Kind<F, Int> -> CM.extractM(hk) }).andThen(Cokleisli(CM, f)).run(fa).equalUnderTheLaw(f(fa), EQ)
            })

    inline fun <reified F> cokleisliRightIdentity(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), genFunctionAToB(genConstructor(Gen.int(), cf)), { fa: Kind<F, Int>, f: (Kind<F, Int>) -> Kind<F, Int> ->
                Cokleisli(CM, f).andThen(Cokleisli(CM, { hk: Kind<F, Kind<F, Int>> -> CM.extractM(hk) })).run(fa).equalUnderTheLaw(f(fa), EQ)
            })

    inline fun <reified F> cobinding(CM: Comonad<F> = comonad<F>(), crossinline cf: (Int) -> Kind<F, Int>, EQ: Eq<Kind<F, Int>>): Unit =
            forAll(genConstructor(Gen.int(), cf), { fa: Kind<F, Int> ->
                CM.cobinding {
                    val x = fa.extractM(CM)
                    val y = extract { CM.map(fa, { it + x }) }
                    CM.map(fa, { x + y })
                }.equalUnderTheLaw(CM.map(fa, { it * 3 }), EQ)
            })
}
