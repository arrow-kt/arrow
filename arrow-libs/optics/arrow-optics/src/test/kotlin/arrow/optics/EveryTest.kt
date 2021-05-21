package arrow.optics

import arrow.core.test.UnitSpec
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.checkAll

class EveryTest : UnitSpec() {
  init {

    with(Every.list<Int>()) {

      "asFold should behave as valid Fold: size" {
        checkAll(Arb.list(Gen.int())) { ints ->
          size(ints) == ints.size
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        checkAll(Arb.list(Gen.int())) { ints ->
          isNotEmpty(ints) == ints.isNotEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        checkAll(Arb.list(Gen.int())) { ints ->
          isEmpty(ints) == ints.isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        checkAll(Arb.list(Gen.int())) { ints ->
          getAll(ints) == ints
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        checkAll(Arb.list(Gen.int())) { ints ->
          combineAll(Monoid.int(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: fold" {
        checkAll(Arb.list(Gen.int())) { ints ->
          fold(Monoid.int(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: headOption" {
        checkAll(Arb.list(Gen.int())) { ints ->
          firstOrNull(ints) == ints.firstOrNull()
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        checkAll(Arb.list(Gen.int())) { ints ->
          lastOrNull(ints) == ints.lastOrNull()
        }
      }
    }

    with(Every.list<Int>()) {

      "Getting all targets of a traversal" {
        checkAll(Arb.list(Gen.int())) { ints ->
          getAll(ints) == ints
        }
      }

      "Folding all the values of a traversal" {
        checkAll(Arb.list(Gen.int())) { ints ->
          fold(Monoid.int(), ints) == ints.sum()
        }
      }

      "Combining all the values of a traversal" {
        checkAll(Arb.list(Gen.int())) { ints ->
          combineAll(Monoid.int(), ints) == ints.sum()
        }
      }

      "Finding an number larger than 10" {
        checkAll(Arb.list(Gen.choose(-100, 100))) { ints ->
          findOrNull(ints) { it > 10 } == ints.firstOrNull { it > 10 }
        }
      }

      "Get the length from a traversal" {
        checkAll(Arb.list(Gen.int())) { ints ->
          size(ints) == ints.size
        }
      }
    }
  }
}
