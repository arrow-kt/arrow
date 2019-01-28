package arrow.optics

import arrow.core.*
import arrow.core.extensions.`try`.applicative.applicative
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eq.eq
import arrow.data.*
import arrow.data.extensions.list.foldable.nonEmpty
import arrow.data.extensions.listk.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.*
import arrow.test.laws.OptionalLaws
import arrow.test.laws.SetterLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class OptionalTest : UnitSpec() {

  init {

    testLaws(OptionalLaws.laws(
      optional = optionalHead,
      aGen = genNonEmptyList(Gen.int()).map { it.all },
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
      aGen = genTuple(genNonEmptyList(Gen.int()).map { it.all }, Gen.bool()),
      bGen = genTuple(Gen.int(), Gen.bool()),
      funcGen = genFunctionAToB(genTuple(Gen.int(), Gen.bool())),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any())
    ))

    testLaws(OptionalLaws.laws(
      optional = optionalHead.first(),
      aGen = genTuple(genNonEmptyList(Gen.int()).map { it.all }, Gen.bool()),
      bGen = genTuple(Gen.int(), Gen.bool()),
      funcGen = genFunctionAToB(genTuple(Gen.int(), Gen.bool())),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any())
    ))

    testLaws(OptionalLaws.laws(
      optional = optionalHead.second(),
      aGen = genTuple(Gen.bool(), genNonEmptyList(Gen.int()).map { it.all }),
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
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    testLaws(SetterLaws.laws(
      setter = optionalHead.asSetter(),
      aGen = Gen.list(Gen.int()),
      bGen = Gen.int(),
      funcGen = genFunctionAToB(Gen.int()),
      EQA = Eq.any()
    ))

    with(optionalHead.asFold()) {

      "asFold should behave as valid Fold: size" {
        forAll { ints: List<Int> ->
          size(ints) == ints.firstOrNull().toOption().map { 1 }.getOrElse { 0 }
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll { ints: List<Int> ->
          nonEmpty(ints) == ints.firstOrNull().toOption().nonEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll { ints: List<Int> ->
          isEmpty(ints) == ints.firstOrNull().toOption().isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll { ints: List<Int> ->
          getAll(ints) == ints.firstOrNull().toOption().toList().k()
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll { ints: List<Int> ->
          combineAll(Int.monoid(), ints) ==
            ints.firstOrNull().toOption().fold({ Int.monoid().empty() }, ::identity)
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll { ints: List<Int> ->
          fold(Int.monoid(), ints) ==
            ints.firstOrNull().toOption().fold({ Int.monoid().empty() }, ::identity)
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll { ints: List<Int> ->
          headOption(ints) == ints.firstOrNull().toOption()
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll { ints: List<Int> ->
          lastOption(ints) == ints.firstOrNull().toOption()
        }
      }
    }

    "unit should always " {
      forAll { string: String ->
        Optional.void<String, Int>().getOption(string) == None
      }
    }

    "unit should always return source when setting target" {
      forAll { int: Int, string: String ->
        Optional.void<String, Int>().set(string, int) == string
      }
    }

    "Checking if there is no target" {
      forAll(Gen.list(Gen.int())) { list ->
        optionalHead.nonEmpty(list) == list.isNotEmpty()
      }
    }

    "Lift should be consistent with modify" {
      forAll(Gen.list(Gen.int())) { list ->
        val f = { i: Int -> i + 5 }
        optionalHead.lift(f)(list) == optionalHead.modify(list, f)
      }
    }

    "LiftF should be consistent with modifyF" {
      forAll(Gen.list(Gen.int()), genTry(Gen.int())) { list, tryInt ->
        val f = { _: Int -> tryInt }
        optionalHead.liftF(Try.applicative(), f)(list) == optionalHead.modifyF(Try.applicative(), list, f)
      }
    }

    "Checking if a target exists" {
      forAll(Gen.list(Gen.int())) { list ->
        optionalHead.isEmpty(list) == list.isEmpty()
      }
    }

    "Finding a target using a predicate should be wrapped in the correct option result" {
      forAll(Gen.list(Gen.int()), Gen.bool()) { list, predicate ->
        optionalHead.find(list) { predicate }.fold({ false }, { true }) == (predicate && list.nonEmpty())
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      forAll(Gen.list(Gen.int()), Gen.bool()) { list, predicate ->
        optionalHead.exists(list) { predicate } == (predicate && list.nonEmpty())
      }
    }

    "Checking satisfaction of predicate over the target should result in opposite result as predicate" {
      forAll(Gen.list(Gen.int()), Gen.bool()) { list, predicate ->
        optionalHead.all(list) { predicate } == if(list.isEmpty()) true else predicate
      }
    }

    "Joining two optionals together with same target should yield same result" {
      val joinedOptional = optionalHead.choice(defaultHead)

      forAll(Gen.int()) { int ->
        joinedOptional.getOption(Left(listOf(int))) == joinedOptional.getOption(Right(int))
      }
    }

    val successInt = Try.success<Int>().asOptional()

    "Extract should extract the focus from the state" {
      forAll(genTry(Gen.int())) { tryInt ->
        successInt.extract().run(tryInt) ==
          State { x: Try<Int> ->
            x toT successInt.getOption(x)
          }.run(tryInt)
      }
    }

    "toState should be an alias to extract" {
      forAll(genTry(Gen.int())) { x ->
        successInt.toState().run(x) == successInt.extract().run(x)
      }
    }

    "extractMap with f should be same as extract and map" {
      forAll(genTry(Gen.int()), genFunctionAToB<Int, Int>(Gen.int())) { x, f ->
        successInt.extractMap(f).run(x) == successInt.extract().map { it.map(f) }.run(x)
      }
    }

    "update f should be same modify f within State and returning new state" {
      forAll(genTry(Gen.int()), genFunctionAToB<Int, Int>(Gen.int())) { x, f ->
        successInt.update(f).run(x) ==
          State { xx: Try<Int> ->
            successInt.modify(xx, f)
              .let { it toT successInt.getOption(it) }
          }.run(x)
      }
    }

    "updateOld f should be same as modify f within State and returning old state" {
      forAll(genTry(Gen.int()), genFunctionAToB<Int, Int>(Gen.int())) { x, f ->
        successInt.updateOld(f).run(x) ==
          State { xx: Try<Int> ->
            successInt.modify(xx, f) toT successInt.getOption(xx)
          }.run(x)
      }
    }

    "update_ f should be as modify f within State and returning Unit" {
      forAll(genTry(Gen.int()), genFunctionAToB<Int, Int>(Gen.int())) { x, f ->
        successInt.update_(f).run(x) ==
          State { xx: Try<Int> ->
            successInt.modify(xx, f) toT Unit
          }.run(x)
      }
    }

    "assign a should be same set a within State and returning new value" {
      forAll(genTry(Gen.int()), Gen.int()) { x, i ->
        successInt.assign(i).run(x) ==
          State { xx: Try<Int> ->
            successInt.set(xx, i)
              .let { it toT successInt.getOption(it) }
          }.run(x)
      }
    }

    "assignOld f should be same as modify f within State and returning old state" {
      forAll(genTry(Gen.int()), Gen.int()) { x, i ->
        successInt.assignOld(i).run(x) ==
          State { xx: Try<Int> ->
            successInt.set(xx, i) toT successInt.getOption(xx)
          }.run(x)
      }
    }

    "assign_ f should be as modify f within State and returning Unit" {
      forAll(genTry(Gen.int()), Gen.int()) { x, i ->
        successInt.assign_(i).run(x) ==
          State { xx: Try<Int> ->
            successInt.set(xx, i) toT Unit
          }.run(x)
      }
    }

  }
}
