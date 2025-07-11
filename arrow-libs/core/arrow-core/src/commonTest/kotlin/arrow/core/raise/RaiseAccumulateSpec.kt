@file:OptIn(ExperimentalRaiseAccumulateApi::class)
package arrow.core.raise

import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class RaiseAccumulateSpec {
  @Test fun raiseAccumulateTakesPrecedenceOverExtensionFunction() = runTest {
    either {
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
      val x: Int by accumulating {
        raise("hello")
      }
      val y: Int by accumulating {
        raise("bye")
      }
      x + y
    } shouldBe nonEmptyListOf("hello", "bye").left()
  }

  @Test fun raiseAccumulatingOneFailure() {
    accumulate(::either) {
      val x by accumulating { 1 }
      val y: Int by accumulating { raise("bye") }
      x + y
    } shouldBe nonEmptyListOf("bye").left()
  }

  @Test fun raiseAccumulatingOneFailureEither() {
    accumulate(::either) {
      val x: Int by 1.right().bindOrAccumulate()
      val y: Int by "bye".left().bindOrAccumulate()
      x + y
    } shouldBe nonEmptyListOf("bye").left()
  }

  @Test fun raiseAccumulatingNoFailure() {
    accumulate<String, _, _>(::either) {
      val x by accumulating { 1 }
      val y by accumulating { 2 }
      x + y
    } shouldBe (1 + 2).right()
  }

  @Test fun raiseAccumulatingIntermediateRaise() {
    accumulate(::either) {
      accumulating { raise("hello") }
      raise("hi")
    } shouldBe nonEmptyListOf("hello", "hi").left()
  }
}
