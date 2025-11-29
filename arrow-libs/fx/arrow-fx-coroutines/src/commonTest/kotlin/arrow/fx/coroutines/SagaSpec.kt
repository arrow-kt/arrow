package arrow.fx.coroutines

import arrow.core.raise.merge
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.fail

class SagaSpec {
  @Test
  fun sagaReturnsActionResult(): TestResult = runTest {
    val value = Random.nextInt()
    sagaScope {
      compensate { fail("Doesn't run") }
      value
    } shouldBe value
  }

  data class SagaFailed(val tag: String) : RuntimeException()

  suspend inline fun assertSagaFails(block: ResourceScope.() -> Unit) =
    assertFailsWith<SagaFailed> { sagaScope { block() } }

  @Test
  fun sagaRunsCompensationIfThrowInBuilderAndRethrowsException(): TestResult = runTest {
    val value = Random.nextInt()
    var compensation: Int? = null
    assertSagaFails {
      compensate { compensation = value }
      throw SagaFailed("Exception in builder")
    }
    compensation shouldBe value
  }

  @Test
  fun sagaRunsCompensationIfThrowInSagaAndRethrowsException(): TestResult = runTest {
    val value = Random.nextInt()
    var compensation: Int? = null
    assertSagaFails {
      compensate { compensation = value }
      saga({ throw SagaFailed("Exception in saga") }) { _: Nothing -> }
    }
    compensation shouldBe value
  }

  @Test
  fun sagaRunsCompensationInOrderAndRethrowsException(): TestResult = runTest {
    val valueA = Random.nextInt()
    val valueB = Random.nextInt()

    val compensations = Channel<Int>(2)
    assertSagaFails {
      compensate { compensations.send(valueA) }
      compensate { compensations.send(valueB) }
      saga({ throw SagaFailed("Exception in saga") }) { _: Nothing -> }
    }
    compensations.receive() shouldBe valueB
    compensations.receive() shouldBe valueA
    compensations.close()
  }

  @Test
  fun sagaComposesCompensationErrors(): TestResult = runTest {
    val value = Random.nextInt()
    val compensationA = CompletableDeferred<Int>()
    val original = SagaFailed("Exception in saga")
    val compensation = SagaFailed("Exception in compensation")
    val res = assertSagaFails {
      compensate { compensationA.complete(value) }
      compensate { throw compensation }
      saga({ throw original }) { fail("Doesn't run") }
    }
    res shouldBe original
    res.suppressedExceptions[0] shouldBe compensation
    value shouldBe compensationA.await()
  }

  @Test
  fun sagaComposesCompensationErrorsWhenThrownInBlock(): TestResult = runTest {
    val value = Random.nextInt()
    val compensationA = CompletableDeferred<Int>()
    val original = SagaFailed("Exception in builder")
    val compensation = SagaFailed("Exception in compensation")

    val res = assertSagaFails {
      compensate { compensationA.complete(value) }
      compensate { throw compensation }
      throw original
    }
    res shouldBe original
    res.suppressedExceptions[0] shouldBe compensation
    compensationA.await() shouldBe value
  }

  @Test
  fun sagaCanTraverse(): TestResult = runTest {
    val valueA = Random.nextInt()
    val valueB = Random.nextInt()
    val valueC = Random.nextInt()
    val values = listOf(valueA, valueB, valueC)

    sagaScope { values.map { saga({ it }) { fail("Doesn't run") } } } shouldBe values
  }

  @Test
  fun sagaCanParTraverse(): TestResult = runTest {
    val valueA = Random.nextInt()
    val valueB = Random.nextInt()
    val valueC = Random.nextInt()
    val values = listOf(valueA, valueB, valueC)

    sagaScope { values.parMap(Dispatchers.Default) { saga({ it }) { fail("Doesn't run") } } } shouldBe values
  }

  @Test
  fun parZipRunsLeftCompensation(): TestResult = runTest {
    val value = Random.nextInt()

    var compensationA: Int? = null
    val latch = CompletableDeferred<Unit>()
    assertSagaFails {
      parZip({
        latch.complete(Unit)
        compensate { compensationA = value }
        value
      }, {
        saga({
          latch.await()
          throw SagaFailed("Exception in saga")
        }) { _: Nothing -> }
      }) { _, _: Nothing -> }
    }
    compensationA shouldBe value
  }

  @Test
  fun parZipRunsRightCompensation(): TestResult = runTest {
    val value = Random.nextInt()

    var compensationB: Int? = null
    val latch = CompletableDeferred<Unit>()
    assertSagaFails {
      parZip({
        saga({
          latch.await()
          throw SagaFailed("Exception in saga")
        }) { _: Nothing -> }
      }, {
        latch.complete(Unit)
        compensationB = value
        value
      }) { _: Nothing, _ -> }
    }
    compensationB shouldBe value
  }

  @Test
  fun sagaRollbacksOnRaise1() = runTest {
    merge {
      sagaScope<Nothing> {
        saga({
          raise("failed")
        }) { _: Nothing -> }
      }
    } shouldBe "failed"
  }

  @Test
  fun sagaRollbacksOnRaise2() = runTest {
    var actionAStarted = false
    var actionARolled = false
    var actionBStarted = false
    merge {
      sagaScope<Nothing> {
        saga({ actionAStarted = true }) { actionARolled = true }
        saga({
          actionBStarted = true
          raise("failed")
        }) { _: Nothing -> }
      }
    } shouldBe "failed"
    actionAStarted.shouldBe(true, "main block A executes")
    actionARolled.shouldBe(true, "rollback block B executes")
    actionBStarted.shouldBe(true, "main block B executes")
  }
}

