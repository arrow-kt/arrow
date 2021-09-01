package arrow.core.computations

import arrow.core.test.UnitSpec
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class IterableEffectTest : UnitSpec() {

  init {
    "expect 2 items" {
      checkAll { list: List<Int> ->
        val result: String? = list.iterateOrNull {
          val a = next()
          list shouldHaveAtLeastSize 1
          val b = next()
          list shouldHaveAtLeastSize 2
          "$a $b"
        }
        result shouldBe when (list.size) {
          0, 1 -> null
          else -> list.let { (a, b) -> "$a $b" }
        }
      }
    }
    "expect 1 item" {
      checkAll { list: List<Int> ->
        val result: String? = list.iterateOrNull {
          val a = next()
          list shouldHaveAtLeastSize 1
          "$a"
        }
        result shouldBe when (list.size) {
          0 -> null
          else -> "${list.first()}"
        }
      }
    }
    "drop first" {
      checkAll { list: List<Int> ->
        val result: String? = list.iterateOrNull {
          dropNext()
          list shouldHaveAtLeastSize 1
          val a = next()
          list shouldHaveAtLeastSize 2
          "$a"
        }
        result shouldBe when (list.size) {
          0, 1 -> null
          else -> "${list.drop(1).first()}"
        }
      }
    }

    "drop middle" {
      checkAll { list: List<Int> ->
        val result: String? = list.iterateOrNull {
          val a = next()
          list shouldHaveAtLeastSize 1
          dropNext()
          list shouldHaveAtLeastSize 2
          val b = next()
          list shouldHaveAtLeastSize 3
          "$a $b"
        }
        result shouldBe when (list.size) {
          0, 1, 2 -> null
          else -> list.let { (a, _, c) -> "$a $c" }
        }
      }
    }

    "drop multiple" {
      checkAll { list: List<Int> ->
        val result: String? = list.iterateOrNull {
          val a = next()
          list shouldHaveAtLeastSize 1
          dropNext(2)
          list shouldHaveAtLeastSize 3
          val b = next()
          list shouldHaveAtLeastSize 4
          "$a $b"
        }
        result shouldBe when (list.size) {
          0, 1, 2, 3 -> null
          else -> list.let { (a, _, _, d) -> "$a $d" }
        }
      }
    }
    "drop only" {
      checkAll { list: List<Int> ->
        val result: String? = list.iterateOrNull {
          dropNext()
          list shouldHaveAtLeastSize 1
          "not empty"
        }
        result shouldBe when (list.size) {
          0 -> null
          else -> "not empty"
        }
      }
    }
    "cancel iteration" {
      checkAll { ints: List<Int>, longs: List<Long> ->
        val list: List<Number> = (ints + longs).shuffled()
        val result: String? = list.iterateOrNull {
          val a = next()
          list shouldHaveAtLeastSize 1
          val b = next() as? Long ?: cancel()
          list shouldHaveAtLeastSize 2
          "$a $b"
        }
        result shouldBe when {
          list.size in 0..1 -> null
          list[1] !is Long -> null
          else -> list.let { (a, b) -> "$a $b" }
        }
      }
    }
  }
}
