package arrow.fx.coroutines

import arrow.atomic.AtomicInt
import arrow.atomic.update
import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.left
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test

class ParMapTest {
  @Test fun parMapIsStackSafe() = runTest {
    val count = 20_000
    val ref = AtomicInt(0)
    (0 until count).parMap { _: Int ->
      ref.update { it + 1 }
    }
    ref.get() shouldBe count
  }

  @Test fun parMapRunsInParallel() = runTest {
    val promiseA = CompletableDeferred<Unit>()
    val promiseB = CompletableDeferred<Unit>()
    val promiseC = CompletableDeferred<Unit>()

    listOf(
      suspend {
        promiseA.await()
        promiseC.complete(Unit)
      },
      suspend {
        promiseB.await()
        promiseA.complete(Unit)
      },
      suspend {
        promiseB.complete(Unit)
        promiseC.await()
      }
    ).parMap { it.invoke() }
  }

  @Test fun parTraverseResultsInTheCorrectError() = runTest {
    checkAll(
      Arb.int(min = 10, max = 20),
      Arb.int(min = 1, max = 9),
      Arb.throwable()
    ) { n, killOn, e ->
      Either.catch {
        (0 until n).parMap { i ->
          if (i == killOn) throw e else Unit
        }
      } should leftException(e)
    }
  }

  @Test fun parMapConcurrency1OnlyRunsOneTaskAtATime() = runTest {
    val promiseA = CompletableDeferred<Unit>()

    withTimeoutOrNull(100.milliseconds) {
      listOf(
        suspend { promiseA.await() },
        suspend { promiseA.complete(Unit) }
      ).parMap(concurrency = 1) { it.invoke() }
    } shouldBe null
  }

  @Test fun parMapWithEitherResultsInTheCorrectLeft() = runTest {
    checkAll(
      Arb.int(min = 10, max = 20),
      Arb.int(min = 1, max = 9),
      Arb.string()
    ) { n, killOn, e ->
      either {
        (0 until n).parMap { i ->
          if (i == killOn) raise(e) else Unit
        }
      } shouldBe e.left()
    }
  }

  @Test fun parMapOrAccumulateIsStackSafe() = runTest {
    val count = 20_000
    val ref = AtomicInt(0)
    (0 until count).parMapOrAccumulate(combine = emptyError) { _: Int ->
      ref.update { it + 1 }
    }
    ref.get() shouldBe count
  }

  @Test fun parMapOrAccumulateRunsInParallel() = runTest {
    val promiseA = CompletableDeferred<Unit>()
    val promiseB = CompletableDeferred<Unit>()
    val promiseC = CompletableDeferred<Unit>()

    listOf(
      suspend {
        promiseA.await()
        promiseC.complete(Unit)
      },
      suspend {
        promiseB.await()
        promiseA.complete(Unit)
      },
      suspend {
        promiseB.complete(Unit)
        promiseC.await()
      }
    ).parMapOrAccumulate(combine = emptyError) { it.invoke() }
  }

  @Test fun parMapOrAccumulateResultsInTheCorrectError() = runTest {
    checkAll(
      Arb.int(min = 10, max = 20),
      Arb.int(min = 1, max = 9),
      Arb.throwable()
    ) { n, killOn, e ->
      Either.catch {
        (0 until n).parMapOrAccumulate(combine = emptyError) { i ->
          if (i == killOn) throw e else Unit
        }
      } should leftException(e)
    }
  }

  @Test fun parMapOrAccumulateConcurrency1OnlyRunsOneTaskAtATime() = runTest {
    val promiseA = CompletableDeferred<Unit>()

    withTimeoutOrNull(100.milliseconds) {
      listOf(
        suspend { promiseA.await() },
        suspend { promiseA.complete(Unit) }
      ).parMapOrAccumulate(concurrency = 1, combine = emptyError) { it.invoke() }
    } shouldBe null
  }

  @Test fun parMapOrAccumulateAccumulatesShifts() = runTest {
    checkAll(Arb.string()) { e ->
      (0 until 100).parMapOrAccumulate { _ ->
        raise(e)
      } shouldBe NonEmptyList(e, (1 until 100).map { e }).left()
    }
  }

  @Test fun parMapNotNullIsStackSafe() = runTest {
    val count = 20_000
    val ref = AtomicInt(0)
    (0 until count).parMapNotNull { _: Int ->
      ref.update { it + 1 }
    }
    ref.get() shouldBe count
  }

  @Test fun parMapNotNullRunsInParallel() = runTest {
    val promiseA = CompletableDeferred<Unit>()
    val promiseB = CompletableDeferred<Unit>()
    val promiseC = CompletableDeferred<Unit>()

    listOf(
      suspend {
        promiseA.await()
        promiseC.complete(Unit)
      },
      suspend {
        promiseB.await()
        promiseA.complete(Unit)
      },
      suspend {
        promiseB.complete(Unit)
        promiseC.await()
      }
    ).parMapNotNull { it.invoke() }
  }

  @Test fun parMapNotNullResultsInTheCorrectError() = runTest {
    checkAll(
      Arb.int(min = 10, max = 20),
      Arb.int(min = 1, max = 9),
      Arb.throwable()
    ) { n, killOn, e ->
      Either.catch {
        (0 until n).parMapNotNull { i ->
          if (i == killOn) throw e else Unit
        }
      } should leftException(e)
    }
  }

  @Test fun parMapNotNullConcurrency1OnlyRunsOneTaskAtATime() = runTest {
    val promiseA = CompletableDeferred<Unit>()

    withTimeoutOrNull(100.milliseconds) {
      listOf(
        suspend { promiseA.await() },
        suspend { promiseA.complete(Unit) }
      ).parMapNotNull(concurrency = 1) { it.invoke() }
    } shouldBe null
  }

  @Test fun parMapNotNullDiscardsNulls() = runTest {
    (0 until 100).parMapNotNull { _ ->
      null
    } shouldBe emptyList()
  }

  @Test fun parMapNotNullRetainsNonNulls() = runTest {
    checkAll(Arb.int()) { i ->
      (0 until 100).parMapNotNull { _ ->
        i
      } shouldBe List(100) { i }
    }
  }
}

private val emptyError: (Nothing, Nothing) -> Nothing =
  { _, _ -> throw AssertionError("Should not be called") }
