package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.None
import arrow.core.Validated
import arrow.core.invalid
import arrow.core.left
import arrow.core.right
import arrow.core.some
import arrow.core.valid
import arrow.typeclasses.Semigroup
import io.kotest.assertions.fail
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInts
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@ExperimentalTime
class FlowTest : ArrowFxSpec(
  spec = {

    "traverseResult" {
      val acc = mutableListOf<Int>()
      val res =
        (0..20_001).asFlow()
          .traverseResult {
            runCatching {
              if (it > 20_000) {
                throw RuntimeException("$it is too big!")
              }
              acc.add(it)
              it
            }
          }
      res.shouldBeFailure {
        it.shouldNotBeNull()
        it.message shouldBe "20001 is too big!"
      }
      acc shouldBe (0..20_000).toList()
    }

    "traverseOption" {
      val acc = mutableListOf<Int>()
      val res =
        (0..20_001).asFlow()
          .traverseOption {
            if (it > 20_000) {
              None
            } else {
              acc.add(it)
              it.some()
            }
          }
      res shouldBe None
      acc shouldBe (0..20_000).toList()
    }

    "traverseNullable" {
      val acc = mutableListOf<Int>()
      val res =
        (0..20_001).asFlow()
          .traverseNullable {
            if (it > 20_000) {
              null
            } else {
              acc.add(it)
              it
            }
          }
      res.shouldBeNull()
      acc shouldBe (0..20_000).toList()
    }

    "traverseEither" {
      val acc = mutableListOf<Int>()
      val res =
        (0..20_001).asFlow()
          .traverseEither {
            if (it > 20_000) {
              "$it is too big!".left()
            } else {
              acc.add(it)
              it.right()
            }
          }
      res.shouldBeInstanceOf<Either.Left<String>>()
      res.value shouldBe "20001 is too big!"
      acc shouldBe (0..20_000).toList()
    }

    "traverseValidated" {
      val acc = mutableListOf<Int>()
      val res = (0..20_001).asFlow()
        .traverseValidated(Semigroup.string()) {
          if (it > 20_000) {
            "$it is too big!".invalid()
          } else {
            acc.add(it)
            it.valid()
          }
        }
      res.shouldBeInstanceOf<Validated.Invalid<String>>()
      res.value shouldBe "20001 is too big!"
      acc shouldBe (0..20_000).toList()
    }

    "traverseValidated accumulates invalids" {
      val acc = mutableListOf<Int>()
      val res =
        (0..10).asFlow()
          .traverseValidated(Semigroup.int()) {
            acc.add(it)
            it.invalid()
          }
      res.shouldBeInstanceOf<Validated.Invalid<String>>()
      res.value shouldBe (0..10).sum()
    }

    "Retry - flow fails" {
      val bang = RuntimeException("Bang!")

      checkAll(Arb.int(), Arb.positiveInts(10)) { a, n ->
        var counter = 0
        val e = assertThrowable {
          flow {
            emit(a)
            if (++counter <= 11) throw bang
          }.retry(Schedule.recurs(n))
            .collect()
        }
        e shouldBe bang
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
        flow.parMap(1) { it }
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

        assertThrowable {
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
          fail("Cannot reach here. CancellationException should be thrown.")
        }.shouldBeTypeOf<CancellationException>()

        val (ii, ex) = exit.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parMap - exception in parMap cancels all running tasks" {
      checkAll(Arb.int(), Arb.throwable(), Arb.int(1..2)) { i, e, n ->
        val latch = CompletableDeferred<Unit>()
        val exit = CompletableDeferred<Pair<Int, ExitCase>>()

        assertThrowable {
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
          fail("Cannot reach here. $e should be thrown.")
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

    "parMapUnordered - concurrency = 1 - identity" {
      checkAll(Arb.flow(Arb.int())) { flow ->
        flow.parMapUnordered(concurrency = 1) { it }
          .toSet() shouldBe flow.toSet()
      }
    }

    "parMapUnordered - runs in parallel" {
      checkAll(Arb.int(), Arb.int(1..2)) { i, n ->
        val latch = CompletableDeferred<Int>()
        flowOf(1, 2).parMapUnordered { index ->
          if (index == n) latch.await()
          else {
            latch.complete(i)
            null
          }
        }.toSet().filterNotNull() shouldBe setOf(i)
      }
    }

    "parMapUnordered - triggers cancel signal" {
      checkAll(Arb.int(), Arb.int(1..2)) { i, n ->
        val latch = CompletableDeferred<Unit>()
        val exit = CompletableDeferred<Pair<Int, ExitCase>>()

        assertThrowable {
          flowOf(1, 2).parMapUnordered { index ->
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
          fail("Cannot reach here. CancellationException should be thrown.")
        }.shouldBeTypeOf<CancellationException>()

        val (ii, ex) = exit.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parMapUnordered - exception in parMap cancels all running tasks" {
      checkAll(Arb.int(), Arb.throwable(), Arb.int(1..2)) { i, e, n ->
        val latch = CompletableDeferred<Unit>()
        val exit = CompletableDeferred<Pair<Int, ExitCase>>()

        assertThrowable {
          flowOf(1, 2).parMapUnordered { index ->
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
          fail("Cannot reach here. $e should be thrown.")
        } shouldBe e

        val (ii, ex) = exit.await()
        ii shouldBe i
        ex.shouldBeTypeOf<ExitCase.Cancelled>()
      }
    }

    "parMapUnordered - Cancelling parMap cancels all running jobs" {
      checkAll(Arb.int(), Arb.int()) { i, i2 ->
        val latch = CompletableDeferred<Unit>()
        val exitA = CompletableDeferred<Pair<Int, ExitCase>>()
        val exitB = CompletableDeferred<Pair<Int, ExitCase>>()

        val job = launch {
          flowOf(1, 2).parMapUnordered { index ->
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
