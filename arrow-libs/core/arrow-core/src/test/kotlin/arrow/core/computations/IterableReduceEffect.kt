package arrow.core.computations

import arrow.core.test.UnitSpec
import io.kotlintest.shouldBe

class IterableReduceEffect: UnitSpec() {

  init {
    "empty - expect 2" {
      val result: String? = emptyList<Int>().partialReduceOrNull {
        val a = next()
        val b = next()
        "$a $b"
      }
      result shouldBe null
    }
    "single - expect 1" {
      val result: String? = listOf(1).partialReduceOrNull {
        val a = next()
        "$a"
      }
      result shouldBe "1"
    }
    "single - expect 2" {
      val result: String? = listOf(1).partialReduceOrNull {
        val a = next()
        val b = next()
        "$a $b"
      }
      result shouldBe null
    }
    "double - expect 2" {
      val result: String? = listOf(1, 2).partialReduceOrNull {
        val a = next()
        val b = next()
        "$a $b"
      }
      result shouldBe "1 2"
    }
    "drop middle values" {
      val result: String? = listOf(1, 2, 3, 4).partialReduceOrNull {
        val a = next()
        drop(2)
        val b = next()
        "$a $b"
      }
      result shouldBe "1 4"
    }
    "drop second value" {
      val result: String? = listOf(1, 2, 3).partialReduceOrNull {
        val a = next()
        dropNext()
        val b = next()
        "$a $b"
      }
      result shouldBe "1 3"
    }
    "drop from empty" {
      val result: String? = emptyList<Int>().partialReduceOrNull {
        drop(1)
        "won't see me"
      }
      result shouldBe null
    }
    "cancel iteration" {
      val result: String? = listOf<Number>(1, 2.0).partialReduceOrNull {
        val a = next()
        val b = next() as? Long ?: cancel()
        "$a $b"
      }
      result shouldBe null
    }
  }

}
