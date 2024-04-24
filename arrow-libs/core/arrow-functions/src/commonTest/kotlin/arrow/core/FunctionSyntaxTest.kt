package arrow.core

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class FunctionSyntaxTest {

  val sum = { i1: Int, i2: Int -> i1 + i2 }
  val add5 = { i: Int -> i + 5 }
  val multiplyBy2 = { i: Int -> i * 2 }

  @Test fun andThenComposesCorrectly() = runTest {
    val potato = "potato"
    val ninja = "ninja"
    val get = { potato }
    val map = { word: String -> ninja + word }
    (get andThen map)()
    (ninja + potato) shouldBe (get andThen map)()
  }

  @Test fun andThen1() = runTest {
    val add5andMultiplyBy2 = add5 andThen multiplyBy2
    add5andMultiplyBy2(2) shouldBe 14
  }

  @Test fun andThen2() = runTest {
    val sumAndMultiplyBy2 = sum andThen multiplyBy2
    sumAndMultiplyBy2(5, 2) shouldBe 14
  }

  @Test fun compose() = runTest {
    val multiplyBy2andAdd5 = add5 compose multiplyBy2
    multiplyBy2andAdd5(2) shouldBe 9
  }

  @Test fun currying() = runTest {
    val sum2ints = { x: Int, y: Int -> x + y }
    val curried = sum2ints.curried()
    curried(2)(4) shouldBe 6
    val addFive = curried(5)
    addFive(7) shouldBe 12
  }

  @Test fun uncurrying() = runTest {
    val sum2ints: (Int, Int) -> Int = { x, y -> x + y }
    val curried: (Int) -> (Int) -> Int = sum2ints.curried()
    curried(2)(4) shouldBe 6
    // same type as sum2ints,
    curried.uncurried()(2, 4) shouldBe 6
    sum2ints(2, 4) shouldBe 6
  }

  @Test fun curryingEffect() = runTest {
    val sum2ints: suspend (Int, Int) -> Int = { x: Int, y: Int -> x + y }
    val curried: (Int) -> suspend (Int) -> Int = sum2ints.curried()
    curried(2)(4) shouldBe 6
    val addFive: suspend (Int) -> Int = curried(5)
    addFive(7) shouldBe 12
  }

  @Test fun uncurryingEffect() = runTest {
    val sum2ints: suspend (Int, Int) -> Int = { x, y -> x + y }
    val curried: (Int) -> suspend (Int) -> Int = sum2ints.curried()
    curried(2)(4) shouldBe 6
    // same type as sum2ints,
    curried.uncurried()(2, 4) shouldBe 6
    sum2ints(2, 4) shouldBe 6
  }

  @Test fun memoize() = runTest {
    var counterA = 0
    var counterB = 0

    val a = { _: Int -> counterA++ }
    val b = { _: Int -> counterB++ }.memoize()

    repeat(5) { a(1) }
    repeat(5) { b(1) }

    counterA shouldBe 5
    counterB shouldBe 1 // calling several times a memoized function with the same parameter is computed just once
  }

  @Test fun memoizeEmpty() = runTest {
    var counterA = 0
    var counterB = 0

    val a = { counterA++ }
    val b = { counterB++ }.memoize()

    repeat(5) { a() }
    repeat(5) { b() }

    counterA shouldBe 5
    counterB shouldBe 1 // calling several times a memoized function with the same parameter is computed just once
  }

  @Test fun partially() = runTest {
    val sum5ints = { a: Int, b: Int, c: Int, d: Int, e: Int -> a + b + c + d + e }
    val sum4intsTo10 = sum5ints.partially5(10)
    val sum3intsTo15 = sum4intsTo10.partially4(5)
    val sum2intsTo17 = sum3intsTo15.partially3(2)
    sum2intsTo17(1, 2) shouldBe 20

    val prefixAndPostfix = { prefix: String, x: String, postfix: String -> "$prefix$x$postfix" }

    val helloX = prefixAndPostfix.partially1("Hello, ").partially2("!")
    helloX("Arrow") shouldBe "Hello, Arrow!"
  }

  @Test fun suspendPartially() = runTest {
    val sum5ints: suspend (Int, Int, Int, Int, Int) -> Int = { a: Int, b: Int, c: Int, d: Int, e: Int -> a + b + c + d + e }
    val sum4intsTo10 = sum5ints.partially5(10)
    val sum3intsTo15 = sum4intsTo10.partially4(5)
    val sum2intsTo17 = sum3intsTo15.partially3(2)
    sum2intsTo17(1, 2) shouldBe 20

    val prefixAndPostfix: suspend (String, String, String) -> String = { prefix: String, x: String, postfix: String -> "$prefix$x$postfix" }

    val helloX = prefixAndPostfix.partially1("Hello, ").partially2("!")
    helloX("Arrow") shouldBe "Hello, Arrow!"
  }

  @Test fun partials() = runTest {
    val sum5ints = { a: Int, b: Int, c: Int, d: Int, e: Int -> a + b + c + d + e }
    val sum4intsTo10: (Int, Int, Int, Int) -> Int = sum5ints.partially5(10)
    val sum3intsTo15: (Int, Int, Int) -> Int = sum4intsTo10.partially4(5)
    val sum2intsTo17: (Int, Int) -> Int = sum3intsTo15.partially3(2)
    sum2intsTo17(1, 2) shouldBe 20
    val prefixAndPostfix = { prefix: String, x: String, postfix: String -> "$prefix$x$postfix" }
    val helloX: (String) -> String = prefixAndPostfix.partially1("Hello, ").partially2("!")
    helloX("Arrow") shouldBe "Hello, Arrow!"
  }

  @Test fun suspendPartials() = runTest {
    val sum5ints: suspend (Int, Int, Int, Int, Int) -> Int = { a: Int, b: Int, c: Int, d: Int, e: Int -> a + b + c + d + e }
    val sum4intsTo10: suspend (Int, Int, Int, Int) -> Int = sum5ints.partially5(10)
    val sum3intsTo15: suspend (Int, Int, Int) -> Int = sum4intsTo10.partially4(5)
    val sum2intsTo17: suspend (Int, Int) -> Int = sum3intsTo15.partially3(2)
    sum2intsTo17(1, 2) shouldBe 20
    val prefixAndPostfix: suspend (String, String, String) -> String = { prefix: String, x: String, postfix: String -> "$prefix$x$postfix" }
    val helloX: suspend (String) -> String = prefixAndPostfix.partially1("Hello, ").partially2("!")
    helloX("Arrow") shouldBe "Hello, Arrow!"
  }

  @Test fun bind() = runTest {
    var i = 0
    fun inc(a: Int) {
      i += a
    }

    val binded = ::inc.partially1(5)
    i shouldBe 0
    binded()
    i shouldBe 5
  }
}
