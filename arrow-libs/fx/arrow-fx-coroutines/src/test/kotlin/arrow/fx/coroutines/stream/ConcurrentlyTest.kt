package arrow.fx.coroutines.stream

import arrow.core.Either
import arrow.fx.coroutines.Atomic
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.Semaphore
import arrow.fx.coroutines.StreamSpec
import arrow.fx.coroutines.assertThrowable
import arrow.fx.coroutines.milliseconds
import arrow.fx.coroutines.never
import arrow.fx.coroutines.sleep
import arrow.fx.coroutines.throwable
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

class ConcurrentlyTest : StreamSpec(spec = {

  "concurrently" - {
    "when background stream terminates, overall stream continues" {
      checkAll(Arb.stream(Arb.int()), Arb.stream(Arb.int())) { s1, s2 ->
        val expected = s1.toList()
        s1.delayBy(25.milliseconds)
          .concurrently(s2)
          .toList() shouldBe expected
      }
    }

    "when background stream fails, overall stream fails" {
      checkAll(Arb.stream(Arb.int()), Arb.throwable()) { s, e ->
        assertThrowable {
          s.delayBy(25.milliseconds)
            .concurrently(Stream.raiseError<Unit>(e))
            .drain()
        } shouldBe e
      }
    }

    "when primary stream fails, overall stream fails and background stream is terminated" {
      checkAll(Arb.throwable()) { e ->
        val semaphore = Semaphore(0)
        val bg = Stream.effect { sleep(50.milliseconds) }.repeat().onFinalize { semaphore.release() }
        val fg = Stream.raiseError<Unit>(e).delayBy(25.milliseconds)

        assertThrowable {
          fg.concurrently(bg)
            .onFinalize { semaphore.acquire() } // Hangs if bg doesn't go through terminate
            .drain()
        } shouldBe e
      }
    }

    "when primary stream terminates, background stream is terminated" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val semaphore = Semaphore(0)

        val bg = Stream.effect { sleep(50.milliseconds) }.repeat().onFinalize { semaphore.release() }
        val fg = s.delayBy(25.milliseconds)

        fg.concurrently(bg)
          .onFinalize { semaphore.acquire() } // Hangs if bg doesn't go through terminate
          .drain()
      }
    }

    "when background stream fails, primary stream fails even when hung" {
      checkAll(Arb.stream(Arb.int()), Arb.throwable()) { s, e ->
        assertThrowable {
          s.concurrently(Stream.raiseError<Unit>(e))
            .effectTap { never() }
            .drain()
        } shouldBe e
      }
    }

    "run finalizers of background stream and properly handle exception" {
      io.kotest.property.checkAll(2000, Arb.stream(Arb.int()), Arb.throwable()) { s, e ->
        val runnerRun = Atomic(false)
        val finRef = Atomic<List<String>>(emptyList())
        val halt = Promise<Unit>()

        val runner = Stream.bracket(
          { runnerRun.set(true) },
          {
            sleep(100.milliseconds) // assure this inner finalizer always take longer run than `outer`
            finRef.update { it + "Inner" } // signal finalizer invoked
            throw e // signal a failure
          }).flatMap { // flag the concurrently had chance to start, as if the `s` will be empty `runner` may not be evaluated at all.
          Stream.effect_ { halt.complete(Unit) } // immediately interrupt the outer stream
        }

        val r = Either.catch {
          Stream.bracket({ Unit }, { finRef.update { it + "Outer" } })
            .flatMap { s.concurrently(runner) }
            .interruptWhen { Either.catch { halt.get() } }
            .toList()
        }

        val runnerStarted = runnerRun.get()
        val finalizers = finRef.get()

        if (runnerStarted) {
          // finalizers shall be called in correct order and exception shall be thrown
          finalizers shouldBe listOf("Inner", "Outer")
          r shouldBe Either.Left(e)
        } else {
          // still the outer finalizer shall be run, but there is no failure in `s`
          finalizers shouldBe listOf("Outer")
          r shouldBe Either.Right(s.toList())
        }
      }
    }
  }
})
