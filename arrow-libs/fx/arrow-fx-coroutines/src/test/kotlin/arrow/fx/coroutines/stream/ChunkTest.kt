package arrow.fx.coroutines.stream

import arrow.core.extensions.list.zip.zipWith
import arrow.fx.coroutines.ArrowFxSpec
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.math.max

class ChunkTest : ArrowFxSpec() {

  init {
    testChunk("BooleanArray", Arb.booleanChunk())
    testChunk("IntArray", Arb.intChunk())
    testChunk("DoubleArray", Arb.doubleChunk())
    testChunk("LongArray", Arb.longChunk())
    testChunk("BytesArray", Arb.byteChunk())
    testChunk("Boxed", Arb.boxedChunk(Arb.int()))
    testChunk("Float", Arb.floatChunk())
    testChunk("Short", Arb.shortChunk())

    "Chunk - toList" {
      checkAll(Arb.list(Arb.int(), 0..10)) { l ->
        Chunk.iterable(l).toList() shouldBe l
      }
    }

    "Chunk - tail" {
      checkAll(Arb.chunk(Arb.int())) { ch ->
        val expected = ch.toList().drop(1)
        ch.tail().toList() shouldBe expected
      }
    }

    "Chunk - filter" {
      checkAll(Arb.chunk(Arb.int())) { ch ->
        val f = { i: Int -> i % 2 == 0 }
        ch.filter(f).toList() shouldBe ch.toList().filter(f)
      }
    }

    "Chunk - first" {
      checkAll(Arb.chunk(Arb.int())) { ch ->
        ch.firstOrNull() shouldBe ch.toList().firstOrNull()
      }
    }

    "Chunk - firstOrNull()" {
      checkAll(Arb.chunk(Arb.int())) { ch ->
        ch.firstOrNull() shouldBe ch.toList().firstOrNull()
      }
    }

    "Chunk - firstOrNull(f)" {
      checkAll(Arb.chunk(Arb.int()), Arb.int()) { ch, n ->
        val f = { i: Int -> i < n }
        ch.firstOrNull(f) shouldBe ch.toList().firstOrNull(f)
      }
    }

    "Chunk - lastOrNull" {
      checkAll(Arb.chunk(Arb.int())) { ch ->
        ch.lastOrNull() shouldBe ch.toList().lastOrNull()
      }
    }

    "Chunk - lastOrNull(f)" {
      checkAll(Arb.chunk(Arb.int()), Arb.int()) { ch, n ->
        val f = { i: Int -> i < n }
        ch.lastOrNull(f) shouldBe ch.toList().lastOrNull(f)
      }
    }

    "Chunk - drop(n)" {
      checkAll(Arb.chunk(Arb.int().nullable()), Arb.int()) { ch, n ->
        ch.drop(n)
          .toList() shouldBe ch.toList().drop(max(n, 0))
      }
    }

    "Chunk - take(n)" {
      checkAll(Arb.chunk(Arb.int().nullable()), Arb.int()) { ch, n ->
        ch.take(n)
          .toList() shouldBe ch.toList().take(max(n, 0))
      }
    }

    "Chunk - takeLast(n)" {
      checkAll(Arb.chunk(Arb.int().nullable()), Arb.int()) { ch, n ->
        ch.takeLast(n)
          .toList() shouldBe ch.toList().takeLast(max(n, 0))
      }
    }

    "Chunk - indexOfFirst" {
      checkAll(Arb.chunk(Arb.int()), Arb.int()) { ch, n ->
        val f = { i: Int -> i < n }
        ch.indexOfFirst(f) shouldBe ch.toList().indexOfFirst(f).let {
          if (it == -1) null else it
        }
      }
    }

    "Chunk - map" {
      checkAll(Arb.chunk(Arb.int()), Arb.int()) { a, b ->
        a.map { b }.toList() shouldBe a.toList().map { b }
      }
    }

    "Chunk - flatMap" {
      checkAll(Arb.chunk(Arb.int()), Arb.chunk(Arb.int())) { a, b ->
        a.flatMap { b }.toList() shouldBe a.toList().flatMap { b.toList() }
      }
    }

    "Chunk - fold" {
      checkAll(Arb.chunk(Arb.int())) { a ->
        a.fold(0) { acc, i -> acc + i } shouldBe a.toList().fold(0) { acc, i -> acc + i }
      }
    }

    "Chunk - forEach" {
      checkAll(Arb.chunk(Arb.int())) { a ->
        var index = 0
        a.forEach { o ->
          o shouldBe a[index++]
        }
      }
    }

    "Chunk - forEachIndexed" {
      checkAll(Arb.chunk(Arb.int())) { a ->
        a.forEachIndexed { index, o ->
          o shouldBe a[index]
        }
      }
    }

    "Chunk - zip" {
      checkAll(Arb.chunk(Arb.int()), Arb.chunk(Arb.int())) { a, b ->
        a.zip(b).toList() shouldBe a.toList().zip(b.toList())
      }
    }

    "Chunk - zipWith" {
      checkAll(Arb.chunk(Arb.int()), Arb.chunk(Arb.int())) { a, b ->
        a.zipWith(b) { x, y -> x + y }.toList() shouldBe a.toList().zipWith(b.toList()) { x, y -> x + y }
      }
    }

    "Chunk - concat" {
      checkAll(Arb.list(Arb.chunk(Arb.int()))) { chs ->
        Chunk.concat(chs).toList() shouldBe chs.flatMap { it.toList() }
      }
    }

    "Chunk - concatBooleans" {
      checkAll(Arb.list(Arb.booleanChunk())) { chs ->
        Chunk.concat(chs).toList() shouldBe chs.flatMap { it.toList() }
      }
    }

    "Chunk - concatDouble" {
      checkAll(Arb.list(Arb.doubleChunk())) { chs ->
        Chunk.concat(chs).toList() shouldBe chs.flatMap { it.toList() }
      }
    }

    "Chunk - concatShorts" {
      checkAll(Arb.list(Arb.shortChunk())) { chs ->
        Chunk.concat(chs).toList() shouldBe chs.flatMap { it.toList() }
      }
    }

    "Chunk - byteChunk" {
      checkAll(Arb.list(Arb.byteChunk())) { chs ->
        Chunk.concat(chs).toList() shouldBe chs.flatMap { it.toList() }
      }
    }

    "Chunk - longChunk" {
      checkAll(Arb.list(Arb.longChunk())) { chs ->
        Chunk.concat(chs).toList() shouldBe chs.flatMap { it.toList() }
      }
    }

    "Chunk - intChunk" {
      checkAll(Arb.list(Arb.intChunk())) { chs ->
        Chunk.concat(chs).toList() shouldBe chs.flatMap { it.toList() }
      }
    }

    "Chunk - equals" {
      checkAll(Arb.chunk(Arb.int())) { chs ->
        assertSoftly {
          chs shouldBe chs
          chs shouldBe Chunk.iterable(chs.toList())
          if (chs.size() > 1) chs.drop(1) shouldNotBe chs
        }
      }
    }

    "Chunk - just.drop is empty" {
      Chunk.just(1).drop(1) shouldBe Chunk.empty()
    }

    "Chunk - hashCode" {
      checkAll(Arb.chunk(Arb.string())) { chs ->
        assertSoftly {
          chs.hashCode() shouldBe chs.hashCode()
          chs.hashCode() shouldBe Chunk.iterable(chs.toList()).hashCode()
          if (chs.size() >= 1) chs.drop(1).hashCode() shouldNotBe chs.hashCode()
        }
      }
    }

    "Chunk.Queue - take" {
      checkAll(Arb.list(Arb.chunk(Arb.int())), Arb.int()) { chs, n ->
        val res = Chunk.Queue(chs).take(n)
        res.toChunk().toList() shouldBe chs.flatMap { it.toList() }.take(max(n, 0))
      }
    }

    "Chunk.Queue - drop" {
      checkAll(Arb.list(Arb.chunk(Arb.int())), Arb.int()) { chs, n ->
        val result = Chunk.Queue(chs).drop(n)
        result.toChunk().toList() shouldBe chs.flatMap { it.toList() }.drop(max(n, 0))
      }
    }

    "Chunk.Queue - takeRight" {
      checkAll(Arb.list(Arb.chunk(Arb.int())), Arb.int()) { chs, n ->
        val result = Chunk.Queue(chs).takeLast(n)
        result.toChunk().toList() shouldBe chs.flatMap { it.toList() }.takeLast(max(n, 0))
      }
    }

    "Chunk.Queue - dropRight" {
      checkAll(Arb.list(Arb.chunk(Arb.int())), Arb.int()) { chs, n ->
        val result = Chunk.Queue(chs).dropLast(n)
        result.toChunk().toList() shouldBe chs.flatMap { it.toList() }.dropLast(max(n, 0))
      }
    }

    "Chunk.Queue - equals" {
      checkAll(Arb.list(Arb.chunk(Arb.int()))) { chs ->
        val cq = Chunk.Queue(chs)
        assertSoftly {
          cq shouldBe cq
          cq shouldBe Chunk.Queue(chs)
          if (cq.size > 1) cq.drop(1) shouldNotBe cq
        }
      }
    }

    "Chunk.Queue - hashCode" {
      checkAll(Arb.list(Arb.chunk(Arb.int()))) { chs ->
        val cq = Chunk.Queue(chs)
        assertSoftly {
          cq.hashCode() shouldBe cq.hashCode()
          cq.hashCode() shouldBe Chunk.Queue(chs).hashCode()
          if (cq.size > 1) cq.drop(1).hashCode() shouldNotBe cq.hashCode()
        }
      }
    }

    "BooleanArray - copyToBooleanArray" {
      checkAll(Arb.booleanChunk()) { ch ->
        val arr = BooleanArray(ch.size() * 2)
        ch.copyToArray(arr, 0)
        ch.copyToArray(arr, ch.size())
        arr.toList() shouldBe (ch.toList() + ch.toList())
      }
    }

    "IntArray - copyToIntArray" {
      checkAll(Arb.intChunk()) { ch ->
        val arr = IntArray(ch.size() * 2)
        ch.copyToArray(arr, 0)
        ch.copyToArray(arr, ch.size())
        arr.toList() shouldBe (ch.toList() + ch.toList())
      }
    }

    "DoubleArray - copyToDoubleArray" {
      checkAll(Arb.doubleChunk()) { ch ->
        val arr = DoubleArray(ch.size() * 2)
        ch.copyToArray(arr, 0)
        ch.copyToArray(arr, ch.size())
        arr.toList() shouldBe (ch.toList() + ch.toList())
      }
    }

    "LongArray - copyToLongArray" {
      checkAll(Arb.longChunk()) { ch ->
        val arr = LongArray(ch.size() * 2)
        ch.copyToArray(arr, 0)
        ch.copyToArray(arr, ch.size())
        arr.toList() shouldBe (ch.toList() + ch.toList())
      }
    }

    "ByteArray - copyToLongArray" {
      checkAll(Arb.byteChunk()) { ch ->
        val arr = ByteArray(ch.size() * 2)
        ch.copyToArray(arr, 0)
        ch.copyToArray(arr, ch.size())
        arr.toList() shouldBe (ch.toList() + ch.toList())
      }
    }

    "ShortArray - copyToShortArray" {
      checkAll(Arb.shortChunk()) { ch ->
        val arr = ShortArray(ch.size() * 2)
        ch.copyToArray(arr, 0)
        ch.copyToArray(arr, ch.size())
        arr.toList() shouldBe (ch.toList() + ch.toList())
      }
    }

    "FloatArray - copyToFloatArray" {
      checkAll(Arb.floatChunk()) { ch ->
        val arr = FloatArray(ch.size() * 2)
        ch.copyToArray(arr, 0)
        ch.copyToArray(arr, ch.size())
        arr.toList() shouldBe (ch.toList() + ch.toList())
      }
    }
  }
}

