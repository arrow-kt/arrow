package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.identity
import arrow.core.merge
import io.kotest.assertions.arrow.core.rethrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.arrow.core.either
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel

class RaceNTest : StringSpec({
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

    "Cancelling race 2 cancels all participants" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()
        val pb = CompletableDeferred<Pair<Int, ExitCase>>()

        val loserA: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }

        val f = async { raceN(loserA, loserB) }

        // Suspend until all racers started
        s.send(Unit)
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

    "race 2 cancels losers with first success or failure determining winner" {
      checkAll(
        Arb.either(Arb.throwable(), Arb.int()),
        Arb.boolean(),
        Arb.int()
      ) { eith, leftWinner, a ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()

        val winner: suspend CoroutineScope.() -> Int = { s.send(Unit); eith.rethrow() }
        val loserA: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never() }) { ex -> pa.complete(Pair(a, ex)) } }

        val res = Either.catch {
          if (leftWinner) raceN(winner, loserA)
          else raceN(loserA, winner)
        }.map { it.merge() }

        pa.await().let { (res, exit) ->
          res shouldBe a
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        res shouldBe either(eith)
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

    "Cancelling race 3 cancels all participants" {
      checkAll(Arb.int(), Arb.int(), Arb.int()) { a, b, c ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()
        val pb = CompletableDeferred<Pair<Int, ExitCase>>()
        val pc = CompletableDeferred<Pair<Int, ExitCase>>()

        val loserA: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never() }) { ex -> pb.complete(Pair(b, ex)) } }
        val loserC: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never() }) { ex -> pc.complete(Pair(c, ex)) } }

        val f = async { raceN(loserA, loserB, loserC) }

        s.send(Unit) // Suspend until all racers started
        s.send(Unit)
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
        pc.await().let { (res, exit) ->
          res shouldBe c
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
      }
    }

    "race 3 cancels losers with first success or failure determining winner" {
      checkAll(
        Arb.either(Arb.throwable(), Arb.int()),
        Arb.element(listOf(1, 2, 3)),
        Arb.int(),
        Arb.int()
      ) { eith, leftWinner, a, b ->
        val s = Channel<Unit>()
        val pa = CompletableDeferred<Pair<Int, ExitCase>>()
        val pb = CompletableDeferred<Pair<Int, ExitCase>>()

        val winner: suspend CoroutineScope.() -> Int = { s.send(Unit); s.send(Unit); eith.rethrow() }
        val loserA: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pa.complete(Pair(a, ex)) } }
        val loserB: suspend CoroutineScope.() -> Int = { guaranteeCase({ s.receive(); never<Int>() }) { ex -> pb.complete(Pair(b, ex)) } }

        val res = Either.catch {
          when (leftWinner) {
            1 -> raceN(winner, loserA, loserB)
            2 -> raceN(loserA, winner, loserB)
            else -> raceN(loserA, loserB, winner)
          }
        }.map { it.fold(::identity, ::identity, ::identity) }

        pa.await().let { (res, exit) ->
          res shouldBe a
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        pb.await().let { (res, exit) ->
          res shouldBe b
          exit.shouldBeInstanceOf<ExitCase.Cancelled>()
        }
        res should either(eith)
      }
    }
  }
)
