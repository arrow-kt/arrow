package arrow.fx.coroutines.stream

import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.Shrinker
import io.kotest.property.arbitrary.arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.short
import kotlin.random.nextInt

@JvmOverloads
inline fun <reified A> Arb.Companion.array(
  gen: Arb<A>,
  range: IntRange = 0..100
): Arb<Array<A>> {
  check(!range.isEmpty())
  check(range.first >= 0)
  return arb(edgecases = listOf(emptyArray<A>()) + gen.edgecases().map { arrayOf(it) }) {
    sequence {
      val genIter = gen.generate(it).iterator()
      while (true) {
        val targetSize = it.random.nextInt(range)
        val list = ArrayList<A>(targetSize)
        while (list.size < targetSize && genIter.hasNext()) {
          list.add(genIter.next().value)
        }
        check(list.size == targetSize)
        yield(list.toArray() as Array<A>)
      }
    }
  }
}

@PublishedApi
internal fun <A, B> arrayChunkGenerator(
  arb: Arb<A>,
  shrinker: Shrinker<B>,
  range: IntRange = 0..10,
  build: (values: List<A>, offset: Int, length: Int) -> B
): Arb<B> {
  check(!range.isEmpty())
  check(range.first >= 0)

  val edgecases =
    arb.edgecases().map { a -> build(listOf(a), 0, 1) } + build(emptyList(), 0, 0)

  return arb(edgecases, shrinker) {
    val genIter = arb.generate(it).iterator()

    sequence {
      while (true) {
        val targetSize = it.random.nextInt(range)
        val list = ArrayList<A>(targetSize)

        while (list.size < targetSize && genIter.hasNext()) {
          list.add(genIter.next().value)
        }

        val offset = (0..list.size).random(it.random)
        val length = (0..(list.size - offset)).random(it.random)

        yield(build(list, offset, length))
      }
    }
  }
}

class ChunkShrinker<A> : Shrinker<Chunk<A>> {
  override fun shrink(value: Chunk<A>): List<Chunk<A>> =
    if (value.isEmpty()) emptyList()
    else listOf(
      Chunk.empty(),
      value.takeLast(1),
      value.take(value.size() / 3),
      value.take(value.size() / 2),
      value.take(value.size() * 2 / 3),
      value.dropLast(1)
    )
}

inline fun <reified A> Arb.Companion.chunk(arb: Arb<A>): Arb<Chunk<A>> =
  object : Arb<Chunk<A>>() {
    override fun edgecases(): List<Chunk<A>> =
      listOf(Chunk.empty<A>()) + arb.edgecases().map { Chunk(it) }

    override fun values(rs: RandomSource): Sequence<Sample<Chunk<A>>> =
      Arb.choose(
        5 to arb.map { Chunk.just(it) },
        10 to Arb.list(arb, 0..20).map { Chunk.iterable(it) },
        10 to Arb.set(arb, 0..20).map { Chunk.iterable(it) },
        10 to Arb.array(arb, 0..20).map { Chunk.array(it) },
        10 to Arb.boxedChunk(arb)
      ).values(rs)
  }

inline fun <reified A> Arb.Companion.boxedChunk(arb: Arb<A>): Arb<Chunk<A>> =
  object : Arb<Chunk<A>>() {
    override fun edgecases(): List<Chunk<A>> =
      listOf(Chunk.empty<A>()) + arb.edgecases().map { Chunk(it) }

    override fun values(rs: RandomSource): Sequence<Sample<Chunk<A>>> =
      arrayChunkGenerator(arb, ChunkShrinker()) { values, offset, length ->
        Chunk.boxed(values.toTypedArray(), offset, length)
      }.values(rs)
  }

fun Arb.Companion.booleanChunk(): Arb<Chunk<Boolean>> =
  object : Arb<Chunk<Boolean>>() {
    override fun edgecases(): List<Chunk<Boolean>> =
      listOf(Chunk.empty<Boolean>()) + Arb.bool().edgecases().map { Chunk(it) }

    override fun values(rs: RandomSource): Sequence<Sample<Chunk<Boolean>>> =
      Arb.choice(
        arrayChunkGenerator(Arb.bool(), ChunkShrinker()) { values, offset, length ->
          Chunk.booleans(values.toBooleanArray(), offset, length)
        },
        arrayChunkGenerator(Arb.bool(), ChunkShrinker()) { values, _, _ ->
          Chunk.array(values.toTypedArray())
        }
      ).values(rs)
  }

