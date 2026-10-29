package arrow.core

import arrow.core.raise.ensure
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.option
import arrow.core.test.sequence
import arrow.core.test.testLaws
import arrow.core.test.unit
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test

class SequenceKTest {

  @Test fun monoidLaws() = testLaws(MonoidLaws("Sequence", emptySequence(), { a, b -> sequenceOf(a, b).flatten() }, Arb.sequence(Arb.int())) { s1, s2 -> s1.toList() == s2.toList() })

  @Test fun zip3Ok() = runTest {
    checkAll(Arb.sequence(Arb.int()), Arb.sequence(Arb.int()), Arb.sequence(Arb.int())) { a, b, c ->
      val result = a.zip(b, c, ::Triple)
      val expected = a.zip(b, ::Pair).zip(c) { (a, b), c -> Triple(a, b, c) }
      result.toList() shouldBe expected.toList()
    }
  }

  @Test fun zip4Ok() = runTest {
    checkAll(
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
    ) { a, b, c, d ->
      val result = a.zip(b, c, d, ::Tuple4)
      val expected = a.zip(b, ::Pair)
        .zip(c) { (a, b), c -> Triple(a, b, c) }
        .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }

      result.toList() shouldBe expected.toList()
    }
  }

  @Test fun zip5Ok() = runTest {
    checkAll(
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
    ) { a, b, c, d, e ->
      val result = a.zip(b, c, d, e, ::Tuple5)
      val expected = a.zip(b, ::Pair)
        .zip(c) { (a, b), c -> Triple(a, b, c) }
        .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
        .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }

      result.toList() shouldBe expected.toList()
    }
  }

  @Test fun zip6Ok() = runTest {
    checkAll(
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
    ) { a, b, c, d, e, f ->
      val result = a.zip(b, c, d, e, f, ::Tuple6)
      val expected = a.zip(b, ::Pair)
        .zip(c) { (a, b), c -> Triple(a, b, c) }
        .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
        .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
        .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }

      result.toList() shouldBe expected.toList()
    }
  }

  @Test fun zip7Ok() = runTest {
    checkAll(
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
    ) { a, b, c, d, e, f, g ->
      val result = a.zip(b, c, d, e, f, g, ::Tuple7)
      val expected = a.zip(b, ::Pair)
        .zip(c) { (a, b), c -> Triple(a, b, c) }
        .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
        .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
        .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
        .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }

      result.toList() shouldBe expected.toList()
    }
  }

  @Test fun zip8Ok() = runTest {
    checkAll(
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
    ) { a, b, c, d, e, f, g, h ->
      val result = a.zip(b, c, d, e, f, g, h, ::Tuple8)
      val expected = a.zip(b, ::Pair)
        .zip(c) { (a, b), c -> Triple(a, b, c) }
        .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
        .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
        .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
        .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }
        .zip(h) { (a, b, c, d, e, f, g), h -> Tuple8(a, b, c, d, e, f, g, h) }

      result.toList() shouldBe expected.toList()
    }
  }

  @Test fun zip9Ok() = runTest {
    checkAll(
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
    ) { a, b, c, d, e, f, g, h, i ->
      val result = a.zip(b, c, d, e, f, g, h, i, ::Tuple9)
      val expected = a.zip(b, ::Pair)
        .zip(c) { (a, b), c -> Triple(a, b, c) }
        .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
        .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
        .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
        .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }
        .zip(h) { (a, b, c, d, e, f, g), h -> Tuple8(a, b, c, d, e, f, g, h) }
        .zip(i) { (a, b, c, d, e, f, g, h), i -> Tuple9(a, b, c, d, e, f, g, h, i) }

      result.toList() shouldBe expected.toList()
    }
  }
  
  @Test fun zip10Ok() = runTest {
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
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
      Arb.sequence(Arb.int()),
    ) { a, b, c, d, e, f, g, h, i, j ->
      val result = a.zip(b, c, d, e, f, g, h, i, j, ::Tuple10)

      val expected = a.zip(b, ::Pair)
        .zip(c) { (a, b), c -> Triple(a, b, c) }
        .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
        .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
        .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
        .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }
        .zip(h) { (a, b, c, d, e, f, g), h -> Tuple8(a, b, c, d, e, f, g, h) }
        .zip(i) { (a, b, c, d, e, f, g, h), i -> Tuple9(a, b, c, d, e, f, g, h, i) }
        .zip(j) { (a, b, c, d, e, f, g, h, i), j -> Tuple10(a, b, c, d, e, f, g, h, i, j) }

      result.toList() shouldBe expected.toList()
    }
  }

  @Test fun crosswalkOk() = runTest {
    checkAll(Arb.list(Arb.int())) { list ->
      val obtained = list.asSequence().crosswalk { listOf(it) }
      val expected = if (list.isEmpty()) {
        emptyList()
      } else {
        listOf(list.map { it })
      }
      obtained.map { it.sorted() } shouldBe expected.map { it.sorted() }
    }
  }

  @Test fun align1() = runTest {
    checkAll(Arb.sequence(Arb.unit()), Arb.sequence(Arb.unit())) { a, b ->
      a.align(b).toList().size shouldBe max(a.toList().size, b.toList().size)
    }
  }

  @Test fun align2() = runTest {
    checkAll(Arb.sequence(Arb.unit()), Arb.sequence(Arb.unit())) { a, b ->
      a.align(b).take(min(a.toList().size, b.toList().size)).forEach {
        it.isBoth() shouldBe true
      }
    }
  }

  @Test fun align3() = runTest {
    checkAll(Arb.sequence(Arb.unit()), Arb.sequence(Arb.unit())) { a, b ->
      val ls = a.toList()
      val rs = b.toList()
      a.align(b).drop(min(ls.size, rs.size)).forEach {
        if (ls.size < rs.size) {
          it.isRight() shouldBe true
        } else {
          it.isLeft() shouldBe true
        }
      }
    }
  }

  @Test fun alignEmpty() = runTest {
    val a = emptyList<String>().asSequence()
    a.align(a).shouldBeEmpty()
  }

  @Test fun alignInfinite() = runTest {
    val seq1 = generateSequence("A") { it }
    val seq2 = generateSequence(0) { it + 1 }

    checkAll(10, Arb.positiveInt(max = 10_000)) { idx: Int ->
      val element = seq1.align(seq2).drop(idx).first()

      element shouldBe Ior.Both("A", idx)
    }
  }

  @Test fun mapNotNullOk() = runTest {
    checkAll(Arb.sequence(Arb.int())) { a ->
      val result = a.mapNotNull {
        when (it % 2 == 0) {
          true -> it.toString()
          else -> null
        }
      }
      val expected =
        a.toList()
          .mapNotNull {
            when (it % 2 == 0) {
              true -> it.toString()
              else -> null
            }
          }
          .asSequence()

      result.toList() shouldBe expected.toList()
    }
  }

  @Test fun filterOptionOk() = runTest {
    checkAll(Arb.list(Arb.option(Arb.int()))) { ints ->
      ints.asSequence().filterOption().toList() shouldBe ints.filterOption()
    }
  }

  @Test fun separateEitherOk() = runTest {
    checkAll(Arb.sequence(Arb.int())) { ints ->
      val sequence = ints.map {
        if (it % 2 == 0) {
          it.left()
        } else {
          it.right()
        }
      }

      val (lefts, rights) = sequence.separateEither()

      lefts.toList() to rights.toList() shouldBe ints.partition { it % 2 == 0 }
    }
  }


  @Test fun crosswalk() = runTest {
    checkAll(Arb.list(Arb.pair(Arb.int(0..5), Arb.int()), 0..10)) { a ->
      fun transform(pair: Pair<Int, Int>): List<String> = buildList {
        repeat(pair.first) {
          add((pair.second + it).toString())
        }
      }

      val expected = if (a.isEmpty()) {
        emptyList()
      } else {
        val dest: Array<MutableList<String>>

        a.reversed()
          .map(::transform)
          .also { list ->
            dest = Array(list.maxBy { it.size }.size) { mutableListOf() }
          }
          .forEach { list ->
            list.forEachIndexed { i, s ->
              dest[i].add(s)
            }
          }

        dest.toList()
      }

      a.asSequence().crosswalk(::transform) shouldBe expected
    }
  }

  @Test fun crosswalkNull() = runTest {
    checkAll(Arb.list(Arb.pair(Arb.boolean(), Arb.int()), 0..10)) { a ->
      fun transform(pair: Pair<Boolean, Int>): String? = if (pair.first) pair.first.toString() else null

      val expected = if (a.isEmpty()) {
        emptyList()
      } else {
        a.reversed()
          .mapNotNull(::transform)
      }

      a.asSequence().crosswalkNull(::transform) shouldBe expected
    }
  }

  @Test fun leftPadZip() = runTest {
    val range = 0..10
    checkAll(Arb.list(Arb.int(), range), Arb.list(Arb.string(0..3), range)) { a, b ->
      val expected = b.mapIndexed { i, s ->
        a.getOrNull(i)?.let { it to s } ?: (null to s)
      }

      a.asSequence().leftPadZip(b.asSequence()).toList() shouldBe expected
    }
  }

  @Test fun rightPadZip() = runTest {
    val range = 0..10
    checkAll(Arb.list(Arb.int(), range), Arb.list(Arb.string(0..3), range)) { a, b ->
      val expected = a.mapIndexed { i, s ->
        s to b.getOrNull(i)
      }

      a.asSequence().rightPadZip(b.asSequence()).toList() shouldBe expected
    }
  }

  @Test fun rightPadZipFunctor() = runTest {
    val range = 0..10
    checkAll(Arb.list(Arb.int(), range), Arb.list(Arb.int(), range)) { a, b ->
      fun transform(i: Int, j: Int?) = i + (j ?: 0)

      val expected = a.mapIndexed { i, n ->
        transform(n, b.getOrNull(i))
      }

      a.asSequence().rightPadZip(b.asSequence(), ::transform).toList() shouldBe expected
    }
  }

  @Test fun padZip() = runTest {
    val range = 0..10
    checkAll(Arb.list(Arb.int(), range), Arb.list(Arb.string(0..3), range)) { a, b ->
      val max = max(a.size, b.size)
      val expected = (0 until max).map { i ->
        a.getOrNull(i) to b.getOrNull(i)
      }

      a.asSequence().padZip(b.asSequence()).toList() shouldBe expected
    }
  }

  @Test fun many() = runTest {
    checkAll(Arb.list(Arb.pair(Arb.int(0..5), Arb.int()), 0..10)) { a ->
      val expected = if (a.isEmpty()) {
        listOf(emptyList())
      } else {
        a.map { pair ->
          Array(pair.first) { pair.second }.toList()
        }
      }

      val actual = a.map { it.second }.asSequence().many()

      val actualList = actual.toList().mapIndexed { i, seq ->
        if (a.isEmpty()) {
          emptyList()
        } else {
          seq.take(a[i].first) // take pair.first elements from unbounded sequence
            .toList()
        }
      }

      actualList shouldBe expected
    }
  }

  @Test fun once() = runTest {
    checkAll(Arb.list(Arb.int(), 0..5)) { a ->
      val expected = if (a.isNotEmpty()) a.take(1) else a

      a.asSequence().once().toList() shouldBe expected
    }
  }

  @Test fun salign() = runTest {
    val range = 0..10
    checkAll(Arb.list(Arb.int(), range), Arb.list(Arb.int(), range)) { a, b ->
      fun combine(i: Int, j: Int) = i + j

      val max = max(a.size, b.size)
      val expected = (0 until max).map { i ->
        val an = a.getOrNull(i)
        val bn = b.getOrNull(i)
        if (an == null) {
          bn
        } else {
          if (bn == null) an else combine(an, bn)
        }
      }

      a.asSequence().salign(b.asSequence(), ::combine).toList() shouldBe expected
    }
  }

  @Test fun mapOrAccumulateCombine() = runTest {
    val codepoints = Arb.of((('a'..'b') + ('0'..'9')).map { Codepoint(it.code) })
    checkAll(20, Arb.list(Arb.string(1, codepoints), 0..5)) { a ->

      fun combine(s1: String, s2: String) = "$s1,$s2"

      val errors = mutableListOf<String>()
      val successes = a.mapNotNull { s ->
        runCatching { s.toInt() }
          .onFailure { errors.add(s) }
          .getOrNull()
      }

      val expected = if (errors.isNotEmpty()) {
        errors.joinToString(",").left()
      } else {
        successes.right()
      }

      a.asSequence().mapOrAccumulate(::combine) {
        ensure(it[0].isDigit()) { it }
        it.toInt()
      } shouldBe expected
    }
  }

  @Test fun mapOrAccumulate() = runTest {
    val codepoints = Arb.of((('a'..'b') + ('0'..'9')).map { Codepoint(it.code) })
    checkAll(20, Arb.list(Arb.string(1, codepoints), 0..5)) { a ->

      val errors = mutableListOf<String>()
      val successes = a.mapNotNull { s ->
        s.toIntOrNull() ?: run {
          errors.add(s)
          null
        }
      }

      a.asSequence().mapOrAccumulate {
        ensure(it[0].isDigit()) { it }
        it.toInt()
      } shouldBe (errors.toNonEmptyListOrNull()?.left() ?: successes.right())
    }
  }

  @Test fun unalign() = runTest {
    val range = 0..10
    checkAll(Arb.list(Arb.int(), range), Arb.list(Arb.int(), range)) { a, b ->
      val max = max(a.size, b.size)
      val initial = (0 until max).map { i ->
        Ior.fromNullables(
          a.getOrNull(i),
          b.getOrNull(i),
        ).shouldNotBeNull()
      }

      initial.asSequence().unalign().let {
        it.first.toList() shouldBe a
        it.second.toList() shouldBe b
      }
    }
  }

  @Test fun unalignTransform() = runTest {
    checkAll(Arb.list(Arb.int(), 0..30)) { a ->
      fun transform(i: Int) = when (i) {
        in 0..10 -> i.toShort().leftIor()
        in 11..20 -> i.toLong().rightIor()
        else -> (i.toShort() to i.toLong()).bothIor()
      }

      val lefts = mutableListOf<Short>()
      val rights = mutableListOf<Long>()
      a.map(::transform)
        .forEach {
          when (it) {
            is Ior.Left -> lefts.add(it.value)
            is Ior.Right -> rights.add(it.value)
            is Ior.Both -> {
              lefts.add(it.leftValue)
              rights.add(it.rightValue)
            }
          }
        }

      a.asSequence().unalign(::transform).let {
        it.first.toList() shouldBe lefts
        it.second.toList() shouldBe rights
      }
    }
  }

  @Test fun unzip() = runTest {
    checkAll(Arb.list(Arb.int())) { a ->
      fun transform(i: Int) = i / 2 to i

      val first = mutableListOf<Int>()
      val second = mutableListOf<Int>()
      a.map(::transform).forEach {
        first.add(it.first)
        second.add(it.second)
      }

      
      a.asSequence().unzip(::transform).let {
        it.first.toList() shouldBe first
        it.second.toList() shouldBe second
      }
    }
  }
}
