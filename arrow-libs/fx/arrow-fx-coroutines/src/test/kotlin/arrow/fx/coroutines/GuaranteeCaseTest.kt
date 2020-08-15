package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

class GuaranteeCaseTest : ArrowFxSpec(spec = {

  "release for success was invoked" {
    checkAll(Arb.int()) { i ->
      val p = Promise<ExitCase>()

      val res = guaranteeCase(
        fa = { i },
        finalizer = { ex -> p.complete(ex) }
      )

      p.get() shouldBe ExitCase.Completed
      res shouldBe i
    }
  }

  "release for error was invoked" {
    checkAll(Arb.throwable()) { e ->
      val p = Promise<ExitCase>()
      val attempted = Either.catch {
        guaranteeCase<Int>(
          fa = { throw e },
          finalizer = { ex -> p.complete(ex) }
        )
      }

      p.get() shouldBe ExitCase.Failure(e)
      attempted shouldBe Either.Left(e)
    }
  }

  "release for never was invoked" {
    val p = Promise<ExitCase>()
    val start = Promise<Unit>()

    val fiber = ForkAndForget {
      guaranteeCase(
        fa = {
          start.complete(Unit)
          never<Unit>()
        },
        finalizer = { ex -> p.complete(ex) }
      )
    }

    start.get()
    fiber.cancel()
    p.get() shouldBe ExitCase.Cancelled
  }
})
