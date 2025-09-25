package arrow.core.raise.result4k

import arrow.core.raise.bind
import arrow.core.raise.ensure
import arrow.core.raise.result4k
import dev.forkhandles.result4k.asFailure
import dev.forkhandles.result4k.asSuccess
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class Result4KTest {
  @Test
  fun bindAndMap() = runTest {
    checkAll(Arb.int()) { n ->
      val one = n.asSuccess()
      val two = result4k { one.bind() + 1 }
      val three = one.map { it + 1 }
      two shouldBeEqual three
    }
  }

  @Test
  fun validate() = runTest {
    checkAll(Arb.int()) { n ->
      val one = n.asSuccess()
      val two = result4k {
        val x = one.bind()
        ensure(x > 0) { "ooh" }
        x
      }
      val three = one.flatMap {
        if (n > 0) it.asSuccess() else "ooh".asFailure()
      }
      two shouldBeEqual three
    }
  }
}
