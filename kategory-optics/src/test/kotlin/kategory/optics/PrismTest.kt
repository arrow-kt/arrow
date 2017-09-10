package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Eq
import kategory.NonEmptyList
import kategory.Option
import kategory.Try
import kategory.UnitSpec
import kategory.applicative
import kategory.genEither
import kategory.genFunctionAToB
import kategory.genTuple
import kategory.left
import kategory.right
import kategory.some
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PrismTest : UnitSpec() {

    sealed class SumType {
        data class A(val string: String) : SumType()
        data class B(val int: Int) : SumType()
    }

    object AGen : Gen<SumType.A> {
        override fun generate(): SumType.A = SumType.A(Gen.string().generate())
    }

    object SumGen : Gen<SumType> {
        override fun generate(): SumType = Gen.oneOf(AGen, Gen.create { SumType.B(Gen.int().generate()) }).generate()
    }

    val sumPrism = Prism<SumType, String>(
            {
                when (it) {
                    is SumType.A -> it.string.right()
                    else -> it.left()
                }
            },
            SumType::A
    )

    val stringPrism = Prism<String, List<Char>>(
            { it.toList().right() },
            { it.joinToString(separator = "") }
    )

    init {
        testLaws(
                PrismLaws.laws(
                        prism = sumPrism,
                        aGen = AGen,
                        bGen = Gen.string(),
                        funcGen = genFunctionAToB(Gen.string()),
                        EQA = Eq.any(),
                        EQB = Eq.any(),
                        FA = Option.applicative()
                ) + PrismLaws.laws(
                        prism = sumPrism.first(),
                        aGen = genTuple(AGen, Gen.int()),
                        bGen = genTuple(Gen.string(), Gen.int()),
                        funcGen = genFunctionAToB(genTuple(Gen.string(), Gen.int())),
                        EQA = Eq.any(),
                        EQB = Eq.any(),
                        FA = Try.applicative()
                ) + PrismLaws.laws(
                        prism = sumPrism.second(),
                        aGen = genTuple(Gen.int(), AGen),
                        bGen = genTuple(Gen.int(), Gen.string()),
                        funcGen = genFunctionAToB(genTuple(Gen.int(), Gen.string())),
                        EQA = Eq.any(),
                        EQB = Eq.any(),
                        FA = Option.applicative()
                ) + PrismLaws.laws(
                        prism = sumPrism.right<SumType, String, Int>(),
                        aGen = genEither(Gen.int(), AGen),
                        bGen = genEither(Gen.int(), Gen.string()),
                        funcGen = genFunctionAToB(genEither(Gen.int(), Gen.string())),
                        EQA = Eq.any(),
                        EQB = Eq.any(),
                        FA = Try.applicative()
                ) + PrismLaws.laws(
                        prism = sumPrism.left<SumType, String, Int>(),
                        aGen = genEither(AGen, Gen.int()),
                        bGen = genEither(Gen.string(), Gen.int()),
                        funcGen = genFunctionAToB(genEither(Gen.string(), Gen.int())),
                        EQA = Eq.any(),
                        EQB = Eq.any(),
                        FA = NonEmptyList.applicative()
                )

        )

        "Joining two prisms together with same target should yield same result" {
            forAll(SumGen, { a ->
                (sumPrism composePrism stringPrism).getOption(a) == sumPrism.getOption(a).flatMap(stringPrism::getOption)
            })
        }

        "Checking if there is no target" {
            forAll(SumGen, { sum ->
                sumPrism.isEmpty(sum) == sum !is SumType.A
            })
        }

        "Checking if a target exists" {
            forAll(SumGen, { sum ->
                sumPrism.isNotEmpty(sum) == sum is SumType.A
            })
        }

        "Setting a target on a prism should set the correct target"{
            forAll(AGen, Gen.string(), { a, string ->
                sumPrism.setOption(string)(a) == a.copy(string = string).some()
            })
        }

        "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
            forAll(SumGen, Gen.bool(), { sum, predicate ->
                sumPrism.find { predicate }(sum).isDefined == (predicate && sum is SumType.A)
            })
        }

        "Checking existence predicate over the target should result in same result as predicate" {
            forAll(SumGen, Gen.bool(), { sum, predicate ->
                sumPrism.exist { predicate }(sum) == (predicate && sum is SumType.A)
            })
        }

        "Checking satisfaction of predicate over the target should result in opposite result as predicate" {
            forAll(SumGen, Gen.bool(), { sum, predicate ->
                sumPrism.all { predicate }(sum) == (predicate || sum is SumType.B)
            })
        }

    }

}