package arrow.optics

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.toOption
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.checkAll

class OptionalTest : UnitSpec() {

  init {

    testLaws(OptionalLaws.laws(
      optional = Optional.listHead(),
      aGen = Arb.list(Gen.int()),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
    ))

    testLaws(OptionalLaws.laws(
      optional = Optional.id(),
      aGen = Gen.int(),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
    ))

    testLaws(OptionalLaws.laws(
      optional = Optional.listHead<Int>().first(),
      aGen = Gen.pair(Arb.list(Gen.int()), Gen.bool()),
      bGen = Gen.pair(Gen.int(), Gen.bool()),
      funcGen = Gen.functionAToB(Gen.pair(Gen.int(), Gen.bool())),
    ))

    testLaws(OptionalLaws.laws(
      optional = Optional.listHead<Int>().first(),
      aGen = Gen.pair(Arb.list(Gen.int()), Gen.bool()),
      bGen = Gen.pair(Gen.int(), Gen.bool()),
      funcGen = Gen.functionAToB(Gen.pair(Gen.int(), Gen.bool())),
    ))

    testLaws(OptionalLaws.laws(
      optional = Optional.listHead<Int>().second(),
      aGen = Gen.pair(Gen.bool(), Arb.list(Gen.int())),
      bGen = Gen.pair(Gen.bool(), Gen.int()),
      funcGen = Gen.functionAToB(Gen.pair(Gen.bool(), Gen.int())),
    ))

    testLaws(TraversalLaws.laws(
      traversal = Optional.listHead<Int>(),
      aGen = Arb.list(Gen.int()),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
    ))

    testLaws(SetterLaws.laws(
      setter = Optional.listHead<Int>(),
      aGen = Arb.list(Gen.int()),
      bGen = Gen.int(),
      funcGen = Gen.functionAToB(Gen.int()),
    ))

    "asSetter should set absent optional" {
      checkAll(genIncompleteUser, genToken) { user, token ->
        val updatedUser = incompleteUserTokenOptional.set(user, token)
        incompleteUserTokenOptional.getOrNull(updatedUser) != null
      }
    }

    with(Optional.listHead<Int>()) {

      "asFold should behave as valid Fold: size" {
        forAll { ints: List<Int> ->
          size(ints) == ints.firstOrNull().toOption().map { 1 }.getOrElse { 0 }
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll { ints: List<Int> ->
          isNotEmpty(ints) == ints.firstOrNull().toOption().nonEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll { ints: List<Int> ->
          isEmpty(ints) == ints.firstOrNull().toOption().isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll { ints: List<Int> ->
          getAll(ints) == ints.firstOrNull().toOption().toList()
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll { ints: List<Int> ->
          combineAll(Monoid.int(), ints) ==
            ints.firstOrNull().toOption().fold({ Monoid.int().empty() }, ::identity)
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll { ints: List<Int> ->
          fold(Monoid.int(), ints) ==
            ints.firstOrNull().toOption().fold({ Monoid.int().empty() }, ::identity)
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll { ints: List<Int> ->
          firstOrNull(ints) == ints.firstOrNull()
        }
      }

      // TODO FIX
//      "asFold should behave as valid Fold: lastOption" {
//        forAll { ints: List<Int> ->
//          lastOrNull(ints) shouldBe ints.lastOrNull()
//        }
//      }
    }

    "unit should always " {
      forAll { string: String ->
        Optional.void<String, Int>().getOrNull(string) == null
      }
    }

    "unit should always return source when setting target" {
      forAll { int: Int, string: String ->
        Optional.void<String, Int>().set(string, int) == string
      }
    }

    "Checking if there is no target" {
      checkAll(Arb.list(Gen.int())) { list ->
        Optional.listHead<Int>().isNotEmpty(list) == list.isNotEmpty()
      }
    }

    "Lift should be consistent with modify" {
      checkAll(Arb.list(Gen.int())) { list ->
        val f = { i: Int -> i + 5 }
        Optional.listHead<Int>().lift(f)(list) == Optional.listHead<Int>().modify(list, f)
      }
    }

    "Checking if a target exists" {
      checkAll(Arb.list(Gen.int())) { list ->
        Optional.listHead<Int>().isEmpty(list) == list.isEmpty()
      }
    }

    "Finding a target using a predicate should be wrapped in the correct option result" {
      checkAll(Arb.list(Gen.int()), Gen.bool()) { list, predicate ->
        Optional.listHead<Int>().findOrNull(list) { predicate }?.let { true } ?: false == (predicate && list.isNotEmpty())
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      checkAll(Arb.list(Gen.int()), Gen.bool()) { list, predicate ->
        Optional.listHead<Int>().exists(list) { predicate } == (predicate && list.isNotEmpty())
      }
    }

    "Checking satisfaction of predicate over the target should result in opposite result as predicate" {
      checkAll(Arb.list(Gen.int()), Gen.bool()) { list, predicate ->
        Optional.listHead<Int>().all(list) { predicate } == if (list.isEmpty()) true else predicate
      }
    }

    "Joining two optionals together with same target should yield same result" {
      val joinedOptional = Optional.listHead<Int>().choice(defaultHead)

      checkAll(Gen.int()) { int ->
        joinedOptional.getOrNull(Left(listOf(int))) == joinedOptional.getOrNull(Right(int))
      }
    }
  }
}
