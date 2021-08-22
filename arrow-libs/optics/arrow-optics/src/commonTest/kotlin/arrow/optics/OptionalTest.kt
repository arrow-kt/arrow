package arrow.optics

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.toOption
import arrow.optics.test.laws.OptionalLaws
import arrow.typeclasses.Monoid
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class OptionalTest : UnitSpec() {

  init {

    testLaws(
      "Optional identity - ",
      OptionalLaws.laws(
        optional = Optional.id(),
        aGen = Arb.int(),
        bGen = Arb.int(),
        funcGen = Arb.functionAToB(Arb.int()),
      )
    )

    testLaws(
      "Optional first head - ",
      OptionalLaws.laws(
        optional = Optional.listHead<Int>().first(),
        aGen = Arb.pair(Arb.list(Arb.int()), Arb.bool()),
        bGen = Arb.pair(Arb.int(), Arb.bool()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.int(), Arb.bool())),
      )
    )

    testLaws(
      "Optional second head - ",
      OptionalLaws.laws(
        optional = Optional.listHead<Int>().second(),
        aGen = Arb.pair(Arb.bool(), Arb.list(Arb.int())),
        bGen = Arb.pair(Arb.bool(), Arb.int()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.bool(), Arb.int())),
      )
    )

    "asSetter should set absent optional" {
      checkAll(genIncompleteUser, genToken) { user, token ->
        val updatedUser = incompleteUserTokenOptional.set(user, token)
        incompleteUserTokenOptional.getOrNull(updatedUser).shouldNotBeNull()
      }
    }

    with(Optional.listHead<Int>()) {

      "asFold should behave as valid Fold: size" {
        checkAll(Arb.list(Arb.int())) { ints: List<Int> ->
          size(ints) shouldBe ints.firstOrNull().toOption().map { 1 }.getOrElse { 0 }
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        checkAll(Arb.list(Arb.int())) { ints: List<Int> ->
          isNotEmpty(ints) shouldBe ints.firstOrNull().toOption().nonEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        checkAll(Arb.list(Arb.int())) { ints: List<Int> ->
          isEmpty(ints) shouldBe ints.firstOrNull().toOption().isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        checkAll(Arb.list(Arb.int())) { ints: List<Int> ->
          getAll(ints) shouldBe ints.firstOrNull().toOption().toList()
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        checkAll(Arb.list(Arb.int())) { ints: List<Int> ->
          combineAll(Monoid.int(), ints) shouldBe
            ints.firstOrNull().toOption().fold({ Monoid.int().empty() }, ::identity)
        }
      }

      "asFold should behave as valid Fold: fold" {
        checkAll(Arb.list(Arb.int())) { ints: List<Int> ->
          fold(Monoid.int(), ints) shouldBe
            ints.firstOrNull().toOption().fold({ Monoid.int().empty() }, ::identity)
        }
      }
    }

    "listHead.firstOrNull == firstOrNull" {
      checkAll(Arb.list(Arb.int().orNull())) { ints ->
        Optional.listHead<Int?>().firstOrNull(ints) shouldBe ints.firstOrNull()
      }
    }

    "listHead.lastOrNull == firstOrNull" {
      checkAll(Arb.list(Arb.int().orNull())) { ints ->
        Optional.listHead<Int?>().lastOrNull(ints) shouldBe ints.firstOrNull()
      }
    }

    "unit should always " {
      checkAll(Arb.string()) { string: String ->
        Optional.void<String, Int>().getOrNull(string) shouldBe null
      }
    }

    "unit should always return source when setting target" {
      checkAll(Arb.int(), Arb.string()) { int: Int, string: String ->
        Optional.void<String, Int>().set(string, int) shouldBe string
      }
    }

    "Checking if there is no target" {
      checkAll(Arb.list(Arb.int())) { list ->
        Optional.listHead<Int>().isNotEmpty(list) shouldBe list.isNotEmpty()
      }
    }

    "Lift should be consistent with modify" {
      checkAll(Arb.list(Arb.int())) { list ->
        val f = { i: Int -> i + 5 }
        Optional.listHead<Int>().lift(f)(list) shouldBe Optional.listHead<Int>().modify(list, f)
      }
    }

    "Checking if a target exists" {
      checkAll(Arb.list(Arb.int())) { list ->
        Optional.listHead<Int>().isEmpty(list) shouldBe list.isEmpty()
      }
    }

    "Finding a target using a predicate should be wrapped in the correct option result" {
      checkAll(Arb.list(Arb.int()), Arb.bool()) { list, predicate ->
        Optional.listHead<Int>().findOrNull(list) { predicate }?.let { true }
          ?: false shouldBe (predicate && list.isNotEmpty())
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      checkAll(Arb.list(Arb.int().orNull()), Arb.bool()) { list, predicate ->
        Optional.listHead<Int?>().exists(list) { predicate } shouldBe (predicate && list.isNotEmpty())
      }
    }

    "Checking satisfaction of predicate over the target should result in opposite result as predicate" {
      checkAll(Arb.list(Arb.int()), Arb.bool()) { list, predicate ->
        Optional.listHead<Int>().all(list) { predicate } shouldBe if (list.isEmpty()) true else predicate
      }
    }

    "Joining two optionals together with same target should yield same result" {
      val joinedOptional = Optional.listHead<Int>().choice(defaultHead)

      checkAll(Arb.int()) { int ->
        joinedOptional.getOrNull(Left(listOf(int))) shouldBe joinedOptional.getOrNull(Right(int))
      }
    }
  }
}
