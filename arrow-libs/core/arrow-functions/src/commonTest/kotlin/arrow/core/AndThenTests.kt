package arrow.core

import arrow.platform.Platform
import arrow.platform.platform
import io.kotest.property.Arb
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class AndThenTests {
  private val count = when (platform) {
    Platform.JVM -> 200_000
    else -> 1000
  }

  private fun <A, B> Arb.Companion.functionAToB(arb: Arb<B>): Arb<(A) -> B> = arbitrary { random ->
    { _: A -> arb.next(random) }.memoize()
  }

  @Test fun andThen0ComposeChainWithAndThen() = runTest {
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

  @Test fun andThen0AndThenStackSafe() = runTest {
    val result = (0 until count).fold({ 0 }) { acc, _ ->
      acc.andThen { it + 1 }
    }.invoke()

    result shouldBe count
  }

  @Test fun andThen1ComposeChainWithAndThen() = runTest {
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

  @Test fun andThen1ComposeChainWithCompose() = runTest {
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

  @Test fun andThen1AndThenStackSafe() = runTest {
    val result = (0 until count).fold({ x: Int -> x }) { acc, _ ->
      acc.andThen { it + 1 }
    }.invoke(0)

    result shouldBe count
  }

  @Test fun andThen1ComposeStackSafe() = runTest {
    val result = (0 until count).fold({ x: Int -> x }) { acc, _ ->
      acc.compose { it + 1 }
    }.invoke(0)

    result shouldBe count
  }

  @Test fun andThen2ComposeChainWithAndThen() = runTest {
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

  @Test fun andThen2AndThenStackSafe() = runTest {
    val result = (0 until count).fold({ x: Int, y: Int -> x + y }) { acc, _ ->
      acc.andThen { it + 1 }
    }.invoke(0, 0)

    result shouldBe count
  }
}
