package arrow.fx.resilience

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.checkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalTime
class FlowTest : StringSpec({

  "Retry - flow fails" {
    val bang = RuntimeException("Bang!")

    checkAll(Arb.int(), Arb.positiveInt(10)) { a, n ->
      var counter = 0
      val e = assertThrowable {
        flow {
          emit(a)
          if (++counter <= 11) throw bang
        }.retry(Schedule.recurs(n.toLong()))
          .collect()
      }
      e shouldBe bang
    }
  }

  "Retry - flow succeeds" {
    checkAll(Arb.int(), Arb.int(5, 10)) { a, n ->
      var counter = 0
      val sum = flow {
        emit(a)
        if (++counter <= 5) throw RuntimeException("Bang!")
      }.retry(Schedule.recurs(n.toLong()))
        .reduce { acc, int -> acc + int }

      sum shouldBe a * 6
    }
  }

  "Retry - schedule with delay" {
    runTest {
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

inline fun <A> assertThrowable(executable: () -> A): Throwable {
  val a = try {
    executable.invoke()
  } catch (e: Throwable) {
    e
  }

  return if (a is Throwable) a else fail("Expected an exception but found: $a")
}
