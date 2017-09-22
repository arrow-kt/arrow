package kategory.optics

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kategory.Eq
import kategory.Option
import kategory.OptionalLaws
import kategory.UnitSpec
import kategory.genFunctionAToB
import kategory.genTuple
import kategory.left
import kategory.right
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionalTest : UnitSpec() {

    init {

        testLaws(
                OptionalLaws.laws(
                        optional = optionalHead,
                        aGen = Gen.list(Gen.int()),
                        bGen = Gen.int(),
                        funcGen = genFunctionAToB(Gen.int()),
                        EQA = Eq.any(),
                        EQB = Eq.any()
                ) + OptionalLaws.laws(
                        optional = Optional.id(),
                        aGen = Gen.int(),
                        bGen = Gen.int(),
                        funcGen = genFunctionAToB(Gen.int()),
                        EQA = Eq.any(),
                        EQB = Eq.any()
                ) + OptionalLaws.laws(
                        optional = optionalHead.first(),
                        aGen = genTuple(Gen.list(Gen.int()), Gen.bool()),
                        bGen = genTuple(Gen.int(), Gen.bool()),
                        funcGen = genFunctionAToB(genTuple(Gen.int(), Gen.bool())),
                        EQA = Eq.any(),
                        EQB = Eq.any()
                ) + OptionalLaws.laws(
                        optional = optionalHead.second(),
                        aGen = genTuple(Gen.bool(), Gen.list(Gen.int())),
                        bGen = genTuple(Gen.bool(), Gen.int()),
                        funcGen = genFunctionAToB(genTuple(Gen.bool(), Gen.int())),
                        EQA = Eq.any(),
                        EQB = Eq.any()
                )
        )

        "void should always " {
            forAll({ string: String ->
                Optional.void<String, Int>().getOption(string) == Option.None
            })
        }

        "void should always return source when setting target" {
            forAll({ int: Int, string: String ->
                Optional.void<String, Int>().set(string, int) == string
            })
        }

        "Checking if there is no target" {
            forAll(Gen.list(Gen.int()), { list ->
                optionalHead.nonEmpty(list) == list.isNotEmpty()
            })
        }

        "Checking if a target exists" {
            forAll(Gen.list(Gen.int()), { list ->
                optionalHead.isEmpty(list) == list.isEmpty()
            })
        }

        "Finding a target using a predicate should be wrapped in the correct option result" {
            forAll(Gen.list(Gen.int()), Gen.bool(), { list, predicate ->
                optionalHead.find(list) { predicate }.fold({ false }, { true }) == predicate
            })
        }

        "Checking existence predicate over the target should result in same result as predicate" {
            forAll(Gen.list(Gen.int()), Gen.bool(), { list, predicate ->
                optionalHead.exists(list) { predicate } == predicate
            })
        }

        "Checking satisfaction of predicate over the target should result in opposite result as predicate" {
            forAll(Gen.list(Gen.int()), Gen.bool(), { list, predicate ->
                optionalHead.all(list) { predicate } == predicate
            })
        }

        "Joining two optionals together with same target should yield same result" {
            val joinedOptional = optionalHead.choice(defaultHead)

            forAll(Gen.int(), { int ->
                joinedOptional.getOption(listOf(int).left()) == joinedOptional.getOption(int.right())
            })
        }

    }

}