package arrow.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.random.Random

class MemoizationTest : StringSpec({
    "Memoize races" {
      checkAll<Int> {
        fun sum(): Int =
          Random.nextInt(Int.MAX_VALUE)

        val memoized = ::sum.memoize()

        val (first, second) = listOf(
          async { memoized() },
          async { memoized() }
        ).awaitAll()

        first shouldBe second
      }
    }

    "Memoize P0 only first execution runs" {
      var runs = 0
      fun sum(): Int {
        runs++
        return 1
      }

      val memoized = ::sum.memoize()

      memoized() shouldBe 1
      memoized() shouldBe 1
      runs shouldBe 1
    }

    "Memoize P1 only first execution runs" {
      var runs = 0
      fun sum(n: Int): Int {
        runs++
        return n + 1
      }

      val memoized = ::sum.memoize()

      memoized(1) shouldBe 2
      memoized(1) shouldBe 2
      runs shouldBe 1
      memoized(2) shouldBe 3
      runs shouldBe 2
      memoized(3) shouldBe 4
      runs shouldBe 3
    }

    "Memoize P2 only first execution runs" {
      var runs = 0
      fun sum(n1: Int, n2: Int): Int {
        runs++
        return n1 + n2 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(2) + 1

      memoized(1, 2) shouldBe result
      memoized(1, 2) shouldBe result
      runs shouldBe 1
      memoized(2, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 2) shouldBe 6
      runs shouldBe 3
    }

    "Memoize P3 only first execution runs" {
      var runs = 0
      fun sum(n1: Int, n2: Int, n3: Int): Int {
        runs++
        return n1 + n2 + n3 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(3) + 1

      memoized(1, 2, 3) shouldBe result
      memoized(1, 2, 3) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P4 only first execution runs" {
      var runs = 0
      fun sum(n1: Int, n2: Int, n3: Int, n4: Int): Int {
        runs++
        return n1 + n2 + n3 + n4 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(4) + 1

      memoized(1, 2, 3, 4) shouldBe result
      memoized(1, 2, 3, 4) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P5 only first execution runs" {
      var runs = 0
      fun sum(n1: Int, n2: Int, n3: Int, n4: Int, n5: Int): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(5) + 1

      memoized(1, 2, 3, 4, 5) shouldBe result
      memoized(1, 2, 3, 4, 5) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 1, 2) shouldBe result
      runs shouldBe 3
    }
})

private fun consecSumResult(n: Int): Int = (n * (n + 1)) / 2
