package arrow.optics

import arrow.core.int
import arrow.core.test.UnitSpec
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class EveryTest : UnitSpec() {
  init {

    with(Every.list<Int>()) {

      "asFold should behave as valid Fold: size" {
        forAll(Gen.list(Gen.int())) { ints ->
          size(ints) == ints.size
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(Gen.list(Gen.int())) { ints ->
          isNotEmpty(ints) == ints.isNotEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(Gen.list(Gen.int())) { ints ->
          isEmpty(ints) == ints.isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(Gen.list(Gen.int())) { ints ->
          getAll(ints) == ints
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(Gen.list(Gen.int())) { ints ->
          combineAll(Monoid.int(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(Gen.list(Gen.int())) { ints ->
          fold(Monoid.int(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(Gen.list(Gen.int())) { ints ->
          firstOrNull(ints) == ints.firstOrNull()
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(Gen.list(Gen.int())) { ints ->
          lastOrNull(ints) == ints.lastOrNull()
        }
      }
    }

    with(Every.list<Int>()) {

      "Getting all targets of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          getAll(ints) == ints
        }
      }

      "Folding all the values of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          fold(Monoid.int(), ints) == ints.sum()
        }
      }

      "Combining all the values of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          combineAll(Monoid.int(), ints) == ints.sum()
        }
      }

      "Finding an number larger than 10" {
        forAll(Gen.list(Gen.choose(-100, 100))) { ints ->
          findOrNull(ints) { it > 10 } == ints.firstOrNull { it > 10 }
        }
      }

      "Get the length from a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          size(ints) == ints.size
        }
      }
    }
  }
}
