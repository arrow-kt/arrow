package arrow.fx.coroutines.parMapN

import arrow.core.Either
import arrow.core.Tuple7
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
import io.kotest.property.arbitrary.next
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CoroutineScope

class ParMap7Test : ArrowFxSpec(
  spec = {
    "parMapN 7 runs in parallel" {
      checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { a, b, c, d, e, f, g ->
        val r = Atomic("")
        val modifyGate1 = CompletableDeferred<Unit>()
        val modifyGate2 = CompletableDeferred<Unit>()
        val modifyGate3 = CompletableDeferred<Unit>()
        val modifyGate4 = CompletableDeferred<Unit>()
        val modifyGate5 = CompletableDeferred<Unit>()
        val modifyGate6 = CompletableDeferred<Unit>()

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
            modifyGate1.await()
            r.update { i -> "$i$f" }
            modifyGate6.complete(Unit)
          },
          {
            r.set("$g")
            modifyGate1.complete(Unit)
          }
        ) { _a, _b, _c, _d, _e, _f, _g ->
          Tuple7(_a, _b, _c, _d, _e, _f, _g)
        }

        r.get() shouldBe "$g$f$e$d$c$b$a"
      }
    }

    "Cancelling parMapN 7 cancels all participants" {
      checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { a, b, c, d, e, f, g ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()
        val pb = CompletableDeferred<Pair<Int, ExitCase>>()
        val pc = CompletableDeferred<Pair<Int, ExitCase>>()
        val pd = CompletableDeferred<Pair<Int, ExitCase>>()
        val pe = CompletableDeferred<Pair<Int, ExitCase>>()
        val pf = CompletableDeferred<Pair<Int, ExitCase>>()
        val pg = CompletableDeferred<Pair<Int, ExitCase>>()

        val loserA: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }
        val loserC: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pc.complete(Pair(c, ex)) } }
        val loserD: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pd.complete(Pair(d, ex)) } }
        val loserE: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pe.complete(Pair(e, ex)) } }
        val loserF: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pf.complete(Pair(f, ex)) } }
        val loserG: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pg.complete(Pair(g, ex)) } }

        val fork = async {
          parZip(loserA, loserB, loserC, loserD, loserE, loserF, loserG) { a, b, c, d, e, f, g ->
            Tuple7(a, b, c, d, e, f, g)
          }
        }

        repeat(7) { s.send(Unit) } // Suspend until all racers started
        fork.cancel()

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
        pd.await().let { (res, exit) ->
          res shouldBe d
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        pe.await().let { (res, exit) ->
          res shouldBe e
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        pf.await().let { (res, exit) ->
          res shouldBe f
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        pg.await().let { (res, exit) ->
          res shouldBe g
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
      }
    }

    "parMapN 7 cancels losers if a failure occurs in one of the tasks" {
      checkAll(
        Arb.throwable(),
        Arb.element(listOf(1, 2, 3, 4, 5, 6, 7))
      ) { e, winningTask ->

        val intGen = Arb.int()
        val a = intGen.next()
        val b = intGen.next()
        val c = intGen.next()
        val d = intGen.next()
        val f = intGen.next()
        val g = intGen.next()

        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()
        val pb = CompletableDeferred<Pair<Int, ExitCase>>()
        val pc = CompletableDeferred<Pair<Int, ExitCase>>()
        val pd = CompletableDeferred<Pair<Int, ExitCase>>()
        val pf = CompletableDeferred<Pair<Int, ExitCase>>()
        val pg = CompletableDeferred<Pair<Int, ExitCase>>()

        val winner: suspend CoroutineScope.() -> Int = { repeat(6) { s.send(Unit) }; throw e }
        val loserA: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }
        val loserC: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pc.complete(Pair(c, ex)) } }
        val loserD: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pd.complete(Pair(d, ex)) } }
        val loserF: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pf.complete(Pair(f, ex)) } }
        val loserG: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pg.complete(Pair(g, ex)) } }

        val r = Either.catch {
          when (winningTask) {
            1 -> parZip(winner, loserA, loserB, loserC, loserD, loserF, loserG) { _, _, _, _, _, _, _ -> Unit }
            2 -> parZip(loserA, winner, loserB, loserC, loserD, loserF, loserG) { _, _, _, _, _, _, _ -> Unit }
            3 -> parZip(loserA, loserB, winner, loserC, loserD, loserF, loserG) { _, _, _, _, _, _, _ -> Unit }
            4 -> parZip(loserA, loserB, loserC, winner, loserD, loserF, loserG) { _, _, _, _, _, _, _ -> Unit }
            5 -> parZip(loserA, loserB, loserC, loserD, winner, loserF, loserG) { _, _, _, _, _, _, _ -> Unit }
            6 -> parZip(loserA, loserB, loserC, loserD, loserF, winner, loserG) { _, _, _, _, _, _, _ -> Unit }
            else -> parZip(loserA, loserB, loserC, loserD, loserF, loserG, winner) { _, _, _, _, _, _, _ -> Unit }
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
        pc.await().let { (res, exit) ->
          res shouldBe c
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        pd.await().let { (res, exit) ->
          res shouldBe d
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        pf.await().let { (res, exit) ->
          res shouldBe f
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        pg.await().let { (res, exit) ->
          res shouldBe g
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        r should leftException(e)
      }
    }
  }
)