/**
 * Test suite for `open` functions of [Chunk].
 */
inline fun <reified A> ArrowFxSpec.testChunk(
  name: String,
  arb: Arb<Chunk<A>>
): Unit {
  "$name - size" {
    checkAll(arb) { ch ->
      ch.size() shouldBe ch.toList().size
    }
  }

  "$name - drop" {
    checkAll(arb, Arb.int()) { ch, n ->
      val expected = ch.toList().drop(max(n, 0))
      ch.drop(n).toList() shouldBe expected
    }
  }

  "$name - take" {
    checkAll(arb, Arb.positiveInts()) { ch, n ->
      ch.take(n).toList() shouldBe ch.toList().take(max(n, 0))
    }
  }

  "$name - isEmpty" {
    checkAll(arb) { ch ->
      ch.isEmpty() shouldBe ch.toList().isEmpty()
    }
  }

  "$name - isNotEmpty" {
    checkAll(arb) { ch ->
      ch.isNotEmpty() shouldBe ch.toList().isNotEmpty()
    }
  }

  "$name - toArray" {
    checkAll(arb) { ch ->
      ch.toArray().toList() shouldBe ch.toList()
      // Do it twice to make sure the first time didn't mutate state
      ch.toArray().toList() shouldBe ch.toList()
    }
  }

  "$name - copyToArray" {
    checkAll(arb) { ch ->
      val arr = arrayOfNulls<A>(ch.size() * 2)
      ch.copyToArray(arr, 0)
      ch.copyToArray(arr, ch.size())
      arr.toList() shouldBe (ch.toList() + ch.toList())
    }
  }
}
