package arrow.fx.coroutines.stream

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.extensions.list.foldable.combineAll
import arrow.core.extensions.list.foldable.foldMap
import arrow.core.extensions.monoid
import arrow.core.extensions.semigroup
import arrow.fx.coroutines.Atomic
import arrow.fx.coroutines.ExitCase
import arrow.fx.coroutines.ForkAndForget
import arrow.fx.coroutines.Promise
import arrow.fx.coroutines.Semaphore
import arrow.fx.coroutines.SideEffect
import arrow.fx.coroutines.StreamSpec
import arrow.fx.coroutines.assertThrowable
import arrow.fx.coroutines.charRange
import arrow.fx.coroutines.guarantee
import arrow.fx.coroutines.guaranteeCase
import arrow.fx.coroutines.intRange
import arrow.fx.coroutines.longRange
import arrow.fx.coroutines.milliseconds
import arrow.fx.coroutines.never
import arrow.fx.coroutines.sleep
import arrow.fx.coroutines.throwable
import arrow.fx.coroutines.timeOutOrNull
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.Sample
import io.kotest.property.arbitrary.arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.arbitrary.set
import java.lang.RuntimeException
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.random.Random

class StreamTest : StreamSpec(spec = {
  "constructors" - {
    "empty() is empty" {
      Stream.empty<Int>()
        .compile()
        .toList() shouldBe emptyList()
    }

    "Stream.unit" {
      Stream.unit
        .compile()
        .toList() shouldBe listOf(Unit)
    }

    "never() should timeout" {
      timeOutOrNull(10.milliseconds) {
        Stream.never<Int>().compile().toList()
      } shouldBe null
    }

    "raiseError" - {
      "compiled stream fails with an error raised in stream" {
        checkAll(Arb.throwable()) { e ->
          assertThrowable {
            Stream.raiseError<Int>(e)
              .compile()
              .drain()
          } shouldBe e
        }
      }

      "compiled stream fails with an error if error raised after an append" {
        checkAll(Arb.throwable()) { e ->
          assertThrowable {
            Stream.just(1)
              .append { Stream.raiseError(e) }
              .compile()
              .drain()
          } shouldBe e
        }
      }

      "compiled stream does not fail if stream is termianted before raiseError" {
        checkAll(Arb.int(), Arb.throwable()) { i, e ->
          Stream.just(i)
            .append { Stream.raiseError(e) }
            .take(1)
            .compile()
            .toList() shouldBe listOf(i)
        }
      }
    }

    "chunk" {
      checkAll(Arb.chunk(Arb.int())) { ch ->
        Stream.chunk(ch)
          .chunks()
          .compile()
          .toList() shouldBe listOf(ch)
      }
    }

    "effect" {
      checkAll(Arb.int()) { i ->
        Stream.effect { i }
          .compile()
          .toList() shouldBe listOf(i)
      }
    }

    "effect_" {
      checkAll(Arb.int()) { i ->
        var effect: Int? = null
        Stream.effect_ { effect = i }
          .compile()
          .drain()
        effect shouldBe i
      }
    }

    "effectUnChunk" {
      checkAll(Arb.chunk(Arb.int())) { ch ->
        Stream.effectUnChunk { ch }
          .compile()
          .toList() shouldBe ch.toList()
      }
    }

    "constant" {
      checkAll(Arb.int(), Arb.int(0..100)) { i, n ->
        Stream.constant(i, n)
          .take(n * 2)
          .chunks()
          .compile()
          .toList() shouldBe if (n == 0) emptyList() else listOf(Chunk(n) { i }, Chunk(n) { i })
      }
    }

    "iterable" {
      checkAll(Arb.set(Arb.int())) { s ->
        Stream.iterable(s)
          .compile()
          .toSet() shouldBe s
      }
    }

    "iterate" {
      Stream.iterate(0, Int::inc)
        .take(100)
        .compile()
        .toList() shouldBe (0..99).toList()
    }

    "iterateEffect" {
      suspend fun Int.increment(): Int =
        this + 1

      Stream.iterateEffect(0) { it.increment() }
        .take(100)
        .compile()
        .toList() shouldBe (0..99).toList()
    }

    "range(IntRange).toList() - IntRange.toList()" {
      checkAll(Arb.intRange(min = -500, max = 500)) { range ->
        Stream.range(range)
          .compile()
          .toList() shouldBe range.toList()
      }
    }

    "range(LongRange).toList() - LongRange.toList()" {
      checkAll(Arb.longRange(min = -500, max = 500)) { range ->
        Stream.range(range)
          .compile()
          .toList() shouldBe range.toList()
      }
    }

    "range(CharRange).toList() - CharRange.toList()" {
      checkAll(Arb.charRange()) { range ->
        Stream.range(range)
          .compile()
          .toList() shouldBe range.toList()
      }
    }

    "unfold" {
      Stream.unfold(Pair(0, 1)) { (f1, f2) ->
        if (f1 <= 13) Pair(Pair(f1, f2), Pair(f2, f1 + f2))
        else null
      }.map { it.first }
        .compile()
        .toList() shouldBe listOf(0, 1, 1, 2, 3, 5, 8, 13)
    }

    "unfoldChunk" {
      Stream.unfoldChunk(4L) { s ->
        if (s > 0) Pair(Chunk.longs(longArrayOf(s, s)), s - 1)
        else null
      }.compile()
        .toList() shouldBe listOf(4L, 4, 3, 3, 2, 2, 1, 1)
    }

    "unfoldEffect" {
      Stream.unfoldEffect(10) { s -> if (s > 0) Pair(s, s - 1) else null }
        .compile()
        .toList() shouldBe (10 downTo 1).toList()
    }

    "unfoldChunkEffect" {
      Stream.unfoldChunkEffect(true) { s ->
        if (s) Pair(Chunk.booleans(booleanArrayOf(s)), false)
        else null
      }
        .compile()
        .toList() shouldBe listOf(true)
    }

    "emits - array.toList()" {
      checkAll(Arb.list(Arb.int())) { ints ->
        Stream.emits(*ints.toTypedArray())
          .compile()
          .toList() shouldBe ints
      }
    }

    "invoke - array.toList()" {
      checkAll(Arb.list(Arb.int())) { ints ->
        Stream.emits(*ints.toTypedArray())
          .compile()
          .toList() shouldBe ints
      }
    }

    "random(seed).take(n).toList - Random(seed).take(n).toList" {
      checkAll(Arb.int(), Arb.int()) { seed, n0 ->
        val n = n0 % 20
        Stream.random(seed)
          .take(n)
          .compile()
          .toList() shouldBe Random(seed).run { List(max(n, 0)) { nextInt() } }
      }
    }
  }

  "append" {
    checkAll(Arb.stream(Arb.int()), Arb.stream(Arb.int())) { s1, s2 ->
      s1.append { s2 }.compile().toList() shouldBe s1.compile().toList() + s2.compile().toList()
    }
  }

  "flatMap" {
    checkAll(Arb.stream(Arb.int()), Arb.stream(Arb.int())) { s1, s2 ->
      s1.flatMap { s2 }.compile().toList() shouldBe s1.compile().toList().flatMap { s2.compile().toList() }
    }
  }

  "take" - {
    "identity" {
      checkAll(Arb.stream(Arb.int()), Arb.int(-10..1000)) { s, n0 ->
        val n = n0 % 20

        s
          .take(n)
          .compile()
          .toList() shouldBe s.compile().toList().take(max(n, 0))
      }
    }

    "takeLast" {
      checkAll(Arb.stream(Arb.int()), Arb.int(-10..1000)) { s, n0 ->
        val n = n0 % 20
        s.takeLast(n).compile().toList() shouldBe s.compile().toList().takeLast(max(0, n))
      }
    }

    "takeWhile - identity" {
      checkAll(Arb.stream(Arb.int()), Arb.int(-10..1000)) { s, n0 ->
        val n = n0 % 20
        val l = s.compile().toList()
        val set = l.take(max(0, n)).toSet()

        s
          .takeWhile(set::contains)
          .compile()
          .toList() shouldBe l.takeWhile(set::contains)
      }
    }

    "takeThrough" {
      checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, n0 ->
        val n = n0 % 20 + 1
        val l = s.compile().toList()
        val isEven = { i: Int -> i % n == 0 }

        val head = l.takeWhile(isEven)
        val tail = l.dropWhile(isEven).firstOrNull()
        val rhs = if (tail != null) head + tail else head

        s
          .takeThrough(isEven)
          .compile()
          .toList() shouldBe rhs
      }
    }
  }

  "drop" - {
    "identity" {
      checkAll(Arb.stream(Arb.int().nullable()), Arb.int()) { s, n ->
        s
          .drop(n)
          .compile()
          .toList() shouldBe s.compile().toList().drop(max(n, 0))
      }
    }

    "last" {
      checkAll(Arb.stream(Arb.int()), Arb.int(-10..1000)) { s, n0 ->
        val n = n0 % 10
        s.dropLast(n).compile().toList() shouldBe s.compile().toList().dropLast(max(0, n))
      }
    }

    "tail" {
      checkAll(Arb.stream(Arb.int())) { s ->
        s.tail().compile().toList() shouldBe s.compile().toList().drop(1)
      }
    }

    "dropWhile" {
      checkAll(Arb.stream(Arb.int()), Arb.int(-10..1000)) { s, n0 ->
        val l = s.compile().toList()
        val n = n0 % 10
        val set = l.take(max(0, n)).toSet()

        s.dropWhile(set::contains)
          .compile()
          .toList() shouldBe l.dropWhile(set::contains)
      }
    }

    "dropThrough" {
      checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, n ->
        val l = s.compile().toList()
        val set = l.take(n).toSet()

        val expected = l.dropWhile(set::contains).drop(1)

        s
          .dropThrough(set::contains)
          .compile()
          .toList() shouldBe expected
      }
    }
  }

  "chunk" - {
    "chunked" {
      val s = Stream(1, 2).append { Stream(3, 4) }
      s.take(3).chunks().compile().toList() shouldBe listOf(Chunk(1, 2), Chunk(3))
    }

    "map identity" {
      checkAll(Arb.list(Arb.list(Arb.int()))) { lli ->
        val s = lli.foldMap(Stream.monoid<Int>()) { Stream.iterable(it) }
        s.chunks().map { it.toList() }.compile().toList() shouldBe lli
      }
    }

    "flatMap(chunk) identity" {
      checkAll(Arb.list(Arb.list(Arb.int()))) { lli ->
        val s =
          if (lli.isEmpty()) Stream.empty() else lli.map { Stream.iterable(it) }.reduce { a, b -> a.append { b } }
        s.chunks().flatMap { Stream.chunk(it) }.compile().toList() shouldBe lli.flatten()
      }
    }

    "chunkLimit" {
      checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, n0 ->
        val n = n0 % 20 + 1
        val sizes = s.chunkLimit(n).compile().toList().map { it.size() }
        sizes.all { it <= n } shouldBe true
        sizes.combineAll(Int.monoid()) shouldBe s.compile().toList().size
      }
    }

    "chunkMin" {
      checkAll(Arb.stream(Arb.int()), Arb.int()) { s, n0 ->
        val n = n0 % 20 + 1
        val chunked = s.chunkMin(n, true).compile().toList()
        val chunkedSmaller = s.chunkMin(n, false).compile().toList()
        val unchunked = s.compile().toList()
        val smallerSet = s.take(n - 1).compile().toList()
        val smallerN = s.take(n - 1).chunkMin(n, false).compile().toList()
        val smallerY = s.take(n - 1).chunkMin(n, true).compile().toList()
        // All but last list have n values
        chunked.dropLast(1).all { it.size() >= n }
        // Equivalent to last chunk with allowFewerTotal
        if (chunked.isNotEmpty() && chunked.last().size() < n)
          chunked.dropLast(1) == chunkedSmaller
        // Flattened sequence with allowFewerTotal true is equal to vector without chunking
        chunked.fold(emptyList<Int>()) { l, ch -> l + ch.toList() } shouldBe unchunked
        // If smaller than Chunk Size and allowFewerTotal false is empty then
        // no elements should be emitted
        smallerN shouldBe emptyList()
        // If smaller than Chunk Size and allowFewerTotal true is equal to the size
        // of the taken chunk initially
        smallerY.fold(emptyList<Int>()) { l, ch -> l + ch.toList() } shouldBe smallerSet
      }
    }
  }

  "repartition" {
    Stream("Hel", "l", "o Wor", "ld")
      .repartition(String.semigroup()) { s -> Chunk.iterable(s.split(" ")) }
      .compile()
      .toList() shouldBe listOf("Hello", "World")
  }

  "buffer" - {
    "identity" {
      checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, n ->
        s
          .buffer(n)
          .compile()
          .toList() shouldBe s.compile().toList()
      }
    }

    "buffers results of effectMap" {
      checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, n0 ->
        val n = n0 % 20 + 1
        var counter = 0
        val s2 = s.append { Stream.iterable(List(n + 1) { 0 }) }.repeat()
        s2.effectMap { i -> counter += 1; i }
          .buffer(n)
          .take(n + 1)
          .compile()
          .drain()

        counter shouldBe (n * 2)
      }
    }
  }

  "bufferAll" - {
    "identity" {
      checkAll(Arb.stream(Arb.int())) { s ->
        s.bufferAll()
          .compile()
          .toList() shouldBe s.compile().toList()
      }
    }

    "buffers results of effectMap" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val size = s.compile().toList().size
        val expected = size * 2
        var counter = 0

        s.append { s }
          .effectMap { i -> counter += 1; i }
          .bufferAll()
          .take(size + 1)
          .compile()
          .drain()

        counter shouldBe expected
      }
    }
  }

  "bufferBy" - {
    "identity" {
      checkAll(Arb.stream(Arb.int())) { s ->
        s.bufferBy { it >= 0 }
          .compile()
          .toList() shouldBe s.compile().toList()
      }
    }

    "buffers results of effectMap" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val size = s.compile().toList().size
        val expected = size * 2 + 1
        var counter = 0

        val s2 = s
          .map { x -> if (x == Int.MIN_VALUE) x + 1 else x }
          .map { it.absoluteValue }

        val s3 = s2.append { Stream(-1) }
          .append { s2 }
          .effectMap { i -> counter += 1; i }

        s3.bufferBy { it >= 0 }
          .take(size + 2)
          .compile()
          .drain()

        counter shouldBe expected
      }
    }
  }

  "handleErrorWith(f)" - {
    "recovers from error" {
      checkAll(Arb.throwable(), Arb.int()) { e, i ->
        Stream.raiseError<Int>(e)
          .handleErrorWith { Stream.just(i) }
          .compile()
          .toList() shouldBe listOf(i)
      }
    }

    "handleErrorWith is not run for happy path" {
      checkAll(Arb.int()) { i ->
        val effect = SideEffect()
        Stream.just(i)
          .handleErrorWith { Stream.effect { effect.increment() } }
          .compile()
          .toList() shouldBe listOf(i)

        effect.counter shouldBe 0
      }
    }
  }

  "stack safe" {
    checkAll(Arb.int(10_000, 20_000), Arb.int()) { n, i ->
      val infinite = Stream.just(i).repeat()

      infinite
        .take(n)
        .compile()
        .toList() shouldBe List(n) { i }
    }
  }

  "interruption" - {
    "can cancel a hung effect" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val latch = Promise<Unit>()
        val exit = Promise<ExitCase>()

        val f = ForkAndForget {
          s.append { Stream(1) } // Make sure is not empty
            .effectMap {
              guaranteeCase({ latch.complete(Unit); never<Unit>() }) { ex -> exit.complete(ex) }
            }.interruptWhen { Right(latch.get().also { sleep(20.milliseconds) }) }
            .compile()
            .toList()
        }

        latch.get()
        f.cancel()
        timeOutOrNull(50.milliseconds) { exit.get() } shouldBe ExitCase.Cancelled
      }
    }

    "can interrupt a hung effect" {
      checkAll(Arb.stream(Arb.int())) { s ->
        s.effectMap { never<Unit>() }
          .interruptWhen { Right(sleep(20.milliseconds)) }
          .compile()
          .toList() shouldBe emptyList()
      }
    }

    "termination successful when stream doing interruption is hung" {
      checkAll(Arb.stream(Arb.int())) { s ->
        Stream.effect { Semaphore(0) }
          .flatMap { semaphore ->
            val interrupt = Stream(true).append { Stream.effect_ { semaphore.release() } }

            s.effectMap { semaphore.acquire() }
              .interruptWhen(interrupt)
          }
          .compile()
          .toList() shouldBe emptyList()
      }
    }

    "constant stream" {
      checkAll(Arb.int()) { i ->
        Stream.constant(i)
          .interruptWhen { Right(sleep(20.milliseconds)) }
          .compile()
          .drain() // Finishes and gets interrupted
      }
    }

    "constant stream with a flatMap" {
      checkAll(Arb.int()) { i ->
        Stream.constant(i)
          .interruptWhen { Right(sleep(20.milliseconds)) }
          .flatMap { Stream(1) }
          .compile()
          .drain()
      }
    }

    "infinite recursive stream" {
      fun loop(i: Int): Stream<Int> =
        Stream(i).flatMap { i -> Stream(i).append { loop(i + 1) } }

      loop(0)
        .interruptWhen { Right(sleep(20.milliseconds)) }
        .compile()
        .drain()
    }

    "infinite recursive stream that never emits" {
      fun loop(): Stream<Int> =
        Stream.effect { Unit }.flatMap { loop() }

      loop()
        .interruptWhen { Right(sleep(20.milliseconds)) }
        .compile()
        .drain()
    }

    "infinite recursive stream that never emits and has no effect" {
      fun loop(): Stream<Int> =
        Stream(Unit).flatMap { loop() }

      loop()
        .interruptWhen { Right(sleep(20.milliseconds)) }
        .compile()
        .drain()
    }

    "effect stream" {
      Stream.effect { Unit }.repeat()
        .interruptWhen { Right(sleep(20.milliseconds)) }
        .compile()
        .drain()
    }

    "Constant drained stream" {
      Stream.constant(true)
        .interruptWhen { Right(sleep(20.milliseconds)) }
        .compile()
        .drain()
    }

    "terminates when interruption stream is infinitely false" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val expected = s.compile().toList()
        s.interruptWhen(Stream.constant(false))
          .compile()
          .toList() shouldBe expected
      }
    }

    "both streams hung" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val barrier = Semaphore(0)
        val enableInterrupt = Semaphore(0)
        val interrupt = Stream.effect { enableInterrupt.acquire() }.flatMap { Stream(false) }
        s.effectMap { i ->
          // enable interruption and hang when hitting a value divisible by 7
          if (i % 7 == 0) {
            enableInterrupt.release()
            barrier.acquire()
            i
          } else i
        }.interruptWhen(interrupt)
          .compile()
          // as soon as we hit a value divisible by 7, we enable interruption then hang before emitting it,
          // so there should be no elements in the output that are divisible by 7
          // this also checks that interruption works fine even if one or both streams are in a hung state
          .toList().forEach { it % 7 shouldNotBe 0 }
      }
    }

    "stream that never terminates in flatMap" {
      checkAll(Arb.stream(Arb.int())) { s ->
        s.interruptWhen { Right(sleep(20.milliseconds)) }
          .flatMap { Stream.never<Int>() }
          .compile()
          .toList() shouldBe emptyList()
      }
    }

    "failure from interruption signal will be propagated to main stream even when flatMap stream is hung" {
      checkAll(Arb.stream(Arb.int()), Arb.throwable()) { s, e ->
        Either.catch {
          Stream.effect { Semaphore(0) }.flatMap { semaphore ->
            Stream(1)
              .append { s }
              .interruptWhen { sleep(20.milliseconds); Either.Left(e) }
              .flatMap { Stream.effect_ { semaphore.acquire() } }
          }
            .compile()
            .toList()
        } shouldBe Either.Left(e)
      }
    }

    "resume on append" {
      Stream.never<Unit>()
        .interruptWhen { Right(sleep(20.milliseconds)) }
        .append { Stream(5) }
        .compile()
        .toList() shouldBe listOf(5)
    }

    "hang in effectMap and then resume on append" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val expected = s.compile().toList()

        s.interruptWhen { Right(sleep(20.milliseconds)) }
          .effectMap { never<Int>() }
          .drain()
          .append { s }
          .compile()
          .toList() shouldBe expected
      }
    }

    "effectMap + filterOption and then resume on append" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val expected = s.compile().toList()

        s.interruptWhen { Right(sleep(20.milliseconds)) }
          .effectMap { never<Option<Int>>() }
          .append { s.map { Some(it) } }
          .filterOption()
          .compile()
          .toList() shouldBe expected
      }
    }

    "interruption works when flatMap is followed by collect" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val expected = s.compile().toList()

        s.append { Stream(1) }
          .interruptWhen { Right(sleep(20.milliseconds)) }
          .map { None }
          .append { s.map { Some(it) } }
          .flatMap {
            when (it) {
              None -> Stream.never<Option<Int>>()
              is Some -> Stream(Some(it.t))
            }
          }
          .filterOption()
          .compile()
          .toList() shouldBe expected
      }
    }

    "if a pipe is interrupted, it will not restart evaluation" {
      checkAll(Arb.int(1..100)) { n ->
        val latch = Promise<Unit>()

        val p: Pipe<Int, Int> = Pipe {
          fun loop(acc: Int, pull: Pull<Int, Unit>): Pull<Int, Unit> =
            pull.uncons1OrNull().flatMap { uncons1 ->
              when (uncons1) {
                null -> Pull.output1(acc)
                else -> Pull.output1(uncons1.head).flatMap {
                  val stop = if (uncons1.head == n) Pull.effect { latch.complete(Unit) } else Pull.done
                  stop.flatMap { loop(acc + uncons1.head, uncons1.tail) }
                }
              }
            }

          loop(0, it.asPull()).stream()
        }

        Stream.iterate(0, Int::inc)
          .flatMap { Stream(it).delayBy(10.milliseconds) }
          .interruptWhen { Right(latch.get()) }
          .through(p)
          .compile()
          .toList()
          .let { result ->
            println("$result")
            result shouldBe listOfNotNull(result.firstOrNull()) + result.drop(1).filter { it != 0 }
          }
      }
    }

    "resume on append with pull" {
      Stream(1)
        .unchunk()
        .interruptWhen { Right(sleep(20.milliseconds)) }
        .asPull()
        .unconsOrNull()
        .flatMap { uncons ->
          when (uncons) {
            null -> Pull.done
            else -> Pull.effect { never<Unit>() }
          }
        }
        .stream()
        .interruptScope()
        .append { Stream(5) }
        .compile()
        .toList() shouldBe listOf(5)
    }

    "resume with append after evalMap interruption" {
      Stream(1)
        .interruptWhen { Right(sleep(20.milliseconds)) }
        .effectMap { never<Int>() }
        .append { Stream(5) }
        .compile()
        .toList() shouldBe listOf(5)
    }

    "interrupted effect is cancelled" {
      val latch = Promise<Unit>()

      timeOutOrNull(500.milliseconds) {
        Stream.effect { guarantee(latch::get) { latch.complete(Unit) } }
          .interruptAfter(50.milliseconds)
          .compile()
          .drain()

        latch.get()
        true
      } shouldBe true
    }

    "nested-interrupt" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val expected = s.compile().toList()

        Stream.effect { Semaphore(0) }.flatMap { semaphore ->
          s.interruptWhen { Right(sleep(20.milliseconds)) }
            .map { None }
            .append { s.map { Option(it) } }
            .interruptWhen { Right(never()) }
            .flatMap {
              when (it) {
                is None -> Stream.effect { semaphore.acquire(); None }
                is Some -> Stream(Some(it.t))
              }
            }.filterOption()
        }
          .compile()
          .toList() shouldBe expected
      }
    }

    "nested-interrupt - interrupt in outer scope interrupts the inner scope" {
      Stream.effect { never<Unit>() }
        .interruptWhen { never() }
        .interruptWhen { Right(Unit) }
        .compile()
        .toList() shouldBe emptyList()
    }

    "nested-interrupt - interrupt in enclosing scope recovers" {
      Stream.effect { never<Unit>() }
        .interruptWhen { never() }
        .append { Stream(1).delayBy(20.milliseconds) }
        .interruptWhen { Right(Unit) }
        .append { Stream(2) }
        .compile()
        .toList() shouldBe listOf(2)
    }

    "Compiled stream can be cancelled" {
      val stop = Promise<ExitCase>()
      val latch = Promise<Unit>()

      val fiber = ForkAndForget {
        guaranteeCase({
          latch.complete(Unit)
          Stream.never<Unit>()
            .compile()
            .resource
            .drain()
            .use { Unit }
        }, { ex -> stop.complete(ex) })
      }

      latch.get()
      fiber.cancel()

      stop.get() shouldBe ExitCase.Cancelled
    }
  }

  "parJoin" - {
    "no concurrency" {
      checkAll(Arb.stream(Arb.int())) { s ->
        s.map { Stream.just(it) }
          .parJoin(1)
          .compile()
          .toList() shouldBe s.compile().toList()
      }
    }

    "concurrency" {
      checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, n0 ->
        val n = (n0 % 20) + 1
        val expected = s.compile().toList().toSet()

        s.map { Stream(it) }
          .parJoin(n)
          .compile()
          .toSet() shouldBe expected
      }
    }

    "concurrent flattening" {
      checkAll(Arb.stream(Arb.stream(Arb.int())), Arb.positiveInts()) { s, n0 ->
        val n = n0 % 20 + 1
        val expected = s.flatten().compile().toSet()

        s.parJoin(n)
          .compile()
          .toSet() shouldBe expected
      }
    }

    "resources acquired in outer stream are released after inner streams complete" {
      val bracketed = Stream.bracket({ Atomic(true) }, { it.set(false) })

      // Starts an inner stream which fails if the resource b is finalized
      val s = bracketed.map { atomic ->
        Stream.effect { atomic.get() }
          .flatMap { b -> if (b) Stream.unit else Stream.raiseError(RuntimeException()) }
          .repeat()
          .take(10000)
      }

      s.parJoinUnbounded()
        .compile()
        .drain()
    }

    "run finalizers of inner streams first" {
      checkAll(Arb.stream(Arb.int()), Arb.bool()) { s1, bias ->
        val err = RuntimeException()
        val biasIdx = if (bias) 1 else 0
        val finalizerRef = Atomic(emptyList<String>())
        val runEvidenceRef = Atomic(emptyList<Int>())
        val halt = Promise<Unit>()

        suspend fun registerRun(idx: Int): Unit =
          runEvidenceRef.update { it + idx }

        // this introduces delay and failure based on bias of the test
        suspend fun finalizer(idx: Int): Unit =
          if (idx == biasIdx) {
            sleep(100.milliseconds)
            finalizerRef.update { it + "Inner $idx" }
            throw err
          } else finalizerRef.update { it + "Inner $idx" }

        val prg0 = Stream.bracket({ Unit }, { finalizerRef.update { it + "Outer" } })
          .flatMap {
            Stream(
              Stream.bracket({ registerRun(0) }, { finalizer(0) }).flatMap { s1 },
              Stream.bracket({ registerRun(1) }, { finalizer(1) })
                .flatMap { Stream.effect_ { halt.complete(Unit) } }
            )
          }

        val r = Either.catch {
          prg0
            .parJoinUnbounded()
            .compile()
            .drain()
        }
        val finalizers = finalizerRef.get()
        val streamRunned = runEvidenceRef.get()

        finalizers shouldContainExactlyInAnyOrder streamRunned.map { idx -> "Inner $idx" } + "Outer"
        finalizers.lastOrNull() shouldBe "Outer"

        if (streamRunned.contains(biasIdx)) r shouldBe Either.Left(err)
        else r shouldBe Either.Right(Unit)
      }
    }

    val full = Stream.constant(42)
    val hang = Stream.effect { never<Nothing>() }.repeat()
    val hang2 = full.drain()

    "Can take from non-hanging stream on left" {
      Stream(full, hang)
        .parJoin(10)
        .take(2)
        .compile()
        .toList() shouldBe listOf(42, 42)
    }

    "Can take from non-hanging stream on right" {
      Stream(hang2, full)
        .parJoin(10)
        .take(1)
        .compile()
        .toList() shouldBe listOf(42)
    }

    "Can take from non-hanging stream in middle" {
      Stream(hang, full, hang2)
        .parJoin(10)
        .take(1)
        .compile()
        .toList() shouldBe listOf(42)
    }

    "outer failed" {
      checkAll(Arb.throwable(), Arb.stream(Arb.int())) { e, s ->
        assertThrowable {
          Stream(s, Stream.raiseError(e))
            .parJoinUnbounded()
            .compile()
            .drain()
        } shouldBe e
      }
    }

    "propagate error from inner stream before append" {
      checkAll(Arb.throwable(), Arb.stream(Arb.int())) { e, s ->
        assertThrowable {
          Stream(Stream.raiseError<Int>(e))
            .parJoinUnbounded()
            .append { s }
            .compile()
            .toList()
        } shouldBe e
      }
    }
  }
})

fun <A> Arb.Companion.`null`(): Arb<A?> =
  Arb.constant(null)

fun <A> Arb<A>.nullable(): Arb<A?> {
  val arbs = listOf(this.map { it as A? }, Arb.`null`())
  return arb(arbs.flatMap(Arb<A?>::edgecases)) { rs ->
    val iters = arbs.map { it.values(rs).iterator() }
    fun next(): Sample<A?>? {
      val iter = iters.shuffled(rs.random).first()
      return if (iter.hasNext()) iter.next() else null
    }

    sequence {
      while (true) {
        var next: Sample<A?>? = null
        while (next == null)
          next = next()
        yield(next.value)
      }
    }
  }
}
