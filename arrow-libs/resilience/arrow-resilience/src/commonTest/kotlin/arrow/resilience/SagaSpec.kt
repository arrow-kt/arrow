package arrow.resilience

import arrow.fx.coroutines.parMap
import arrow.fx.coroutines.parZip
import arrow.resilience.saga
import arrow.resilience.transact
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("unused")
class SagaSpec {

  @Test
  fun sagaReturnsActionResult(): TestResult = runTest {
    val value = Random.nextInt()
    val saga = saga({ value }) { fail("Doesn't run") }
    assertEquals(value, saga.transact())
  }

  data class SagaFailed(val tag: String) : RuntimeException()

  @Test
  fun sagaRunsCompensationIfThrowInBuilderAndRethrowsException(): TestResult = runTest {
    val value = Random.nextInt()
    val compensation = CompletableDeferred<Int>()
    val saga = saga {
      saga({ value }) { compensation.complete(it) }
      throw SagaFailed("Exception in builder")
    }
    assertFailsWith<SagaFailed> { saga.transact() }
    assertEquals(value, compensation.await())
  }

  @Test
  fun sagaRunsCompensationIfThrowInSagaAndRethrowsException(): TestResult = runTest {
    val value = Random.nextInt()
    val compensation = CompletableDeferred<Int>()
    val saga = saga {
      saga({ value }) { compensation.complete(it) }
      saga({ throw SagaFailed("Exception in saga") }) { fail("Doesn't run") }
    }
    assertFailsWith<SagaFailed> { saga.transact() }
    assertEquals(value, compensation.await())
  }

  @Test
  fun sagaRunsCompensationInOrderAndRethrowsException(): TestResult = runTest {
    val valueA = Random.nextInt()
    val valueB = Random.nextInt()

    val compensations = Channel<Int>(2)
    val saga = saga {
      saga({ valueA }) { compensations.send(it) }
      saga({ valueB }) { compensations.send(it) }
      saga({ throw SagaFailed("Exception in saga") }) { fail("Doesn't run") }
    }
    assertFailsWith<SagaFailed> { saga.transact() }
    assertEquals(valueB, compensations.receive())
    assertEquals(valueA, compensations.receive())
    compensations.close()
  }

  @Test
  fun sagaComposesCompensationErrors(): TestResult = runTest {
    val value = Random.nextInt()
    val compensationA = CompletableDeferred<Int>()
    val original = SagaFailed("Exception in saga")
    val compensation = SagaFailed("Exception in compensation")
    val saga = saga {
      saga({ value }) { compensationA.complete(it) }
      saga({}) { throw compensation }
      saga({ throw original }) { fail("Doesn't run") }
    }

    val res = assertFailsWith<SagaFailed> { saga.transact() }
    assertEquals(original, res)
    assertEquals(compensation, res.suppressedExceptions[0])
    assertEquals(value, compensationA.await())
  }

  @Test
  fun sagaComposesCompensationErrorsWhenThrownInBlock(): TestResult = runTest {
    val value = Random.nextInt()
    val compensationA = CompletableDeferred<Int>()
    val original = SagaFailed("Exception in builder")
    val compensation = SagaFailed("Exception in compensation")

    val saga = saga {
      saga({ value }) { compensationA.complete(it) }
      saga({}) { throw compensation }
      throw original
    }

    val res = assertFailsWith<SagaFailed> { saga.transact() }
    assertEquals(original, res)
    assertEquals(compensation, res.suppressedExceptions[0])
    assertEquals(value, compensationA.await())
  }

  @Test
  fun sagaCanTraverse(): TestResult = runTest {
    val valueA = Random.nextInt()
    val valueB = Random.nextInt()
    val valueC = Random.nextInt()
    val values = listOf(valueA, valueB, valueC)

    val result = saga { values.map { saga({ it }) { fail("Doesn't run") } } }.transact()
    assertEquals(values, result)
  }

  @Test
  fun sagaCanParTraverse(): TestResult = runTest {
    val valueA = Random.nextInt()
    val valueB = Random.nextInt()
    val valueC = Random.nextInt()
    val values = listOf(valueA, valueB, valueC)

    val result = saga { values.parMap(Dispatchers.Default) { saga({ it }) { fail("Doesn't run") } } }.transact()
    assertEquals(values, result)
  }

  @Test
  fun parZipRunsLeftCompensation(): TestResult = runTest {
    val value = Random.nextInt()

    val compensationA = CompletableDeferred<Int>()
    val latch = CompletableDeferred<Unit>()
    val saga = saga {
      parZip({
        saga({
          latch.complete(Unit)
          value
        }) { compensationA.complete(it) }
      }, {
        saga({
          latch.await()
          throw SagaFailed("Exception in saga")
        }) { fail("Doesn't run") }
      }) { _, _ -> }
    }

    assertFailsWith<SagaFailed> { saga.transact() }
    assertEquals(value, compensationA.await())
  }

  @Test
  fun parZipRunsRightCompensation(): TestResult = runTest {
    val value = Random.nextInt()

    val compensationB = CompletableDeferred<Int>()
    val latch = CompletableDeferred<Unit>()
    val saga = saga {
      parZip({
        saga({
          latch.await()
          throw SagaFailed("Exception in saga")
        }) { fail("Doesn't run") }
      }, {
        saga({
          latch.complete(Unit)
          value
        }) { compensationB.complete(it) }
      }) { _, _ -> }
    }

    assertFailsWith<SagaFailed> { saga.transact() }
    assertEquals(value, compensationB.await())
  }
}
