package arrow.optics

import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.SetterLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Monoid
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
      ),

      SetterLaws.laws(
        setter = sumPrism,
        aGen = genSum,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      ),

      TraversalLaws.laws(
        traversal = sumPrism,
        aGen = genSum,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      ),

      OptionalLaws.laws(
        optional = sumPrism,
        aGen = genSum,
        bGen = Gen.string(),
        funcGen = Gen.functionAToB(Gen.string()),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = sumPrism.first(),
        aGen = Gen.pair(genSum, Gen.int()),
        bGen = Gen.pair(Gen.string(), Gen.int()),
        funcGen = Gen.functionAToB(Gen.pair(Gen.string(), Gen.int())),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = sumPrism.second(),
        aGen = Gen.pair(Gen.int(), genSum),
        bGen = Gen.pair(Gen.int(), Gen.string()),
        funcGen = Gen.functionAToB(Gen.pair(Gen.int(), Gen.string())),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = sumPrism.right(),
        aGen = Gen.either(Gen.int(), genSum),
        bGen = Gen.either(Gen.int(), Gen.string()),
        funcGen = Gen.functionAToB(Gen.either(Gen.int(), Gen.string())),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = sumPrism.left(),
        aGen = Gen.either(genSum, Gen.int()),
        bGen = Gen.either(Gen.string(), Gen.int()),
        funcGen = Gen.functionAToB(Gen.either(Gen.string(), Gen.int())),
      )
    )

    testLaws(
      PrismLaws.laws(
        prism = Prism.id(),
        aGen = Gen.either(Gen.int(), Gen.int()),
        bGen = Gen.either(Gen.int(), Gen.int()),
        funcGen = Gen.functionAToB(Gen.either(Gen.int(), Gen.int())),
      )
    )

    with(sumPrism) {

      "asFold should behave as valid Fold: size" {
        forAll(genSum) { sum: SumType ->
          size(sum) == sumPrism.getOrNull(sum)?.let { 1 } ?: 0
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        forAll(genSum) { sum: SumType ->
          isNotEmpty(sum) == (sumPrism.getOrNull(sum) != null)
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        forAll(genSum) { sum: SumType ->
          isEmpty(sum) == (sumPrism.getOrNull(sum) == null)
        }
      }

      "asFold should behave as valid Fold: getAll" {
        forAll(genSum) { sum: SumType ->
          getAll(sum) == listOfNotNull(sumPrism.getOrNull(sum))
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        forAll(genSum) { sum: SumType ->
          combineAll(Monoid.string(), sum) ==
              sumPrism.getOrNull(sum) ?: Monoid.string().empty()
        }
      }

      "asFold should behave as valid Fold: fold" {
        forAll(genSum) { sum: SumType ->
          fold(Monoid.string(), sum) ==
              sumPrism.getOrNull(sum) ?: Monoid.string().empty()
        }
      }

      "asFold should behave as valid Fold: headOption" {
        forAll(genSum) { sum: SumType ->
          firstOrNull(sum) == sumPrism.getOrNull(sum)
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        forAll(genSum) { sum: SumType ->
          lastOrNull(sum) == sumPrism.getOrNull(sum)
        }
      }
    }

    "Joining two prisms together with same target should yield same result" {
      forAll(genSum) { a ->
        (sumPrism compose stringPrism).getOrNull(a) == sumPrism.getOrNull(a)?.let(stringPrism::getOrNull) &&
            (sumPrism + stringPrism).getOrNull(a) == (sumPrism compose stringPrism).getOrNull(a)
      }
    }

    "Checking if a prism exists with a target" {
      forAll(genSum, genSum, Gen.bool()) { a, other, bool ->
        Prism.only(a) { _, _ -> bool }.isEmpty(other) == bool
      }
    }

    "Checking if there is no target" {
      forAll(genSum) { sum ->
        sumPrism.isEmpty(sum) == sum !is SumType.A
      }
    }

    "Checking if a target exists" {
      forAll(genSum) { sum ->
        sumPrism.isNotEmpty(sum) == sum is SumType.A
      }
    }

    "Setting a target on a prism should set the correct target" {
      forAll(genSumTypeA, Gen.string()) { a, string ->
        (sumPrism.setNullable(a, string)!!) == a.copy(string = string)
      }
    }

    "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
      forAll(genSum, Gen.bool()) { sum, predicate ->
        sumPrism.findOrNull(sum) { predicate }?.let { true } ?: false == (predicate && sum is SumType.A)
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      forAll(genSum, Gen.bool()) { sum, predicate ->
        sumPrism.any(sum) { predicate } == (predicate && sum is SumType.A)
      }
    }

    "Checking satisfaction of predicate over the target should result in opposite result as predicate" {
      forAll(genSum, Gen.bool()) { sum, predicate ->
        sumPrism.all(sum) { predicate } == (predicate || sum is SumType.B)
      }
    }
  }
}
