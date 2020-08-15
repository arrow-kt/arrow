package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import java.util.concurrent.Executors

class RacePairTest : ArrowFxSpec(spec = {

  "race pair returns to original context" {
    val racerName = "racePair"
    val racer = Resource.fromExecutor { Executors.newFixedThreadPool(2, NamedThreadFactory { racerName }) }

    checkAll(Arb.int(1..2)) { choose ->
      single.zip(racer).use { (single, raceCtx) ->
        evalOn(single) {
          threadName() shouldBe singleThreadName

          val racedOn = when (choose) {
            1 -> racePair(raceCtx, { threadName() }, { never<Nothing>() })
              .fold({ a, _ -> a }, { _, _ -> null })
            else -> racePair(raceCtx, { never<Nothing>() }, { threadName() })
              .fold({ _, _ -> null }, { _, b -> b })
          }

          racedOn shouldBe racerName
          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "race pair returns to original context on failure" {
    val racerName = "racePair"
    val racer = Resource.fromExecutor { Executors.newFixedThreadPool(2, NamedThreadFactory { racerName }) }

    checkAll(Arb.int(1..2), Arb.throwable()) { choose, e ->
      single.zip(racer).use { (single, raceCtx) ->
        evalOn(single) {
          threadName() shouldBe singleThreadName

          Either.catch {
            when (choose) {
              1 -> racePair(raceCtx, { e.suspend() }, { never<Nothing>() })
                .fold({ a, _ -> a }, { _, _ -> null })
              else -> racePair(raceCtx, { never<Nothing>() }, { e.suspend() })
                .fold({ _, _ -> null }, { _, b -> b })
            }
          } shouldBe Either.Left(e)

          threadName() shouldBe singleThreadName
        }
      }
    }
  }

  "race pair mirrors left winner" {
    checkAll(Arb.either(Arb.throwable(), Arb.int())) { fa ->

      Either.catch {
        racePair({ fa.rethrow() }, { never<Unit>() })
          .fold(Pair<Int, Fiber<Unit>>::first) { fail("never can not win race") }
      } shouldBe fa
    }
  }

  "race pair mirrors right winner" {
    checkAll(Arb.either(Arb.throwable(), Arb.int())) { fa ->

      Either.catch {
        racePair({ never<Unit>() }, { fa.rethrow() })
          .fold({ fail("never can not win race") }, Pair<Fiber<Unit>, Int>::second)
      } shouldBe fa
    }
  }

  "race pair can cancel loser" {
    checkAll(
      Arb.either(Arb.throwable(), Arb.int()),
      Arb.bool(),
      Arb.int()
    ) { fa, leftWinner, i ->
      val s = Semaphore(0)
      val p = Promise<Int>()
      val winner = suspend { s.acquire(); fa.rethrow() }
      val loser = suspend { bracket(acquire = { s.release() }, use = { never<String>() }, release = { p.complete(i) }) }

      Either.catch {
        if (leftWinner) racePair(winner, loser)
        else racePair(loser, winner)
      }.fold({ p.get() }, {
        it.fold(
          { (_, fiberB) -> ForkConnected { fiberB.cancel() }; p.get() },
          { (fiberA, _) -> ForkConnected { fiberA.cancel() }; p.get() }
        )
      }) shouldBe i
    }
  }

  "race pair can join left" {
    checkAll(Arb.int()) { i ->
      val p = Promise<Int>()
      racePair({ p.get() }, { Unit })
        .fold(
          { fail("promise.get cannot win race") },
          { (fiber, _) -> p.complete(i); fiber.join() }
        ) shouldBe i
    }
  }

  "race pair can join right" {
    checkAll(Arb.int()) { i ->
      val p = Promise<Int>()
      racePair({ Unit }, { p.get() })
        .fold(
          { (_, fiber) -> p.complete(i); fiber.join() },
          { fail("promise.get cannot win race") }
        ) shouldBe i
    }
  }

  "cancelling race pair cancels both" {
    checkAll(Arb.int(), Arb.int()) { a, b ->
      val s = Semaphore(0)
      val pa = Promise<Int>()
      val pb = Promise<Int>()

      val loserA = suspend { bracket({ s.release() }, use = { never<Int>() }, release = { pa.complete(a) }) }
      val loserB = suspend { bracket({ s.release() }, use = { never<Int>() }, release = { pb.complete(b) }) }

      val fiber = ForkAndForget { racePair(loserA, loserB) }

      s.acquireN(2) // Wait until both racers started

      fiber.cancel()

      pa.get() shouldBe a
      pb.get() shouldBe b
    }
  }
})
