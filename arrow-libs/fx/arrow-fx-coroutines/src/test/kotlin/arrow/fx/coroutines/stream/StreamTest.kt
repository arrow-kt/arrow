package arrow.fx.coroutines.stream

import arrow.core.None
import arrow.core.Some
import arrow.core.extensions.list.foldable.combineAll
import arrow.core.extensions.list.foldable.foldMap
import arrow.core.extensions.monoid
import arrow.core.extensions.semigroup
import arrow.fx.coroutines.SideEffect
import arrow.fx.coroutines.StreamSpec
import arrow.fx.coroutines.assertThrowable
import arrow.fx.coroutines.charRange
import arrow.fx.coroutines.intRange
import arrow.fx.coroutines.longRange
import arrow.fx.coroutines.milliseconds
import arrow.fx.coroutines.never
import arrow.fx.coroutines.throwable
import arrow.fx.coroutines.timeOutOrNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.arbitrary.set
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.random.Random

class StreamTest : StreamSpec(spec = {
  "constructors" - {
    "empty() is empty" {
      Stream.empty<Int>()
        .toList() shouldBe emptyList()
    }

    "Stream.unit" {
      Stream.unit
        .toList() shouldBe listOf(Unit)
    }

    "never() should timeout" {
      timeOutOrNull(10.milliseconds) {
        Stream.never<Int>().toList()
      } shouldBe null
    }

    "raiseError" - {
      "compiled stream fails with an error raised in stream" {
        checkAll(Arb.throwable()) { e ->
          assertThrowable {
            Stream.raiseError<Int>(e)
              .drain()
          } shouldBe e
        }
      }

      "compiled stream fails with an error if error raised after an append" {
        checkAll(Arb.throwable()) { e ->
          assertThrowable {
            Stream.just(1)
              .append { Stream.raiseError(e) }
              .drain()
          } shouldBe e
        }
      }

      "compiled stream does not fail if stream is termianted before raiseError" {
        checkAll(Arb.int(), Arb.throwable()) { i, e ->
          Stream.just(i)
            .append { Stream.raiseError(e) }
            .take(1)
            .toList() shouldBe listOf(i)
        }
      }
    }

    "chunk" {
      checkAll(Arb.chunk(Arb.int())) { ch ->
        Stream.chunk(ch)
          .chunks()
          .toList() shouldBe listOf(ch)
      }
    }

    "effect" {
      checkAll(Arb.int()) { i ->
        Stream.effect { i }
          .toList() shouldBe listOf(i)
      }
    }

    "effect_" {
      checkAll(Arb.int()) { i ->
        var effect: Int? = null
        Stream.effect_ { effect = i }
          .drain()
        effect shouldBe i
      }
    }

    "effectUnChunk" {
      checkAll(Arb.chunk(Arb.int())) { ch ->
        Stream.effectUnChunk { ch }
          .toList() shouldBe ch.toList()
      }
    }

    "constant" {
      checkAll(Arb.int(), Arb.int(0..100)) { i, n ->
        Stream.constant(i, n)
          .take(n * 2)
          .chunks()
          .toList() shouldBe if (n == 0) emptyList() else listOf(Chunk(n) { i }, Chunk(n) { i })
      }
    }

    "iterable" {
      checkAll(Arb.set(Arb.int())) { s ->
        Stream.iterable(s)
          .toSet() shouldBe s
      }
    }

    "iterate" {
      Stream.iterate(0, Int::inc)
        .take(100)
        .toList() shouldBe (0..99).toList()
    }

    "iterateEffect" {
      suspend fun Int.increment(): Int =
        this + 1

      Stream.iterateEffect(0) { it.increment() }
        .take(100)
        .toList() shouldBe (0..99).toList()
    }

    "range(IntRange).toList() - IntRange.toList()" {
      checkAll(Arb.intRange(min = -500, max = 500)) { range ->
        Stream.range(range)
          .toList() shouldBe range.toList()
      }
    }

    "range(LongRange).toList() - LongRange.toList()" {
      checkAll(Arb.longRange(min = -500, max = 500)) { range ->
        Stream.range(range)
          .toList() shouldBe range.toList()
      }
    }

    "range(CharRange).toList() - CharRange.toList()" {
      checkAll(Arb.charRange()) { range ->
        Stream.range(range)
          .toList() shouldBe range.toList()
      }
    }

    "unfold" {
      Stream.unfold(Pair(0, 1)) { (f1, f2) ->
        if (f1 <= 13) Pair(Pair(f1, f2), Pair(f2, f1 + f2))
        else null
      }.map { it.first }
        .toList() shouldBe listOf(0, 1, 1, 2, 3, 5, 8, 13)
    }

    "unfoldChunk" {
      Stream.unfoldChunk(4L) { s ->
        if (s > 0) Pair(Chunk.longs(longArrayOf(s, s)), s - 1)
        else null
      }.toList() shouldBe listOf(4L, 4, 3, 3, 2, 2, 1, 1)
    }

    "unfoldEffect" {
      Stream.unfoldEffect(10) { s -> if (s > 0) Pair(s, s - 1) else null }
        .toList() shouldBe (10 downTo 1).toList()
    }

    "unfoldChunkEffect" {
      Stream.unfoldChunkEffect(true) { s ->
        if (s) Pair(Chunk.booleans(booleanArrayOf(s)), false)
        else null
      }
        .toList() shouldBe listOf(true)
    }

    "emits - array.toList()" {
      checkAll(Arb.list(Arb.int())) { ints ->
        Stream.emits(*ints.toTypedArray())
          .toList() shouldBe ints
      }
    }

    "invoke - array.toList()" {
      checkAll(Arb.list(Arb.int())) { ints ->
        Stream.emits(*ints.toTypedArray())
          .toList() shouldBe ints
      }
    }

    "random(seed).take(n).toList - Random(seed).take(n).toList" {
      checkAll(Arb.int(), Arb.int()) { seed, n0 ->
        val n = n0 % 20
        Stream.random(seed)
          .take(n)
          .toList() shouldBe Random(seed).run { List(max(n, 0)) { nextInt() } }
      }
    }
  }

  "filterNotNull" {
    checkAll(Arb.stream(Arb.int().orNull())) { s ->
      s.filterNotNull()
        .toList() shouldBe s.toList().filterNotNull()
    }
  }

  "append" {
    checkAll(Arb.stream(Arb.int()), Arb.stream(Arb.int())) { s1, s2 ->
      s1.append { s2 }.toList() shouldBe s1.toList() + s2.toList()
    }
  }

  "flatMap" {
    checkAll(Arb.stream(Arb.int()), Arb.stream(Arb.int())) { s1, s2 ->
      s1.flatMap { s2 }.toList() shouldBe s1.toList().flatMap { s2.toList() }
    }
  }

  "take" - {
    "identity" {
      checkAll(Arb.stream(Arb.int()), Arb.int(-10..1000)) { s, n0 ->
        val n = n0 % 20

        s
          .take(n)
          .toList() shouldBe s.toList().take(max(n, 0))
      }
    }

    "takeLast" {
      checkAll(Arb.stream(Arb.int()), Arb.int(-10..1000)) { s, n0 ->
        val n = n0 % 20
        s.takeLast(n).toList() shouldBe s.toList().takeLast(max(0, n))
      }
    }

    "takeWhile - identity" {
      checkAll(Arb.stream(Arb.int()), Arb.int(-10..1000)) { s, n0 ->
        val n = n0 % 20
        val l = s.toList()
        val set = l.take(max(0, n)).toSet()

        s
          .takeWhile(set::contains)
          .toList() shouldBe l.takeWhile(set::contains)
      }
    }

    "takeThrough" {
      checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, n0 ->
        val n = n0 % 20 + 1
        val l = s.toList()
        val isEven = { i: Int -> i % n == 0 }

        val head = l.takeWhile(isEven)
        val tail = l.dropWhile(isEven).firstOrNull()
        val rhs = if (tail != null) head + tail else head

        s
          .takeThrough(isEven)
          .toList() shouldBe rhs
      }
    }
  }

  "drop" - {
    "identity" {
      checkAll(Arb.stream(Arb.int().orNull()), Arb.int()) { s, n ->
        s
          .drop(n)
          .toList() shouldBe s.toList().drop(max(n, 0))
      }
    }

    "last" {
      checkAll(Arb.stream(Arb.int()), Arb.int(-10..1000)) { s, n0 ->
        val n = n0 % 10
        s.dropLast(n).toList() shouldBe s.toList().dropLast(max(0, n))
      }
    }

    "tail" {
      checkAll(Arb.stream(Arb.int())) { s ->
        s.tail().toList() shouldBe s.toList().drop(1)
      }
    }

    "dropWhile" {
      checkAll(Arb.stream(Arb.int()), Arb.int(-10..1000)) { s, n0 ->
        val l = s.toList()
        val n = n0 % 10
        val set = l.take(max(0, n)).toSet()

        s.dropWhile(set::contains)
          .toList() shouldBe l.dropWhile(set::contains)
      }
    }

    "dropThrough" {
      checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, n ->
        val l = s.toList()
        val set = l.take(n).toSet()

        val expected = l.dropWhile(set::contains).drop(1)

        s
          .dropThrough(set::contains)
          .toList() shouldBe expected
      }
    }
  }

  "chunk" - {
    "chunked" {
      val s = Stream(1, 2).append { Stream(3, 4) }
      s.take(3).chunks().toList() shouldBe listOf(Chunk(1, 2), Chunk(3))
    }

    "map identity" {
      checkAll(Arb.list(Arb.list(Arb.int()))) { lli ->
        val s = lli.foldMap(Stream.monoid<Int>()) { Stream.iterable(it) }
        s.chunks().map { it.toList() }.toList() shouldBe lli
      }
    }

    "flatMap(chunk) identity" {
      checkAll(Arb.list(Arb.list(Arb.int()))) { lli ->
        val s =
          if (lli.isEmpty()) Stream.empty() else lli.map { Stream.iterable(it) }.reduce { a, b -> a.append { b } }
        s.chunks().flatMap { Stream.chunk(it) }.toList() shouldBe lli.flatten()
      }
    }

    "chunkLimit" {
      checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, n0 ->
        val n = n0 % 20 + 1
        val sizes = s.chunkLimit(n).toList().map { it.size() }
        sizes.all { it <= n } shouldBe true
        sizes.combineAll(Int.monoid()) shouldBe s.toList().size
      }
    }

    "chunkMin" {
      checkAll(Arb.stream(Arb.int()), Arb.int()) { s, n0 ->
        val n = n0 % 20 + 1
        val chunked = s.chunkMin(n, true).toList()
        val chunkedSmaller = s.chunkMin(n, false).toList()
        val unchunked = s.toList()
        val smallerSet = s.take(n - 1).toList()
        val smallerN = s.take(n - 1).chunkMin(n, false).toList()
        val smallerY = s.take(n - 1).chunkMin(n, true).toList()
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
      .toList() shouldBe listOf("Hello", "World")
  }

  "buffer" - {
    "identity" {
      checkAll(Arb.stream(Arb.int()), Arb.positiveInts()) { s, n ->
        s
          .buffer(n)
          .toList() shouldBe s.toList()
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
          .drain()

        counter shouldBe (n * 2)
      }
    }
  }

  "bufferAll" - {
    "identity" {
      checkAll(Arb.stream(Arb.int())) { s ->
        s.bufferAll()
          .toList() shouldBe s.toList()
      }
    }

    "buffers results of effectMap" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val size = s.toList().size
        val expected = size * 2
        var counter = 0

        s.append { s }
          .effectMap { i -> counter += 1; i }
          .bufferAll()
          .take(size + 1)
          .drain()

        counter shouldBe expected
      }
    }
  }

  "bufferBy" - {
    "identity" {
      checkAll(Arb.stream(Arb.int())) { s ->
        s.bufferBy { it >= 0 }
          .toList() shouldBe s.toList()
      }
    }

    "buffers results of effectMap" {
      checkAll(Arb.stream(Arb.int())) { s ->
        val size = s.toList().size
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
          .toList() shouldBe listOf(i)
      }
    }

    "handleErrorWith is not run for happy path" {
      checkAll(Arb.int()) { i ->
        val effect = SideEffect()
        Stream.just(i)
          .handleErrorWith { Stream.effect { effect.increment() } }
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
        .toList() shouldBe List(n) { i }
    }
  }

  "terminateOn" {
    Stream(1, 2, 3, 4)
      .terminateOn { it % 3 == 0 }
      .toList() shouldBe listOf(1, 2)
  }

  "terminateOnNull" {
    Stream(1, 2, null, 4)
      .terminateOnNull()
      .toList() shouldBe listOf(1, 2)
  }

  "terminateOnNone" {
    Stream(Some(1), Some(2), None, Some(4))
      .terminateOnNone()
      .toList() shouldBe listOf(1, 2)
  }
})
