package arrow.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arrow.core.functionAToB
import io.kotest.property.checkAll

class AndThenTests : StringSpec({
  val count = 1000

  "AndThen0 - compose a chain of functions with andThen should be same with AndThen" {
    checkAll(Arb.int(), Arb.list(Arb.functionAToB<Int, Int>(Arb.int()))) { i, fs ->
      val result = fs.fold({ i }) { acc, f ->
        { f(acc()) }
      }.invoke()

      val expect = fs.fold({ i }) { acc, f ->
        acc.andThen(f)
      }.invoke()

      result shouldBe expect
    }
  }

  "AndThen0 - andThen is stack safe" {
    val result = (0 until count).fold({ 0 }) { acc, _ ->
      acc.andThen { it + 1 }
    }.invoke()

    result shouldBe count
  }

  "AndThen1 - compose a chain of functions with andThen should be same with AndThen" {
    checkAll(Arb.int(), Arb.list(Arb.functionAToB<Int, Int>(Arb.int()))) { i, fs ->
      val result = fs.fold({ x: Int -> x }) { acc, f ->
        { x: Int -> f(acc(x)) }
      }.invoke(i)

      val expect = fs.fold({ x: Int -> x }) { acc, f ->
        acc.andThen(f)
      }.invoke(i)

      result shouldBe expect
    }
  }

  "AndThen1 - compose a chain of function with compose should be same with AndThen" {
    checkAll(Arb.int(), Arb.list(Arb.functionAToB<Int, Int>(Arb.int()))) { i, fs ->
      val result = fs.fold({ x: Int -> x }) { acc, f ->
        { x: Int -> acc(f(x)) }
      }.invoke(i)

      val expect = fs.fold({ x: Int -> x }) { acc, b ->
        acc.compose(b)
      }.invoke(i)

      result shouldBe expect
    }
  }

  "AndThen1 - andThen is stack safe" {
    val result = (0 until count).fold({ x: Int -> x }) { acc, _ ->
      acc.andThen { it + 1 }
    }.invoke(0)

    result shouldBe count
  }

  "AndThen1 - compose is stack safe" {
    val result = (0 until count).fold({ x: Int -> x }) { acc, _ ->
      acc.compose { it + 1 }
    }.invoke(0)

    result shouldBe count
  }

  "AndThen2 - compose a chain of functions with andThen should be same with AndThen" {
    checkAll(Arb.int(), Arb.int(), Arb.list(Arb.functionAToB<Int, Int>(Arb.int()))) { i, j, fs ->
      val result = fs.fold({ x: Int, y: Int -> x + y }) { acc, f ->
        { x: Int, y: Int -> f(acc(x, y)) }
      }.invoke(i, j)

      val expect = fs.fold({ x: Int, y: Int -> x + y }) { acc, f ->
        acc.andThen(f)
      }.invoke(i, j)

      result shouldBe expect
    }
  }

  "AndThen2 - andThen is stack safe" {
    val result = (0 until count).fold({ x: Int, y: Int -> x + y }) { acc, _ ->
      acc.andThen { it + 1 }
    }.invoke(0, 0)

    result shouldBe count
  }
})
