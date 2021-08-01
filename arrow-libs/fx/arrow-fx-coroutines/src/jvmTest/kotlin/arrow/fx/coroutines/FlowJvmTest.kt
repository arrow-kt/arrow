package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.test.runBlockingTest
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

@ExperimentalTime
class FlowJvmTest : ArrowFxSpec(spec = {
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

  "parMap - single thread - identity" {
    single.use { ctx ->
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap { it }
          .toList() shouldBe flow.toList()
      }
    }
  }

  "parMapUnordered - single thread - identity" {
    single.use { ctx ->
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMapUnordered { it }.flowOn(ctx)
          .toSet() shouldBe flow.toSet()
      }
    }
  }
})
