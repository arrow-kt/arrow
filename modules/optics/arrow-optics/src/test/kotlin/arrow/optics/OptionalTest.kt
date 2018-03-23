package arrow.optics

import arrow.core.*
import arrow.data.k
import arrow.syntax.collections.firstOption
import arrow.syntax.either.left
import arrow.syntax.either.right
import arrow.syntax.foldable.combineAll
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genTry
import arrow.test.generators.genTuple
import arrow.test.laws.OptionalLaws
import arrow.test.laws.SetterLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionalTest : UnitSpec() {

    init {

        testLaws(OptionalLaws.laws(
                optional = optionalHead,
                aGen = Gen.list(Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any())
        ))

        testLaws(OptionalLaws.laws(
                optional = Optional.id(),
                aGen = Gen.int(),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any())
        ))

        testLaws(OptionalLaws.laws(
                optional = optionalHead.first(),
                aGen = genTuple(Gen.list(Gen.int()), Gen.bool()),
                bGen = genTuple(Gen.int(), Gen.bool()),
                funcGen = genFunctionAToB(genTuple(Gen.int(), Gen.bool())),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any())
        ))

        testLaws(OptionalLaws.laws(
                optional = optionalHead.first(),
                aGen = genTuple(Gen.list(Gen.int()), Gen.bool()),
                bGen = genTuple(Gen.int(), Gen.bool()),
                funcGen = genFunctionAToB(genTuple(Gen.int(), Gen.bool())),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any())
        ))

        testLaws(OptionalLaws.laws(
                optional = optionalHead.second(),
                aGen = genTuple(Gen.bool(), Gen.list(Gen.int())),
                bGen = genTuple(Gen.bool(), Gen.int()),
                funcGen = genFunctionAToB(genTuple(Gen.bool(), Gen.int())),
                EQA = Eq.any(),
                EQOptionB = Option.eq(Eq.any())
        ))

        testLaws(TraversalLaws.laws(
                traversal = optionalHead.asTraversal(),
                aGen = Gen.list(Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any()
        ))

        testLaws(SetterLaws.laws(
                setter = optionalHead.asSetter(),
                aGen = Gen.list(Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any()
        ))

        "asFold should behave as valid Fold: size" {
            forAll { ints: List<Int> ->
                optionalHead.asFold().size(ints) == ints.firstOption().map { 1 }.getOrElse { 0 }
            }
        }

        "asFold should behave as valid Fold: nonEmpty" {
            forAll { ints: List<Int> ->
                optionalHead.asFold().nonEmpty(ints) == ints.firstOption().nonEmpty()
            }
        }

        "asFold should behave as valid Fold: isEmpty" {
            forAll { ints: List<Int> ->
                optionalHead.asFold().isEmpty(ints) == ints.firstOption().isEmpty()
            }
        }

        "asFold should behave as valid Fold: getAll" {
            forAll { ints: List<Int> ->
                optionalHead.asFold().getAll(ints) == ints.firstOption().toList().k()
            }
        }

        "asFold should behave as valid Fold: combineAll" {
            forAll { ints: List<Int> ->
                optionalHead.asFold().combineAll(ints) == ints.firstOption().combineAll()
            }
        }

        "asFold should behave as valid Fold: fold" {
            forAll { ints: List<Int> ->
                optionalHead.asFold().fold(ints) == ints.firstOption().combineAll()
            }
        }

        "asFold should behave as valid Fold: headOption" {
            forAll { ints: List<Int> ->
                optionalHead.asFold().headOption(ints) == ints.firstOption()
            }
        }

        "asFold should behave as valid Fold: lastOption" {
            forAll { ints: List<Int> ->
                optionalHead.asFold().lastOption(ints) == ints.firstOption()
            }
        }

        "void should always " {
            forAll({ string: String ->
                Optional.void<String, Int>().getOption(string) == None
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

        "Lift should be consistent with modify" {
            forAll(Gen.list(Gen.int()), { list ->
                val f = { i: Int -> i + 5 }
                optionalHead.lift(f)(list) == optionalHead.modify(list, f)
            })
        }

        "LiftF should be consistent with modifyF" {
            forAll(Gen.list(Gen.int()), genTry(Gen.int()), { list, tryInt ->
                val f = { _: Int -> tryInt }
                optionalHead.liftF(f, Try.applicative())(list) == optionalHead.modifyF(list, f, Try.applicative())
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
