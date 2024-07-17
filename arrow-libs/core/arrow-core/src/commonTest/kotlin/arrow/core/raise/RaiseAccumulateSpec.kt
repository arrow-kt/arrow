package arrow.core.raise

import arrow.core.Either
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.nonEmptyListOf
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class RaiseAccumulateSpec {
  @Test fun raiseAccumulateTakesPrecedenceOverExtensionFunction() = runTest {
    either<NonEmptyList<String>, Int> {
      zipOrAccumulate(
        { ensure(false) { "false" } },
        { mapOrAccumulate(1..2) { ensure(false) { "$it: IsFalse" } } }
      ) { _, _ -> 1 }
    } shouldBe nonEmptyListOf("false", "1: IsFalse", "2: IsFalse").left()
  }

  @Test fun mapOrAccumulateSemanticsDependOnReceiver() = runTest {
    val numbers = listOf(1, 2, 3, 4, 5, 6)
    val func: Raise<String>.(Int) -> Int = { if (it > 4) raise("$it") else it * 10 }

    val outerResult = either {
      ior(combineError = String::plus) { this.mapOrAccumulate(numbers, func) } shouldBe Ior.Both("56", listOf(10, 20, 30, 40))
      iorNel { this.mapOrAccumulate(numbers, func) } shouldBe Ior.Both(nonEmptyListOf("5", "6"), listOf(10, 20, 30, 40))
      either { this.mapOrAccumulate(numbers, func) } shouldBe Either.Left(nonEmptyListOf("5", "6"))
      iorNel { this.mapOrAccumulate(numbers, func) }.toEither().bind()
    }

    outerResult shouldBe Either.Right(listOf(10, 20, 30, 40))
  }

  @Test fun iorMapOrAccumulateResultsInBothIfAllErrors() = runTest {
    val numbers = listOf(1, 2, 3, 4, 5, 6)
    val func: Raise<String>.(Int) -> Int = { raise("$it") }

    ior(combineError = String::plus) { mapOrAccumulate(numbers, func) } shouldBe Ior.Both("123456", emptyList())
  }

  @Test fun iorMapOrAccumulateResultsInBothIfAnySuccesses() = runTest {
    val numbers = listOf(1, 2, 3, 4, 5, 6)
    val func: Raise<String>.(Int) -> Int = { if (it > 4) raise("$it") else it * 10 }

    ior(combineError = String::plus) { mapOrAccumulate(numbers, func) } shouldBe Ior.Both("56", listOf(10, 20, 30, 40))
  }

  @Test fun iorMapOrAccumulateResultsInRightIfAllSuccesses() = runTest {
    val numbers = listOf(1, 2, 3, 4, 5, 6)
    val func: Raise<String>.(Int) -> Int = { it * 10 }

    ior(combineError = String::plus) { mapOrAccumulate(numbers, func) } shouldBe Ior.Right(listOf(10, 20, 30, 40, 50, 60))
  }
}
