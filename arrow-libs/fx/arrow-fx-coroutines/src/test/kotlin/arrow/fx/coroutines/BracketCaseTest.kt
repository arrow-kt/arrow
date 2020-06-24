package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

class BracketCaseTest : ArrowFxSpec(spec = {

  "Uncancellable back pressures timeoutOrNull" {
    checkAll(Arb.long(20, 40), Arb.long(90, 100)) { a, b ->
      val start = System.currentTimeMillis()

      val n = timeOutOrNull(a.milliseconds) {
        uncancellable { sleep(b.milliseconds) }
      }

      val end = System.currentTimeMillis()

      n shouldBe null // timed-out so should be null
      require((end - start) >= b) {
        "Should've taken longer than $b milliseconds, but took ${start - end}ms"
      }
    }
  }

  "Immediate acquire bracketCase finishes successfully" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      bracketCase(
        acquire = { a },
        use = { aa -> Pair(aa, b) },
        release = { _, _ -> CancelToken.unit }
      ) shouldBe Pair(a, b)
    }
  }

  "Suspended acquire bracketCase finishes successfully" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      bracketCase(
        acquire = { a.suspend() },
        use = { aa -> Pair(aa, b) },
        release = { _, _ -> CancelToken.unit }
      ) shouldBe Pair(a, b)
    }
  }

  "Immediate error in acquire stays the same error" {
    checkAll(Arb.throwable()) { e ->
      Either.catch {
        bracketCase<Unit, Int>(
          acquire = { throw e },
          use = { 5 },
          release = { _, _ -> CancelToken.unit }
        )
      } shouldBe Either.Left(e)
    }
  }

  "Suspend error in acquire stays the same error" {
    checkAll(Arb.throwable()) { e ->
      Either.catch {
        bracketCase<Unit, Int>(
          acquire = { e.suspend() },
          use = { 5 },
          release = { _, _ -> CancelToken.unit }
        )
      } shouldBe Either.Left(e)
    }
  }

  "Immediate use bracketCase finishes successfully" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      bracketCase(
        acquire = { a },
        use = { aa -> Pair(aa, b).suspend() },
        release = { _, _ -> CancelToken.unit }
      ) shouldBe Pair(a, b)
    }
  }

  "Suspended use bracketCase finishes successfully" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      bracketCase(
        acquire = { a },
        use = { aa -> Pair(aa, b).suspend() },
        release = { _, _ -> CancelToken.unit }
      ) shouldBe Pair(a, b)
    }
  }

  "bracketCase must run release task on use immediate error" {
    checkAll(Arb.int(), Arb.throwable()) { i, e ->
      val promise = Promise<ExitCase>()

      Either.catch {
        bracketCase<Int, Int>(
          acquire = { i },
          use = { throw e },
          release = { _, ex -> promise.complete(ex) }
        )
      }

      promise.get() shouldBe ExitCase.Failure(e)
    }
  }

  "bracketCase must run release task on use suspended error" {
    checkAll(Arb.int(), Arb.throwable()) { x, e ->
      val promise = Promise<Pair<Int, ExitCase>>()

      Either.catch {
        bracketCase<Int, Int>(
          acquire = { x },
          use = { e.suspend() },
          release = { xx, ex -> promise.complete(Pair(xx, ex)) }
        )
      }

      promise.get() shouldBe Pair(x, ExitCase.Failure(e))
    }
  }

  "bracketCase must always run immediate release" {
    checkAll(Arb.int()) { x ->
      val promise = Promise<Pair<Int, ExitCase>>()

      Either.catch {
        bracketCase(
          acquire = { x },
          use = { it },
          release = { xx, ex -> promise.complete(Pair(xx, ex)) }
        )
      }

      promise.get() shouldBe Pair(x, ExitCase.Completed)
    }
  }

  "bracketCase must always run suspended release" {
    checkAll(Arb.int()) { x ->
      val promise = Promise<Pair<Int, ExitCase>>()

      Either.catch {
        bracketCase(
          acquire = { x },
          use = { it },
          release = { xx, ex -> promise.complete(Pair(xx, ex)).suspend() }
        )
      }

      promise.get() shouldBe Pair(x, ExitCase.Completed)
    }
  }

  "bracketCase must always run immediate release error" {
    checkAll(Arb.int(), Arb.throwable()) { n, e ->
      Either.catch {
        bracketCase(
          acquire = { n },
          use = { it },
          release = { _, _ -> throw e }
        )
      } shouldBe Either.Left(e)
    }
  }

  "bracketCase must always run suspended release error" {
    checkAll(Arb.int(), Arb.throwable()) { n, e ->
      Either.catch {
        bracketCase(
          acquire = { n },
          use = { it },
          release = { _, _ -> e.suspend() }
        )
      } shouldBe Either.Left(e)
    }
  }

  "bracketCase must compose immediate use & immediate release error" {
    checkAll(Arb.int(), Arb.throwable(), Arb.throwable()) { n, e, e2 ->
      Either.catch {
        bracketCase<Int, Unit>(
          acquire = { n },
          use = { throw e },
          release = { _, _ -> throw e2 }
        )
      } shouldBe Either.Left(Platform.composeErrors(e, e2))
    }
  }

  "bracketCase must compose suspend use & immediate release error" {
    checkAll(Arb.int(), Arb.throwable(), Arb.throwable()) { n, e, e2 ->
      Either.catch {
        bracketCase<Int, Unit>(
          acquire = { n },
          use = { e.suspend() },
          release = { _, _ -> throw e2 }
        )
      } shouldBe Either.Left(Platform.composeErrors(e, e2))
    }
  }

  "bracketCase must compose immediate use & suspend release error" {
    checkAll(Arb.int(), Arb.throwable(), Arb.throwable()) { n, e, e2 ->
      Either.catch {
        bracketCase<Int, Unit>(
          acquire = { n },
          use = { throw e },
          release = { _, _ -> e2.suspend() }
        )
      } shouldBe Either.Left(Platform.composeErrors(e, e2))
    }
  }

  "bracketCase must compose suspend use & suspend release error" {
    checkAll(Arb.int(), Arb.throwable(), Arb.throwable()) { n, e, e2 ->
      Either.catch {
        bracketCase<Int, Unit>(
          acquire = { n },
          use = { e.suspend() },
          release = { _, _ -> e2.suspend() }
        )
      } shouldBe Either.Left(Platform.composeErrors(e, e2))
    }
  }

  "cancel on bracketCase releases" {
    val start = Promise<Unit>()
    val exit = Promise<ExitCase>()

    val f = ForkAndForget {
      bracketCase(
        acquire = { Unit },
        use = {
          // Signal that fiber is running
          start.complete(Unit)
          never<Unit>()
        },
        release = { _, exitCase ->
          exit.complete(exitCase)
        }
      )
    }

    // Wait until the fiber is started before cancelling
    start.get()
    f.cancel()
    exit.get() shouldBe ExitCase.Cancelled
  }

  "acquire on bracketCase is not cancellable" {
    checkAll(Arb.int(), Arb.int()) { x, y ->
      val mVar = ConcurrentVar(x)
      val latch = Promise<Unit>()
      val p = Promise<ExitCase>()

      val fiber = ForkAndForget {
        bracketCase(
          acquire = {
            latch.complete(Unit)
            mVar.put(y)
          },
          use = { never<Unit>() },
          release = { _, exitCase -> p.complete(exitCase) }
        )
      }

      // Wait until acquire started
      latch.get()
      ForkAndForget { fiber.cancel() }

      mVar.take() shouldBe x
      mVar.take() shouldBe y
      p.get() shouldBe ExitCase.Cancelled
    }
  }

  "release on bracketCase is not cancellable" {
    checkAll(Arb.int(), Arb.int()) { x, y ->
      val mVar = ConcurrentVar(x)
      val latch = Promise<Unit>()

      val fiber = ForkAndForget {
        bracketCase(
          acquire = { latch.complete(Unit) },
          use = { never<Unit>() },
          release = { _, _ -> mVar.put(y) }
        )
      }

      latch.get()
      ForkAndForget { fiber.cancel() }

      mVar.take() shouldBe x
      mVar.take() shouldBe y
    }
  }
})
