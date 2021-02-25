package arrow.optics

import arrow.core.Option
import arrow.core.int
import arrow.core.k
import arrow.core.test.UnitSpec
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.listK
import arrow.core.test.generators.tuple2
import arrow.core.toOption
import arrow.core.toT
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class TraversalTest : UnitSpec() {

  init {

    val listKTraverse = Traversal.list<Int>()

    testLaws(
      TraversalLaws.laws(
        traversal = listKTraverse,
        aGen = Gen.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      ),

      SetterLaws.laws(
        setter = listKTraverse.asSetter(),
        aGen = Gen.list(Gen.int()),
        bGen = Gen.int(),
        funcGen = Gen.functionAToB(Gen.int()),
        EQA = Eq.any()
      )
    )

    testLaws(
      TraversalLaws.laws(
        traversal = Traversal({ it.a }, { it.b }, { a, b, _ -> a toT b }),
        aGen = Gen.tuple2(Gen.float(), Gen.float()),
        bGen = Gen.float(),
        funcGen = Gen.functionAToB(Gen.float()),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    with(listKTraverse.asFold()) {

      "asFold should behave as valid Fold: size" {
        forAll(Gen.listK(Gen.int())) { ints ->
          size(ints) == ints.size
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(Gen.listK(Gen.int())) { ints ->
          nonEmpty(ints) == ints.isNotEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(Gen.listK(Gen.int())) { ints ->
          isEmpty(ints) == ints.isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(Gen.listK(Gen.int())) { ints ->
          getAll(ints) == ints.k()
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(Gen.listK(Gen.int())) { ints ->
          combineAll(Monoid.int(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(Gen.listK(Gen.int())) { ints ->
          fold(Monoid.int(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(Gen.listK(Gen.int())) { ints ->
          headOption(ints) == ints.firstOrNull().toOption()
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(Gen.listK(Gen.int())) { ints ->
          lastOption(ints) == ints.lastOrNull().toOption()
        }
      }
    }

    with(listKTraverse) {

      "Getting all targets of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          getAll(ints.k()) == ints.k()
        }
      }

      "Folding all the values of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          fold(Monoid.int(), ints.k()) == ints.sum()
        }
      }

      "Combining all the values of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          combineAll(Monoid.int(), ints.k()) == ints.sum()
        }
      }

      "Finding an number larger than 10" {
        forAll(Gen.list(Gen.choose(-100, 100))) { ints ->
          find(ints.k()) { it > 10 } == Option.fromNullable(ints.firstOrNull { it > 10 })
        }
      }

      "Get the length from a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          size(ints.k()) == ints.size
        }
      }
    }
  }
}
