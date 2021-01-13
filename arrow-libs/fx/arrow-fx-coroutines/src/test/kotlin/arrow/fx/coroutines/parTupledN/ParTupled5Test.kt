package arrow.fx.coroutines.parTupledN

import arrow.core.Either
import arrow.core.Tuple5
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
import arrow.fx.coroutines.parTupledN
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
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class ParTupled5Test : ArrowFxSpec(spec = {

  "parTupledN 5 returns to original context" {
    val mapCtxName = "parTupled5"
    val mapCtx = Resource.fromExecutor { Executors.newFixedThreadPool(5, NamedThreadFactory { mapCtxName }) }

    checkAll {
      single.zip(mapCtx).use { (single, mapCtx) ->
        withContext(single) {
          threadName() shouldBe singleThreadName

          val (s1, s2, s3, s4, s5) = parTupledN(mapCtx, threadName, threadName, threadName, threadName, threadName)

          s1 shouldBe mapCtxName
          s2 shouldBe mapCtxName
          s3 shouldBe mapCtxName
          s4 shouldBe mapCtxName
          s5 shouldBe mapCtxName
          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "parTupledN 5 returns to original context on failure" {
    val mapCtxName = "parTupled5"
    val mapCtx = Resource.fromExecutor { Executors.newFixedThreadPool(5, NamedThreadFactory { mapCtxName }) }

    checkAll(Arb.int(1..5), Arb.throwable()) { choose, e ->
      single.zip(mapCtx).use { (single, mapCtx) ->
        withContext(single) {
          threadName() shouldBe singleThreadName

          Either.catch {
            when (choose) {
              1 -> parTupledN(
                mapCtx,
                { e.suspend() },
                { never<Nothing>() },
                { never<Nothing>() },
                { never<Nothing>() },
                { never<Nothing>() })
              2 -> parTupledN(
                mapCtx,
                { never<Nothing>() },
                { e.suspend() },
                { never<Nothing>() },
                { never<Nothing>() },
                { never<Nothing>() })
              3 -> parTupledN(
                mapCtx,
                { never<Nothing>() },
                { never<Nothing>() },
                { e.suspend() },
                { never<Nothing>() },
                { never<Nothing>() })
              4 -> parTupledN(
                mapCtx,
                { never<Nothing>() },
                { never<Nothing>() },
                { never<Nothing>() },
                { e.suspend() },
                { never<Nothing>() })
              else -> parTupledN(
                mapCtx,
                { never<Nothing>() },
                { never<Nothing>() },
                { never<Nothing>() },
                { never<Nothing>() },
                { e.suspend() })
            }
          } should leftException(e)

          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "ParTupledN 5 runs in parallel" {
    checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { a, b, c, d, e ->
      val r = Atomic("")
      val modifyGate1 = Promise<Unit>()
      val modifyGate2 = Promise<Unit>()
      val modifyGate3 = Promise<Unit>()
      val modifyGate4 = Promise<Unit>()

      parTupledN(
        {
          modifyGate4.get()
          r.update { i -> "$i$a" }
        },
        {
          modifyGate3.get()
          r.update { i -> "$i$b" }
          modifyGate4.complete(Unit)
        },
        {
          modifyGate2.get()
          r.update { i -> "$i$c" }
          modifyGate3.complete(Unit)
        },
        {
          modifyGate1.get()
          r.update { i -> "$i$d" }
          modifyGate2.complete(Unit)
        },
        {
          r.set("$e")
          modifyGate1.complete(Unit)
        }
      )

      r.get() shouldBe "$e$d$c$b$a"
    }
  }

  "ParTupledN 5 finishes on single thread" {
    checkAll(Arb.string()) { name ->
      single.use { ctx ->
        parTupledN(ctx, threadName, threadName, threadName, threadName, threadName)
      } shouldBe Tuple5("single", "single", "single", "single", "single")
    }
  }

  "Cancelling ParTupledN 5 cancels all participants" {
    checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { a, b, c, d, e ->
      val s = Semaphore(0L)
      val pa = Promise<Pair<Int, ExitCase>>()
      val pb = Promise<Pair<Int, ExitCase>>()
      val pc = Promise<Pair<Int, ExitCase>>()
      val pd = Promise<Pair<Int, ExitCase>>()
      val pe = Promise<Pair<Int, ExitCase>>()

      val loserA = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
      val loserB = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }
      val loserC = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pc.complete(Pair(c, ex)) } }
      val loserD = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pd.complete(Pair(d, ex)) } }
      val loserE = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pe.complete(Pair(e, ex)) } }

      val f = ForkAndForget { parTupledN(loserA, loserB, loserC, loserD, loserE) }

      s.acquireN(5) // Suspend until all racers started
      f.cancel()

      pa.get().let { (res, exit) ->
        res shouldBe a
        exit.shouldBeInstanceOf<ExitCase.Cancelled>()
      }
      pb.get().let { (res, exit) ->
        res shouldBe b
        exit.shouldBeInstanceOf<ExitCase.Cancelled>()
      }
      pc.get().let { (res, exit) ->
        res shouldBe c
        exit.shouldBeInstanceOf<ExitCase.Cancelled>()
      }
      pd.get().let { (res, exit) ->
        res shouldBe d
        exit.shouldBeInstanceOf<ExitCase.Cancelled>()
      }
      pe.get().let { (res, exit) ->
        res shouldBe e
        exit.shouldBeInstanceOf<ExitCase.Cancelled>()
      }
    }
  }

  "ParTupledN 5 cancels losers if a failure occurs in one of the tasks" {
    checkAll(
      Arb.throwable(),
      Arb.element(listOf(1, 2, 3, 4, 5)),
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.int()
    ) { e, leftWinner, a, b, c, d ->
      val s = Semaphore(0L)
      val pa = Promise<Pair<Int, ExitCase>>()
      val pb = Promise<Pair<Int, ExitCase>>()
      val pc = Promise<Pair<Int, ExitCase>>()
      val pd = Promise<Pair<Int, ExitCase>>()

      val winner = suspend { s.acquireN(4); throw e }
      val loserA = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
      val loserB = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }
      val loserC = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pc.complete(Pair(c, ex)) } }
      val loserD = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pd.complete(Pair(d, ex)) } }

      val r = Either.catch {
        when (leftWinner) {
          1 -> parTupledN(winner, loserA, loserB, loserC, loserD)
          2 -> parTupledN(loserA, winner, loserB, loserC, loserD)
          3 -> parTupledN(loserA, loserB, winner, loserC, loserD)
          4 -> parTupledN(loserA, loserB, loserC, winner, loserD)
          else -> parTupledN(loserA, loserB, loserC, loserD, winner)
        }
      }

      pa.get().let { (res, exit) ->
        res shouldBe a
        exit.shouldBeInstanceOf<ExitCase.Cancelled>()
      }
      pb.get().let { (res, exit) ->
        res shouldBe b
        exit.shouldBeInstanceOf<ExitCase.Cancelled>()
      }
      pc.get().let { (res, exit) ->
        res shouldBe c
        exit.shouldBeInstanceOf<ExitCase.Cancelled>()
      }
      pd.get().let { (res, exit) ->
        res shouldBe d
        exit.shouldBeInstanceOf<ExitCase.Cancelled>()
      }
      r should leftException(e)
    }
  }
})
