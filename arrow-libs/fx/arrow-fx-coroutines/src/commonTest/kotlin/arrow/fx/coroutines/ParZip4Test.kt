package arrow.fx.coroutines.parZip

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.atomic.value
import arrow.core.Either
import arrow.core.Tuple4
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

class ParZip4Test : StringSpec({
    "parZip 4 runs in parallel" {
      checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int()) { a, b, c, d ->
        val r = Atomic("")
        val modifyGate1 = CompletableDeferred<Unit>()
        val modifyGate2 = CompletableDeferred<Unit>()
        val modifyGate3 = CompletableDeferred<Unit>()

        parZip(
          {
            modifyGate2.await()
            r.update { i -> "$i$a" }
          },
          {
            modifyGate3.await()
            r.update { i -> "$i$b" }
            modifyGate2.complete(Unit)
          },
          {
            modifyGate1.await()
            r.update { i -> "$i$c" }
            modifyGate3.complete(Unit)
          },
          {
            r.value = "$d"
            modifyGate1.complete(Unit)
          }
        ) { _a, _b, _c, _d ->
          Tuple4(_a, _b, _c, _d)
        }

        r.value shouldBe "$d$c$b$a"
      }
    }

    "Cancelling parZip 4 cancels all participants" {
        val s = Channel<Unit>()
        val pa = CompletableDeferred<ExitCase>()
        val pb = CompletableDeferred<ExitCase>()
        val pc = CompletableDeferred<ExitCase>()
        val pd = CompletableDeferred<ExitCase>()

        val loserA: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pa) }
        val loserB: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pb) }
        val loserC: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pc) }
        val loserD: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pd) }

        val f = async { parZip(loserA, loserB, loserC, loserD) { _a, _b, _c, _d -> Tuple4(_a, _b, _c, _d) } }

        repeat(4) { s.send(Unit) } // Suspend until all racers started
        f.cancel()

        pa.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pb.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pc.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pd.await().shouldBeTypeOf<ExitCase.Cancelled>()
    }

    "parZip 4 cancels losers if a failure occurs in one of the tasks" {
      checkAll(
        Arb.throwable(),
        Arb.element(listOf(1, 2, 3, 4)),
      ) { e, winningTask ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<ExitCase>()
        val pb = CompletableDeferred<ExitCase>()
        val pc = CompletableDeferred<ExitCase>()

        val winner: suspend CoroutineScope.() -> Int = { repeat(3) { s.send(Unit) }; throw e }
        val loserA: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pa) }
        val loserB: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pb) }
        val loserC: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pc) }

        val r = Either.catch {
          when (winningTask) {
            1 -> parZip(winner, loserA, loserB, loserC) { _, _, _, _ -> }
            2 -> parZip(loserA, winner, loserB, loserC) { _, _, _, _ -> }
            3 -> parZip(loserA, loserB, winner, loserC) { _, _, _, _ -> }
            else -> parZip(loserA, loserB, loserC, winner) { _, _, _, _ -> }
          }
        }

        pa.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pb.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pc.await().shouldBeTypeOf<ExitCase.Cancelled>()
        r should leftException(e)
      }
    }

    "parZip CancellationException on right can cancel rest" {
      checkAll(Arb.string(), Arb.int(1..4)) { msg, cancel ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<ExitCase>()
        val pb = CompletableDeferred<ExitCase>()
        val pc = CompletableDeferred<ExitCase>()

        val winner: suspend CoroutineScope.() -> Int = { repeat(3) { s.send(Unit) }; throw CancellationException(msg) }
        val loserA: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pa) }
        val loserB: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pb) }
        val loserC: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pc) }

        try {
          when (cancel) {
            1 -> parZip(winner, loserA, loserB, loserC) { _, _, _, _ -> }
            2 -> parZip(loserA, winner, loserB, loserC) { _, _, _, _ -> }
            3 -> parZip(loserA, loserB, winner, loserC) { _, _, _, _ -> }
            else -> parZip(loserA, loserB, loserC, winner) { _, _, _, _ -> }
          }
        } catch (e: CancellationException) {
          e.message shouldBe msg
        }
        pa.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pb.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pc.await().shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }
  }
)
