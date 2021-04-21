package arrow.fx.coroutines.parMapN

import arrow.core.Either
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
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class ParMap2Test : ArrowFxSpec(
  spec = {
    "parMapN 2 returns to original context" {
      val mapCtxName = "parMap2"
      val mapCtx = Resource.fromExecutor { Executors.newFixedThreadPool(2, NamedThreadFactory { mapCtxName }) }

      checkAll {
        single.zip(mapCtx).use { (_single, _mapCtx) ->
          withContext(_single) {
            threadName() shouldBe singleThreadName

            val (s1, s2) = parZip(_mapCtx, { Thread.currentThread().name }, { Thread.currentThread().name }) { a, b -> Pair(a, b) }

            s1 shouldBe mapCtxName
            s2 shouldBe mapCtxName
            threadName() shouldBe singleThreadName
          }
        }
      }
    }

    "parMapN 2 returns to original context on failure" {
      val mapCtxName = "parMap2"
      val mapCtx = Resource.fromExecutor { Executors.newFixedThreadPool(2, NamedThreadFactory { mapCtxName }) }

      checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
        single.zip(mapCtx).use { (_single, _mapCtx) ->
          withContext(_single) {
            threadName() shouldBe singleThreadName

            Either.catch {
              when (choose) {
                1 -> parZip(_mapCtx, { e.suspend() }, { never<Nothing>() }) { _, _ -> Unit }
                else -> parZip(_mapCtx, { never<Nothing>() }, { e.suspend() }) { _, _ -> Unit }
              }
            } should leftException(e)

            threadName() shouldBe singleThreadName
          }
        }
      }
    }

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

    "parMapN 2 finishes on single thread" {
      checkAll(Arb.string()) {
        single.use { ctx ->
          parZip(ctx, { Thread.currentThread().name }, { Thread.currentThread().name }) { a, b -> Pair(a, b) }
        } shouldBe Pair("single", "single")
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
        Arb.bool(),
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
