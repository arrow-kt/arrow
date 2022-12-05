package arrow.fx.coroutines

import io.kotest.core.spec.style.StringSpec
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.checkAll
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

@ExperimentalTime
class FlowTest : StringSpec({

    "Retry - flow fails" {
      val bang = RuntimeException("Bang!")

      checkAll(Arb.int(), Arb.positiveInts(10)) { a, n ->
        var counter = 0
        val e = assertThrowable {
          flow {
            emit(a)
            if (++counter <= 11) throw bang
          }.retry(Schedule.recurs(n))
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
        }.retry(Schedule.recurs(n))
          .reduce { acc, int -> acc + int }

        sum shouldBe a * 6
      }
    }

    "parMap - concurrency = 1 - identity" {
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap(1) { it }
          .toList() shouldBe flow.toList()
      }
    }

    "parMap - runs in parallel" {
      checkAll(Arb.int(), Arb.int(1..2)) { i, n ->
        val latch = CompletableDeferred<Int>()
        flowOf(1, 2).parMap { index ->
          if (index == n) latch.await()
          else {
            latch.complete(i)
            null
          }
        }.toList().filterNotNull() shouldBe listOf(i)
      }
    }

    "parMap - triggers cancel signal" {
        val latch = CompletableDeferred<Unit>()
        val exit = CompletableDeferred<ExitCase>()

        val job = launch {
          flowOf(1).parMap { index ->
            guaranteeCase({
              latch.complete(Unit)
              awaitCancellation()
            }, { ex -> exit.complete(ex) })
          }.collect()
        }
        latch.await()
        job.cancelAndJoin()
        job.isCancelled shouldBe true
        exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
    }

    "parMap - exception in parMap cancels all running tasks" {
      checkAll(Arb.int(), Arb.throwable(), Arb.int(1..2)) { i, e, n ->
        val latch = CompletableDeferred<Unit>()
        val exit = CompletableDeferred<Pair<Int, ExitCase>>()

        assertThrowable {
          flowOf(1, 2).parMap { index ->
            if (index == n) {
              guaranteeCase({
                latch.complete(Unit)
                awaitCancellation()
              }, { ex -> exit.complete(Pair(i, ex)) })
            } else {
              latch.await()
              throw e
            }
          }.collect()
          fail("Cannot reach here. $e should be thrown.")
        } shouldBe e

        val (ii, ex) = exit.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parMap - Cancelling parMap cancels all running jobs" {
      checkAll(Arb.int(), Arb.int()) { i, i2 ->
        val latch = CompletableDeferred<Unit>()
        val exitA = CompletableDeferred<Pair<Int, ExitCase>>()
        val exitB = CompletableDeferred<Pair<Int, ExitCase>>()

        val job = launch {
          flowOf(1, 2).parMap { index ->
            guaranteeCase({
              if (index == 2) latch.complete(Unit)
              awaitCancellation()
            }, { ex ->
              if (index == 1) exitA.complete(Pair(i, ex))
              else exitB.complete(Pair(i2, ex))
            })
          }.collect()
        }

        latch.await()
        job.cancel()

        val (ii, ex) = exitA.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()

        val (ii2, ex2) = exitB.await()
        ii2 shouldBe i2
        ex2.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parMapUnordered - concurrency = 1 - identity" {
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMapUnordered(concurrency = 1) { it }
          .toSet() shouldBe flow.toSet()
      }
    }

    "parMapUnordered - runs in parallel" {
      checkAll(Arb.int(), Arb.int(1..2)) { i, n ->
        val latch = CompletableDeferred<Int>()
        flowOf(1, 2).parMapUnordered { index ->
          if (index == n) latch.await()
          else {
            latch.complete(i)
            null
          }
        }.toSet().filterNotNull() shouldBe setOf(i)
      }
    }

    "parMapUnordered - triggers cancel signal" {
        val latch = CompletableDeferred<Unit>()
        val exit = CompletableDeferred<ExitCase>()

        val job = launch {
          flowOf(1).parMapUnordered {
            guaranteeCase({
              latch.complete(Unit)
              awaitCancellation()
            }, { ex -> exit.complete(ex) })
          }.collect()
        }
        latch.await()
        job.cancelAndJoin()

        job.isCancelled shouldBe true
        exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
    }

    "parMapUnordered - exception in parMap cancels all running tasks" {
      checkAll(Arb.int(), Arb.throwable(), Arb.int(1..2)) { i, e, n ->
        val latch = CompletableDeferred<Unit>()
        val exit = CompletableDeferred<Pair<Int, ExitCase>>()

        assertThrowable {
          flowOf(1, 2).parMapUnordered { index ->
            if (index == n) {
              guaranteeCase({
                latch.complete(Unit)
                awaitCancellation()
              }, { ex -> exit.complete(Pair(i, ex)) })
            } else {
              latch.await()
              throw e
            }
          }.collect()
          fail("Cannot reach here. $e should be thrown.")
        } shouldBe e

        val (ii, ex) = exit.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parMapUnordered - Cancelling parMap cancels all running jobs" {
      checkAll(Arb.int(), Arb.int()) { i, i2 ->
        val latch = CompletableDeferred<Unit>()
        val exitA = CompletableDeferred<Pair<Int, ExitCase>>()
        val exitB = CompletableDeferred<Pair<Int, ExitCase>>()

        val job = launch {
          flowOf(1, 2).parMapUnordered { index ->
            guaranteeCase({
              if (index == 2) latch.complete(Unit)
              awaitCancellation()
            }, { ex ->
              if (index == 1) exitA.complete(Pair(i, ex))
              else exitB.complete(Pair(i2, ex))
            })
          }.collect()
        }

        latch.await()
        job.cancel()

        val (ii, ex) = exitA.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()

        val (ii2, ex2) = exitB.await()
        ii2 shouldBe i2
        ex2.shouldBeTypeOf<ExitCase.Cancelled>()
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
  
    "fixedDelay" {
      runTest {
        checkAll(Arb.positiveInts().map(Int::toLong), Arb.int(1..100)) { waitPeriod, n ->
          val emissionDuration = waitPeriod / 10L
          var state: Long? = null
        
          val rate = flow { emit(delay(waitPeriod)) }.repeat()
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
      runTest {
        checkAll(Arb.positiveInts().map(Int::toLong), Arb.int(1..100)) { waitPeriod, n ->
          val emissionDuration = waitPeriod / 10
          var state: Long? = null
        
          val rate = fixedRate(waitPeriod) { currentTime }
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
      runTest {
        val buffer = mutableListOf<Unit>()
        withTimeoutOrNull(4500) {
          fixedRate(1000, true) { currentTime }
            .mapIndexed { index, _ ->
              if (index == 0) delay(3000) else Unit
              advanceTimeBy(1)
            }.collect(buffer::add)
        }
        buffer.size shouldBe 2
      }
    }
  
    "fixedRate(dampen = false)" {
      runTest {
        val buffer = mutableListOf<Unit>()
        withTimeoutOrNull(4500) {
          fixedRate(1000, false) { currentTime }
            .mapIndexed { index, _ ->
              if (index == 0) delay(3000) else Unit
              advanceTimeBy(1)
            }.collect(buffer::add)
        }
        buffer.size shouldBe 4
      }
    }
  }
)
