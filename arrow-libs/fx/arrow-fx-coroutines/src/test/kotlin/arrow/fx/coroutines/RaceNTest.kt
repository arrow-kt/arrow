package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.identity
import arrow.core.orNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import java.util.concurrent.Executors

class RaceNTest : ArrowFxSpec(spec = {

  "race2 returns to original context" {
    val racerName = "race2"
    val racer = Resource.fromExecutor { Executors.newFixedThreadPool(2, NamedThreadFactory { racerName }) }

    checkAll(Arb.int(1..2)) { choose ->
      single.zip(racer).use { (single, raceCtx) ->
        evalOn(single) {
          threadName() shouldBe singleThreadName

          val racedOn = when (choose) {
            1 -> raceN(raceCtx, { threadName() }, { never<Nothing>() }).swap().orNull()
            else -> raceN(raceCtx, { never<Nothing>() }, { threadName() }).orNull()
          }

          racedOn shouldBe racerName
          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "race2 returns to original context on failure" {
    val racerName = "race2"
    val racer = Resource.fromExecutor { Executors.newFixedThreadPool(2, NamedThreadFactory { racerName }) }

    checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
      single.zip(racer).use { (single, raceCtx) ->
        evalOn(single) {
          threadName() shouldBe singleThreadName

          Either.catch {
            when (choose) {
              1 -> raceN(raceCtx, { e.suspend() }, { never<Nothing>() }).swap().orNull()
              else -> raceN(raceCtx, { never<Nothing>() }, { e.suspend() }).orNull()
            }
          } shouldBe Either.Left(e)

          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "race2 can join first" {
    checkAll(Arb.int()) { i ->
      raceN({ i }, { never<Unit>() }) shouldBe Either.Left(i)
    }
  }

  "race2 can join second" {
    checkAll(Arb.int()) { i ->
      raceN({ never<Unit>() }, { i }) shouldBe Either.Right(i)
    }
  }

  "first racer out of 2 always wins on a single thread" {
    single.use { ctx ->
      raceN(ctx, threadName, threadName)
    } shouldBe Either.Left("single")
  }

  "Cancelling race 2 cancels all participants" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      val s = Semaphore(0L)
      val pa = Promise<Pair<Int, ExitCase>>()
      val pb = Promise<Pair<Int, ExitCase>>()

      val loserA = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
      val loserB = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }

      val f = ForkAndForget { raceN(loserA, loserB) }

      s.acquireN(2) // Suspend until all racers started
      f.cancel()

      pa.get() shouldBe Pair(a, ExitCase.Cancelled)
      pb.get() shouldBe Pair(b, ExitCase.Cancelled)
    }
  }

  "race 2 cancels losers with first success or failure determining winner" {
    checkAll(
      Arb.either(Arb.throwable(), Arb.int()),
      Arb.bool(),
      Arb.int()
    ) { eith, leftWinner, a ->
      val s = Semaphore(0L)
      val pa = Promise<Pair<Int, ExitCase>>()

      val winner = suspend { s.acquire(); eith.rethrow() }
      val loserA = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }

      Either.catch {
        if (leftWinner) raceN(winner, loserA)
        else raceN(loserA, winner)
      }

      pa.get() shouldBe Pair(a, ExitCase.Cancelled)
    }
  }

  "race3 returns to original context" {
    val racerName = "race3"
    val racer = Resource.fromExecutor { Executors.newFixedThreadPool(3, NamedThreadFactory { racerName }) }

    checkAll(Arb.int(1..3)) { choose ->
      single.zip(racer).use { (single, raceCtx) ->
        evalOn(single) {
          threadName() shouldBe singleThreadName

          val racedOn = when (choose) {
            1 -> raceN(raceCtx, { threadName() }, { never<Nothing>() }, { never<Nothing>() })
              .fold(::identity, { null }, { null })
            2 -> raceN(raceCtx, { never<Nothing>() }, { threadName() }, { never<Nothing>() })
              .fold({ null }, ::identity, { null })
            else -> raceN(raceCtx, { never<Nothing>() }, { never<Nothing>() }, { threadName() })
              .fold({ null }, { null }, ::identity)
          }

          racedOn shouldBe racerName
          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "race3 returns to original context on failure" {
    val racerName = "race3"
    val racer = Resource.fromExecutor { Executors.newFixedThreadPool(3, NamedThreadFactory { racerName }) }

    checkAll(Arb.int(1..3), Arb.throwable()) { choose, e ->
      single.zip(racer).use { (single, raceCtx) ->
        evalOn(single) {
          threadName() shouldBe singleThreadName

          Either.catch {
            when (choose) {
              1 -> raceN(raceCtx, { e.suspend() }, { never<Nothing>() }, { never<Nothing>() })
                .fold(::identity, { null }, { null })
              2 -> raceN(raceCtx, { never<Nothing>() }, { e.suspend() }, { never<Nothing>() })
                .fold({ null }, ::identity, { null })
              else -> raceN(raceCtx, { never<Nothing>() }, { never<Nothing>() }, { e.suspend() })
                .fold({ null }, { null }, ::identity)
            }
          } shouldBe Either.Left(e)

          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "race3 can join first" {
    checkAll(Arb.int()) { i ->
      raceN({ i }, { never<Unit>() }, { never<Unit>() }) shouldBe Race3.First(i)
    }
  }

  "race3 can join second" {
    checkAll(Arb.int()) { i ->
      raceN({ never<Unit>() }, { i }, { never<Unit>() }) shouldBe Race3.Second(i)
    }
  }

  "race3 can join third" {
    checkAll(Arb.int()) { i ->
      raceN({ never<Unit>() }, { never<Unit>() }, { i }) shouldBe Race3.Third(i)
    }
  }

  "first racer out of 3 always wins on a single thread" {
    single.use { ctx ->
      raceN(ctx, threadName, threadName, threadName)
    } shouldBe Race3.First("single")
  }

  "Cancelling race 3 cancels all participants" {
    checkAll(Arb.int(), Arb.int(), Arb.int()) { a, b, c ->
      val s = Semaphore(0L)
      val pa = Promise<Pair<Int, ExitCase>>()
      val pb = Promise<Pair<Int, ExitCase>>()
      val pc = Promise<Pair<Int, ExitCase>>()

      val loserA = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
      val loserB = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }
      val loserC = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pc.complete(Pair(c, ex)) } }

      val f = ForkAndForget { raceN(loserA, loserB, loserC) }

      s.acquireN(3) // Suspend until all racers started
      f.cancel()

      pa.get() shouldBe Pair(a, ExitCase.Cancelled)
      pb.get() shouldBe Pair(b, ExitCase.Cancelled)
      pc.get() shouldBe Pair(c, ExitCase.Cancelled)
    }
  }

  "race 3 cancels losers with first success or failure determining winner" {
    checkAll(
      Arb.either(Arb.throwable(), Arb.int()),
      Arb.element(listOf(1, 2, 3)),
      Arb.int(),
      Arb.int()
    ) { eith, leftWinner, a, b ->
      val s = Semaphore(0L)
      val pa = Promise<Pair<Int, ExitCase>>()
      val pb = Promise<Pair<Int, ExitCase>>()

      val winner = suspend { s.acquireN(2); eith.rethrow() }
      val loserA = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
      val loserB = suspend { guaranteeCase({ s.release(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }

      Either.catch {
        when (leftWinner) {
          1 -> raceN(winner, loserA, loserB)
          2 -> raceN(loserA, winner, loserB)
          else -> raceN(loserA, loserB, winner)
        }
      }

      pa.get() shouldBe Pair(a, ExitCase.Cancelled)
      pb.get() shouldBe Pair(b, ExitCase.Cancelled)
    }
  }
})
