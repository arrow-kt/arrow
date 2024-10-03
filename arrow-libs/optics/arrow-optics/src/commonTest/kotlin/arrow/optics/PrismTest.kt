package arrow.optics

import arrow.optics.test.either
import arrow.optics.test.functionAToB
import arrow.optics.test.laws.OptionalLaws
import arrow.optics.test.laws.PrismLaws
import arrow.optics.test.laws.TraversalLaws
import arrow.optics.test.laws.testLaws
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PrismTest {

  @Test
  fun prismSumLaws() =
    testLaws(
      PrismLaws(
        prism = Prism.sumType(),
        aGen = Arb.sumType(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      ),

      TraversalLaws(
        traversal = Prism.sumType(),
        aGen = Arb.sumType(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      ),

      OptionalLaws(
        optional = Prism.sumType(),
        aGen = Arb.sumType(),
        bGen = Arb.string(),
        funcGen = Arb.functionAToB(Arb.string()),
      )
    )

  @Test
  fun prismSumFirstLaws() =
    testLaws(
      PrismLaws(
        prism = Prism.sumType().first(),
        aGen = Arb.pair(Arb.sumType(), Arb.int()),
        bGen = Arb.pair(Arb.string(), Arb.int()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.string(), Arb.int())),
      )
    )

  @Test
  fun prismSumSecondLaws() =
    testLaws(
      PrismLaws(
        prism = Prism.sumType().second(),
        aGen = Arb.pair(Arb.int(), Arb.sumType()),
        bGen = Arb.pair(Arb.int(), Arb.string()),
        funcGen = Arb.functionAToB(Arb.pair(Arb.int(), Arb.string())),
      )
    )

  @Test
  fun prismSumRightLaws() =
    testLaws(
      PrismLaws(
        prism = Prism.sumType().right(),
        aGen = Arb.either(Arb.int(), Arb.sumType()),
        bGen = Arb.either(Arb.int(), Arb.string()),
        funcGen = Arb.functionAToB(Arb.either(Arb.int(), Arb.string())),
      )
    )

  @Test
  fun prismSumLeftLaws() =
    testLaws(
      PrismLaws(
        prism = Prism.sumType().left(),
        aGen = Arb.either(Arb.sumType(), Arb.int()),
        bGen = Arb.either(Arb.string(), Arb.int()),
        funcGen = Arb.functionAToB(Arb.either(Arb.string(), Arb.int())),
      )
    )

  @Test
  fun prismIdentityLaws() =
    testLaws(
      PrismLaws(
        prism = Prism.id(),
        aGen = Arb.either(Arb.int(), Arb.int()),
        bGen = Arb.either(Arb.int(), Arb.int()),
        funcGen = Arb.functionAToB(Arb.either(Arb.int(), Arb.int())),
      )
    )

  @Test
  fun sizeOk() = runTest {
    checkAll(Arb.sumType()) { sum: SumType ->
      Prism.sumType().size(sum) shouldBe (Prism.sumType().getOrNull(sum)?.let { 1 } ?: 0)
    }
  }

  @Test
  fun nonEmptyOk() = runTest {
    checkAll(Arb.sumType()) { sum: SumType ->
      Prism.sumType().isNotEmpty(sum) shouldBe (Prism.sumType().getOrNull(sum) != null)
    }
  }

  @Test
  fun isEmptyOk() = runTest {
    checkAll(Arb.sumType()) { sum: SumType ->
      Prism.sumType().isEmpty(sum) shouldBe (Prism.sumType().getOrNull(sum) == null)
    }
  }

  @Test
  fun getAllOk() = runTest {
    checkAll(Arb.sumType()) { sum: SumType ->
      Prism.sumType().getAll(sum) shouldBe listOfNotNull(Prism.sumType().getOrNull(sum))
    }
  }

  @Test
  fun foldOk() = runTest {
    checkAll(Arb.sumType()) { sum: SumType ->
      Prism.sumType().fold("", { x, y -> x + y }, sum) shouldBe
        (Prism.sumType().getOrNull(sum) ?: "")
    }
  }

  @Test
  fun firstOrNullOk() = runTest {
    checkAll(Arb.sumType()) { sum: SumType ->
      Prism.sumType().firstOrNull(sum) shouldBe Prism.sumType().getOrNull(sum)
    }
  }

  @Test
  fun lastOrNullOk() = runTest {
    checkAll(Arb.sumType()) { sum: SumType ->
      Prism.sumType().lastOrNull(sum) shouldBe Prism.sumType().getOrNull(sum)
    }
  }

  @Test
  fun joinOk() = runTest {
    checkAll(Arb.sumType()) { a ->
      (Prism.sumType() compose Prism.string()).getOrNull(a) shouldBe Prism.sumType().getOrNull(a)
        ?.let(Prism.string()::getOrNull)
      (Prism.sumType() + Prism.string()).getOrNull(a) shouldBe (Prism.sumType() compose Prism.string()).getOrNull(a)
    }
  }

  @Test
  fun checkExists() = runTest {
    checkAll(Arb.sumType(), Arb.sumType(), Arb.boolean()) { a, other, bool ->
      Prism.only(a) { _, _ -> bool }.isEmpty(other) shouldBe bool
    }
  }

  @Test
  fun checkNoTarget() = runTest {
    checkAll(Arb.sumType()) { sum ->
      Prism.sumType().isEmpty(sum) shouldBe (sum !is SumType.A)
    }
  }

  @Test
  fun checkTargetExists() = runTest {
    checkAll(Arb.sumType()) { sum ->
      Prism.sumType().isNotEmpty(sum) shouldBe (sum is SumType.A)
    }
  }

  @Test
  fun setOk() = runTest {
    checkAll(Arb.sumTypeA(), Arb.string()) { a, string ->
      (Prism.sumType().setNullable(a, string)!!) shouldBe a.copy(string = string)
    }
  }

  @Test
  fun findOrNullOk() = runTest {
    checkAll(Arb.sumType(), Arb.boolean()) { sum, predicate ->
      (Prism.sumType().findOrNull(sum) { predicate }?.let { true } ?: false) shouldBe (predicate && sum is SumType.A)
    }
  }

  @Test
  fun anyOk() = runTest {
    checkAll(Arb.sumType(), Arb.boolean()) { sum, predicate ->
      Prism.sumType().any(sum) { predicate } shouldBe (predicate && sum is SumType.A)
    }
  }

  @Test
  fun allOk() = runTest {
    checkAll(Arb.sumType(), Arb.boolean()) { sum, predicate ->
      Prism.sumType().all(sum) { predicate } shouldBe (predicate || sum is SumType.B)
    }
  }
}
