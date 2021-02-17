package arrow.optics

import arrow.core.extensions.list.foldable.nonEmpty
import arrow.core.int
import arrow.core.test.UnitSpec
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class FoldTest : UnitSpec() {

  init {

    "Fold select a list that contains one" {
      val select = Fold.select<List<Int>> { it.contains(1) }

      forAll(Gen.list(Gen.int())) { ints ->
        select.run { getAll(ints) }.firstOrNull() ==
          ints.let { if (it.contains(1)) it else null }
      }
    }

    with(Fold.list<Int>()) {

      "Folding a list of ints" {
        forAll(Gen.list(Gen.int())) { ints ->
          fold(Monoid.int(), ints) == ints.sum()
        }
      }

      "Folding a list should yield same result as combineAll" {
        forAll(Gen.list(Gen.int())) { ints ->
          combineAll(Monoid.int(), ints) == ints.sum()
        }
      }

      "Folding and mapping a list of strings" {
        forAll(Gen.list(Gen.int())) { ints ->
          Fold.list<String>()
            .foldMap(Monoid.int(), ints.map(Int::toString), String::toInt) == ints.sum()
        }
      }

      "Get all targets" {
        forAll(Gen.list(Gen.int())) { ints ->
          getAll(ints) == ints
        }
      }

      "Get the size of the fold" {
        forAll(Gen.list(Gen.int())) { ints ->
          size(ints) == ints.size
        }
      }

      "Find the first element matching the predicate" {
        forAll(Gen.list(Gen.choose(-100, 100))) { ints ->
          findOrNull(ints) { it > 10 } == ints.firstOrNull { it > 10 }
        }
      }

      "Checking existence of a target" {
        forAll(Gen.list(Gen.int()), Gen.bool()) { ints, predicate ->
          exists(ints) { predicate } == (predicate && ints.nonEmpty())
        }
      }

      "Check if all targets match the predicate" {
        forAll(Gen.list(Gen.int())) { ints ->
          forall(ints) { it % 2 == 0 } == ints.all { it % 2 == 0 }
        }
      }

      "Check if there is no target" {
        forAll(Gen.list(Gen.int())) { ints ->
          isEmpty(ints) == ints.isEmpty()
        }
      }

      "Check if there is a target" {
        forAll(Gen.list(Gen.int())) { ints ->
          isNotEmpty(ints) == ints.isNotEmpty()
        }
      }
    }
  }
}
