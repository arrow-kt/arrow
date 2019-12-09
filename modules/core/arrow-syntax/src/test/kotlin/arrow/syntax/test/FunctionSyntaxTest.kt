package arrow.syntax.test

import arrow.syntax.function.andThen
import arrow.syntax.function.bind
import arrow.syntax.function.complement
import arrow.syntax.function.compose
import arrow.syntax.function.curried
import arrow.syntax.function.forwardCompose
import arrow.syntax.function.invoke
import arrow.syntax.function.memoize
import arrow.syntax.function.paired
import arrow.syntax.function.partially1
import arrow.syntax.function.partially2
import arrow.syntax.function.partially3
import arrow.syntax.function.partially4
import arrow.syntax.function.partially5
import arrow.syntax.function.reverse
import arrow.syntax.function.tripled
import arrow.syntax.function.tupled
import arrow.syntax.function.uncurried
import arrow.syntax.function.unpaired
import arrow.syntax.function.untripled
import arrow.syntax.function.untupled
import arrow.core.Tuple2
import arrow.test.UnitSpec
import io.kotlintest.shouldBe
import java.util.Random

class FunctionSyntaxTest : UnitSpec() {

  val f = { prefix: String, numericPostfix: Int, values: List<String> ->
    values.map { "$prefix$it$numericPostfix" }
  }

  private val sum = { i1: Int, i2: Int -> i1 + i2 }
  private val add5 = { i: Int -> i + 5 }
  private val multiplyBy2 = { i: Int -> i * 2 }

  init {

    "complement" {
      val isEven = { x: Int -> x % 2 == 0 }
      isEven(2) shouldBe true

      val notEven = isEven.complement()
      notEven(2) shouldBe false
    }

    "it should compose function correctly (andThen)" {
      val potato = "potato"
      val ninja = "ninja"
      val get = { potato }
      val map = { word: String -> ninja + word }
      (get andThen map)()
      (ninja + potato) shouldBe (get andThen map)()
    }

    "it should compose function correctly (forwardCompose)" {
      val randomDigit = Random().nextInt()
      val get = { randomDigit }
      val pow = { i: Int -> i * i }
      randomDigit * randomDigit shouldBe (get forwardCompose pow)()
    }

    "testAndThen" {
      val add5andMultiplyBy2 = add5 andThen multiplyBy2
      add5andMultiplyBy2(2) shouldBe 14
    }

    "testAndThen2" {
      val sumAndMultiplyBy2 = sum andThen multiplyBy2
      sumAndMultiplyBy2(5, 2) shouldBe 14
    }

    "testForwardCompose" {
      val add5andMultiplyBy2 = add5 forwardCompose multiplyBy2
      add5andMultiplyBy2(2) shouldBe 14
    }

    "testForwardCompose2" {
      val sumAndMultiplyBy2 = sum forwardCompose multiplyBy2
      sumAndMultiplyBy2(5, 2) shouldBe 14
    }

    "testCompose" {
      val multiplyBy2andAdd5 = add5 compose multiplyBy2
      multiplyBy2andAdd5(2) shouldBe 9
    }

    "testCurrying" {
      val sum2ints = { x: Int, y: Int -> x + y }
      val curried = sum2ints.curried()
      curried(2)(4) shouldBe 6
      val add5 = curried(5)
      add5(7) shouldBe 12
    }

    "testUncurrying" {
      val sum2ints: (Int, Int) -> Int = { x, y -> x + y }
      val curried: (Int) -> (Int) -> Int = sum2ints.curried()
      curried(2)(4) shouldBe 6
      // same type as sum2ints,
      curried.uncurried()(2, 4) shouldBe 6
      sum2ints(2, 4) shouldBe 6
    }

    "testTupling" {
      val sum2ints = { x: Int, y: Int -> x + y }
      val tupled = sum2ints.tupled()
      tupled(Tuple2(2, 4)) shouldBe 6
    }

    "testUntupling" {
      val sum2ints = { t: Tuple2<Int, Int> -> t.a + t.b }
      val untupled = sum2ints.untupled()
      untupled(2, 4) shouldBe 6
    }

    "memoize" {
      var counterA = 0
      var counterB = 0

      val a = { _: Int -> counterA++ }
      val b = { _: Int -> counterB++ }.memoize()

      repeat(5) { a(1) }
      repeat(5) { b(1) }

      counterA shouldBe 5
      counterB shouldBe 1 // calling several times a memoized function with the same parameter is computed just once
    }

    "memoizeEmpty" {
      var counterA = 0
      var counterB = 0

      val a = { counterA++ }
      val b = { counterB++ }.memoize()

      repeat(5) { a() }
      repeat(5) { b() }

      counterA shouldBe 5
      counterB shouldBe 1 // calling several times a memoized function with the same parameter is computed just once
    }

    "testPaired" {
      val sum2ints = { x: Int, y: Int -> x + y }

      val paired = sum2ints.paired()
      val unpaired = paired.unpaired()

      sum2ints(5, 9) shouldBe paired(5 to 9)
      paired(5 to 9) shouldBe unpaired(5, 9)
    }

    "testTripled" {
      val sum3ints = { x: Int, y: Int, z: Int -> x + y + z }

      val tripled = sum3ints.tripled()
      val untripled = tripled.untripled()

      sum3ints(1, 2, 3) shouldBe tripled(Triple(1, 2, 3))
      tripled(Triple(9, 8, 7)) shouldBe untripled(9, 8, 7)
    }

    "testReverse" {
      val j: (String, List<String>) -> List<String> = f(p2 = 1)
      j("x", listOf("a", "b", "c")) shouldBe j.reverse()(listOf("a", "b", "c"), "x")
    }

    "partially" {
      val sum5ints = { a: Int, b: Int, c: Int, d: Int, e: Int -> a + b + c + d + e }
      val sum4intsTo10 = sum5ints.partially5(10)
      val sum3intsTo15 = sum4intsTo10.partially4(5)
      val sum2intsTo17 = sum3intsTo15.partially3(2)
      sum2intsTo17(1, 2) shouldBe 20

      val prefixAndPostfix = { prefix: String, x: String, postfix: String -> "$prefix$x$postfix" }

      val helloX = prefixAndPostfix.partially1("Hello, ").partially2("!")
      helloX("Arrow") shouldBe "Hello, Arrow!"
    }

    "partials" {
      val sum5ints = { a: Int, b: Int, c: Int, d: Int, e: Int -> a + b + c + d + e }
      val sum4intsTo10: (Int, Int, Int, Int) -> Int = sum5ints(p5 = 10)
      val sum3intsTo15: (Int, Int, Int) -> Int = sum4intsTo10(p4 = 5)
      val sum2intsTo17: (Int, Int) -> Int = sum3intsTo15(p3 = 2)
      sum2intsTo17(1, 2) shouldBe 20
      val prefixAndPostfix = { prefix: String, x: String, postfix: String -> "$prefix$x$postfix" }
      val helloX: (String) -> String = prefixAndPostfix(p1 = "Hello, ")(p2 = "!")
      helloX("Arrow") shouldBe "Hello, Arrow!"
    }

    "bind" {
      var i = 0
      fun inc(a: Int) {
        i += a
      }

      val binded = ::inc.bind(5)
      i shouldBe 0
      binded()
      i shouldBe 5
    }
  }
}
