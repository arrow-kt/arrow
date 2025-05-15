package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class GuaranteeCaseTest {

  @Test
  fun releaseForSuccessWasInvoked() = runTest {
    checkAll(10, Arb.int()) { i ->
      val p = CompletableDeferred<ExitCase>()

      val res = guaranteeCase(
        fa = { i },
        finalizer = { ex -> require(p.complete(ex)) }
      )

      p.await() shouldBe ExitCase.Completed
      res shouldBe i
    }
  }

  @Test
  fun releaseForErrorWasInvoked() = runTest {
    checkAll(10, Arb.throwable()) { e ->
      val p = CompletableDeferred<ExitCase>()
      val attempted = Either.catch {
        guaranteeCase<Int>(
          fa = { throw e },
          finalizer = { ex -> require(p.complete(ex)) }
        )
      }

      p.await() shouldBe ExitCase.Failure(e)
      attempted shouldBe Either.Left(e)
    }
  }

  @Test
  fun releaseForNeverWasInvoked() = runTest {
    val p = CompletableDeferred<ExitCase>()
    val start = CompletableDeferred<Unit>()

    val fiber = async {
      guaranteeCase(
        fa = {
          start.complete(Unit)
          awaitCancellation()
        },
        finalizer = { ex -> require(p.complete(ex)) }
      )
    }

    start.await()
    fiber.cancel()
    p.await().shouldBeInstanceOf<ExitCase.Cancelled>()
  }
}
