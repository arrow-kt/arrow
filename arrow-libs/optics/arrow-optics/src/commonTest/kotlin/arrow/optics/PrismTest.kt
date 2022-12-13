package arrow.optics

import arrow.core.test.UnitSpec
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.typeclasses.Monoid
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string

class PrismTest : UnitSpec() {

  init {
    testLaws(
      "Prism sum - ",
      PrismLaws.laws(
        prism = Prism.sumType(),
        aGen = Arb.sumType(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      ),

      TraversalLaws.laws(
        traversal = Prism.sumType(),
        aGen = Arb.sumType(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      ),

      OptionalLaws.laws(
        optional = Prism.sumType(),
        aGen = Arb.sumType(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

    testLaws(
      "Prism sum first - ",
      PrismLaws.laws(
        prism = Prism.sumType().first(),
        aGen = Arb.pair(Arb.sumType(), Arb.int()),
        bGen = Arb.pair(Arb.string(), Arb.int()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.string(), Arb.int())),
      )
    )

    testLaws(
      "Prism sum second - ",
      PrismLaws.laws(
        prism = Prism.sumType().second(),
        aGen = Arb.pair(Arb.int(), Arb.sumType()),
        bGen = Arb.pair(Arb.int(), Arb.string()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.int(), Arb.string())),
      )
    )

    testLaws(
      "Prism sum right - ",
      PrismLaws.laws(
        prism = Prism.sumType().right(),
        aGen = Arb.either(Arb.int(), Arb.sumType()),
        bGen = Arb.either(Arb.int(), Arb.string()),
        funcGen = Arb.functionAToB(Arb.either(Arb.int(), Arb.string())),
      )
    )

    testLaws(
      "Prism sum left - ",
      PrismLaws.laws(
        prism = Prism.sumType().left(),
        aGen = Arb.either(Arb.sumType(), Arb.int()),
        bGen = Arb.either(Arb.string(), Arb.int()),
        funcGen = Arb.functionAToB(Arb.either(Arb.string(), Arb.int())),
      )
    )

    testLaws(
      "Prism identity - ",
      PrismLaws.laws(
        prism = Prism.id(),
        aGen = Arb.either(Arb.int(), Arb.int()),
        bGen = Arb.either(Arb.int(), Arb.int()),
        funcGen = Arb.functionAToB(Arb.either(Arb.int(), Arb.int())),
      )
    )

    with(Prism.sumType()) {
      "asFold should behave as valid Fold: size" {
        checkAll(Arb.sumType()) { sum: SumType ->
          size(sum) shouldBe (Prism.sumType().getOrNull(sum)?.let { 1 } ?: 0)
        }
      }

      "asFold should behave as valid Fold: nonEmpty" {
        checkAll(Arb.sumType()) { sum: SumType ->
          isNotEmpty(sum) shouldBe (Prism.sumType().getOrNull(sum) != null)
        }
      }

      "asFold should behave as valid Fold: isEmpty" {
        checkAll(Arb.sumType()) { sum: SumType ->
          isEmpty(sum) shouldBe (Prism.sumType().getOrNull(sum) == null)
        }
      }

      "asFold should behave as valid Fold: getAll" {
        checkAll(Arb.sumType()) { sum: SumType ->
          getAll(sum) shouldBe listOfNotNull(Prism.sumType().getOrNull(sum))
        }
      }

      "asFold should behave as valid Fold: combineAll" {
        checkAll(Arb.sumType()) { sum: SumType ->
          combineAll(Monoid.string(), sum) shouldBe
            (Prism.sumType().getOrNull(sum) ?: Monoid.string().empty())
        }
      }

      "asFold should behave as valid Fold: fold" {
        checkAll(Arb.sumType()) { sum: SumType ->
          fold(Monoid.string(), sum) shouldBe
            (Prism.sumType().getOrNull(sum) ?: Monoid.string().empty())
        }
      }

      "asFold should behave as valid Fold: headOption" {
        checkAll(Arb.sumType()) { sum: SumType ->
          firstOrNull(sum) shouldBe Prism.sumType().getOrNull(sum)
        }
      }

      "asFold should behave as valid Fold: lastOption" {
        checkAll(Arb.sumType()) { sum: SumType ->
          lastOrNull(sum) shouldBe Prism.sumType().getOrNull(sum)
        }
      }
    }

    "Joining two prisms together with same target should yield same result" {
      checkAll(Arb.sumType()) { a ->
        (Prism.sumType() compose Prism.string()).getOrNull(a) shouldBe Prism.sumType().getOrNull(a)
          ?.let(Prism.string()::getOrNull)
        (Prism.sumType() + Prism.string()).getOrNull(a) shouldBe (Prism.sumType() compose Prism.string()).getOrNull(a)
      }
    }

    "Checking if a prism exists with a target" {
      checkAll(Arb.sumType(), Arb.sumType(), Arb.boolean()) { a, other, bool ->
        Prism.only(a) { _, _ -> bool }.isEmpty(other) shouldBe bool
      }
    }

    "Checking if there is no target" {
      checkAll(Arb.sumType()) { sum ->
        Prism.sumType().isEmpty(sum) shouldBe (sum !is SumType.A)
      }
    }

    "Checking if a target exists" {
      checkAll(Arb.sumType()) { sum ->
        Prism.sumType().isNotEmpty(sum) shouldBe (sum is SumType.A)
      }
    }

    "Setting a target on a prism should set the correct target" {
      checkAll(Arb.sumTypeA(), Arb.string()) { a, string ->
        (Prism.sumType().setNullable(a, string)!!) shouldBe a.copy(string = string)
      }
    }

    "Finding a target using a predicate within a Lens should be wrapped in the correct option result" {
      checkAll(Arb.sumType(), Arb.boolean()) { sum, predicate ->
        Prism.sumType().findOrNull(sum) { predicate }?.let { true } ?: false shouldBe (predicate && sum is SumType.A)
      }
    }

    "Checking existence predicate over the target should result in same result as predicate" {
      checkAll(Arb.sumType(), Arb.boolean()) { sum, predicate ->
        Prism.sumType().any(sum) { predicate } shouldBe (predicate && sum is SumType.A)
      }
    }

    "Checking satisfaction of predicate over the target should result in opposite result as predicate" {
      checkAll(Arb.sumType(), Arb.boolean()) { sum, predicate ->
        Prism.sumType().all(sum) { predicate } shouldBe (predicate || sum is SumType.B)
      }
    }
  }
}
