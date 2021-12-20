package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async

class GuaranteeCaseTest : ArrowFxSpec(
  spec = {

    "release for success was invoked" {
      checkAll(Arb.int()) { i ->
        val p = CompletableDeferred<ExitCase>()

        val res = guaranteeCase(
          fa = { i },
          finalizer = { ex -> require(p.complete(ex)) }
        )

        p.await() shouldBe ExitCase.Completed
        res shouldBe i
      }
    }

    "release for error was invoked" {
      checkAll(Arb.throwable()) { e ->
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

    "release for never was invoked" {
      val p = CompletableDeferred<ExitCase>()
      val start = CompletableDeferred<Unit>()

      val fiber = async {
        guaranteeCase(
          fa = {
            start.complete(Unit)
            never<Unit>()
          },
          finalizer = { ex -> require(p.complete(ex)) }
        )
      }

      start.await()
      fiber.cancel()
      p.await().shouldBeInstanceOf<ExitCase.Cancelled>()
    }
  }
)
