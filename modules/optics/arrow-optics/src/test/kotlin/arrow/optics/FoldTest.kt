package arrow.optics

import arrow.core.Option
import arrow.data.ForListK
import arrow.data.ListK
import arrow.data.k
import arrow.instances.monoid
import arrow.instances.listk.foldable.foldable
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class FoldTest : UnitSpec() {

  init {

    val intFold = Fold.fromFoldable<ForListK, Int>(ListK.foldable())
    val stringFold = Fold.fromFoldable<ForListK, String>(ListK.foldable())

    "Fold select a list that contains one" {
      val select = Fold.select<List<Int>> { it.contains(1) }

      forAll(Gen.list(Gen.int())) { ints ->
        select.run { getAll(ints) }.firstOrNull() ==
          ints.let { if (it.contains(1)) it else null }
      }
    }

    with(intFold) {

      "Folding a list of ints" {
        forAll(Gen.list(Gen.int())) { ints ->
          fold(Int.monoid(), ints.k()) == ints.sum()
        }
      }

      "Folding a list should yield same result as combineAll" {
        forAll(Gen.list(Gen.int())) { ints ->
          combineAll(Int.monoid(), ints.k()) == ints.sum()
        }
      }

      "Folding and mapping a list of strings" {
        forAll(Gen.list(Gen.int())) { ints ->
          stringFold.run { foldMap(Int.monoid(), ints.map(Int::toString).k(), String::toInt) } == ints.sum()
        }
      }

      "Get all targets" {
        forAll(Gen.list(Gen.int())) { ints ->
          getAll(ints.k()) == ints.k()
        }
      }

      "Get the size of the fold" {
        forAll(Gen.list(Gen.int())) { ints ->
          size(ints.k()) == ints.size
        }
      }

      "Find the first element matching the predicate" {
        forAll(Gen.list(Gen.choose(-100, 100))) { ints ->
          find(ints.k()) { it > 10 } == Option.fromNullable(ints.firstOrNull { it > 10 })
        }
      }

      "Checking existence of a target" {
        forAll(Gen.list(Gen.int()), Gen.bool()) { ints, predicate ->
          exists(ints.k()) { predicate } == predicate
        }
      }

      "Check if all targets match the predicate" {
        forAll(Gen.list(Gen.int())) { ints ->
          forall(ints.k()) { it % 2 == 0 } == ints.all { it % 2 == 0 }
        }
      }

      "Check if there is no target" {
        forAll(Gen.list(Gen.int())) { ints ->
          isEmpty(ints.k()) == ints.isEmpty()
        }
      }

      "Check if there is a target" {
        forAll(Gen.list(Gen.int())) { ints ->
          nonEmpty(ints.k()) == ints.isNotEmpty()
        }
      }
    }
  }
}
