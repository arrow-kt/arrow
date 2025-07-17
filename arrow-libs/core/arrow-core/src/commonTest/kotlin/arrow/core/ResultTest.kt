package arrow.core

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class ResultTest {

  @Test
  fun flatMap() = runTest {
    checkAll(20, Arb.int(0..3), Arb.int(0..1)) { a, b ->
      fun block(i: Int) = 100 / i
      fun transform(i: Int, j: Int) = (i / j).toString()

      val expected = runCatching {
        block(a)
      }.runCatching {
        transform(getOrThrow(), b)
      }

      val actual = runCatching {
        block(a)
      }.flatMap {
        runCatching {
          transform(it, b)
        }
      }

      with(actual) {
        when (isSuccess) {
          true -> {
            getOrThrow() shouldBe expected.getOrThrow()
          }
          false -> {
            exceptionOrNull() shouldBe expected.exceptionOrNull()
          }
        }
      }
    }
  }
}
