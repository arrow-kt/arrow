package arrow.core

import arrow.core.test.either
import arrow.core.test.ior
import arrow.core.test.option
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds

class IterableTest {
  @Test
  fun flattenOrAccumulateCombine() = runTest(timeout = 30.seconds) {
    checkAll(Arb.list(Arb.either(Arb.string(maxSize = 10), Arb.int()), range = 0..20)) { list ->
      val expected =
        if (list.any { it.isLeft() }) {
          list
            .filterIsInstance<Either.Left<String>>()
            .fold("") { acc, either -> "$acc${either.value}" }
            .left()
        } else {
          list.filterIsInstance<Either.Right<Int>>().map { it.value }.right()
        }

      list.flattenOrAccumulate(String::plus) shouldBe expected
    }
  }

  @Test
  fun flattenOrAccumulateOk() = runTest {
    checkAll(Arb.list(Arb.either(Arb.int(), Arb.int()), range = 0..20)) { list ->
      val expected =
        if (list.any { it.isLeft() }) {
          list
            .filterIsInstance<Either.Left<Int>>()
            .map { it.value }
            .toNonEmptyListOrNull()
            .shouldNotBeNull()
            .left()
        } else {
          list.filterIsInstance<Either.Right<Int>>().map { it.value }.right()
        }

      list.flattenOrAccumulate() shouldBe expected
    }
  }

  @Test
  fun mapOrAccumulateOrder() = runTest {
    val acc = mutableListOf<Int>()
    val range = (0..20_000)
    val res =
      range.mapOrAccumulate(String::plus) {
        acc.add(it)
        it
      }
    res shouldBe acc.right()
    res shouldBe range.toList().right()
  }

  @Test
  fun mapOrAccumulateAccumulates() = runTest {
    fun predicate(i: Int) = i % 2 == 0

    checkAll(Arb.list(Arb.int(), range = 0..20)) { ints ->
      val res = ints.mapOrAccumulate { i -> if (predicate(i)) i else raise(i) }

      val expected: Either<NonEmptyList<Int>, List<Int>> =
        ints.filterNot(::predicate).toNonEmptyListOrNull()?.left()
          ?: ints.filter(::predicate).right()

      res shouldBe expected
    }
  }

  @Test
  fun mapOrAccumulateString() = runTest {
    listOf(1, 2, 3).mapOrAccumulate(String::plus) { raise("fail") } shouldBe
      Either.Left("failfailfail")
  }

  @Test
  fun zip3Ok() = runTest {
    checkAll(Arb.list(Arb.int()), Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b, c ->
      val result = a.zip(b, c, ::Triple)
      val expected = a.zip(b, ::Pair).zip(c) { (a, b), c -> Triple(a, b, c) }
      result shouldBe expected
    }
  }

