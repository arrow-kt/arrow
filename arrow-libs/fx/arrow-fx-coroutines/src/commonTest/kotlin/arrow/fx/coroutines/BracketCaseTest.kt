package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.ExperimentalTime

@ExperimentalTime
class BracketCaseTest {
  @Test
  fun immediateAcquireBracketCaseFinishesSuccessfully() = runTest {
    checkAll(10, Arb.int(), Arb.int()) { a, b ->
      var once = true
      bracketCase(
        acquire = { a },
        use = { aa -> Pair(aa, b) },
        release = { _, _ ->
          require(once)
          once = false
        }
      ) shouldBe Pair(a, b)
    }
  }

  @Test
  fun suspendedAcquireBracketCaseFinishedSuccessfully() = runTest {
    checkAll(10, Arb.int(), Arb.int()) { a, b ->
      var once = true
      bracketCase(
        acquire = { a.suspend() },
        use = { aa -> Pair(aa, b) },
        release = { _, _ ->
          require(once)
          once = false
        }
      ) shouldBe Pair(a, b)
    }
  }

  @Test
  fun immediateErrorInAcquireStaysTheSameError() = runTest {
    checkAll(10, Arb.throwable()) { e ->
      Either.catch {
        bracketCase<Unit, Int>(
          acquire = { throw e },
          use = { 5 },
          release = { _, _ -> Unit }
        )
      } should leftException(e)
    }
  }

  @Test
  fun suspendErrorInAcquireStaysTheSameError() = runTest {
    checkAll(10, Arb.throwable()) { e ->
      Either.catch {
        bracketCase<Unit, Int>(
          acquire = { e.suspend() },
          use = { 5 },
          release = { _, _ -> Unit }
        )
      } should leftException(e)
    }
  }

  @Test
  fun immediateUseBracketCaseFinishedSuccessfully() = runTest {
    checkAll(10, Arb.int(), Arb.int()) { a, b ->
      var once = true
      bracketCase(
        acquire = { a },
        use = { aa -> Pair(aa, b).suspend() },
        release = { _, _ ->
          require(once)
          once = false
        }
      ) shouldBe Pair(a, b)
    }
  }

  @Test
  fun suspendedUseBracketCaseFinishesSuccessfully() = runTest {
    checkAll(10, Arb.int(), Arb.int()) { a, b ->
      var once = true
      bracketCase(
        acquire = { a },
        use = { aa -> Pair(aa, b).suspend() },
        release = { _, _ ->
          require(once)
          once = false
        }
      ) shouldBe Pair(a, b)
    }
  }

  @Test
  fun bracketCaseMustRunReleaseTaskOnUseImmediateError() = runTest {
    checkAll(10, Arb.int(), Arb.throwable()) { i, e ->
      val promise = CompletableDeferred<ExitCase>()

      Either.catch {
        bracketCase<Int, Int>(
          acquire = { i },
          use = { throw e },
          release = { _, ex ->
            require(promise.complete(ex)) { "Release should only be called once, called again with $ex" }
          }
        )
      }

      promise.await() shouldBe ExitCase.Failure(e)
    }
  }

  @Test
  fun bracketCaseMustRunReleaseTaskOnUseSuspendedError() = runTest {
    checkAll(10, Arb.int(), Arb.throwable()) { x, e ->
      val promise = CompletableDeferred<Pair<Int, ExitCase>>()

      Either.catch {
        bracketCase<Int, Int>(
          acquire = { x },
          use = { e.suspend() },
          release = { xx, ex ->
            require(promise.complete(Pair(xx, ex))) { "Release should only be called once, called again with $ex" }
          }
        )
      }

      promise.await() shouldBe Pair(x, ExitCase.Failure(e))
    }
  }

  @Test
  fun bracketCaseMustAlwaysRunImmediateRelease() = runTest {
    checkAll(10, Arb.int()) { x ->
      val promise = CompletableDeferred<Pair<Int, ExitCase>>()

      Either.catch {
        bracketCase(
          acquire = { x },
          use = { it },
          release = { xx, ex ->
            require(promise.complete(Pair(xx, ex))) { "Release should only be called once, called again with $ex" }
          }
        )
      }

      promise.await() shouldBe Pair(x, ExitCase.Completed)
    }
  }

  @Test
  fun bracketCaseMustAlwaysRunSuspendedRelease() = runTest {
    checkAll(10, Arb.int()) { x ->
      val promise = CompletableDeferred<Pair<Int, ExitCase>>()

      Either.catch {
        bracketCase(
          acquire = { x },
          use = { it },
          release = { xx, ex ->
            require(promise.complete(Pair(xx, ex))) { "Release should only be called once, called again with $ex" }
              .suspend()
          }
        )
      }

      promise.await() shouldBe Pair(x, ExitCase.Completed)
    }
  }

  @Test
  fun bracketCaseMustAlwaysRunImmediateReleaseError() = runTest {
    checkAll(10, Arb.int(), Arb.throwable()) { n, e ->
      Either.catch {
        bracketCase(
          acquire = { n },
          use = { it },
          release = { _, _ -> throw e }
        )
      } should leftException(e)
    }
  }

  @Test
  fun bracketCaseMustAlwaysRunSuspendedReleaseError() = runTest {
    checkAll(10, Arb.int(), Arb.throwable()) { n, e ->
      Either.catch {
        bracketCase(
          acquire = { n },
          use = { it },
          release = { _, _ -> e.suspend() }
        )
      } should leftException(e)
    }
  }

