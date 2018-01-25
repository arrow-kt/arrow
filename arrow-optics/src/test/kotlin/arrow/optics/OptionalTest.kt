package arrow.optics

import arrow.core.None
import arrow.core.Option
import arrow.core.eq
import arrow.data.Try
import arrow.data.applicative
import arrow.optics.instances.listElementOptional
import arrow.optics.instances.listElementPositionOptional
import arrow.optics.instances.nullableOptional
import arrow.syntax.either.left
import arrow.syntax.either.right
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genNullable
import arrow.test.generators.genTry
import arrow.test.generators.genTuple
import arrow.test.laws.OptionalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith
import java.util.*

@RunWith(KTestJUnitRunner::class)
class OptionalTest : UnitSpec() {

    init {

        testLaws(OptionalLaws.laws(
                optional = listElementOptional({ it == 0.toByte() }),
                aGen = Gen.list(byte()),
                bGen = byte(),
                funcGen = genFunctionAToB(byte()),
                EQA = Eq.any(),
                EQOptionB = Eq.any()))

        testLaws(OptionalLaws.laws(
                optional = listElementPositionOptional(50),
                aGen = Gen.list(Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Eq.any()))

        testLaws(OptionalLaws.laws(
                optional = nullableOptional(),
                aGen = genNullable(Gen.int()),
                bGen = Gen.int(),
                funcGen = genFunctionAToB(Gen.int()),
                EQA = Eq.any(),
                EQOptionB = Eq.any()))

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

    fun byte() = object : Gen<Byte> {
        val RANDOM = Random()
        override fun generate(): Byte {
            val arr = ByteArray(1)
            RANDOM.nextBytes(arr)
            return arr[0]
        }
    }

}
