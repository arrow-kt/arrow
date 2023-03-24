package arrow.fx.resilience

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalTime
class FlowTest {

  @Test
  fun retryFlowFails(): TestResult = runTest {
    val bang = RuntimeException("Bang!")
    val value = Random.nextInt()
    val iterations = Random.nextLong(1..10L)


    var counter = 0
    val e = assertThrowable {
      flow {
        emit(value)
        if (++counter <= 11) throw bang
      }.retry(Schedule.recurs(iterations))
        .collect()
    }
    assertEquals(bang, e)
  }

  @Test
  fun retryFlowSucceeds(): TestResult = runTest {
    val value = Random.nextInt()
    val iterations = Random.nextLong(5..10L)

    var counter = 0
    val sum = flow {
      emit(value)
      if (++counter <= 5) throw RuntimeException("Bang!")
    }.retry(Schedule.recurs(iterations))
      .reduce { acc, int -> acc + int }

    assertEquals(value * 6, sum)
  }

  @Test
  fun retryScheduleWithDelay(): TestResult = runTest {
    val value = Random.nextInt()
    val delayMs = Random.nextLong(100..1000L)

    val start = currentTime
    val timestamps = mutableListOf<Long>()
    assertFailsWith<RuntimeException> {
      flow {
        emit(value)
        timestamps.add(currentTime)
        throw RuntimeException("Bang!")
      }
        .retry(Schedule.recurs<Throwable>(2) and Schedule.spaced(delayMs.milliseconds))
        .collect()
    }
    assertEquals(3, timestamps.size)

    // total run should be between start time + delay * 3 AND start + tolerance %
    val min = start + (delayMs * 2)
    val max = min + delayMs / 10

    assertTrue { timestamps.last() >= min }
    assertTrue { timestamps.last() < max }
  }
}

inline fun <A> assertThrowable(executable: () -> A): Throwable {
  val a = try {
    executable.invoke()
  } catch (e: Throwable) {
    e
  }

  return if (a is Throwable) a else fail("Expected an exception but found: $a")
}
