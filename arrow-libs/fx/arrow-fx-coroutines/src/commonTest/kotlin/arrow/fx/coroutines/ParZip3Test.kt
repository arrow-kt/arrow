package arrow.fx.coroutines.parZip

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.atomic.value
import arrow.core.Either
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.awaitExitCase
import arrow.fx.coroutines.leftException
import arrow.fx.coroutines.parZip
import arrow.fx.coroutines.throwable
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CoroutineScope

class ParZip3Test : StringSpec({
    "parZip 3 runs in parallel" {
      checkAll(Arb.int(), Arb.int(), Arb.int()) { a, b, c ->
        val r = Atomic("")
        val modifyGate1 = CompletableDeferred<Unit>()
        val modifyGate2 = CompletableDeferred<Unit>()

        parZip(
          {
            modifyGate2.await()
            r.update { i -> "$i$a" }
          },
          {
            modifyGate1.await()
            r.update { i -> "$i$b" }
            modifyGate2.complete(Unit)
          },
          {
            r.value ="$c"
            modifyGate1.complete(Unit)
          }
        ) { _a, _b, _c ->
          Triple(_a, _b, _c)
        }

        r.value shouldBe "$c$b$a"
      }
    }

    "Cancelling parZip 3 cancels all participants" {
        val s = Channel<Unit>()
        val pa = CompletableDeferred<ExitCase>()
        val pb = CompletableDeferred<ExitCase>()
        val pc = CompletableDeferred<ExitCase>()

        val loserA: suspend CoroutineScope.() -> Int =
          { awaitExitCase(s, pa) }
        val loserB: suspend CoroutineScope.() -> Int =
          { awaitExitCase(s, pb) }
        val loserC: suspend CoroutineScope.() -> Int =
          { awaitExitCase(s, pc) }

        val f = async { parZip(loserA, loserB, loserC) { _a, _b, _c -> Triple(_a, _b, _c) } }

        s.send(Unit) // Suspend until all racers started
        s.send(Unit)
        s.send(Unit)
        f.cancel()

        pa.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pb.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pc.await().shouldBeTypeOf<ExitCase.Cancelled>()
    }

    "parZip 3 cancels losers if a failure occurs in one of the tasks" {
      checkAll(
        Arb.throwable(),
        Arb.element(listOf(1, 2, 3)),
      ) { e, winningTask ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<ExitCase>()
        val pb = CompletableDeferred<ExitCase>()

        val winner: suspend CoroutineScope.() -> Int = { s.send(Unit); s.send(Unit); throw e }
        val loserA: suspend CoroutineScope.() -> Int =
          { awaitExitCase(s, pa) }
        val loserB: suspend CoroutineScope.() -> Int =
          { awaitExitCase(s, pb) }

        val r = Either.catch {
          when (winningTask) {
            1 -> parZip(winner, loserA, loserB) { _, _, _ -> }
            2 -> parZip(loserA, winner, loserB) { _, _, _ -> }
            else -> parZip(loserA, loserB, winner) { _, _, _ -> }
          }
        }

        pa.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pb.await().shouldBeTypeOf<ExitCase.Cancelled>()
        r should leftException(e)
      }
    }

    "parZip CancellationException on right can cancel rest" {
      checkAll(Arb.string(), Arb.int(1..3)) { msg, cancel ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<ExitCase>()
        val pb = CompletableDeferred<ExitCase>()

        val winner: suspend CoroutineScope.() -> Int = { repeat(2) { s.send(Unit) }; throw CancellationException(msg) }
        val loserA: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pa) }
        val loserB: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pb) }

        try {
          when (cancel) {
            1 -> parZip(winner, loserA, loserB) { _, _, _ -> }
            2 -> parZip(loserA, winner, loserB) { _, _, _ -> }
            else -> parZip(loserA, loserB, winner) { _, _, _ -> }
          }
        } catch (e: CancellationException) {
          e.message shouldBe msg
        }
        pa.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pb.await().shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }
  }
)
