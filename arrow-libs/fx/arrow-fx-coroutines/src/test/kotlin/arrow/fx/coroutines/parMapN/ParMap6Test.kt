package arrow.fx.coroutines.parMapN

import arrow.core.Either
import arrow.core.Tuple6
import arrow.fx.coroutines.ArrowFxSpec
import arrow.fx.coroutines.Atomic
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ForkAndForget
import arrow.fx.coroutines.NamedThreadFactory
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.Semaphore
import arrow.fx.coroutines.evalOn
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
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import java.util.concurrent.Executors

class ParMap6Test : ArrowFxSpec(spec = {
  "parMapN 6 returns to original context" {
    val mapCtxName = "parMap6"
    val mapCtx = Resource.fromExecutor { Executors.newFixedThreadPool(6, NamedThreadFactory { mapCtxName }) }
    checkAll {
      single.zip(mapCtx).use { (_single, _mapCtx) ->
        evalOn(_single) {
          threadName() shouldBe singleThreadName

          val (s1, s2, s3, s4, s5, s6) = parMapN(
            _mapCtx, threadName, threadName, threadName, threadName, threadName, threadName) { a, b, c, d, e, f ->
            Tuple6(a, b, c, d, e, f)
          }

          s1 shouldBe mapCtxName
          s2 shouldBe mapCtxName
          s3 shouldBe mapCtxName
          s4 shouldBe mapCtxName
          s5 shouldBe mapCtxName
          s6 shouldBe mapCtxName
          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "parMapN 6 returns to original context on failure" {
    val mapCtxName = "parMap6"
    val mapCtx = Resource.fromExecutor { Executors.newFixedThreadPool(6, NamedThreadFactory { mapCtxName }) }

    checkAll(Arb.int(1..6), Arb.throwable()) { choose, e ->
      single.zip(mapCtx).use { (_single, _mapCtx) ->
        evalOn(_single) {
          threadName() shouldBe singleThreadName

          Either.catch {
            when (choose) {
              1 -> parMapN(
                _mapCtx,
                suspend { e.suspend() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() }) { _, _, _, _, _, _ -> Unit }
              2 -> parMapN(
                _mapCtx,
                suspend { never<Nothing>() },
                suspend { e.suspend() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() }) { _, _, _, _, _, _ -> Unit }
              3 -> parMapN(
                _mapCtx,
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { e.suspend() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() }) { _, _, _, _, _, _ -> Unit }
              4 -> parMapN(
                _mapCtx,
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { e.suspend() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() }) { _, _, _, _, _, _ -> Unit }
              5 -> parMapN(
                _mapCtx,
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { e.suspend() },
                suspend { never<Nothing>() }) { _, _, _, _, _, _ -> Unit }
              else -> parMapN(
                _mapCtx,
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { never<Nothing>() },
                suspend { e.suspend() }) { _, _, _, _, _, _ -> Unit }
            }
          } should leftException(e)
          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "parMapN 6 runs in parallel" {
    checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { a, b, c, d, e, f ->
      val r = Atomic("")
      val modifyGate1 = Promise<Unit>()
      val modifyGate2 = Promise<Unit>()
      val modifyGate3 = Promise<Unit>()
      val modifyGate4 = Promise<Unit>()
      val modifyGate5 = Promise<Unit>()

      parMapN(
        {
          modifyGate2.get()
          r.update { i -> "$i$a" }
        },
        {
          modifyGate3.get()
          r.update { i -> "$i$b" }
          modifyGate2.complete(Unit)
        },
        {
          modifyGate4.get()
          r.update { i -> "$i$c" }
          modifyGate3.complete(Unit)
        },
        {
          modifyGate5.get()
          r.update { i -> "$i$d" }
          modifyGate4.complete(Unit)
        },
        {
          modifyGate1.get()
          r.update { i -> "$i$e" }
          modifyGate5.complete(Unit)
        },
        {
          r.set("$f")
          modifyGate1.complete(Unit)
        }
      ) { _a, _b, _c, _d, _e, _f ->
        Tuple6(_a, _b, _c, _d, _e, _f)
      }

      r.get() shouldBe "$f$e$d$c$b$a"
    }
  }

  "parMapN 6 finishes on single thread" {
    checkAll(Arb.string()) {
      single.use { ctx ->
        parMapN(ctx, threadName, threadName, threadName, threadName, threadName, threadName) { a, b, c, d, e, f ->
          Tuple6(a, b, c, d, e, f)
        }
      } shouldBe Tuple6("single", "single", "single", "single", "single", "single")
    }
  }

  "Cancelling parMapN 6 cancels all participants" {
    checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int(), Arb.int()) { a, b, c, d, e, f ->
      val s = Semaphore(0L)
      val pa = Promise<Pair<Int, ExitCase>>()
      val pb = Promise<Pair<Int, ExitCase>>()
      val pc = Promise<Pair<Int, ExitCase>>()
      val pd = Promise<Pair<Int, ExitCase>>()
      val pe = Promise<Pair<Int, ExitCase>>()
      val pf = Promise<Pair<Int, ExitCase>>()

      val loserA = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
      val loserB = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }
      val loserC = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pc.complete(Pair(c, ex)) } }
      val loserD = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pd.complete(Pair(d, ex)) } }
      val loserE = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pe.complete(Pair(e, ex)) } }
      val loserF = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pf.complete(Pair(f, ex)) } }

      val fork = ForkAndForget {
        parMapN(loserA, loserB, loserC, loserD, loserE, loserF) { _a, _b, _c, _d, _e, _f ->
          Tuple6(_a, _b, _c, _d, _e, _f)
        }
      }

      s.acquireN(6) // Suspend until all racers started
      fork.cancel()

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
      pf.get().let { (res, exit) ->
        res shouldBe f
        exit.shouldBeInstanceOf<ExitCase.Cancelled>()
      }
    }
  }

  "parMapN 6 cancels losers if a failure occurs in one of the tasks" {
    checkAll(
      Arb.throwable(),
      Arb.element(listOf(1, 2, 3, 4, 5, 6))
    ) { e, winningTask ->

      val intGen = Arb.int()
      val a = intGen.next()
      val b = intGen.next()
      val c = intGen.next()
      val d = intGen.next()
      val f = intGen.next()

      val s = Semaphore(0L)
      val pa = Promise<Pair<Int, ExitCase>>()
      val pb = Promise<Pair<Int, ExitCase>>()
      val pc = Promise<Pair<Int, ExitCase>>()
      val pd = Promise<Pair<Int, ExitCase>>()
      val pf = Promise<Pair<Int, ExitCase>>()

      val winner = suspend { s.acquireN(5); throw e }
      val loserA = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
      val loserB = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }
      val loserC = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pc.complete(Pair(c, ex)) } }
      val loserD = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pd.complete(Pair(d, ex)) } }
      val loserF = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pf.complete(Pair(f, ex)) } }

      val r = Either.catch {
        when (winningTask) {
          1 -> parMapN(winner, loserA, loserB, loserC, loserD, loserF) { _, _, _, _, _, _ -> Unit }
          2 -> parMapN(loserA, winner, loserB, loserC, loserD, loserF) { _, _, _, _, _, _ -> Unit }
          3 -> parMapN(loserA, loserB, winner, loserC, loserD, loserF) { _, _, _, _, _, _ -> Unit }
          4 -> parMapN(loserA, loserB, loserC, winner, loserD, loserF) { _, _, _, _, _, _ -> Unit }
          5 -> parMapN(loserA, loserB, loserC, loserD, winner, loserF) { _, _, _, _, _, _ -> Unit }
          else -> parMapN(loserA, loserB, loserC, loserD, loserF, winner) { _, _, _, _, _, _ -> Unit }
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
      pf.get().let { (res, exit) ->
        res shouldBe f
        exit.shouldBeInstanceOf<ExitCase.Cancelled>()
      }
      r should leftException(e)
    }
  }
})
