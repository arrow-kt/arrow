package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.core.spec.style.StringSpec
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
import kotlin.time.ExperimentalTime

@ExperimentalTime
class BracketCaseTest : StringSpec({
    "Immediate acquire bracketCase finishes successfully" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
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

    "Suspended acquire bracketCase finishes successfully" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
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

    "Immediate error in acquire stays the same error" {
      checkAll(Arb.throwable()) { e ->
        Either.catch {
          bracketCase<Unit, Int>(
            acquire = { throw e },
            use = { 5 },
            release = { _, _ -> Unit }
          )
        } should leftException(e)
      }
    }

    "Suspend error in acquire stays the same error" {
      checkAll(Arb.throwable()) { e ->
        Either.catch {
          bracketCase<Unit, Int>(
            acquire = { e.suspend() },
            use = { 5 },
            release = { _, _ -> Unit }
          )
        } should leftException(e)
      }
    }

    "Immediate use bracketCase finishes successfully" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
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

    "Suspended use bracketCase finishes successfully" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
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

    "bracketCase must run release task on use immediate error" {
      checkAll(Arb.int(), Arb.throwable()) { i, e ->
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

    "bracketCase must run release task on use suspended error" {
      checkAll(Arb.int(), Arb.throwable()) { x, e ->
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

    "bracketCase must always run immediate release" {
      checkAll(Arb.int()) { x ->
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

    "bracketCase must always run suspended release" {
      checkAll(Arb.int()) { x ->
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

    "bracketCase must always run immediate release error" {
      checkAll(Arb.int(), Arb.throwable()) { n, e ->
        Either.catch {
          bracketCase(
            acquire = { n },
            use = { it },
            release = { _, _ -> throw e }
          )
        } should leftException(e)
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
        } should leftException(e)
      }
    }
  
    operator fun Throwable.plus(other: Throwable): Throwable =
      apply { addSuppressed(other) }

    "bracketCase must compose immediate use & immediate release error" {
      checkAll(Arb.int(), Arb.throwable(), Arb.throwable()) { n, e, e2 ->
        Either.catch {
          bracketCase<Int, Unit>(
            acquire = { n },
            use = { throw e },
            release = { _, _ -> throw e2 }
          )
        } shouldBe Either.Left(e + e2)
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
        } shouldBe Either.Left(e + e2)
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
        } shouldBe Either.Left(e + e2)
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
        } shouldBe Either.Left(e + e2)
      }
    }

    "cancel on bracketCase releases with immediate acquire" {
      val start = CompletableDeferred<Unit>()
      val exit = CompletableDeferred<ExitCase>()

      val f = async {
        bracketCase(
          acquire = { Unit },
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

    "cancel on bracketCase releases with suspending acquire" {
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

    "cancel on bracketCase doesn't invoke after finishing" {
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

    "acquire on bracketCase is not cancellable" {
      checkAll(Arb.int(), Arb.int()) { x, y ->
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

    "release on bracketCase is not cancellable" {
      checkAll(Arb.int(), Arb.int()) { x, y ->
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
)