fun Arb.Companion.byteChunk(): Arb<Chunk<Byte>> =
  object : Arb<Chunk<Byte>>() {
    override fun edgecases(): List<Chunk<Byte>> =
      listOf(Chunk.empty<Byte>()) + Arb.byte().edgecases().map { Chunk(it) }

    override fun values(rs: RandomSource): Sequence<Sample<Chunk<Byte>>> =
      Arb.choice(
        arrayChunkGenerator(Arb.byte(), ChunkShrinker()) { values, offset, length ->
          Chunk.bytes(values.toByteArray(), offset, length)
        },
        arrayChunkGenerator(Arb.byte(), ChunkShrinker()) { values, _, _ ->
          Chunk.array(values.toTypedArray())
        }
      ).values(rs)
  }

fun Arb.Companion.intChunk(): Arb<Chunk<Int>> =
  object : Arb<Chunk<Int>>() {
    override fun edgecases(): List<Chunk<Int>> =
      listOf(Chunk.empty<Int>()) + Arb.int().edgecases().map { Chunk(it) }

    override fun values(rs: RandomSource): Sequence<Sample<Chunk<Int>>> =
      Arb.choice(
        arrayChunkGenerator(Arb.int(), ChunkShrinker()) { values, offset, length ->
          Chunk.ints(values.toIntArray(), offset, length)
        },
        arrayChunkGenerator(Arb.int(), ChunkShrinker()) { values, _, _ ->
          Chunk.array(values.toTypedArray())
        }
      ).values(rs)
  }

fun Arb.Companion.longChunk(): Arb<Chunk<Long>> =
  object : Arb<Chunk<Long>>() {
    override fun edgecases(): List<Chunk<Long>> =
      listOf(Chunk.empty<Long>()) + Arb.long().edgecases().map { Chunk(it) }

    override fun values(rs: RandomSource): Sequence<Sample<Chunk<Long>>> =
      Arb.choice(
        arrayChunkGenerator(Arb.long(), ChunkShrinker()) { values, offset, length ->
          Chunk.longs(values.toLongArray(), offset, length)
        },
        arrayChunkGenerator(Arb.long(), ChunkShrinker()) { values, _, _ ->
          Chunk.array(values.toTypedArray())
        }
      ).values(rs)
  }

fun Arb.Companion.doubleChunk(): Arb<Chunk<Double>> =
  object : Arb<Chunk<Double>>() {
    override fun edgecases(): List<Chunk<Double>> =
      listOf(Chunk.empty<Double>()) + Arb.double().edgecases().map { Chunk(it) }

    override fun values(rs: RandomSource): Sequence<Sample<Chunk<Double>>> =
      Arb.choice(
        arrayChunkGenerator(Arb.double(), ChunkShrinker()) { values, offset, length ->
          Chunk.doubles(values.toDoubleArray(), offset, length)
        },
        arrayChunkGenerator(Arb.double(), ChunkShrinker()) { values, _, _ ->
          Chunk.array(values.toTypedArray())
        }
      ).values(rs)
  }

fun Arb.Companion.floatChunk(): Arb<Chunk<Float>> =
  object : Arb<Chunk<Float>>() {
    override fun edgecases(): List<Chunk<Float>> =
      listOf(Chunk.empty<Float>()) + Arb.float().edgecases().map { Chunk(it) }

    override fun values(rs: RandomSource): Sequence<Sample<Chunk<Float>>> =
      Arb.choice(
        arrayChunkGenerator(Arb.float(), ChunkShrinker()) { values, offset, length ->
          Chunk.floats(values.toFloatArray(), offset, length)
        },
        arrayChunkGenerator(Arb.float(), ChunkShrinker()) { values, _, _ ->
          Chunk.array(values.toTypedArray())
        }
      ).values(rs)
  }

fun Arb.Companion.shortChunk(): Arb<Chunk<Short>> =
  object : Arb<Chunk<Short>>() {
    override fun edgecases(): List<Chunk<Short>> =
      listOf(Chunk.empty<Short>()) + Arb.short().edgecases().map { Chunk(it) }

    override fun values(rs: RandomSource): Sequence<Sample<Chunk<Short>>> =
      Arb.choice(
        arrayChunkGenerator(Arb.short(), ChunkShrinker()) { values, offset, length ->
          Chunk.shorts(values.toShortArray(), offset, length)
        },
        arrayChunkGenerator(Arb.short(), ChunkShrinker()) { values, _, _ ->
          Chunk.array(values.toTypedArray())
        }
      ).values(rs)
  }
