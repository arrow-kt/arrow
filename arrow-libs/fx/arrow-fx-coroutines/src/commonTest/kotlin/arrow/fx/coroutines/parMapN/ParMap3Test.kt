package arrow.fx.coroutines.parMapN

import arrow.core.Either
import arrow.fx.coroutines.ArrowFxSpec
import arrow.fx.coroutines.Atomic
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.leftException
import arrow.fx.coroutines.never
import arrow.fx.coroutines.parZip
import arrow.fx.coroutines.throwable
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CoroutineScope

class ParMap3Test : ArrowFxSpec(
  spec = {
    "parMapN 3 runs in parallel" {
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
            r.set("$c")
            modifyGate1.complete(Unit)
          }
        ) { _a, _b, _c ->
          Triple(_a, _b, _c)
        }

        r.get() shouldBe "$c$b$a"
      }
    }

    "Cancelling parMapN 3 cancels all participants" {
      checkAll(Arb.int(), Arb.int(), Arb.int()) { a, b, c ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()
        val pb = CompletableDeferred<Pair<Int, ExitCase>>()
        val pc = CompletableDeferred<Pair<Int, ExitCase>>()

        val loserA: suspend CoroutineScope.() -> Int =
          { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB: suspend CoroutineScope.() -> Int =
          { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }
        val loserC: suspend CoroutineScope.() -> Int =
          { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pc.complete(Pair(c, ex)) } }

        val f = async { parZip(loserA, loserB, loserC) { _a, _b, _c -> Triple(_a, _b, _c) } }

        s.send(Unit) // Suspend until all racers started
        s.send(Unit)
        s.send(Unit)
        f.cancel()

        pa.await().let { (res, exit) ->
          res shouldBe a
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        pb.await().let { (res, exit) ->
          res shouldBe b
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        pc.await().let { (res, exit) ->
          res shouldBe c
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
      }
    }

    "parMapN 3 cancels losers if a failure occurs in one of the tasks" {
      checkAll(
        Arb.throwable(),
        Arb.element(listOf(1, 2, 3)),
        Arb.int(),
        Arb.int()
      ) { e, winningTask, a, b ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()
        val pb = CompletableDeferred<Pair<Int, ExitCase>>()

        val winner: suspend CoroutineScope.() -> Int = { s.send(Unit); s.send(Unit); throw e }
        val loserA: suspend CoroutineScope.() -> Int =
          { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB: suspend CoroutineScope.() -> Int =
          { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }

        val r = Either.catch {
          when (winningTask) {
            1 -> parZip(winner, loserA, loserB) { _, _, _ -> Unit }
            2 -> parZip(loserA, winner, loserB) { _, _, _ -> Unit }
            else -> parZip(loserA, loserB, winner) { _, _, _ -> Unit }
          }
        }

        pa.await().let { (res, exit) ->
          res shouldBe a
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        pb.await().let { (res, exit) ->
          res shouldBe b
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        r should leftException(e)
      }
    }
  }
)
