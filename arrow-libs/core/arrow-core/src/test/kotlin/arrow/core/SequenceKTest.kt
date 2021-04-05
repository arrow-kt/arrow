package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.sequence
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import io.kotlintest.matchers.sequences.shouldBeEmpty
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlin.math.max
import kotlin.math.min

class SequenceKTest : UnitSpec() {

  init {

    testLaws(MonoidLaws.laws(Monoid.sequence(), Gen.sequence(Gen.int())) { s1, s2 -> s1.toList() == s2.toList() })

    "traverseEither stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = generateSequence(0) { it + 1 }.traverseEither { a ->
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

    "traverseValidated stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).asSequence().traverseValidated(Semigroup.string()) {
        acc.add(it)
        Validated.Valid(it)
      }.map { it.toList() }
      res shouldBe Validated.Valid(acc)
      res shouldBe Validated.Valid((0..20_000).toList())
    }

    "traverseValidated acummulates" {
      forAll(Gen.list(Gen.int())) { ints ->
        val ints = ints.asSequence()
        val res: ValidatedNel<Int, Sequence<Int>> = ints.map { i -> if (i % 2 == 0) i.validNel() else i.invalidNel() }
          .sequenceValidated(Semigroup.nonEmptyList())

        val expected: ValidatedNel<Int, Sequence<Int>> = NonEmptyList.fromList(ints.filterNot { it % 2 == 0 }.toList())
          .fold({ ints.filter { it % 2 == 0 }.validNel() }, { it.invalid() })

        res.map { it.toList() } == expected.map { it.toList() }
      }
    }

    "zip3" {
      forAll(Gen.sequence(Gen.int()), Gen.sequence(Gen.int()), Gen.sequence(Gen.int())) { a, b, c ->
        val result = a.zip(b, c, ::Triple)
        val expected = a.zip(b, ::Pair).zip(c) { (a, b), c -> Triple(a, b, c) }
        result.toList() == expected.toList()
      }
    }

    "zip4" {
      forAll(Gen.sequence(Gen.int()), Gen.sequence(Gen.int()), Gen.sequence(Gen.int()), Gen.sequence(Gen.int())) { a, b, c, d ->
        val result = a.zip(b, c, d, ::Tuple4)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }

        result.toList() == expected.toList()
      }
    }

    "zip5" {
      forAll(
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int())
      ) { a, b, c, d, e ->
        val result = a.zip(b, c, d, e, ::Tuple5)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }

        result.toList() == expected.toList()
      }
    }

    "zip6" {
      forAll(
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int())
      ) { a, b, c, d, e, f ->
        val result = a.zip(b, c, d, e, f, ::Tuple6)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }

        result.toList() == expected.toList()
      }
    }

    "zip7" {
      forAll(
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int())
      ) { a, b, c, d, e, f, g ->
        val result = a.zip(b, c, d, e, f, g, ::Tuple7)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
          .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }

        result.toList() == expected.toList()
      }
    }

    "zip8" {
      forAll(
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int())
      ) { a, b, c, d, e, f, g, h ->
        val result = a.zip(b, c, d, e, f, g, h, ::Tuple8)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
          .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }
          .zip(h) { (a, b, c, d, e, f, g), h -> Tuple8(a, b, c, d, e, f, g, h) }

        result.toList() == expected.toList()
      }
    }

    "zip9" {
      forAll(
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int())
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

        result.toList() == expected.toList()
      }
    }

    "zip10" {
      forAll(
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int()),
        Gen.sequence(Gen.int())
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

        result.toList() == expected.toList()
      }
    }

    "can align sequences" {
      forAll(Gen.sequence(Gen.int()), Gen.sequence(Gen.string())) { a, b ->
        a.align(b).toList().size == max(a.toList().size, b.toList().size)
      }

      forAll(Gen.sequence(Gen.int()), Gen.sequence(Gen.string())) { a, b ->
        a.align(b).take(min(a.toList().size, b.toList().size)).all {
          it.isBoth
        }
      }

      forAll(Gen.sequence(Gen.int()), Gen.sequence(Gen.string())) { a, b ->
        val ls = a.toList()
        val rs = b.toList()
        a.align(b).drop(min(ls.size, rs.size)).all {
          if (ls.size < rs.size) {
            it.isRight
          } else {
            it.isLeft
          }
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

      forAll(10, Gen.positiveIntegers().filter { it < 10_000 }) { idx: Int ->
        val element = seq1.align(seq2).drop(idx).first()

        element == Ior.Both("A", idx)
      }
    }

    "mapNotNull" {
      forAll(Gen.sequence(Gen.int())) { a ->
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

        result.toList() == expected.toList()
      }
    }
  }
}
