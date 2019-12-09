package arrow.optics

import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.ListK
import arrow.core.k
import arrow.core.extensions.monoid
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.either
import arrow.test.generators.functionAToB
import arrow.test.generators.tuple2
import arrow.test.laws.OptionalLaws
import arrow.test.laws.PrismLaws
import arrow.test.laws.SetterLaws
import arrow.test.laws.TraversalLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class PrismTest : UnitSpec() {

  init {
    testLaws(
      PrismLaws.laws(
        prism = sumPrism,
        aGen = genSum,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      ),

      SetterLaws.laws(
        setter = sumPrism.asSetter(),
        aGen = genSum,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any()
      ),

      TraversalLaws.laws(
        traversal = sumPrism.asTraversal(),
        aGen = genSum,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any()),
        EQListB = ListK.eq(Eq.any())
      ),

      OptionalLaws.laws(
        optional = sumPrism.asOptional(),
        aGen = genSum,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
        EQA = Eq.any(),
        EQOptionB = Option.eq(Eq.any())
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = sumPrism.first(),
        aGen = Gen.tuple2(genSum, Gen.int()),
        bGen = Gen.tuple2(Gen.string(), Gen.int()),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.string(), Gen.int())),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = sumPrism.second(),
        aGen = Gen.tuple2(Gen.int(), genSum),
        bGen = Gen.tuple2(Gen.int(), Gen.string()),
        funcGen = Gen.functionAToB(Gen.tuple2(Gen.int(), Gen.string())),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = sumPrism.right(),
        aGen = Gen.either(Gen.int(), genSum),
        bGen = Gen.either(Gen.int(), Gen.string()),
        funcGen = Gen.functionAToB(Gen.either(Gen.int(), Gen.string())),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = sumPrism.left(),
        aGen = Gen.either(genSum, Gen.int()),
        bGen = Gen.either(Gen.string(), Gen.int()),
        funcGen = Gen.functionAToB(Gen.either(Gen.string(), Gen.int())),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Prism.id(),
        aGen = Gen.either(Gen.int(), Gen.int()),
        bGen = Gen.either(Gen.int(), Gen.int()),
        funcGen = Gen.functionAToB(Gen.either(Gen.int(), Gen.int())),
        EQA = Eq.any(),
        EQOptionB = Eq.any()
      )
    )

    with(sumPrism.asFold()) {

      "asFold should behave as valid Fold: size" {
        forAll(genSum) { sum: SumType ->
          size(sum) == sumPrism.getOption(sum).map { 1 }.getOrElse { 0 }
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(genSum) { sum: SumType ->
          nonEmpty(sum) == sumPrism.getOption(sum).nonEmpty()
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(genSum) { sum: SumType ->
          isEmpty(sum) == sumPrism.getOption(sum).isEmpty()
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(genSum) { sum: SumType ->
          getAll(sum) == sumPrism.getOption(sum).toList().k()
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(genSum) { sum: SumType ->
          combineAll(String.monoid(), sum) ==
              sumPrism.getOption(sum).fold({ String.monoid().empty() }, ::identity)
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(genSum) { sum: SumType ->
          fold(String.monoid(), sum) ==
              sumPrism.getOption(sum).fold({ String.monoid().empty() }, ::identity)
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(genSum) { sum: SumType ->
          headOption(sum) == sumPrism.getOption(sum)
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(genSum) { sum: SumType ->
          lastOption(sum) == sumPrism.getOption(sum)
        }
      }
    }

    "Joining two prisms together with same target should yield same result" {
      forAll(genSum) { a ->
        (sumPrism compose stringPrism).getOption(a) == sumPrism.getOption(a).flatMap(stringPrism::getOption) &&
            (sumPrism + stringPrism).getOption(a) == (sumPrism compose stringPrism).getOption(a)
      }
    }

    "Checking if a prism exists with a target" {
      forAll(genSum, genSum, Gen.bool()) { a, other, bool ->
        Prism.only(a, object : Eq<SumType> {
          override fun SumType.eqv(b: SumType): Boolean = bool
        }).isEmpty(other) == bool
      }
    }

    "Checking if there is no target" {
      forAll(genSum) { sum ->
        sumPrism.isEmpty(sum) == sum !is SumType.A
      }
    }

    "Checking if a target exists" {
      forAll(genSum) { sum ->
        sumPrism.nonEmpty(sum) == sum is SumType.A
      }
    }

    "Setting a target on a prism should set the correct target" {
      forAll(genSumTypeA, Gen.string()) { a, string ->
        sumPrism.setOption(a, string) == Some(a.copy(string = string))
      }
    }

    "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
      forAll(genSum, Gen.bool()) { sum, predicate ->
        sumPrism.find(sum) { predicate }.fold({ false }, { true }) == (predicate && sum is SumType.A)
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      forAll(genSum, Gen.bool()) { sum, predicate ->
        sumPrism.exist(sum) { predicate } == (predicate && sum is SumType.A)
      }
    }

    "Checking satisfaction of predicate over the target should result in opposite result as predicate" {
      forAll(genSum, Gen.bool()) { sum, predicate ->
        sumPrism.all(sum) { predicate } == (predicate || sum is SumType.B)
      }
    }
  }
}
