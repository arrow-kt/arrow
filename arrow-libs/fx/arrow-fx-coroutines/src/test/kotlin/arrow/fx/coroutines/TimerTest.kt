package arrow.fx.coroutines

import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.checkAll

class TimerTest : ArrowFxSpec(spec = {

  suspend fun timeNano(): Long =
    System.nanoTime()

  suspend fun timeMilis(): Long =
    System.currentTimeMillis()

  "sleep should last specified time" {
    checkAll(Arb.positiveInts(max = 50)) { i ->
      val length = i.toLong()
      val start = timeNano()
      sleep(length.milliseconds)
      val end = timeNano()
      require((end - start) >= length) { "Expected (end - start) >= length but found ($end - $start) <= $length" }
    }
  }

  "negative sleep should be immediate" {
    checkAll(Arb.int(Int.MIN_VALUE, -1)) { i ->
      val start = timeNano()
      sleep(i.nanoseconds)
      val end = timeNano()
      require((start - end) <= 0L) { "Expected (end - start) <= 0L but found (${start - end}) <= 0L" }
    }
  }

  "sleep can be cancelled" {
    checkAll(Arb.int(100, 500)) { d ->
      assertCancellable { sleep(d.milliseconds) }
    }
  }

  "timeout can lose" {
    checkAll(Arb.int()) { i ->
      timeOutOrNull(1.milliseconds) {
        sleep(100.milliseconds)
        i
      } shouldBe null
    }
  }

  "timeout wins non suspend" {
    checkAll(Arb.int()) { i ->
      timeOutOrNull(10.milliseconds) {
        i
      } shouldBe i
    }
  }

  "timeout wins suspend" {
    checkAll(Arb.int()) { i ->
      timeOutOrNull(100.milliseconds) {
        sleep(1.milliseconds)
        i
      } shouldBe i
    }
  }
})
