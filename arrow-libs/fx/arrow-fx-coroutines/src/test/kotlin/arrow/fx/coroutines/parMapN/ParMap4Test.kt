package arrow.fx.coroutines.parMapN

import arrow.core.Either
import arrow.core.Tuple4
import arrow.fx.coroutines.ArrowFxSpec
import arrow.fx.coroutines.Atomic
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.NamedThreadFactory
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.leftException
import arrow.fx.coroutines.never
import arrow.fx.coroutines.parZip
import arrow.fx.coroutines.single
import arrow.fx.coroutines.singleThreadName
import arrow.fx.coroutines.suspend
import arrow.fx.coroutines.threadName
import arrow.fx.coroutines.throwable
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class ParMap4Test : ArrowFxSpec(
  spec = {
    "parMapN 4 returns to original context" {
      val mapCtxName = "parMap4"
      val mapCtx = Resource.fromExecutor { Executors.newFixedThreadPool(4, NamedThreadFactory { mapCtxName }) }
      checkAll {
        single.zip(mapCtx).use { (_single, _mapCtx) ->
          withContext(_single) {
            threadName() shouldBe singleThreadName

            val (s1, s2, s3, s4) = parZip(
              _mapCtx, { Thread.currentThread().name }, { Thread.currentThread().name }, { Thread.currentThread().name }, { Thread.currentThread().name }
            ) { a, b, c, d -> Tuple4(a, b, c, d) }

            s1 shouldBe mapCtxName
            s2 shouldBe mapCtxName
            s3 shouldBe mapCtxName
            s4 shouldBe mapCtxName
            threadName() shouldBe singleThreadName
          }
        }
      }
    }

    "parMapN 4 returns to original context on failure" {
      val mapCtxName = "parMap4"
      val mapCtx = Resource.fromExecutor { Executors.newFixedThreadPool(4, NamedThreadFactory { mapCtxName }) }

      checkAll(Arb.int(1..4), Arb.throwable()) { choose, e ->
        single.zip(mapCtx).use { (_single, _mapCtx) ->
          withContext(_single) {
            threadName() shouldBe singleThreadName

            Either.catch {
              when (choose) {
                1 -> parZip(
                  _mapCtx,
                   { e.suspend() },
                   { never<Nothing>() },
                   { never<Nothing>() },
                   { never<Nothing>() }
                ) { _, _, _, _ -> Unit }
                2 -> parZip(
                  _mapCtx,
                   { never<Nothing>() },
                   { e.suspend() },
                   { never<Nothing>() },
                   { never<Nothing>() }
                ) { _, _, _, _ -> Unit }
                3 -> parZip(
                  _mapCtx,
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { e.suspend() },
                  { never<Nothing>() }
                ) { _, _, _, _ -> Unit }
                else -> parZip(
                  _mapCtx,
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { never<Nothing>() },
                  { e.suspend() }
                ) { _, _, _, _ -> Unit }
              }
            } should leftException(e)

            threadName() shouldBe singleThreadName
          }
        }
      }
    }

    "parMapN 4 runs in parallel" {
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
            r.set("$d")
            modifyGate1.complete(Unit)
          }
        ) { _a, _b, _c, _d ->
          Tuple4(_a, _b, _c, _d)
        }

        r.get() shouldBe "$d$c$b$a"
      }
    }

    "parMapN 4 finishes on single thread" {
      checkAll(Arb.string()) {
        single.use { ctx ->
          parZip(ctx, { Thread.currentThread().name }, { Thread.currentThread().name }, { Thread.currentThread().name }, { Thread.currentThread().name }) { a, b, c, d -> Tuple4(a, b, c, d) }
        } shouldBe Tuple4("single", "single", "single", "single")
      }
    }

    "Cancelling parMapN 4 cancels all participants" {
      checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int()) { a, b, c, d ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()
        val pb = CompletableDeferred<Pair<Int, ExitCase>>()
        val pc = CompletableDeferred<Pair<Int, ExitCase>>()
        val pd = CompletableDeferred<Pair<Int, ExitCase>>()

        val loserA: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }
        val loserC: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pc.complete(Pair(c, ex)) } }
        val loserD: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pd.complete(Pair(d, ex)) } }

        val f = async { parZip(loserA, loserB, loserC, loserD) { _a, _b, _c, _d -> Tuple4(_a, _b, _c, _d) } }

        repeat(4) { s.send(Unit) } // Suspend until all racers started
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

        pd.await().let { (res, exit) ->
          res shouldBe d
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
      }
    }

    "parMapN 4 cancels losers if a failure occurs in one of the tasks" {
      checkAll(
        Arb.throwable(),
        Arb.element(listOf(1, 2, 3, 4)),
        Arb.int(),
        Arb.int(),
        Arb.int()
      ) { e, winningTask, a, b, c ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()
        val pb = CompletableDeferred<Pair<Int, ExitCase>>()
        val pc = CompletableDeferred<Pair<Int, ExitCase>>()

        val winner: suspend CoroutineScope.() -> Int = { repeat(3) { s.send(Unit) }; throw e }
        val loserA: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }
        val loserC: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pc.complete(Pair(c, ex)) } }

        val r = Either.catch {
          when (winningTask) {
            1 -> parZip(winner, loserA, loserB, loserC) { _, _, _, _ -> Unit }
            2 -> parZip(loserA, winner, loserB, loserC) { _, _, _, _ -> Unit }
            3 -> parZip(loserA, loserB, winner, loserC) { _, _, _, _ -> Unit }
            else -> parZip(loserA, loserB, loserC, winner) { _, _, _, _ -> Unit }
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
        r should leftException(e)
      }
    }
  }
)
