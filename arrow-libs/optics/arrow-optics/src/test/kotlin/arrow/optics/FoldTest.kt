package arrow.optics

import arrow.core.test.UnitSpec
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.checkAll

class FoldTest : UnitSpec() {

  init {

    "Fold select a list that contains one" {
      val select = Fold.select<List<Int>> { it.contains(1) }

      checkAll(Arb.list(Gen.int())) { ints ->
        select.run { getAll(ints) }.firstOrNull() ==
          ints.let { if (it.contains(1)) it else null }
      }
    }

    with(Fold.list<Int>()) {

      "Folding a list of ints" {
        checkAll(Arb.list(Gen.int())) { ints ->
          fold(Monoid.int(), ints) == ints.sum()
        }
      }

      "Folding a list should yield same result as combineAll" {
        checkAll(Arb.list(Gen.int())) { ints ->
          combineAll(Monoid.int(), ints) == ints.sum()
        }
      }

      "Folding and mapping a list of strings" {
        checkAll(Arb.list(Gen.int())) { ints ->
          Fold.list<String>()
            .foldMap(Monoid.int(), ints.map(Int::toString), String::toInt) == ints.sum()
        }
      }

      "Get all targets" {
        checkAll(Arb.list(Gen.int())) { ints ->
          getAll(ints) == ints
        }
      }

      "Get the size of the fold" {
        checkAll(Arb.list(Gen.int())) { ints ->
          size(ints) == ints.size
        }
      }

      "Find the first element matching the predicate" {
        checkAll(Arb.list(Gen.choose(-100, 100))) { ints ->
          findOrNull(ints) { it > 10 } == ints.firstOrNull { it > 10 }
        }
      }

      "Checking existence of a target" {
        checkAll(Arb.list(Gen.int()), Gen.bool()) { ints, predicate ->
          exists(ints) { predicate } == (predicate && ints.isNotEmpty())
        }
      }

      "Check if all targets match the predicate" {
        checkAll(Arb.list(Gen.int())) { ints ->
          all(ints) { it % 2 == 0 } == ints.all { it % 2 == 0 }
        }
      }

      "Check if there is no target" {
        checkAll(Arb.list(Gen.int())) { ints ->
          isEmpty(ints) == ints.isEmpty()
        }
      }

      "Check if there is a target" {
        checkAll(Arb.list(Gen.int())) { ints ->
          isNotEmpty(ints) == ints.isNotEmpty()
        }
      }
    }
  }
}
