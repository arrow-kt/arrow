package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class RaceTripleTest : ArrowFxSpec(spec = {

  "race triple mirrors left winner" {
    checkAll(Arb.either(Arb.throwable(), Arb.int())) { fa ->

      Either.catch {
        raceTriple({ fa.rethrow() }, { never<Unit>() }, { never<Unit>() })
          .fold(
            { a, _, _ -> a },
            { _, _, _ -> fail("never can not win race") },
            { _, _, _ -> fail("never can not win race") }
          )
      } shouldBe fa
    }
  }

  "race triple mirrors middle winner" {
    checkAll(Arb.either(Arb.throwable(), Arb.int())) { fa ->

      Either.catch {
        raceTriple({ never<Unit>() }, { fa.rethrow() }, { never<Unit>() })
          .fold(
            { _, _, _ -> fail("never can not win race") },
            { _, b, _ -> b },
            { _, _, _ -> fail("never can not win race") }
          )
      } shouldBe fa
    }
  }

  "race triple mirrors right winner" {
    checkAll(Arb.either(Arb.throwable(), Arb.int())) { fa ->

      Either.catch {
        raceTriple({ never<Unit>() }, { never<Unit>() }, { fa.rethrow() })
          .fold(
            { _, _, _ -> fail("never can not win race") },
            { _, _, _ -> fail("never can not win race") },
            { _, _, b -> b }
          )
      } shouldBe fa
    }
  }

  "race triple can cancel loser" {
    checkAll(
      Arb.either(Arb.throwable(), Arb.int()),
      Arb.int(0, 2),
      Arb.int(),
      Arb.int()
    ) { fa, decision, a, b ->
      val s = Semaphore(0)
      val pa = Promise<Int>()
      val pb = Promise<Int>()

      val winner = suspend { s.acquireN(2); fa.rethrow() }
      val loserA =
        suspend { bracket(acquire = { s.release() }, use = { never<String>() }, release = { pa.complete(a) }) }
      val loserB =
        suspend { bracket(acquire = { s.release() }, use = { never<String>() }, release = { pb.complete(b) }) }

      Either.catch {
        when (decision) {
          0 -> raceTriple(winner, loserA, loserB)
          1 -> raceTriple(loserA, winner, loserB)
          else -> raceTriple(loserA, loserB, winner)
        }
      }.fold(
        { Pair(pa.get(), pb.get()) },
        {
          it.fold(
            { _, fiberB, fiberC -> ForkAndForget { fiberB.cancel(); fiberC.cancel() }; Pair(pa.get(), pb.get()) },
            { fiberA, _, fiberC -> ForkAndForget { fiberA.cancel(); fiberC.cancel() }; Pair(pa.get(), pb.get()) },
            { fiberA, fiberB, _ -> ForkAndForget { fiberA.cancel(); fiberB.cancel() }; Pair(pa.get(), pb.get()) }
          )
        }) shouldBe Pair(a, b)
    }
  }

  "race triple can join left" {
    checkAll(Arb.int()) { i ->
      val p = Promise<Int>()
      raceTriple({ p.get() }, { Unit }, { Unit })
        .fold(
          { _, _, _ -> fail("promise.get cannot win race") },
          { fiber, _, _ -> p.complete(i); fiber.join() },
          { fiber, _, _ -> p.complete(i); fiber.join() }
        ) shouldBe i
    }
  }

  "race triple can join middle" {
    checkAll(Arb.int()) { i ->
      val p = Promise<Int>()
      raceTriple({ Unit }, { p.get() }, { Unit })
        .fold(
          { _, fiber, _ -> p.complete(i); fiber.join() },
          { _, _, _ -> fail("promise.get cannot win race") },
          { _, fiber, _ -> p.complete(i); fiber.join() }
        ) shouldBe i
    }
  }

  "race triple can join right" {
    checkAll(Arb.int()) { i ->
      val p = Promise<Int>()
      raceTriple({ Unit }, { Unit }, { p.get() })
        .fold(
          { _, _, fiber -> p.complete(i); fiber.join() },
          { _, _, fiber -> p.complete(i); fiber.join() },
          { _, _, _ -> fail("promise.get cannot win race") }
        ) shouldBe i
    }
  }

  "Cancelling race triple cancels both" {
    checkAll(Arb.int(), Arb.int(), Arb.int()) { a, b, c ->
      val s = Semaphore(0)
      val pa = Promise<Int>()
      val pb = Promise<Int>()
      val pc = Promise<Int>()

      val loserA = suspend { bracket({ s.release() }, use = { never<Int>() }, release = { pa.complete(a) }) }
      val loserB = suspend { bracket({ s.release() }, use = { never<Int>() }, release = { pb.complete(b) }) }
      val loserC = suspend { bracket({ s.release() }, use = { never<Int>() }, release = { pc.complete(c) }) }

      val fiber = ForkAndForget { raceTriple(loserA, loserB, loserC) }

      s.acquireN(3) // Wait until both racers started

      fiber.cancel()
      pa.get() shouldBe a
      pb.get() shouldBe b
      pc.get() shouldBe c
    }
  }
})
