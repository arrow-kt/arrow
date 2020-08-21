package arrow.fx.coroutines.stream

import arrow.core.Either
import arrow.core.Right
import arrow.core.identity
import arrow.fx.coroutines.Atomic
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ForkAndForget
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.StreamSpec
import arrow.fx.coroutines.assertThrowable
import arrow.fx.coroutines.milliseconds
import arrow.fx.coroutines.parTupledN
import arrow.fx.coroutines.sleep
import arrow.fx.coroutines.throwable
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class BracketTest : StreamSpec(spec = {

  "bracket" - {
    "single bracket" - {
      suspend fun <A> singleBracketTest(use: Stream<A>): Unit {
        val events = Atomic(emptyList<BracketEvent>())
        Either.catch {
          events.recordBracketEvents(1)
            .effectMap { events.get() shouldBe listOf(Acquired.one) }
            .flatMap { use }
            .drain()
        }.fold({ e ->
          if (e is RuntimeException) Unit else throw e
        }, ::identity)

        events.get() shouldBe listOf(Acquired.one, Released.one)
      }

      "normal termination" {
        singleBracketTest<Unit>(Stream.empty())
      }
      "failure" {
        singleBracketTest<Unit>(Stream.raiseError(RuntimeException()))
      }
      "throw from append" {
        singleBracketTest(Stream(1, 2, 3).append { throw RuntimeException() })
      }
    }

    "double bracket" - {
      suspend fun <A> doubleBracketTest(use: Stream<A>): Unit {
        val events = Atomic(emptyList<BracketEvent>())
        Either.catch {
          events.recordBracketEvents(1)
            .effectMap { events.get() shouldBe listOf(Acquired(1)) }
            .flatMap { events.recordBracketEvents(2) }
            .effectMap { events.get() shouldBe listOf(Acquired(1), Acquired(2)) }
            .flatMap { use }
            .drain()
        }.fold({ e ->
          if (e is RuntimeException) Unit else throw e
        }) { identity(it) }

        events.get() shouldBe listOf(Acquired.one, Acquired.two, Released.two, Released.one)
      }

      "normal termination" {
        doubleBracketTest<Unit>(Stream.empty())
      }
      "failure" {
        doubleBracketTest<Unit>(Stream.raiseError(RuntimeException()))
      }
      "throw from append" {
        doubleBracketTest(Stream(1, 2, 3).append { throw RuntimeException() })
      }
    }

    "bracket.append { bracket }" - {
      suspend fun <A> appendBracketTest(use1: Stream<A>, use2: Stream<A>): Unit {
        val events = Atomic(emptyList<BracketEvent>())
        Either.catch {
          events.recordBracketEvents(1).flatMap { use1 }
            .append {
              events.recordBracketEvents(2).flatMap { use2 }
            }
            .drain()
        }.fold({ e ->
          if (e is RuntimeException) Unit else throw e
        }) { identity(it) }

        events.get() shouldBe listOf(Acquired.one, Released.one, Acquired.two, Released.two)
      }

      "normal termination" {
        appendBracketTest<Unit>(Stream.empty(), Stream.empty())
      }

      "failure" {
        appendBracketTest<Unit>(Stream.empty(), Stream.raiseError(RuntimeException()))
      }

      "throw from append" {
        appendBracketTest(Stream.empty(), Stream(1, 2, 3).append { throw RuntimeException() })
      }
    }

    "nested" {
      checkAll(Arb.list(Arb.int()), Arb.bool(), Arb.throwable()) { s0, finalizerFail, e ->
        // construct a deeply nested bracket stream in which the innermost stream fails
        // and check that as we unwind the stack, all resources get released
        // Also test for case where finalizer itself throws an error
        val counter = Counter()

        val innermost: Stream<Int> =
          if (finalizerFail) Stream.bracket(counter::increment) { counter.decrement(); throw e }.void()
          else Stream.raiseError(e)

        val nested = s0.foldRight(innermost) { i, acc ->
          Stream.bracket(counter::increment) { counter.decrement() }
            .flatMap { Stream(i).append { acc } }
        }

        assertThrowable {
          nested
            .drain()
        } shouldBe e

        counter.count() shouldBe 0L
      }
    }

    "early termination" {
      checkAll(Arb.stream(Arb.int()), Arb.long(), Arb.long(), Arb.long()) { s, i0, j0, k0 ->
        val i = i0 % 10
        val j = j0 % 10
        val k = k0 % 10
        val counter = Counter()
        val bracketed = Stream.bracket(counter::increment) { counter.decrement() }.append { s }
        val earlyTermination = bracketed.take(i)
        val twoLevels = bracketed.take(i).take(j)
        val twoLevels2 = bracketed.take(i).take(i)
        val threeLevels = bracketed.take(i).take(j).take(k)
        val fiveLevels = bracketed.take(i).take(j).take(k).take(j).take(i)
        val all =
          earlyTermination.append { twoLevels.append { twoLevels2.append { threeLevels.append { fiveLevels } } } }
        all.drain()
        counter.count() shouldBe 0L
      }
    }

    "finalizer should not be called until necessary" {
      val buffer = mutableListOf<String>()

      Stream.bracket({ buffer += "Acquired" }, { buffer += "Released" })
        .flatMap {
          buffer += "Used"
          Stream(Unit)
        }.flatMap { s ->
          buffer += "FlatMapped"
          Stream(s)
        }
        .toList()

      buffer shouldBe listOf(
        "Acquired",
        "Used",
        "FlatMapped",
        "Released"
      )
    }

    val bracketsInSequence = 10_000L
    // TODO This fails with `Stream.range` for some reason double check.
    "$bracketsInSequence brackets in sequence" {
      val counter = Counter()

      Stream.chunk(Chunk.iterable(0..bracketsInSequence)).flatMap { i ->
        Stream.bracket({ counter.increment() }) { counter.decrement() }
          .flatMap { Stream(i) }
      }
        .toList() shouldBe (0..bracketsInSequence).toList()

      counter.count() shouldBe 0
    }

    "evaluating a bracketed stream multiple times is safe" {
      val s = suspend {
        Stream.bracket({ Unit }, { Unit })
          .drain()
      }

      s.invoke()
      s.invoke()
    }

    "finalizers are run in LIFO order" - {
      "explicit release" {
        var o = emptyList<Int>()
        (0 until 10)
          .fold(Stream.effect { 0 }) { acc, i ->
            Stream.bracket({ i }, { i -> o = o + i })
              .flatMap { acc }
          }
          .drain()

        o shouldBe (0 until 10).toList()
      }

      "scope closure" {
        var o = emptyList<Int>()
        val error = RuntimeException()
        (0 until 10).fold(Stream.just(1).map { throw error } as Stream<Int>) { acc, i ->
          Stream.just(i).append {
            Stream.bracket({ i }, { i -> o = o + i })
              .flatMap { acc }
          }
        }
          .attempt()
          .drain()

        o shouldBe (0 until 10).toList()
      }
    }

    "propagate error from release" {
      checkAll(Arb.stream(Arb.int()), Arb.throwable()) { s, e ->
        Either.catch {
          Stream.bracket({ Unit }, { throw e })
            .flatMap { s }
            .toList()
        } shouldBe Either.Left(e)
      }
    }

    "propagate error from pure interrupted stream" {
      checkAll(Arb.throwable()) { e ->
        val exit = Promise<ExitCase>()

        assertThrowable {
          Stream.bracketCase({ Unit }, { _, ex ->
            exit.complete(ex)
            throw e
          }).flatMap { Stream.never<Unit>() }
            .interruptWhen { Right(sleep(50.milliseconds)) }
            .drain()
        } shouldBe e

        exit.get() shouldBe ExitCase.Cancelled
      }
    }

    "propagate error from closing the root scope - fail right" {
      checkAll(Arb.int(), Arb.string(), Arb.throwable()) { i, s, e ->
        val s1 = Stream.bracket({ i }, { Unit })
        val s2 = Stream.bracket({ s }, { throw e })

        assertThrowable {
          s1.zip(s2)
            .toList()
        } shouldBe e
      }
    }

    "propagate error from closing the root scope - fail left" {
      checkAll(Arb.int(), Arb.string(), Arb.throwable()) { i, s, e ->
        val s1 = Stream.bracket({ i }, { throw e })
        val s2 = Stream.bracket({ s }, { Unit })

        assertThrowable {
          s1.zip(s2)
            .toList()
        } shouldBe e
      }
    }

    "handleErrorWith closes scopes" {
      checkAll(Arb.throwable()) { e ->
        val events = Atomic(emptyList<BracketEvent>())

        events.recordBracketEvents(1)
          .flatMap { Stream.raiseError<Unit>(e) }
          .handleErrorWith { Stream(1) }
          .flatMap { events.recordBracketEvents(2) }
          .drain()

        events.get() shouldBe listOf(Acquired.one, Released.one, Acquired.two, Released.two)
      }
    }
  }

  "bracketCase" - {
    "normal termination" {
      checkAll(Arb.list(Arb.stream(Arb.int()))) { s0 ->
        val counter = Counter()
        var ecs = emptyList<ExitCase>()

        val s = s0.map { s ->
          Stream.bracketCase(counter::increment) { _: Unit, ec ->
            ecs = ecs + ec
            counter.decrement()
          }.flatMap { s }
        }

        val s2 = s.fold(Stream.empty<Int>()) { acc, ss -> acc.append { ss } }
        s2.append { s2.take(10) }.take(10).drain()

        counter.count() shouldBe 0L
        ecs.all { it is ExitCase.Completed } shouldBe true
      }
    }

    "failure" {
      checkAll(Arb.list(Arb.stream(Arb.int())), Arb.throwable()) { s0, e ->
        val counter = Counter()
        var ecs = emptyList<ExitCase>()
        val s = s0.map { s ->
          Stream.bracketCase(counter::increment) { _, ec ->
            ecs = ecs + ec
            counter.decrement()
          }.flatMap { s.append { Stream.raiseError(e) } }
        }
        val s2 = s.fold(Stream.empty<Int>()) { acc, i -> acc.append { i } }
        Either.catch { s2.drain() }
        counter.count() shouldBe 0L
        ecs.all { it == ExitCase.Failure(e) } shouldBe true
      }
    }

    "cancellation" {
      checkAll(Arb.stream(Arb.int())) { s0 ->
        val counter = Counter()
        var ecs = emptyList<ExitCase>()
        val latch = Promise<Unit>()

        val s = Stream.bracketCase(counter::increment) { _, ec ->
          ecs = ecs + ec
          counter.decrement()
        }.flatMap { s0.append { Stream.never() } }

        val f = ForkAndForget {
          parTupledN(
            { s.drain() },
            { latch.complete(Unit) }
          )
        }

        parTupledN({ latch.get() }, { sleep(50.milliseconds) })

        f.cancel()

        counter.count() shouldBe 0
        ecs.all { it is ExitCase.Cancelled } shouldBe true
      }
    }

    "interruption" {
      checkAll(Arb.stream(Arb.int())) { s0 ->
        val counter = Counter()
        var ecs = emptyList<ExitCase>()
        val s = Stream.bracketCase(counter::increment) { _, ec ->
          ecs = ecs + ec
          counter.decrement()
        }.flatMap { s0.append { Stream.never() } }

        s
          .interruptAfter(50.milliseconds)
          .drain()

        counter.count() shouldBe 0L
        ecs.all { it is ExitCase.Cancelled } shouldBe true
      }
    }
  }
})
