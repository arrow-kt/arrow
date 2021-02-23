package arrow.fx.coroutines.parMapN

import arrow.core.Either
import arrow.fx.coroutines.ArrowFxSpec
import arrow.fx.coroutines.Atomic
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ForkAndForget
import arrow.fx.coroutines.NamedThreadFactory
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.Semaphore

import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.leftException
import arrow.fx.coroutines.never
import arrow.fx.coroutines.parMapN
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

            val (s1, s2) = parMapN(_mapCtx, threadName, threadName) { a, b -> Pair(a, b) }

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
                1 -> parMapN(_mapCtx, suspend { e.suspend() }, suspend { never<Nothing>() }) { _, _ -> Unit }
                else -> parMapN(_mapCtx, suspend { never<Nothing>() }, suspend { e.suspend() }) { _, _ -> Unit }
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
        val modifyGate = Promise<Int>()

        parMapN(
          suspend {
            modifyGate.get()
            r.update { i -> "$i$a" }
          },
          suspend {
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
          parMapN(ctx, threadName, threadName) { a, b -> Pair(a, b) }
        } shouldBe Pair("single", "single")
      }
    }

    "Cancelling parMapN 2 cancels all participants" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        val s = Semaphore(0L)
        val pa = Promise<Pair<Int, ExitCase>>()
        val pb = Promise<Pair<Int, ExitCase>>()

        val loserA = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }

        val f = ForkAndForget { parMapN(loserA, loserB) { _a, _b -> Pair(_a, _b) } }

        s.acquireN(2) // Suspend until all racers started
        f.cancel()

        pa.get().let { (res, exit) ->
          res shouldBe a
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        pb.get().let { (res, exit) ->
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
        val s = Semaphore(0L)
        val pa = Promise<Pair<Int, ExitCase>>()

        val winner = suspend { s.acquire(); throw e }
        val loserA = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }

        val r = Either.catch {
          if (leftWinner) parMapN(winner, loserA) { _, _ -> Unit }
          else parMapN(loserA, winner) { _, _ -> Unit }
        }

        pa.get().let { (res, exit) ->
          res shouldBe a
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        r should leftException(e)
      }
    }
  }
)
