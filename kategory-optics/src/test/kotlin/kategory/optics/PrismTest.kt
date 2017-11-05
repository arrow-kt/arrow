package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Eq
import kategory.NonEmptyList
import kategory.Option
import kategory.PrismLaws
import kategory.Try
import kategory.UnitSpec
import kategory.applicative
import kategory.genEither
import kategory.genFunctionAToB
import kategory.genTuple
import kategory.some
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PrismTest : UnitSpec() {

    init {
        testLaws(
            PrismLaws.laws(
                prism = sumPrism,
                aGen = SumGen,
                bGen = Gen.string(),
                funcGen = genFunctionAToB(Gen.string()),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            PrismLaws.laws(
                prism = sumPrism.first(),
                aGen = genTuple(SumGen, Gen.int()),
                bGen = genTuple(Gen.string(), Gen.int()),
                funcGen = genFunctionAToB(genTuple(Gen.string(), Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            PrismLaws.laws(
                prism = sumPrism.second(),
                aGen = genTuple(Gen.int(), SumGen),
                bGen = genTuple(Gen.int(), Gen.string()),
                funcGen = genFunctionAToB(genTuple(Gen.int(), Gen.string())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            PrismLaws.laws(
                prism = sumPrism.right<SumType, SumType, String, String, Int>(),
                aGen = genEither(Gen.int(), SumGen),
                bGen = genEither(Gen.int(), Gen.string()),
                funcGen = genFunctionAToB(genEither(Gen.int(), Gen.string())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            PrismLaws.laws(
                prism = sumPrism.left<SumType, SumType, String, String, Int>(),
                aGen = genEither(SumGen, Gen.int()),
                bGen = genEither(Gen.string(), Gen.int()),
                funcGen = genFunctionAToB(genEither(Gen.string(), Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any()),

            PrismLaws.laws(
                prism = Prism.id(),
                aGen = genEither(Gen.int(), Gen.int()),
                bGen = genEither(Gen.int(), Gen.int()),
                funcGen = genFunctionAToB(genEither(Gen.int(), Gen.int())),
                EQA = Eq.any(),
                EQB = Eq.any(),
                EQOptionB = Eq.any())
        )

        "Joining two prisms together with same target should yield same result" {
            forAll(SumGen, { a ->
                (sumPrism compose stringPrism).getOption(a) == sumPrism.getOption(a).flatMap(stringPrism::getOption) &&
                        (sumPrism + stringPrism).getOption(a) == (sumPrism compose stringPrism).getOption(a)
            })
        }

        "Checking if a prism exists with a target" {
            forAll(SumGen, SumGen, Gen.bool(), { a, other, bool ->
                Prism.only(a, object : Eq<SumType> {
                    override fun eqv(a: SumType, b: SumType): Boolean = bool
                }).isEmpty(other) == bool
            })
        }

        "Checking if there is no target" {
            forAll(SumGen, { sum ->
                sumPrism.isEmpty(sum) == sum !is SumType.A
            })
        }

        "Checking if a target exists" {
            forAll(SumGen, { sum ->
                sumPrism.nonEmpty(sum) == sum is SumType.A
            })
        }

        "Setting a target on a prism should set the correct target"{
            forAll(AGen, Gen.string(), { a, string ->
                sumPrism.setOption(a, string) == a.copy(string = string).some()
            })
        }

        "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
            forAll(SumGen, Gen.bool(), { sum, predicate ->
                sumPrism.find(sum) { predicate }.fold({ false }, { true }) == (predicate && sum is SumType.A)
            })
        }

        "Checking existence predicate over the target should result in same result as predicate" {
            forAll(SumGen, Gen.bool(), { sum, predicate ->
                sumPrism.exist(sum) { predicate } == (predicate && sum is SumType.A)
            })
        }

        "Checking satisfaction of predicate over the target should result in opposite result as predicate" {
            forAll(SumGen, Gen.bool(), { sum, predicate ->
                sumPrism.all(sum) { predicate } == (predicate || sum is SumType.B)
            })
        }

    }

}
