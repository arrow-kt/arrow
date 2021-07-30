package arrow.core.computations

import arrow.core.test.UnitSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class IterableEffectTest : UnitSpec() {

  init {
    "expect 2 items" {
      checkAll { list: List<Int> ->
        val result: String? = list.iterateOrNull {
          val a = next()
          val b = next()
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
          val a = next()
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
          dropNext()
          val b = next()
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
          dropNext(2)
          val b = next()
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
          val b = next() as? Long ?: cancel()
          "$a $b"
        }
        result shouldBe when (list.size) {
          0, 1 -> null
          else -> when {
            list[1] !is Long -> null
            else -> list.let { (a, b) -> "$a $b" }
          }
        }
      }
    }
  }
}