  operator fun Throwable.plus(other: Throwable): Throwable =
    apply { addSuppressed(other) }

  @Test
  fun bracketCaseMustComposeImmediateUseAndImmediateReleaseError() = runTest {
    checkAll(10, Arb.int(), Arb.throwable(), Arb.throwable()) { n, e, e2 ->
      Either.catch {
        bracketCase<Int, Unit>(
          acquire = { n },
          use = { throw e },
          release = { _, _ -> throw e2 }
        )
      } shouldBe Either.Left(e + e2)
    }
  }

  @Test
  fun bracketCaseMustComposeSuspendUseAndImmediateReleaseError() = runTest {
    checkAll(10, Arb.int(), Arb.throwable(), Arb.throwable()) { n, e, e2 ->
      Either.catch {
        bracketCase<Int, Unit>(
          acquire = { n },
          use = { e.suspend() },
          release = { _, _ -> throw e2 }
        )
      } shouldBe Either.Left(e + e2)
    }
  }

  @Test
  fun bracketCaseMustComposeImmediateUseAndSuspendReleaseError() = runTest {
    checkAll(10, Arb.int(), Arb.throwable(), Arb.throwable()) { n, e, e2 ->
      Either.catch {
        bracketCase<Int, Unit>(
          acquire = { n },
          use = { throw e },
          release = { _, _ -> e2.suspend() }
        )
      } shouldBe Either.Left(e + e2)
    }
  }

  @Test
  fun bracketCaseMustComposeSuspendUseAndSuspendReleaseError() = runTest {
    checkAll(10, Arb.int(), Arb.throwable(), Arb.throwable()) { n, e, e2 ->
      Either.catch {
        bracketCase<Int, Unit>(
          acquire = { n },
          use = { e.suspend() },
          release = { _, _ -> e2.suspend() }
        )
      } shouldBe Either.Left(e + e2)
    }
  }

  @Test
  fun cancelOnBracketCaseReleaseWithImmediateAcquire() = runTest {
    val start = CompletableDeferred<Unit>()
    val exit = CompletableDeferred<ExitCase>()

    val f = async {
      bracketCase(
        acquire = { },
        use = {
          // Signal that fiber is running
          start.complete(Unit)
          awaitCancellation()
        },
        release = { _, exitCase ->
          require(exit.complete(exitCase)) { "Release should only be called once, called again with $exitCase" }
        }
      )
    }

    // Wait until the fiber is started before cancelling
    start.await()
    f.cancel()
    exit.await().shouldBeInstanceOf<ExitCase.Cancelled>()
  }

  @Test
  fun cancelOnBracketCaseReleasesWithSuspendingAcquire() = runTest {
    val start = CompletableDeferred<Unit>()
    val exit = CompletableDeferred<ExitCase>()

    val f = async {
      bracketCase(
        acquire = { Unit.suspend() },
        use = {
          // Signal that fiber is running
          start.complete(Unit)
          awaitCancellation()
        },
        release = { _, exitCase ->
          require(exit.complete(exitCase)) { "Release should only be called once, called again with $exitCase" }
        }
      )
    }

    // Wait until the fiber is started before cancelling
    start.await()
    f.cancel()
    exit.await().shouldBeInstanceOf<ExitCase.Cancelled>()
  }

  @Test
  fun cancelOnBracketCaseDoesNotInvokeAfterFinishing() = runTest {
    val start = CompletableDeferred<Unit>()
    val exit = CompletableDeferred<ExitCase>()

    val f = async {
      bracketCase(
        acquire = { Unit },
        use = { Unit.suspend() },
        release = { _, exitCase ->
          require(exit.complete(exitCase)) { "Release should only be called once, called again with $exitCase" }
        }
      )

      // Signal that fiber can be cancelled running
      start.complete(Unit)
      awaitCancellation()
    }

    // Wait until the fiber is started before cancelling
    start.await()
    f.cancel()
    exit.await() shouldBe ExitCase.Completed
  }

  @Test
  fun acquireOnBracketCaseIsNotCancellable() = runTest {
    checkAll(10, Arb.int(), Arb.int()) { x, y ->
      val mVar = Channel<Int>(1).apply { send(x) }
      val latch = CompletableDeferred<Unit>()
      val p = CompletableDeferred<ExitCase>()

      val fiber = async {
        bracketCase(
          acquire = {
            latch.complete(Unit)
            // This should be uncancellable, and suspends until capacity 1 is received
            mVar.send(y)
          },
          use = { awaitCancellation() },
          release = { _, exitCase -> require(p.complete(exitCase)) }
        )
      }

      // Wait until acquire started
      latch.await()
      async { fiber.cancel() }

      mVar.receive() shouldBe x
      mVar.receive() shouldBe y
      p.await().shouldBeInstanceOf<ExitCase.Cancelled>()
    }
  }

  @Test
  fun releaseOnBracketCaseIsNotCancellable() = runTest {
    checkAll(10, Arb.int(), Arb.int()) { x, y ->
      val mVar = Channel<Int>(1).apply { send(x) }
      val latch = CompletableDeferred<Unit>()

      val fiber = async {
        bracketCase(
          acquire = { latch.complete(Unit) },
          use = { awaitCancellation() },
          release = { _, _ -> mVar.send(y) }
        )
      }

      latch.await()
      async { fiber.cancel() }

      mVar.receive() shouldBe x
      // If release was cancelled this hangs since the buffer is empty
      mVar.receive() shouldBe y
    }
  }
}
