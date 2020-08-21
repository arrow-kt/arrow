package arrow.fx.coroutines.stream

import arrow.fx.coroutines.ArrowFxSpec
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ForkAndForget
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.rethrow
import io.kotest.assertions.failure
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int

class CancellationTest : ArrowFxSpec(spec = {

  "constant" {
    checkAll(Arb.int()) { i ->
      Stream.constant(i).assertCancellable()
    }
  }

  "bracketed stream" {
    checkAll(Arb.int()) { i ->
      val exitCase = Promise<ExitCase>()

      assertCancellable { latch ->
        Stream.bracketCase(
          { latch.complete(Unit) },
          { _, ex ->
            exitCase.complete(ex)
              .mapLeft { failure("Bracket finalizer may only be called once") }
              .rethrow()
          }
        ).flatMap { Stream.constant(i) }
      }

      exitCase.get() shouldBe ExitCase.Cancelled
    }
  }

  "parJoin" {
    checkAll(Arb.int()) { i ->
      val s = Stream.constant(i)
      assertCancellable { latch ->
        Stream(s, s, Stream.effect { latch.complete(Unit) })
          .parJoin(3)
      }
    }
  }

  "concurrently" {
    checkAll(Arb.int()) { i ->
      val s = Stream.constant(i)
      assertCancellable { latch ->
        // concurrent stream is started before this stream is
        Stream.effect { latch.complete(Unit) }.flatMap { s }
          .concurrently(s)
      }
    }
  }
})

@JvmName("assertStreamCancellable")
private suspend fun <A> assertCancellable(fa: (latch: Promise<Unit>) -> Stream<A>): Unit {
  val p = Promise<ExitCase>()
  val latch = Promise<Unit>()

  val fiber = ForkAndForget {
    guaranteeCase(
      fa = {
        fa(latch).drain()
      },
      finalizer = { ex -> p.complete(ex) }
    )
  }

  latch.get()
  fiber.cancel()
  p.get() shouldBe ExitCase.Cancelled
}

private suspend fun <A> Stream<A>.assertCancellable(): Unit {
  val p = Promise<ExitCase>()
  val start = Promise<Unit>()

  val fiber = ForkAndForget {
    guaranteeCase(
      fa = {
        Stream.effect { start.complete(Unit) }
          .flatMap { this@assertCancellable }
          .drain()
      },
      finalizer = { ex -> p.complete(ex) }
    )
  }

  start.get()
  fiber.cancel()
  p.get() shouldBe ExitCase.Cancelled
}
