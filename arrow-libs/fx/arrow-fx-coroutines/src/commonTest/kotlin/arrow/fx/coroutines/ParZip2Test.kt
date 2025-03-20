@file:OptIn(ExperimentalAtomicApi::class)

package arrow.fx.coroutines

import arrow.core.Either
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.Channel
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.Test

class ParZip2Test {
  @Test fun parZip2RunsInParallel() = runTestUsingDefaultDispatcher {
    checkAll(10, Arb.int(), Arb.int()) { a, b ->
      val r = AtomicInt(0)
      val modifyGate = CompletableDeferred<Int>()

      parZip(
        {
          modifyGate.await()
          r.addAndFetch(a)
        },
        {
          r.store(b)
          modifyGate.complete(0)
        }
      ) { _a, _b ->
        Pair(_a, _b)
      }

      r.load() shouldBe b + a
    }
  }

  @Test fun cancellingParZip2CancelsAllParticipants() = runTestUsingDefaultDispatcher {
    checkAll(10, Arb.int(), Arb.int()) { a, b ->
      val s = Channel<Unit>()
      val pa = CompletableDeferred<Pair<Int, ExitCase>>()
      val pb = CompletableDeferred<Pair<Int, ExitCase>>()

      val loserA: suspend CoroutineScope.() -> Int =
        { guaranteeCase({ s.receive(); awaitCancellation() }) { ex -> pa.complete(Pair(a, ex)) } }
      val loserB: suspend CoroutineScope.() -> Int =
        { guaranteeCase({ s.receive(); awaitCancellation() }) { ex -> pb.complete(Pair(b, ex)) } }

      val f = async { parZip(loserA, loserB) { _a, _b -> Pair(_a, _b) } }

      s.send(Unit) // Suspend until all racers started
      s.send(Unit)
      f.cancel()

      pa.await().let { (res, exit) ->
        res shouldBe a
        exit.shouldBeTypeOf<ExitCase.Cancelled>()
      }
      pb.await().let { (res, exit) ->
        res shouldBe b
        exit.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }
  }

  @Test fun parZip2CancelsLosersIfAFailtureOccursInOneOfTheTasts() = runTestUsingDefaultDispatcher {
    checkAll(10, Arb.throwable(), Arb.boolean()) { e, leftWinner ->
      val s = Channel<Unit>()
      val pa = CompletableDeferred<ExitCase>()

      val winner: suspend CoroutineScope.() -> Unit = { s.send(Unit); throw e }
      val loserA: suspend CoroutineScope.() -> Int =
        { guaranteeCase({ s.receive(); awaitCancellation() }) { ex -> pa.complete(ex) } }

      val r = Either.catch {
        if (leftWinner) parZip(winner, loserA) { _, _ -> Unit }
        else parZip(loserA, winner) { _, _ -> Unit }
      }

      pa.await().shouldBeTypeOf<ExitCase.Cancelled>()
      r should leftException(e)
    }
  }

  @Test fun parZipCancellationExceptionOnRightCanCancelRest() = runTestUsingDefaultDispatcher {
    checkAll(10, Arb.string()) { msg ->
      val exit = CompletableDeferred<ExitCase>()
      val start = CompletableDeferred<Unit>()
      try {
        parZip<Unit, Unit, Unit>({
          awaitExitCase(start, exit)
        }, {
          start.await()
          throw CancellationException(msg)
        }) { _, _ -> }
      } catch (e: CancellationException) {
        e.message shouldBe msg
      }
      exit.await().shouldBeTypeOf<ExitCase.Cancelled>()
    }
  }
}
