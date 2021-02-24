package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInts
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.test.runBlockingTest
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
class FlowTest : ArrowFxSpec(
  spec = {

    "Retry - flow fails" {
      checkAll(Arb.int(), Arb.positiveInts(10)) { a, n ->
        var counter = 0
        val e = shouldThrow<RuntimeException> {
          flow {
            emit(a)
            if (++counter <= 11) throw RuntimeException("Bang!")
          }.retry(Schedule.recurs(n))
            .collect()
        }
        e.message shouldBe "Bang!"
      }
    }

    "Retry - flow succeeds" {
      checkAll(Arb.int(), Arb.int(5, 10)) { a, n ->
        var counter = 0
        val sum = flow {
          emit(a)
          if (++counter <= 5) throw RuntimeException("Bang!")
        }.retry(Schedule.recurs(n))
          .reduce { acc, int -> acc + int }

        sum shouldBe a * 6
      }
    }

    "Retry - schedule with delay" {
      runBlockingTest {
        checkAll(Arb.int(), Arb.int(100, 1000)) { a, delayMs ->
          val start = currentTime
          val timestamps = mutableListOf<Long>()
          shouldThrow<RuntimeException> {
            flow {
              emit(a)
              timestamps.add(currentTime)
              throw RuntimeException("Bang!")
            }
              .retry(Schedule.recurs<Throwable>(2) and Schedule.spaced(delayMs.milliseconds))
              .collect()
          }
          timestamps.size shouldBe 3

          // total run should be between start time + delay * 3 AND start + tolerance %
          val min = start + (delayMs * 2)
          val max = min + delayMs / 10

          timestamps.last() shouldBeGreaterThanOrEqual min
          timestamps.last() shouldBeLessThan max
        }
      }
    }
  }
)
