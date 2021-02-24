package arrow.syntax.function

import arrow.fx.coroutines.parMapN
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class MemoizationTest : FreeSpec() {

  init {
    "Memoize races" {
      forAll<Int> {
        runBlocking {
          fun sum(): Int =
            Random.nextInt(Int.MAX_VALUE)

          val memoized = ::sum.memoize()

          val (first, second) = parMapN({ memoized() }, { memoized() }, ::Pair)

          first == second
        }
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

    "Memoize P6 only first execution runs" {
      var runs = 0
      fun sum(n1: Int, n2: Int, n3: Int, n4: Int, n5: Int, n6: Int): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(6) + 1

      memoized(1, 2, 3, 4, 5, 6) shouldBe result
      memoized(1, 2, 3, 4, 5, 6) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P7 only first execution runs" {
      var runs = 0
      fun sum(n1: Int, n2: Int, n3: Int, n4: Int, n5: Int, n6: Int, n7: Int): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(7) + 1

      memoized(1, 2, 3, 4, 5, 6, 7) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P8 only first execution runs" {
      var runs = 0
      fun sum(n1: Int, n2: Int, n3: Int, n4: Int, n5: Int, n6: Int, n7: Int, n8: Int): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(8) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P9 only first execution runs" {
      var runs = 0
      fun sum(n1: Int, n2: Int, n3: Int, n4: Int, n5: Int, n6: Int, n7: Int, n8: Int, n9: Int): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(9) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P10 only first execution runs" {
      var runs = 0
      fun sum(n1: Int, n2: Int, n3: Int, n4: Int, n5: Int, n6: Int, n7: Int, n8: Int, n9: Int, n10: Int): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(10) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P11 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(11) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P12 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int,
        n12: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(12) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P13 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int,
        n12: Int,
        n13: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(13) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P14 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int,
        n12: Int,
        n13: Int,
        n14: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + n14 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(14) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P15 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int,
        n12: Int,
        n13: Int,
        n14: Int,
        n15: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + n14 + n15 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(15) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P16 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int,
        n12: Int,
        n13: Int,
        n14: Int,
        n15: Int,
        n16: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + n14 + n15 + n16 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(16) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P17 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int,
        n12: Int,
        n13: Int,
        n14: Int,
        n15: Int,
        n16: Int,
        n17: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + n14 + n15 + n16 + n17 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(17) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P18 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int,
        n12: Int,
        n13: Int,
        n14: Int,
        n15: Int,
        n16: Int,
        n17: Int,
        n18: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + n14 + n15 + n16 + n17 + n18 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(18) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P19 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int,
        n12: Int,
        n13: Int,
        n14: Int,
        n15: Int,
        n16: Int,
        n17: Int,
        n18: Int,
        n19: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + n14 + n15 + n16 + n17 + n18 + n19 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(19) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P20 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int,
        n12: Int,
        n13: Int,
        n14: Int,
        n15: Int,
        n16: Int,
        n17: Int,
        n18: Int,
        n19: Int,
        n20: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + n14 + n15 + n16 + n17 + n18 + n19 + n20 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(20) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P21 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int,
        n12: Int,
        n13: Int,
        n14: Int,
        n15: Int,
        n16: Int,
        n17: Int,
        n18: Int,
        n19: Int,
        n20: Int,
        n21: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + n14 + n15 + n16 + n17 + n18 + n19 + n20 + n21 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(21) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 1, 2) shouldBe result
      runs shouldBe 3
    }

    "Memoize P22 only first execution runs" {
      var runs = 0
      fun sum(
        n1: Int,
        n2: Int,
        n3: Int,
        n4: Int,
        n5: Int,
        n6: Int,
        n7: Int,
        n8: Int,
        n9: Int,
        n10: Int,
        n11: Int,
        n12: Int,
        n13: Int,
        n14: Int,
        n15: Int,
        n16: Int,
        n17: Int,
        n18: Int,
        n19: Int,
        n20: Int,
        n21: Int,
        n22: Int
      ): Int {
        runs++
        return n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10 + n11 + n12 + n13 + n14 + n15 + n16 + n17 + n18 + n19 + n20 + n21 + n22 + 1
      }

      val memoized = ::sum.memoize()
      val result = consecSumResult(22) + 1

      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22) shouldBe result
      memoized(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22) shouldBe result
      runs shouldBe 1
      memoized(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 1) shouldBe result
      runs shouldBe 2
      memoized(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 1, 2) shouldBe result
      runs shouldBe 3
    }
  }
}

private fun consecSumResult(n: Int): Int = (n * (n + 1)) / 2
