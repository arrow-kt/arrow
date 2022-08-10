package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.positiveInts
import kotlin.time.Duration
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runBlockingTest
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withTimeoutOrNull

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
    resourceScope {
      val ctx = singleThreadContext("single")
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap { it }.flowOn(ctx)
          .toList() shouldBe flow.toList()
      }
    }
  }
  
  "parMap - flowOn" {
    resourceScope {
      val ctx = singleThreadContext("single")
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap { Thread.currentThread().name }.flowOn(ctx)
          .toList().forEach {
            it shouldContain "single"
          }
      }
    }
  }
  
  "parMapUnordered - single thread - identity" {
    resourceScope {
      val ctx = singleThreadContext("single")
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMapUnordered { it }.flowOn(ctx)
          .toSet() shouldBe flow.toSet()
      }
    }
  }
  
  "parMapUnordered - flowOn" {
    resourceScope {
      val ctx = singleThreadContext("single")
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap { Thread.currentThread().name }.flowOn(ctx)
          .toList().forEach {
            it shouldContain "single"
          }
      }
    }
  }
  
  "fixedDelay" {
    runBlockingTest {
      checkAll(Arb.positiveInts().map(Int::toLong), Arb.int(1..100)) { waitPeriod, n ->
        val emissionDuration = waitPeriod / 10L
        var state: Long? = null
        
        val rate = flow { emit(delay(Duration.milliseconds(waitPeriod))) }.repeat()
          .map {
            val now = state ?: currentTime
            val nextNow = currentTime
            val lapsed = nextNow - now
            state = nextNow
            delay(emissionDuration)
            lapsed
          }
          .take(n)
          .toList()
        
        rate.first() shouldBe 0 // First element is immediately
        rate.drop(1).forEach { act ->
          act shouldBe (waitPeriod + emissionDuration) // Remaining elements all take delay + emission duration
        }
      }
    }
  }
  
  "fixedRate" {
    runBlockingTest {
      checkAll(Arb.positiveInts().map(Int::toLong), Arb.int(1..100)) { waitPeriod, n ->
        val emissionDuration = waitPeriod / 10
        var state: Long? = null
        
        val rate = fixedRate(Duration.milliseconds(waitPeriod)) { currentTime }
          .map {
            val now = state ?: currentTime
            val nextNow = currentTime
            val lapsed = nextNow - now
            state = nextNow
            delay(emissionDuration)
            lapsed
          }
          .take(n)
          .toList()
        
        rate.first() shouldBe 0 // First element is immediately
        rate.drop(1).forEach { act ->
          // Remaining elements all take total of waitPeriod, emissionDuration is correctly taken into account.
          act shouldBe waitPeriod
        }
      }
    }
  }
  
  "fixedRate(dampen = true)" {
    val waitPeriod = 1000L
    val n = 3
    val timeout = (n + 1) * waitPeriod + 500
    val buffer = mutableListOf<Unit>()
    
    withTimeoutOrNull(timeout) {
      fixedRate(Duration.milliseconds(waitPeriod), true) { timeInMillis() }
        .mapIndexed { index, _ ->
          if (index == 0) delay(waitPeriod * n) else Unit
        }
        .collect(buffer::add)
    }
    
    buffer.size shouldBe 2
  }
  
  "fixedRate(dampen = false)" {
    val waitPeriod = 1000L
    val n = 3
    val timeout = (n + 1) * waitPeriod + 500
    val buffer = mutableListOf<Unit>()
    
    withTimeoutOrNull(timeout) {
      fixedRate(Duration.milliseconds(waitPeriod), false) { timeInMillis() }
        .mapIndexed { index, _ ->
          if (index == 0) delay(waitPeriod * n) else Unit
        }
        .collect(buffer::add)
    }
    
    buffer.size shouldBe n + 1
  }
})
