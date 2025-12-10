@file:OptIn(ExperimentalRaiseAccumulateApi::class)
package arrow.core.raise

import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
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

  @Test fun raiseAccumulateTakesPrecedenceOverExtensionFunctionNel() {
    accumulate(::either) {
      accumulating { ensure(false) { "false" } }
      accumulating { mapOrAccumulate(1..2) { ensure(false) { "$it: IsFalse" } } }
      1
    } shouldBe nonEmptyListOf("false", "1: IsFalse", "2: IsFalse").left()
  }

  @Test fun accumulateWithBindAndMap() {
    accumulate(::either) {
      (1 .. 2).map { "$it: IsFalse".left() }.map {
        it.bindOrAccumulate()
      }
    } shouldBe nonEmptyListOf("1: IsFalse", "2: IsFalse").left()
  }

  @Test fun raiseAccumulatingTwoFailures() {
    accumulate(::either) {
      val x = accumulating<Int> { raise("hello") }
      val y = accumulating<Int> { raise("bye") }
      x.value + y.value
    } shouldBe nonEmptyListOf("hello", "bye").left()
  }

  @Test fun raiseAccumulatingOneFailure() {
    accumulate(::either) {
      val x = accumulating { 1 }
      val y = accumulating<Int> { raise("bye") }
      x.value + y.value
    } shouldBe nonEmptyListOf("bye").left()
  }

  @Test fun raiseAccumulatingOneFailureEither() {
    accumulate(::either) {
      val x = 1.right().bindOrAccumulate()
      val y = "bye".left().bindOrAccumulate<Int>()
      x.value + y.value
    } shouldBe nonEmptyListOf("bye").left()
  }

  @Test fun raiseAccumulatingNoFailure() {
    accumulate<String, _, _>(::either) {
      val x = accumulating { 1 }
      val y = accumulating { 2 }
      x.value + y.value
    } shouldBe (1 + 2).right()
  }

  @Test fun raiseAccumulatingIntermediateRaise() {
    accumulate(::either) {
      val x = accumulating<Int> { raise("hello") }
      raise("hi")
      val y = accumulating { 2 }
      x.value + y.value
    } shouldBe nonEmptyListOf("hello", "hi").left()
  }

  @Test fun preservesAccumulatedErrorsInAccumulating() {
    var reachedEnd = false
    accumulate(::either) {
      val x = accumulating {
        accumulate("nonfatal")
        "output: failed"
      }
      x.value shouldBe "output: failed"
      reachedEnd = true
    } shouldBe nonEmptyListOf("nonfatal").left()
    reachedEnd shouldBe true
  }

  @Test fun toleratesValueInAccumulating() {
    var reachedEnd = false
    accumulate(::either) {
      val x = accumulating { raise("nonfatal") }
      accumulating { x.value }
      reachedEnd = true
    } shouldBe nonEmptyListOf("nonfatal").left()
    reachedEnd shouldBe true
  }
}