  @Test
  fun zip4Ok() = runTest {
    checkAll(
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
    ) { a, b, c, d ->
      val result = a.zip(b, c, d, ::Tuple4)
      val expected =
        a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }

      result shouldBe expected
    }
  }

  @Test
  fun zip5Ok() = runTest {
    checkAll(
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
    ) { a, b, c, d, e ->
      val result = a.zip(b, c, d, e, ::Tuple5)
      val expected =
        a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }

      result shouldBe expected
    }
  }

  @Test
  fun zip6Ok() = runTest {
    checkAll(
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
    ) { a, b, c, d, e, f ->
      val result = a.zip(b, c, d, e, f, ::Tuple6)
      val expected =
        a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }

      result shouldBe expected
    }
  }

  @Test
  fun zip7Ok() = runTest {
    checkAll(
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
    ) { a, b, c, d, e, f, g ->
      val result = a.zip(b, c, d, e, f, g, ::Tuple7)
      val expected =
        a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
          .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }

      result shouldBe expected
    }
  }

  @Test
  fun zip8Ok() = runTest {
    checkAll(
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
    ) { a, b, c, d, e, f, g, h ->
      val result = a.zip(b, c, d, e, f, g, h, ::Tuple8)
      val expected =
        a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
          .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }
          .zip(h) { (a, b, c, d, e, f, g), h -> Tuple8(a, b, c, d, e, f, g, h) }

      result shouldBe expected
    }
  }

  @Test
  fun zip9Ok() = runTest {
    checkAll(
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
    ) { a, b, c, d, e, f, g, h, i ->
      val result = a.zip(b, c, d, e, f, g, h, i, ::Tuple9)
      val expected =
        a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
          .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }
          .zip(h) { (a, b, c, d, e, f, g), h -> Tuple8(a, b, c, d, e, f, g, h) }
          .zip(i) { (a, b, c, d, e, f, g, h), i -> Tuple9(a, b, c, d, e, f, g, h, i) }

      result shouldBe expected
    }
  }

  @Test
  fun zip10Ok() = runTest {
    data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(
      val first: A,
      val second: B,
      val third: C,
      val fourth: D,
      val fifth: E,
      val sixth: F,
      val seventh: G,
      val eighth: H,
      val ninth: I,
      val tenth: J,
    )

    checkAll(
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
      Arb.list(Arb.int()),
    ) { a, b, c, d, e, f, g, h, i, j ->
      val result = a.zip(b, c, d, e, f, g, h, i, j, ::Tuple10)
      val expected =
        a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
          .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }
          .zip(h) { (a, b, c, d, e, f, g), h -> Tuple8(a, b, c, d, e, f, g, h) }
          .zip(i) { (a, b, c, d, e, f, g, h), i -> Tuple9(a, b, c, d, e, f, g, h, i) }
          .zip(j) { (a, b, c, d, e, f, g, h, i), j -> Tuple10(a, b, c, d, e, f, g, h, i, j) }

      result shouldBe expected
    }
  }

  @Test
  fun alignDifferentLength() = runTest {
    checkAll(Arb.list(Arb.boolean()), Arb.list(Arb.boolean())) { a, b ->
      a.align(b).size shouldBe max(a.size, b.size)
    }

    checkAll(Arb.list(Arb.boolean()), Arb.list(Arb.boolean())) { a, b ->
      a.align(b).take(min(a.size, b.size)).forEach { it.isBoth() shouldBe true }
    }

    checkAll(Arb.list(Arb.boolean()), Arb.list(Arb.boolean())) { a, b ->
      a.align(b).drop(min(a.size, b.size)).forEach {
        if (a.size < b.size) {
          it.isRight() shouldBe true
        } else {
          it.isLeft() shouldBe true
        }
      }
    }
  }

  @Test
  fun alignCompareContents() = runTest {
    checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
      val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
      val right = b.map { it } + List(max(0, a.count() - b.count())) { null }
      val expected = left.zip(right) { l, r ->
        Ior.fromNullables(l, r)
      }

      a.align(b) shouldBe expected
    }
  }

  @Test
  fun leftPadZipMap() = runTest {
    checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
      val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
      val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

      val result = a.leftPadZip(b) { a, b -> a to b }

      result shouldBe left.zip(right) { l, r -> l to r }.filter { it.second != null }
    }
  }

  @Test
  fun leftPadZipNoMap() = runTest {
    checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
      val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
      val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

      val result = a.leftPadZip(b)

      result shouldBe left.zip(right) { l, r -> l to r }.filter { it.second != null }
    }
  }

  @Test
  fun rightPadZipNoMap() = runTest {
    checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
      val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
      val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

      val result = a.rightPadZip(b)

      result shouldBe left.zip(right) { l, r -> l to r }.filter { it.first != null }
      result.map { it.first } shouldBe a
    }
  }

  @Test
  fun rightPadZipMap() = runTest {
    checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
      val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
      val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

      val result = a.rightPadZip(b) { a, b -> a to b }

      result shouldBe left.zip(right) { l, r -> l to r }.filter { it.first != null }
      result.map { it.first } shouldBe a
    }
  }

  @Test
  fun padZipOk() = runTest {
    checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
      val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
      val right = b.map { it } + List(max(0, a.count() - b.count())) { null }
      a.padZip(b) { l, r -> Ior.fromNullables(l, r) } shouldBe
        left.zip(right) { l, r -> Ior.fromNullables(l, r) }
    }
  }

  @Test
  fun padZipNull() = runTest {
    checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
      val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
      val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

      a.padZip(b) shouldBe left.zip(right) { l, r -> l to r }
    }
  }

  @Test
  fun filterOptionOk() = runTest {
    checkAll(Arb.list(Arb.option(Arb.int()))) { listOfOption ->
      listOfOption.filterOption() shouldBe listOfOption.mapNotNull { it.getOrNull() }
    }
  }

  @Test
  fun flattenOptionOk() = runTest {
    checkAll(Arb.list(Arb.option(Arb.int()))) { listOfOption ->
      listOfOption.flattenOption() shouldBe listOfOption.mapNotNull { it.getOrNull() }
    }
  }

  @Test
  fun separateEitherOk() = runTest {
    fun predicate(i: Int) = i % 2 == 0

    checkAll(Arb.list(Arb.int())) { ints ->
      val pairOfLists = ints.separateEither { if (predicate(it)) it.left() else it.right() }
      pairOfLists shouldBe ints.partition(::predicate)
    }
  }

  @Test
  fun unalignInverseOfAlign() = runTest {
    fun <A, B> Pair<List<A?>, List<B?>>.fix(): Pair<List<A>, List<B>> = first.mapNotNull { it } to second.mapNotNull { it }

    checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
      a.align(b).unalign().fix() shouldBe (a to b)
    }
  }

  @Test
  fun alignInverseOfUnalign() = runTest {
    fun <A, B> Ior<A?, B?>.fix(): Ior<A, B> = fold(
      { Ior.Left(it!!) },
      { Ior.Right(it!!) },
      { a, b ->
        when {
          a == null -> Ior.Right(b!!)
          b == null -> Ior.Left(a)
          else -> Ior.Both(a, b)
        }
      },
    )

    checkAll(Arb.list(Arb.ior(Arb.int(), Arb.int()))) { xs ->
      val (a, b) = xs.unalign()
      a.align(b) { it.fix() } shouldBe xs
    }
  }

  @Test
  fun unalignOk() = runTest {
    checkAll(Arb.list(Arb.ior(Arb.int(), Arb.int()))) { xs ->
      xs.unalign { it } shouldBe xs.unalign()
    }
  }

  @Test
  fun reduceOrNullCompatibleWithReduce() = runTest {
    checkAll(Arb.list(Arb.int())) { xs ->
      val rs = xs.reduceOrNull({ it }) { a, b -> a + b }

      if (xs.isEmpty()) {
        rs.shouldBeNull()
      } else {
        rs shouldBe xs.reduce { a, b -> a + b }
      }
    }
  }

  @Test
  fun reduceRightNullCompatibleWithReduce() = runTest {
    checkAll(Arb.list(Arb.int())) { xs ->
      val rs = xs.reduceRightNull({ it }) { a, b -> a + b }

      if (xs.isEmpty()) {
        rs.shouldBeNull()
      } else {
        rs shouldBe xs.reduceRight { a, b -> a + b }
      }
    }
  }

  @Test
  fun flattenOrAccumulateEitherNel() = runTest {
    checkAll(
      Arb.list(
        Arb.either(
          Arb.list(Arb.string(0..5), 0..5),
          Arb.int(),
        ),
        0..10,
      ),
    ) { list ->
      val nelslist = list.mapNotNull { it.withLeftListAsNelOrNull() }

      val expected =
        if (nelslist.any { it.isLeft() }) {
          nelslist
            .filterIsInstance<Either.Left<NonEmptyList<String>>>()
            .map { it.value }
            .flatten()
            .toNonEmptyListOrNull()
            .shouldNotBeNull()
            .left()
        } else {
          nelslist.filterIsInstance<Either.Right<Int>>().map { it.value }.right()
        }

      nelslist.flattenOrAccumulate() shouldBe expected
    }
  }

  @Test
  fun flattenOrAccumulateEitherNelCombine() = runTest {
    checkAll(
      Arb.list(
        Arb.either(
          Arb.list(Arb.string(0..5), 0..5),
          Arb.int(),
        ),
        0..10,
      ),
    ) { list ->
      val nelslist = list.mapNotNull { it.withLeftListAsNelOrNull() }

      val expected =
        if (nelslist.any { it.isLeft() }) {
          nelslist
            .filterIsInstance<Either.Left<NonEmptyList<String>>>()
            .fold("") { accA, either ->
              "$accA${
                either.value.fold("") {accB, entry -> "$accB$entry"}
              }"
            }
            .left()
        } else {
          nelslist.filterIsInstance<Either.Right<Int>>().map { it.value }.right()
        }

      nelslist.flattenOrAccumulate(String::plus) shouldBe expected
    }
  }

  @Test
  fun separateIor() = runTest {
    checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
      a.align(b).separateIor() shouldBe (a to b)
    }
  }

  @Test
  fun unweave() = runTest {
    checkAll(Arb.list(Arb.int(), 0..10), Arb.list(Arb.int(), 0..10)) { a, incr ->
      fun trans(i: Int) = incr.map { it + i }

      var expected = emptyList<Int>()
      a.reversed().forEach { n ->
        expected = trans(n).interleave(expected)
      }

      a.unweave(::trans) shouldBe expected
    }
  }

  @Test
  fun crosswalk() = runTest {
    checkAll(Arb.list(Arb.int(), 0..10), Arb.list(Arb.int(), 0..10)) { a, incr ->
      fun translst(i: Int) = incr.map { it + i }
      val transarr: List<(Int) -> Int> = incr.map { i -> { it + i } }

      val expected = mutableListOf<List<Int>>()
      val arev = a.reversed()
      transarr.forEach { t ->
        arev.map { t(it) }.takeIf { it.isNotEmpty() }
          ?.let(expected::add)
      }

      a.crosswalk(::translst) shouldBe expected
    }
  }

  @Test
  fun crosswalkMap() = runTest {
    checkAll(Arb.list(Arb.int(), 0..10), Arb.list(Arb.int(), 0..10)) { a, incr ->
      val translst = { i: Int -> incr.map { it + i } }
      fun transmap(i: Int) = mapOf(i to translst(i))

      val expected = mutableMapOf<Int, MutableList<List<Int>>>()
      a.forEach { n ->
        val lst = translst(n)
        expected[n]?.add(lst) ?: expected.put(n, mutableListOf(lst))
      }

      a.crosswalkMap(::transmap) shouldBe expected
    }
  }

  @Test
  fun crosswalkNull() = runTest {
    checkAll(Arb.list(Arb.int(), 0..10), Arb.pair(Arb.int(1..1000), Arb.int())) { a, mod ->
      fun trans(i: Int) = if (i % mod.first == 0) i + mod.second else null

      val expected = a.reversed().mapNotNull(::trans)
      a.crosswalkNull(::trans) shouldBe expected
    }
  }

  @Test
  fun compareTo() = runTest {
    checkAll(Arb.list(Arb.int(), 0..100), Arb.list(Arb.int(), 0..100)) { a, b ->
      val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
      val right = b.map { it } + List(max(0, a.count() - b.count())) { null }
      val both = left.zip(right)

      val expected = both.firstNotNullOfOrNull {
        val (l, r) = it
        when {
          (l != null && r == null) -> 1
          (l == null && r != null) -> -1
          (l != null && r != null) -> {
            val cmp = l.compareTo(r)
            when {
              cmp > 0 -> 1
              cmp < 0 -> -1
              else -> null
            }
          }
          else -> null
        }
      } ?: 0

      a.compareTo(b) shouldBe expected
    }
  }
}

private fun <E, A> Either<List<E>, A>.withLeftListAsNelOrNull(): Either<NonEmptyList<E>, A>? = when (this) {
  is Either.Left -> {
    value.toNonEmptyListOrNull()?.left()
  }

  is Either.Right -> value.right()
}
