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
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel

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
            r.set("$b")
            modifyGate.complete(0)
          }
        ) { _a, _b ->
          Pair(_a, _b)
        }

        r.get() shouldBe "$b$a"
      }
    }

    "Cancelling parMapN 2 cancels all participants" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()
        val pb = CompletableDeferred<Pair<Int, ExitCase>>()

        val loserA: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }

        val f = async { parZip(loserA, loserB) { _a, _b -> Pair(_a, _b) } }

        s.send(Unit) // Suspend until all racers started
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
      }
    }

    "parMapN 2 cancels losers if a failure occurs in one of the tasks" {
      checkAll(
        Arb.throwable(),
        Arb.boolean(),
        Arb.int()
      ) { e, leftWinner, a ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()

        val winner: suspend CoroutineScope.() -> Unit = { s.send(Unit); throw e }
        val loserA: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }

        val r = Either.catch {
          if (leftWinner) parZip(winner, loserA) { _, _ -> Unit }
          else parZip(loserA, winner) { _, _ -> Unit }
        }

        pa.await().let { (res, exit) ->
          res shouldBe a
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        r should leftException(e)
      }
    }
  }
)
