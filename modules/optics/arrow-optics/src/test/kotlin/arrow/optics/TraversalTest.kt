package arrow.optics

import arrow.core.Option
import arrow.core.toOption
import arrow.core.toT
import arrow.data.*
import arrow.instances.IntMonoidInstance
import arrow.instances.monoid
import arrow.instances.listk.eq.eq
import arrow.instances.listk.traverse.traverse
import arrow.instances.option.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.genFunctionAToB
import arrow.test.generators.genListK
import arrow.test.generators.genTuple
import arrow.test.laws.SetterLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class TraversalTest : UnitSpec() {

  init {

    val listKTraverse = Traversal.fromTraversable<ForListK, Int, Int>(ListK.traverse())

    testLaws(
      TraversalLaws.laws(
        traversal = listKTraverse,
        aGen = genListK(Gen.int()),
        bGen = Gen.int(),
        funcGen = genFunctionAToB(Gen.int()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      ),

      SetterLaws.laws(
        setter = listKTraverse.asSetter(),
        aGen = genListK(Gen.int()),
        bGen = Gen.int(),
        funcGen = genFunctionAToB(Gen.int()),
        EQA = ListK.eq(Eq.any())
      )
    )

    testLaws(TraversalLaws.laws(
      traversal = Traversal({ it.a }, { it.b }, { a, b, _ -> a toT b }),
      aGen = genTuple(Gen.float(), Gen.float()),
      bGen = Gen.float(),
      funcGen = genFunctionAToB(Gen.float()),
      EQA = Eq.any(),
      EQOptionB = Option.eq(Eq.any()),
      EQListB = ListK.eq(Eq.any())
    ))

    with(listKTraverse.asFold()) {

      "asFold should behave as valid Fold: size" {
        forAll(genListK(Gen.int())) { ints ->
          size(ints) == ints.size
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(genListK(Gen.int())) { ints ->
          nonEmpty(ints) == ints.isNotEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(genListK(Gen.int())) { ints ->
          isEmpty(ints) == ints.isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(genListK(Gen.int())) { ints ->
          getAll(ints) == ints.k()
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(genListK(Gen.int())) { ints ->
          combineAll(Int.monoid(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(genListK(Gen.int())) { ints ->
          fold(Int.monoid(), ints) == ints.sum()
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(genListK(Gen.int())) { ints ->
          headOption(ints) == ints.firstOrNull().toOption()
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(genListK(Gen.int())) { ints ->
          lastOption(ints) == ints.lastOrNull()?.toOption()
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
          fold(Int.monoid(), ints.k()) == ints.sum()
        }
      }

      "Combining all the values of a traversal" {
        forAll(Gen.list(Gen.int())) { ints ->
          combineAll(Int.monoid(), ints.k()) == ints.sum()
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
