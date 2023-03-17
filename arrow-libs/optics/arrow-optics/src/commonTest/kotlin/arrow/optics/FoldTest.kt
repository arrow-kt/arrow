package arrow.optics

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll

class FoldTest : StringSpec({

    "Fold select a list that contains one" {
      val select = Fold.select<List<Int>> { it.contains(1) }

      checkAll(Arb.list(Arb.int())) { ints ->
        select.run { getAll(ints) }.firstOrNull() shouldBe
          ints.let { if (it.contains(1)) it else null }
      }
    }

    with(Fold.list<Int>()) {

      "Folding a list of ints" {
        checkAll(Arb.list(Arb.int())) { ints ->
          fold(0, { x, y -> x + y }, ints) shouldBe ints.sum()
        }
      }

      "Folding and mapping a list of strings" {
        checkAll(Arb.list(Arb.int())) { ints ->
          Fold.list<String>()
            .foldMap(0, { x, y -> x + y }, ints.map(Int::toString), String::toInt) shouldBe ints.sum()
        }
      }

      "Get all targets" {
        checkAll(Arb.list(Arb.int())) { ints ->
          getAll(ints) shouldBe ints
        }
      }

      "Get the size of the fold" {
        checkAll(Arb.list(Arb.int())) { ints ->
          size(ints) shouldBe ints.size
        }
      }

      "Find the first element matching the predicate" {
        checkAll(Arb.list(Arb.int(-100..100).orNull())) { ints ->
          val predicate = { i: Int? -> i?.let { it > 10 } ?: false }
          Fold.list<Int?>().findOrNull(ints, predicate) shouldBe ints.firstOrNull(predicate)
        }
      }

      "Checking existence of a target" {
        checkAll(Arb.list(Arb.int().orNull()), Arb.boolean()) { ints, predicate ->
          Fold.list<Int?>().exists(ints) { predicate } shouldBe (predicate && ints.isNotEmpty())
        }
      }

      "Check if all targets match the predicate" {
        checkAll(Arb.list(Arb.int())) { ints ->
          all(ints) { it % 2 == 0 } shouldBe ints.all { it % 2 == 0 }
        }
      }

      "Check if there is no target" {
        checkAll(Arb.list(Arb.int())) { ints ->
          isEmpty(ints) shouldBe ints.isEmpty()
        }
      }

      "Check if there is a target" {
        checkAll(Arb.list(Arb.int())) { ints ->
          isNotEmpty(ints) shouldBe ints.isNotEmpty()
        }
      }
    }

})
