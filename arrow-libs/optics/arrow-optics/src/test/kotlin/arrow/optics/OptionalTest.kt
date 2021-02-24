package arrow.optics

import arrow.core.Left
import arrow.core.ListK
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.extensions.list.foldable.nonEmpty
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.monoid
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.eq.eq
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.k
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.option
import arrow.core.test.generators.tuple2
import arrow.core.toOption
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class OptionalTest : UnitSpec() {

  init {

    testLaws(
      OptionalLaws.laws(
        optional = Optional.listHead(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optional = Optional.id(),
        aGen = Gen.int(),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optional = Optional.listHead<Int>().first(),
        aGen = Gen.tuple2(Gen.list(Gen.int()), Gen.bool()),
        bGen = Gen.tuple2(Gen.int(), Gen.bool()),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.int(), Gen.bool())),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optional = Optional.listHead<Int>().first(),
        aGen = Gen.tuple2(Gen.list(Gen.int()), Gen.bool()),
        bGen = Gen.tuple2(Gen.int(), Gen.bool()),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.int(), Gen.bool())),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      )
    )

    testLaws(
      OptionalLaws.laws(
        optional = Optional.listHead<Int>().second(),
        aGen = Gen.tuple2(Gen.bool(), Gen.list(Gen.int())),
        bGen = Gen.tuple2(Gen.bool(), Gen.int()),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.bool(), Gen.int())),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Optional.listHead<Int>().asTraversal(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      )
    )

    testLaws(
      SetterLaws.laws(
        setter = Optional.listHead<Int>().asSetter(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any()
      )
    )

    "asSetter should set absent optional" {
      forAll(genIncompleteUser, genToken) { user, token ->
        val updatedUser = incompleteUserTokenOptional.asSetter().set(user, token)
        incompleteUserTokenOptional.getOption(updatedUser).nonEmpty()
      }
    }

    with(Optional.listHead<Int>().asFold()) {

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
        Optional.listHead<Int>().nonEmpty(list) == list.isNotEmpty()
      }
    }

    "Lift should be consistent with modify" {
      forAll(Gen.list(Gen.int())) { list ->
        val f = { i: Int -> i + 5 }
        Optional.listHead<Int>().lift(f)(list) == Optional.listHead<Int>().modify(list, f)
      }
    }

    "LiftF should be consistent with modifyF" {
      forAll(Gen.list(Gen.int()), Gen.option(Gen.int())) { list, tryInt ->
        val f = { _: Int -> tryInt }
        Optional.listHead<Int>().liftF(Option.applicative(), f)(list) == Optional.listHead<Int>().modifyF(Option.applicative(), list, f)
      }
    }

    "Checking if a target exists" {
      forAll(Gen.list(Gen.int())) { list ->
        Optional.listHead<Int>().isEmpty(list) == list.isEmpty()
      }
    }

    "Finding a target using a predicate should be wrapped in the correct option result" {
      forAll(Gen.list(Gen.int()), Gen.bool()) { list, predicate ->
        Optional.listHead<Int>().find(list) { predicate }.fold({ false }, { true }) == (predicate && list.nonEmpty())
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      forAll(Gen.list(Gen.int()), Gen.bool()) { list, predicate ->
        Optional.listHead<Int>().exists(list) { predicate } == (predicate && list.nonEmpty())
      }
    }

    "Checking satisfaction of predicate over the target should result in opposite result as predicate" {
      forAll(Gen.list(Gen.int()), Gen.bool()) { list, predicate ->
        Optional.listHead<Int>().all(list) { predicate } == if (list.isEmpty()) true else predicate
      }
    }

    "Joining two optionals together with same target should yield same result" {
      val joinedOptional = Optional.listHead<Int>().choice(defaultHead)

      forAll(Gen.int()) { int ->
        joinedOptional.getOption(Left(listOf(int))) == joinedOptional.getOption(Right(int))
      }
    }
  }
}
