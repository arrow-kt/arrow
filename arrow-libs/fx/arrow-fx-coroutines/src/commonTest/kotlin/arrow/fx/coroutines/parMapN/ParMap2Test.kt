package arrow.fx.coroutines.parMapN

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.core.Either
import arrow.fx.coroutines.ArrowFxSpec
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.awaitExitCase
import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.leftException
import arrow.fx.coroutines.never
import arrow.fx.coroutines.parZip
import arrow.fx.coroutines.throwable
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel

@OptIn(ExperimentalTime::class)
class ParMap2Test : ArrowFxSpec(
  spec = {

    "parMapN 2 runs in parallel" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        val r = Atomic("")
        val modifyGate = CompletableDeferred<Int>()

        parZip(
          {
            modifyGate.await()
            r.update { i -> "$i$a" }
          },
          {
            r.value = "$b"
            modifyGate.complete(0)
          }
        ) { _a, _b ->
          Pair(_a, _b)
        }

        r.value shouldBe "$b$a"
      }
    }

    "Cancelling parMapN 2 cancels all participants" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()
        val pb = CompletableDeferred<Pair<Int, ExitCase>>()

        val loserA: suspend CoroutineScope.() -> Int =
          { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB: suspend CoroutineScope.() -> Int =
          { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }

        val f = async { parZip(loserA, loserB) { _a, _b -> Pair(_a, _b) } }

        s.send(Unit) // Suspend until all racers started
        s.send(Unit)
        f.cancel()

        pa.await().let { (res, exit) ->
          res shouldBe a
          exit.shouldBeTypeOf<ExitCase.Cancelled>()
        }
        pb.await().let { (res, exit) ->
          res shouldBe b
          exit.shouldBeTypeOf<ExitCase.Cancelled>()
        }
      }
    }

    "parMapN 2 cancels losers if a failure occurs in one of the tasks" {
      checkAll(Arb.throwable(), Arb.boolean()) { e, leftWinner ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<ExitCase>()

        val winner: suspend CoroutineScope.() -> Unit = { s.send(Unit); throw e }
        val loserA: suspend CoroutineScope.() -> Int =
          { guaranteeCase({ s.receive(); awaitCancellation() }) { ex -> pa.complete(ex) } }

        val r = Either.catch {
          if (leftWinner) parZip(winner, loserA) { _, _ -> Unit }
          else parZip(loserA, winner) { _, _ -> Unit }
        }

        pa.await().shouldBeTypeOf<ExitCase.Cancelled>()
        r should leftException(e)
      }
    }

    "parMapN CancellationException on right can cancel rest" {
      checkAll(Arb.string()) { msg ->
        val exit = CompletableDeferred<ExitCase>()
        val start = CompletableDeferred<Unit>()
        try {
          parZip<Unit, Unit, Unit>({
            awaitExitCase(start, exit)
          }, {
            start.await()
            throw CancellationException(msg)
          }) { _, _ -> }
        } catch (e: CancellationException) {
          e.message shouldBe msg
        }
        exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }
  }
)
