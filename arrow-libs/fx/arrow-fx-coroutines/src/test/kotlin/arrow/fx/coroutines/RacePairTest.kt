package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class RacePairTest : StringSpec({

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
