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
    eitherNel {
      val x by accumulating { ensure(false) { "false" } }
      val y by accumulating { mapOrAccumulate(1..2) { ensure(false) { "$it: IsFalse" } } }
      1
    } shouldBe nonEmptyListOf("false", "1: IsFalse", "2: IsFalse").left()
  }

  @Test fun raiseAccumulatingTwoFailures() {
    eitherNel {
      val x by accumulating {
        raise("hello")
        1
      }
      val y by accumulating { raise("bye") ; 2 }
      x + y
    } shouldBe nonEmptyListOf("hello", "bye").left()
  }

  @Test fun raiseAccumulatingOneFailure() {
    eitherNel {
      val x by accumulating { 1 }
      val y by accumulating { raise("bye") ; 2 }
      x + y
    } shouldBe nonEmptyListOf("bye").left()
  }

  @Test fun raiseAccumulatingNoFailure() {
    eitherNel<String, _> {
      val x by accumulating { 1 }
      val y by accumulating { 2 }
      x + y
    } shouldBe (1 + 2).right()
  }

  @Test fun raiseAccumulatingIntermediateRaise() {
    eitherNel {
      val x by accumulating { raise("hello") ; 1 }
      raise("hi")
      val y by accumulating { 2 }
      x + y
    } shouldBe nonEmptyListOf("hello", "hi").left()
  }
}
