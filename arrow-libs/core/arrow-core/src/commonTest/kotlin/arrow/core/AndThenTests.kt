package arrow.core

import arrow.core.test.functionAToB
import arrow.core.test.stackSafeIteration
import io.kotest.property.Arb
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class AndThenTests {
  val count = stackSafeIteration()

  @Test fun andThen0composeChain() = runTest {
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

  @Test fun andThen0stackSafe() {
    val result = (0 until count).fold({ 0 }) { acc, _ ->
      acc.andThen { it + 1 }
    }.invoke()

    result shouldBe count
  }

  @Test fun andThen1composeChain() = runTest {
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

  @Test fun composeComposeChain() = runTest {
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

  @Test fun andThen1stackSafe() {
    val result = (0 until count).fold({ x: Int -> x }) { acc, _ ->
      acc.andThen { it + 1 }
    }.invoke(0)

    result shouldBe count
  }

  @Test fun composeStackSafe() {
    val result = (0 until count).fold({ x: Int -> x }) { acc, _ ->
      acc.compose { it + 1 }
    }.invoke(0)

    result shouldBe count
  }

  @Test fun andThen2composeChain() = runTest {
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

  @Test fun andThen2stackSafe() = runTest {
    val result = (0 until count).fold({ x: Int, y: Int -> x + y }) { acc, _ ->
      acc.andThen { it + 1 }
    }.invoke(0, 0)

    result shouldBe count
  }
}
