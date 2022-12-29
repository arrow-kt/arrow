package arrow.optics

import arrow.typeclasses.Monoid
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll

class EveryTest : StringSpec({

    with(Every.list<Int>()) {

      "asFold should behave as valid Fold: size" {
        checkAll(Arb.list(Arb.int())) { ints ->
          size(ints) shouldBe ints.size
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        checkAll(Arb.list(Arb.int())) { ints ->
          isNotEmpty(ints) shouldBe ints.isNotEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        checkAll(Arb.list(Arb.int())) { ints ->
          isEmpty(ints) shouldBe ints.isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        checkAll(Arb.list(Arb.int())) { ints ->
          getAll(ints) shouldBe ints
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        checkAll(Arb.list(Arb.int())) { ints ->
          fold(Monoid.int(), ints) shouldBe ints.sum()
        }
      }

      "asFold should behave as valid Fold: fold" {
        checkAll(Arb.list(Arb.int())) { ints ->
          fold(Monoid.int(), ints) shouldBe ints.sum()
        }
      }

      "asFold should behave as valid Fold: headOption" {
        checkAll(Arb.list(Arb.int().orNull())) { ints ->
          Every.list<Int?>().firstOrNull(ints) shouldBe ints.firstOrNull()
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        checkAll(Arb.list(Arb.int().orNull())) { ints ->
          Every.list<Int?>().lastOrNull(ints) shouldBe ints.lastOrNull()
        }
      }
    }

    with(Every.list<Int>()) {

      "Getting all targets of a traversal" {
        checkAll(Arb.list(Arb.int())) { ints ->
          getAll(ints) shouldBe ints
        }
      }

      "Folding all the values of a traversal" {
        checkAll(Arb.list(Arb.int())) { ints ->
          fold(Monoid.int(), ints) shouldBe ints.sum()
        }
      }

      "Combining all the values of a traversal" {
        checkAll(Arb.list(Arb.int())) { ints ->
          fold(Monoid.int(), ints) shouldBe ints.sum()
        }
      }

      "Finding an number larger than 10" {
        checkAll(Arb.list(Arb.int(-100..100).orNull())) { ints ->
          val predicate = { i: Int? -> i?.let { it > 10 } ?: false }
          Every.list<Int?>().findOrNull(ints, predicate) shouldBe ints.firstOrNull(predicate)
        }
      }

      "Get the length from a traversal" {
        checkAll(Arb.list(Arb.int())) { ints ->
          size(ints) shouldBe ints.size
        }
      }
    }

})
