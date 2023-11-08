package arrow.fx.coroutines

import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.time.ExperimentalTime

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@ExperimentalTime
class FlowTest {

  @Test
  fun parMapConcurrentEqualOneMinusIdentity() = runTest {
    checkAll(Arb.flow(Arb.int())) { flow ->
      flow.parMap(1) { it }
        .toList() shouldBe flow.toList()
    }
  }

  @Test
  fun parMapRunsInParallel() = runTest {
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

  @Test
  fun parMapTriggersCancelSignal() = runTest {
    val latch = CompletableDeferred<Unit>()
    val exit = CompletableDeferred<ExitCase>()

    val job = launch {
      flowOf(1).parMap { _ ->
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

  @Test
  fun parMapExceptionInParMapCancelsAllRunningTasks() = runTest {
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

  @Test
  fun parMapCancellingParMapCancelsAllRunningJobs() = runTest {
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

  @Test
  fun parMapUnorderedConcurrentEqualOneMinusIdentity() = runTest {
    checkAll(Arb.flow(Arb.int())) { flow ->
      flow.parMapUnordered(concurrency = 1) { it }
        .toSet() shouldBe flow.toSet()
    }
  }

  @Test
  fun parMapUnorderedRunsInParallel() = runTest {
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

  @Test
  fun parMapUnorderedTriggersCancelSignal() = runTest {
    val latch = CompletableDeferred<Unit>()
    val exit = CompletableDeferred<ExitCase>()

    val job = launch {
      flowOf(1).parMapUnordered { _ ->
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

  @Test
  fun parMapUnorderedExceptionInParMapCancelsAllRunningTasks() = runTest {
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

  @Test
  fun parMapUnorderedCancellingParMapCancelsAllRunningJobs() = runTest {
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

  @Test
  fun fixedDelay() = runTest {
    checkAll(Arb.positiveInt().map(Int::toLong), Arb.int(1..100)) { waitPeriod, n ->
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

  @Test
  fun fixedRate() = runTest {
    checkAll(Arb.positiveInt().map(Int::toLong), Arb.int(1..100)) { waitPeriod, n ->
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


    @Test
    fun fixedRateWithDampenTrue() = runTest {
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

    @Test
    fun fixedRateWithDampenFalse() = runTest {
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

    @Test
    fun mapIndexed() = runTest {
      val flow = flowOf(1, 2, 3)
        .mapIndexed { index, value -> IndexedValue(index, value) }

      flow.toList() shouldBe listOf(
        IndexedValue(0, 1),
        IndexedValue(1, 2),
        IndexedValue(2, 3)
      )

      flow.toList() shouldBe listOf(
        IndexedValue(0, 1),
        IndexedValue(1, 2),
        IndexedValue(2, 3)
      )
    }
  }
}
