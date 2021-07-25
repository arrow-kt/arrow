package arrow.fx.coroutines

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.positiveInts
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@ExperimentalTime
class FlowTest : ArrowFxSpec(
  spec = {

    "Retry - flow fails" {
      checkAll(Arb.int(), Arb.positiveInts(10)) { a, n ->
        var counter = 0
        val e = shouldThrow<RuntimeException> {
          flow {
            emit(a)
            if (++counter <= 11) throw RuntimeException("Bang!")
          }.retry(Schedule.recurs(n))
            .collect()
        }
        e.message shouldBe "Bang!"
      }
    }

    "Retry - flow succeeds" {
      checkAll(Arb.int(), Arb.int(5, 10)) { a, n ->
        var counter = 0
        val sum = flow {
          emit(a)
          if (++counter <= 5) throw RuntimeException("Bang!")
        }.retry(Schedule.recurs(n))
          .reduce { acc, int -> acc + int }

        sum shouldBe a * 6
      }
    }

    "parMap - concurrency = 1 - identity" {
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMap(concurrency = 1) { it }
          .toList() shouldBe flow.toList()
      }
    }

    "parMap - runs in parallel" {
      checkAll(Arb.int(), Arb.int(1..2)) { i, n ->
        val latch = CompletableDeferred<Int>()
        flowOf(1, 2).parMap { index ->
          if (index == n) latch.await()
          else {
            latch.complete(i)
            null
          }
        }.toList().filterNotNull() shouldBe listOf(i)
      }
    }

    "parMap - triggers cancel signal" {
      checkAll(Arb.int(), Arb.int(1..2)) { i, n ->
        val latch = CompletableDeferred<Unit>()
        val exit = CompletableDeferred<Pair<Int, ExitCase>>()

        shouldThrow<CancellationException> {
          flowOf(1, 2).parMap { index ->
            if (index == n) {
              guaranteeCase({
                latch.complete(Unit)
                never<Unit>()
              }, { ex -> exit.complete(Pair(i, ex)) })
            } else {
              latch.await()
              throw CancellationException(null, null)
            }
          }.collect()
        }

        val (ii, ex) = exit.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parMap - exception in parMap cancels all running tasks" {
      checkAll(Arb.int(), Arb.throwable(), Arb.int(1..2)) { i, e, n ->
        val latch = CompletableDeferred<Unit>()
        val exit = CompletableDeferred<Pair<Int, ExitCase>>()

        shouldThrow<Throwable> {
          flowOf(1, 2).parMap { index ->
            if (index == n) {
              guaranteeCase({
                latch.complete(Unit)
                never<Unit>()
              }, { ex -> exit.complete(Pair(i, ex)) })
            } else {
              latch.await()
              throw e
            }
          }.collect()
        } shouldBe e

        val (ii, ex) = exit.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parMap - Cancelling parMap cancels all running jobs" {
      checkAll(Arb.int(), Arb.int()) { i, i2 ->
        val latch = CompletableDeferred<Unit>()
        val exitA = CompletableDeferred<Pair<Int, ExitCase>>()
        val exitB = CompletableDeferred<Pair<Int, ExitCase>>()

        val job = launch {
          flowOf(1, 2).parMap { index ->
            guaranteeCase({
              if (index == 2) latch.complete(Unit)
              never<Unit>()
            }, { ex ->
              if (index == 1) exitA.complete(Pair(i, ex))
              else exitB.complete(Pair(i2, ex))
            })
          }.collect()
        }

        latch.await()
        job.cancel()

        val (ii, ex) = exitA.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()

        val (ii2, ex2) = exitB.await()
        ii2 shouldBe i2
        ex2.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }
  }
)

fun <A> Arb.Companion.flow(arbA: Arb<A>): Arb<Flow<A>> =
  Arb.list(arbA).map { it.asFlow() }
