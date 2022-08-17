package arrow.fx.coroutines.parMapN

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.core.Either
import arrow.core.Tuple8
import arrow.fx.coroutines.ArrowFxSpec
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.awaitExitCase
import arrow.fx.coroutines.leftException
import arrow.fx.coroutines.parZip
import arrow.fx.coroutines.throwable
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CoroutineScope

class ParMap8Test : ArrowFxSpec(
  spec = {
    "parMapN 8 runs in parallel" {
      checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { a, b, c, d, e, f, g, h ->
        val r = Atomic("")
        val modifyGate1 = CompletableDeferred<Unit>()
        val modifyGate2 = CompletableDeferred<Unit>()
        val modifyGate3 = CompletableDeferred<Unit>()
        val modifyGate4 = CompletableDeferred<Unit>()
        val modifyGate5 = CompletableDeferred<Unit>()
        val modifyGate6 = CompletableDeferred<Unit>()
        val modifyGate7 = CompletableDeferred<Unit>()

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
            modifyGate4.await()
            r.update { i -> "$i$c" }
            modifyGate3.complete(Unit)
          },
          {
            modifyGate5.await()
            r.update { i -> "$i$d" }
            modifyGate4.complete(Unit)
          },
          {
            modifyGate6.await()
            r.update { i -> "$i$e" }
            modifyGate5.complete(Unit)
          },
          {
            modifyGate7.await()
            r.update { i -> "$i$f" }
            modifyGate6.complete(Unit)
          },
          {
            modifyGate1.await()
            r.update { i -> "$i$g" }
            modifyGate7.complete(Unit)
          },
          {
            r.value = "$h"
            modifyGate1.complete(Unit)
          }
        ) { _a, _b, _c, _d, _e, _f, _g, _h ->
          Tuple8(_a, _b, _c, _d, _e, _f, _g, _h)
        }

        r.value shouldBe "$h$g$f$e$d$c$b$a"
      }
    }

    "Cancelling parMapN 8 cancels all participants" {
      checkAll {
        val s = Channel<Unit>()
        val pa = CompletableDeferred<ExitCase>()
        val pb = CompletableDeferred<ExitCase>()
        val pc = CompletableDeferred<ExitCase>()
        val pd = CompletableDeferred<ExitCase>()
        val pe = CompletableDeferred<ExitCase>()
        val pf = CompletableDeferred<ExitCase>()
        val pg = CompletableDeferred<ExitCase>()
        val ph = CompletableDeferred<ExitCase>()

        val loserA: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pa) }
        val loserB: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pb) }
        val loserC: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pc) }
        val loserD: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pd) }
        val loserE: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pe) }
        val loserF: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pf) }
        val loserG: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pg) }
        val loserH: suspend CoroutineScope.() -> Int = { awaitExitCase(s, ph) }

        val fork = async {
          parZip(loserA, loserB, loserC, loserD, loserE, loserF, loserG, loserH) { a, b, c, d, e, f, g, h ->
            Tuple8(a, b, c, d, e, f, g, h)
          }
        }

        repeat(8) { s.send(Unit) } // Suspend until all racers started
        fork.cancel()

        pa.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pb.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pc.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pd.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pe.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pf.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pg.await().shouldBeTypeOf<ExitCase.Cancelled>()
        ph.await().shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parMapN 8 cancels losers if a failure occurs in one of the tasks" {
      checkAll(
        Arb.throwable(),
        Arb.element(listOf(1, 2, 3, 4, 5, 6, 7, 8))
      ) { e, winningTask ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<ExitCase>()
        val pb = CompletableDeferred<ExitCase>()
        val pc = CompletableDeferred<ExitCase>()
        val pd = CompletableDeferred<ExitCase>()
        val pf = CompletableDeferred<ExitCase>()
        val pg = CompletableDeferred<ExitCase>()
        val ph = CompletableDeferred<ExitCase>()

        val winner: suspend CoroutineScope.() -> Int = { repeat(7) { s.send(Unit) }; throw e }
        val loserA: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pa) }
        val loserB: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pb) }
        val loserC: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pc) }
        val loserD: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pd) }
        val loserF: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pf) }
        val loserG: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pg) }
        val loserH: suspend CoroutineScope.() -> Int = { awaitExitCase(s, ph) }

        val r = Either.catch {
          when (winningTask) {
            1 -> parZip(winner, loserA, loserB, loserC, loserD, loserF, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
            2 -> parZip(loserA, winner, loserB, loserC, loserD, loserF, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
            3 -> parZip(loserA, loserB, winner, loserC, loserD, loserF, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
            4 -> parZip(loserA, loserB, loserC, winner, loserD, loserF, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
            5 -> parZip(loserA, loserB, loserC, loserD, winner, loserF, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
            6 -> parZip(loserA, loserB, loserC, loserD, loserF, winner, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
            7 -> parZip(loserA, loserB, loserC, loserD, loserF, loserG, winner, loserH) { _, _, _, _, _, _, _, _ -> }
            else -> parZip(loserA, loserB, loserC, loserD, loserF, loserG, loserH, winner) { _, _, _, _, _, _, _, _ -> }
          }
        }

        pa.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pb.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pc.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pd.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pf.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pg.await().shouldBeTypeOf<ExitCase.Cancelled>()
        ph.await().shouldBeTypeOf<ExitCase.Cancelled>()
        r should leftException(e)
      }
    }

    "parMapN CancellationException on right can cancel rest" {
      checkAll(Arb.string(), Arb.int(1..8)) { msg, cancel ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<ExitCase>()
        val pb = CompletableDeferred<ExitCase>()
        val pc = CompletableDeferred<ExitCase>()
        val pd = CompletableDeferred<ExitCase>()
        val pe = CompletableDeferred<ExitCase>()
        val pf = CompletableDeferred<ExitCase>()
        val pg = CompletableDeferred<ExitCase>()

        val winner: suspend CoroutineScope.() -> Int = { repeat(7) { s.send(Unit) }; throw CancellationException(msg) }
        val loserA: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pa) }
        val loserB: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pb) }
        val loserC: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pc) }
        val loserD: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pd) }
        val loserF: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pe) }
        val loserG: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pf) }
        val loserH: suspend CoroutineScope.() -> Int = { awaitExitCase(s, pg) }

        try {
            when (cancel) {
              1 -> parZip(winner, loserA, loserB, loserC, loserD, loserF, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
              2 -> parZip(loserA, winner, loserB, loserC, loserD, loserF, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
              3 -> parZip(loserA, loserB, winner, loserC, loserD, loserF, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
              4 -> parZip(loserA, loserB, loserC, winner, loserD, loserF, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
              5 -> parZip(loserA, loserB, loserC, loserD, winner, loserF, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
              6 -> parZip(loserA, loserB, loserC, loserD, loserF, winner, loserG, loserH) { _, _, _, _, _, _, _, _ -> }
              7 -> parZip(loserA, loserB, loserC, loserD, loserF, loserG, winner, loserH) { _, _, _, _, _, _, _, _ -> }
              else -> parZip(loserA, loserB, loserC, loserD, loserF, loserG, loserH, winner) { _, _, _, _, _, _, _, _ -> }
            }
        } catch (e: CancellationException) {
          e.message shouldBe msg
        }
        pa.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pb.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pc.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pd.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pe.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pf.await().shouldBeTypeOf<ExitCase.Cancelled>()
        pg.await().shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }
  }
)
