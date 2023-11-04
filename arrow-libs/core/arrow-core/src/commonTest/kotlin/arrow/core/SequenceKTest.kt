@file:Suppress("DUPLICATE_PARAMETER_NAME_IN_FUNCTION_TYPE")

package arrow.core

import arrow.core.test.laws.MonoidLaws
import arrow.core.test.option
import arrow.core.test.sequence
import arrow.core.test.testLaws
import arrow.core.test.unit
import arrow.typeclasses.Semigroup
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.checkAll
import kotlin.math.max
import kotlin.math.min

class SequenceKTest : StringSpec({

    testLaws(MonoidLaws("Sequence", emptySequence(), { a, b -> sequenceOf(a, b).flatten()} , Arb.sequence(Arb.int())) { s1, s2 -> s1.toList() == s2.toList() })

    "traverse for Either stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = generateSequence(0) { it + 1 }.traverse { a ->
        if (a > 20_000) {
          Either.Left(Unit)
        } else {
          acc.add(a)
          Either.Right(a)
        }
      }
      acc shouldBe (0..20_000).toList()
      res shouldBe Either.Left(Unit)
    }

    "traverse for Option stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = generateSequence(0) { it + 1 }.traverse { a ->
        if ((a <= 20_000)) {
          acc.add(a)
          Some(a)
        } else {
          None
        }
      }
      acc shouldBe (0..20_000).toList()
      res shouldBe None
    }

    "mapOrAccumlate for Either stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).asSequence().mapOrAccumulate(String::plus) {
        acc.add(it)
        Either.Right(it).bind()
      }.map { it.toList() }
      res shouldBe Either.Right(acc)
      res shouldBe Either.Right((0..20_000).toList())
    }

    "traverse for Validated acummulates" {
      checkAll(Arb.sequence(Arb.int())) { ints ->
        val res: ValidatedNel<Int, List<Int>> = ints.map { i -> if (i % 2 == 0) i.validNel() else i.invalidNel() }
          .sequence(Semigroup.nonEmptyList())

        val expected: ValidatedNel<Int, Sequence<Int>> =
          ints.filterNot { it % 2 == 0 }.toList()
            .toNonEmptyListOrNull()?.invalid() ?: ints.filter { it % 2 == 0 }.validNel()

        res.map { it.toList() } shouldBe expected.map { it.toList() }
      }
    }

    "zip3" {
      checkAll(Arb.sequence(Arb.int()), Arb.sequence(Arb.int()), Arb.sequence(Arb.int())) { a, b, c ->
        val result = a.zip(b, c, ::Triple)
        val expected = a.zip(b, ::Pair).zip(c) { (a, b), c -> Triple(a, b, c) }
        result.toList() shouldBe expected.toList()
      }
    }

    "zip4" {
      checkAll(
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int())
      ) { a, b, c, d ->
        val result = a.zip(b, c, d, ::Tuple4)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }

        result.toList() shouldBe expected.toList()
      }
    }

    "zip5" {
      checkAll(
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int())
      ) { a, b, c, d, e ->
        val result = a.zip(b, c, d, e, ::Tuple5)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }

        result.toList() shouldBe expected.toList()
      }
    }

    "zip6" {
      checkAll(
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int())
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

    "zip7" {
      checkAll(
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int())
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

    "zip8" {
      checkAll(
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int())
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

    "zip9" {
      checkAll(
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int()),
        Arb.sequence(Arb.int())
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

    "zip10" {
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
        Arb.sequence(Arb.int())
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

    "crosswalk the sequence to a List function" {
      checkAll(Arb.list(Arb.int())){ list ->
        val obtained = list.asSequence().crosswalk { listOf(it) }
        val expected = if (list.isEmpty()) emptyList()
                      else listOf(list.map { it })
        obtained.map{ it.sorted() } shouldBe expected.map { it.sorted() }
      }
    }

    "can align sequences - 1" {
      checkAll(Arb.sequence(Arb.unit()), Arb.sequence(Arb.unit())) { a, b ->
        a.align(b).toList().size shouldBe max(a.toList().size, b.toList().size)
      }
    }

    "can align sequences - 2" {
      checkAll(Arb.sequence(Arb.unit()), Arb.sequence(Arb.unit())) { a, b ->
        a.align(b).take(min(a.toList().size, b.toList().size)).forEach {
          it.isBoth() shouldBe true
        }
      }
    }

    "can align sequences - 3" {
      checkAll(Arb.sequence(Arb.unit()), Arb.sequence(Arb.unit())) { a, b ->
        val ls = a.toList()
        val rs = b.toList()
        a.align(b).drop(min(ls.size, rs.size)).forEach {
          if (ls.size < rs.size) it.isRight() shouldBe true
          else it.isLeft() shouldBe true
        }
      }
    }

    "align empty sequences" {
      val a = emptyList<String>().asSequence()
      a.align(a).shouldBeEmpty()
    }

    "align infinite sequences" {
      val seq1 = generateSequence("A") { it }

      val seq2 = generateSequence(0) { it + 1 }

      checkAll(10, Arb.positiveInt(max = 10_000)) { idx: Int ->
        val element = seq1.align(seq2).drop(idx).first()

        element shouldBe Ior.Both("A", idx)
      }
    }

    "mapNotNull" {
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

  "filterOption should filter None" {
      checkAll(Arb.list(Arb.option(Arb.int()))) { ints ->
        ints.asSequence().filterOption().toList() shouldBe ints.filterOption()
      }
    }

    "separateEither" {
      checkAll(Arb.sequence(Arb.int())) { ints ->
        val sequence = ints.map {
          if (it % 2 == 0) it.left()
          else it.right()
        }

        val (lefts, rights) = sequence.separateEither()

        lefts.toList() to rights.toList() shouldBe ints.partition { it % 2 == 0 }
      }
    }

    "separateValidated" {
      checkAll(Arb.sequence(Arb.int())) { ints ->
        val sequence = ints.map {
          if (it % 2 == 0) it.invalid()
          else it.valid()
        }

        val (invalids, valids) = sequence.separateValidated()

        invalids.toList() to valids.toList() shouldBe ints.partition { it % 2 == 0 }
      }
    }
})
