package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.option
import arrow.typeclasses.Semigroup
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import kotlin.math.max
import kotlin.math.min

class IterableTest : UnitSpec() {
  init {
    "traverseEither stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).traverseEither { a ->
        acc.add(a)
        Either.Right(a)
      }
      res shouldBe Either.Right(acc)
      res shouldBe Either.Right((0..20_000).toList())
    }

    "traverseEither short-circuit" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverseEither {
          if (it % 2 == 0) {
            acc.add(it)
            Either.Right(it)
          } else Either.Left(it)
        }
        acc shouldBe ints.takeWhile { it % 2 == 0 }
        when (evens) {
          is Either.Right -> evens.value shouldBe ints
          is Either.Left -> evens.value shouldBe ints.first { it % 2 != 0 }
        }
      }
    }

    "sequenceEither should be consistent with traverseEither" {
      checkAll(Arb.list(Arb.int())) { ints ->
        ints.map { it.right() }.sequenceEither() shouldBe ints.traverseEither { it.right() }
      }
    }

    "traverseOption is stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).traverseOption { a ->
        acc.add(a)
        Some(a)
      }
      res shouldBe Some(acc)
      res shouldBe Some((0..20_000).toList())
    }

    "traverseOption short-circuits" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverseOption {
          (it % 2 == 0).maybe {
            acc.add(it)
            it
          }
        }
        acc shouldBe ints.takeWhile { it % 2 == 0 }
        evens.fold({ Unit }) { it shouldBe ints }
      }
    }

    "sequenceOption yields some when all entries in the list are some" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val evens = ints.map { (it % 2 == 0).maybe { it } }.sequenceOption()
        evens.fold({ Unit }) { it shouldBe ints }
      }
    }

    "sequenceOption should be consistent with traverseOption" {
      checkAll(Arb.list(Arb.int())) { ints ->
        ints.map { Some(it) }.sequenceOption() shouldBe ints.traverseOption { Some(it) }
      }
    }

    "traverseValidated stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).traverseValidated(Semigroup.string()) {
        acc.add(it)
        Validated.Valid(it)
      }
      res shouldBe Validated.Valid(acc)
      res shouldBe Validated.Valid((0..20_000).toList())
    }

    "traverseValidated acummulates" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val res: ValidatedNel<Int, List<Int>> = ints.map { i -> if (i % 2 == 0) i.validNel() else i.invalidNel() }
          .sequenceValidated()

        val expected: ValidatedNel<Int, List<Int>> = NonEmptyList.fromList(ints.filterNot { it % 2 == 0 })
          .fold({ ints.filter { it % 2 == 0 }.validNel() }, { it.invalid() })

        res shouldBe expected
      }
    }

    "sequenceValidated should be consistent with traverseValidated" {
      checkAll(Arb.list(Arb.int())) { ints ->
        ints.map { it.valid() }.sequenceValidated(Semigroup.string()) shouldBe
          ints.traverseValidated(Semigroup.string()) { it.valid() }
      }
    }

    "zip3" {
      checkAll(Arb.list(Arb.int()), Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b, c ->
        val result = a.zip(b, c, ::Triple)
        val expected = a.zip(b, ::Pair).zip(c) { (a, b), c -> Triple(a, b, c) }
        result shouldBe expected
      }
    }

    "zip4" {
      checkAll(Arb.list(Arb.int()), Arb.list(Arb.int()), Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b, c, d ->
        val result = a.zip(b, c, d, ::Tuple4)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }

        result shouldBe expected
      }
    }

    "zip5" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int())
      ) { a, b, c, d, e ->
        val result = a.zip(b, c, d, e, ::Tuple5)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }

        result shouldBe expected
      }
    }

    "zip6" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int())
      ) { a, b, c, d, e, f ->
        val result = a.zip(b, c, d, e, f, ::Tuple6)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }

        result shouldBe expected
      }
    }

    "zip7" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int())
      ) { a, b, c, d, e, f, g ->
        val result = a.zip(b, c, d, e, f, g, ::Tuple7)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
          .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }

        result shouldBe expected
      }
    }

    "zip8" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int())
      ) { a, b, c, d, e, f, g, h ->
        val result = a.zip(b, c, d, e, f, g, h, ::Tuple8)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
          .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }
          .zip(h) { (a, b, c, d, e, f, g), h -> Tuple8(a, b, c, d, e, f, g, h) }

        result shouldBe expected
      }
    }

    "zip9" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int()),
        Arb.list(Arb.int())
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

        result shouldBe expected
      }
    }

    "zip10" {
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
        Arb.list(Arb.int())
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

        result shouldBe expected
      }
    }

    "can align lists with different lengths" {
      checkAll(Arb.list(Arb.bool()), Arb.list(Arb.bool())) { a, b ->
        a.align(b).size shouldBe max(a.size, b.size)
      }

      checkAll(Arb.list(Arb.bool()), Arb.list(Arb.bool())) { a, b ->
        a.align(b).take(min(a.size, b.size)).forEach {
          it.isBoth shouldBe true
        }
      }

      checkAll(Arb.list(Arb.bool()), Arb.list(Arb.bool())) { a, b ->
        a.align(b).drop(min(a.size, b.size)).forEach {
          if (a.size < b.size) {
            it.isRight shouldBe true
          } else {
            it.isLeft shouldBe true
          }
        }
      }
    }

    "leftPadZip (with map)" {
      checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        val result = a.leftPadZip(b) { a, b -> a to b }

        result shouldBe left.zip(right) { l, r -> l to r }.filter { it.second != null }
      }
    }

    "leftPadZip (without map)" {
      checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        val result = a.leftPadZip(b)

        result shouldBe left.zip(right) { l, r -> l to r }.filter { it.second != null }
      }
    }

    "rightPadZip (without map)" {
      checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        val result = a.rightPadZip(b)

        result shouldBe left.zip(right) { l, r -> l to r }.filter { it.first != null }
        result.map { it.first } shouldBe a
      }
    }

    "rightPadZip (with map)" {
      checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        val result = a.rightPadZip(b) { a, b -> a to b }

        result shouldBe left.zip(right) { l, r -> l to r }.filter { it.first != null }
        result.map { it.first } shouldBe a
      }
    }

    "padZip" {
      checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }
        a.padZip(b) { l, r -> Ior.fromNullables(l, r) } shouldBe left.zip(right) { l, r -> Ior.fromNullables(l, r) }
      }
    }

    "padZipWithNull" {
      checkAll(Arb.list(Arb.int()), Arb.list(Arb.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        a.padZip(b) shouldBe left.zip(right) { l, r -> l to r }
      }
    }

    "filterOption" {
      checkAll(Arb.list(Arb.option(Arb.int()))) { listOfOption ->
        listOfOption.filterOption() shouldBe listOfOption.mapNotNull { it.orNull() }
      }
    }

    "flattenOption" {
      checkAll(Arb.list(Arb.option(Arb.int()))) { listOfOption ->
        listOfOption.flattenOption() shouldBe listOfOption.mapNotNull { it.orNull() }
      }
    }
  }
}
