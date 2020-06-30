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

  "sleep returns on the original context" {
    single.use { ctx ->
      checkAll(Arb.int()) {
        evalOn(ctx) {
          val n0 = threadName.invoke()
          sleep(10.milliseconds)
          val n = threadName.invoke()

          n0 shouldBe n
        }
      }
    }
  }

  "timeOutOrNull" - {
    "returns to original context without timing out" {
      singleThreadContext("1").zip(single).use { (one, single) ->
        checkAll(Arb.int()) {
          evalOn(single) {
            val n0 = threadName.invoke()

            timeOutOrNull(1.minutes) {
              one.shift()
              threadName.invoke() shouldBe "1"
              Unit.suspend()
            }

            n0 shouldBe threadName.invoke()
          }
        }
      }
    }

    "returns to original context when timing out" {
      singleThreadContext("1").zip(single).use { (one, single) ->
        checkAll(Arb.int()) {
          evalOn(single) {
            val n0 = threadName.invoke()

            timeOutOrNull(50.milliseconds) {
              one.shift()
              threadName.invoke() shouldBe "1"
              never<Unit>()
            } shouldBe null

            n0 shouldBe threadName.invoke()
          }
        }
      }
    }
  }

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
