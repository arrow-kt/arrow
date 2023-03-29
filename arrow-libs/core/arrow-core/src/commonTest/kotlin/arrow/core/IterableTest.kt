package arrow.core

import arrow.core.test.either
import arrow.core.test.functionAToB
import arrow.core.test.ior
import arrow.core.test.option
import arrow.typeclasses.Semigroup
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.property.Arb
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.math.max
import kotlin.math.min

class IterableTest : StringSpec({

  "flattenOrAccumulate(combine)" {
    checkAll(Arb.list(Arb.either(Arb.string(), Arb.int()))) { list ->
      val expected =
        if (list.any { it.isLeft() }) list.filterIsInstance<Either.Left<String>>()
          .fold("") { acc, either -> "$acc${either.value}" }.left()
        else list.filterIsInstance<Either.Right<Int>>().map { it.value }.right()

      list.flattenOrAccumulate(String::plus) shouldBe expected
    }
  }

  "flattenOrAccumulate" {
    checkAll(Arb.list(Arb.either(Arb.string(), Arb.int()))) { list ->
      val expected =
        if (list.any { it.isLeft() }) list.filterIsInstance<Either.Left<String>>()
          .map { it.value }.toNonEmptyListOrNull().shouldNotBeNull().left()
        else list.filterIsInstance<Either.Right<Int>>().map { it.value }.right()

      list.flattenOrAccumulate() shouldBe expected
    }
  }

  "mapAccumulating stack-safe, and runs in original order" {
    val acc = mutableListOf<Int>()
    val res = (0..20_000).mapOrAccumulate(String::plus) {
      acc.add(it)
      it
    }
    res shouldBe acc.right()
    res shouldBe (0..20_000).toList().right()
  }

  "mapAccumulating accumulates" {
    checkAll(Arb.list(Arb.int())) { ints ->
      val res = ints.mapOrAccumulate { i ->
        if (i % 2 == 0) i else raise(i)
      }

      val expected: Either<NonEmptyList<Int>, List<Int>> = ints.filterNot { it % 2 == 0 }
        .toNonEmptyListOrNull()?.left() ?: ints.filter { it % 2 == 0 }.right()

      res shouldBe expected
    }
  }

  "mapAccumulating with String::plus" {
    listOf(1, 2, 3).mapOrAccumulate(String::plus) { i ->
      raise("fail")
    } shouldBe Either.Left("failfailfail")
  }

    "traverse Either stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).traverse { a ->
        acc.add(a)
        Either.Right(a)
      }
      res shouldBe Either.Right(acc)
      res shouldBe Either.Right((0..20_000).toList())
    }

    "traverse Either short-circuit" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverse {
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

    "sequenceEither should be consistent with traverse Either" {
      checkAll(Arb.list(Arb.int())) { ints ->
        ints.map { it.right() }.sequence() shouldBe ints.traverse { it.right() }
      }
    }

    "traverse Result stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).traverse { a ->
        acc.add(a)
        Result.success(a)
      }
      res shouldBe Result.success(acc)
      res shouldBe Result.success((0..20_000).toList())
    }

    "traverse Result short-circuit" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverse {
          if (it % 2 == 0) {
            acc.add(it)
            Result.success(it)
          } else Result.failure(RuntimeException())
        }
        acc shouldBe ints.takeWhile { it % 2 == 0 }
        evens.fold(
          { it shouldBe ints },
          { }
        )
      }
    }

    "sequence Result should be consistent with traverse Result" {
      checkAll(Arb.list(Arb.int())) { ints ->
        ints.map { Result.success(it) }.sequence() shouldBe ints.traverse { Result.success(it) }
      }
    }

    "traverse Option is stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).traverse { a: Int ->
        acc.add(a)
        Some(a)
      }
      res shouldBe Some(acc)
      res shouldBe Some((0..20_000).toList())
    }

    "traverse Option short-circuits" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverse {
          (it % 2 == 0).maybe {
            acc.add(it)
            it
          }
        }
        acc shouldBe ints.takeWhile { it % 2 == 0 }
        evens.fold({ Unit }) { it shouldBe ints }
      }
    }

    "sequence Option yields some when all entries in the list are some" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val evens = ints.map { (it % 2 == 0).maybe { it } }.sequence()
        evens.fold({ Unit }) { it shouldBe ints }
      }
    }

    "sequence Option should be consistent with traverse Option" {
      checkAll(Arb.list(Arb.int())) { ints ->
        ints.map { Some(it) }.sequence() shouldBe ints.traverse { Some(it) }
      }
    }

    "traverse Nullable is stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int?>()
      val res = (0..20_000).traverse { a: Int ->
        acc.add(a)
        a
      }
      res.shouldNotBeNull() shouldBe acc
      res.shouldNotBeNull() shouldBe (0..20_000).toList()
    }

    "traverse Nullable short-circuits" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverse {
          if (it % 2 == 0) {
            acc.add(it)
            it
          } else {
            null
          }
        }

        val expected = ints.takeWhile { it % 2 == 0 }
        acc shouldBe expected

        if (ints.any { it % 2 != 0 }) {
          evens.shouldBeNull()
        } else {
          evens.shouldNotBeNull() shouldContainExactly expected
        }
      }
    }

    "sequence Nullable yields some when all entries in the list are not null" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val evens = ints.map { if (it % 2 == 0) it else null }.sequence()

        if (ints.any { it % 2 != 0 }) {
          evens.shouldBeNull()
        } else {
          evens.shouldNotBeNull() shouldContainExactly ints.takeWhile { it % 2 == 0 }
        }
      }
    }

    "sequence Nullable should be consistent with travers Nullable" {
      checkAll(Arb.list(Arb.int())) { ints ->
        ints.map { it as Int? }.sequence() shouldBe ints.traverse { it as Int? }
      }
    }

    "traverse Validated stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).traverse(Semigroup.string()) {
        acc.add(it)
        Validated.Valid(it)
      }
      res shouldBe Validated.Valid(acc)
      res shouldBe Validated.Valid((0..20_000).toList())
    }

    "traverse Validated acumulates" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val res: ValidatedNel<Int, List<Int>> =
          ints.map { i -> if (i % 2 == 0) Valid(i) else Invalid(nonEmptyListOf(i)) }
            .sequence()

        val expected: ValidatedNel<Int, List<Int>> = ints.filterNot { it % 2 == 0 }
          .toNonEmptyListOrNull()?.invalid() ?: Valid(ints.filter { it % 2 == 0 })

        res shouldBe expected
      }
    }

    "sequence Validated should be consistent with traverse Validated" {
      checkAll(Arb.list(Arb.int())) { ints ->
        ints.map { it.valid() }.sequence(Semigroup.string()) shouldBe
          ints.traverse(Semigroup.string()) { it.valid() }
      }
    }

    "sequence Either traverse Nullable interoperate - and proof map + sequence equality with traverse" {
      checkAll(
        Arb.list(Arb.int()),
        Arb.functionAToB<Int, Either<String, Int>?>(Arb.either(Arb.string(), Arb.int()).orNull())
      ) { ints, f ->

        val res: Either<String, List<Int>>? =
          ints.traverse(f)?.sequence()

        val expected: Either<String, List<Int>>? =
          ints.map(f).sequence()?.sequence()

        res shouldBe expected
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
      checkAll(Arb.list(Arb.boolean()), Arb.list(Arb.boolean())) { a, b ->
        a.align(b).size shouldBe max(a.size, b.size)
      }

      checkAll(Arb.list(Arb.boolean()), Arb.list(Arb.boolean())) { a, b ->
        a.align(b).take(min(a.size, b.size)).forEach {
          it.isBoth shouldBe true
        }
      }

      checkAll(Arb.list(Arb.boolean()), Arb.list(Arb.boolean())) { a, b ->
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

  "partitionMap" {
    checkAll(Arb.list(Arb.int())) { ints ->
      val partitioned = ints.partitionMap {
        if (it % 2 == 0) it.left()
        else it.right()
      }
      partitioned shouldBe ints.partition { it % 2 == 0 }
    }
  }

    "separateEither" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val list = ints.map {
          if (it % 2 == 0) it.left()
          else it.right()
        }
        list.separateEither() shouldBe ints.partition { it % 2 == 0 }
      }
    }

    "separateValidated" {
      checkAll(Arb.list(Arb.int())) { ints ->
        val list = ints.map {
          if (it % 2 == 0) it.invalid()
          else it.valid()
        }
        list.separateValidated() shouldBe ints.partition { it % 2 == 0 }
      }
    }

  "unzip is the inverse of zip" {
    checkAll(Arb.list(Arb.int())) { xs ->

      val zipped = xs.zip(xs)
      val ls = zipped.unzip()
      val rs = xs to xs

      ls shouldBe rs
    }
  }

  "unzip(fn)" {
    checkAll(Arb.list(Arb.pair(Arb.int(), Arb.string()))) { xs ->
      xs.unzip { it } shouldBe xs.unzip()
    }
  }

  "unalign is the inverse of align" {
    checkAll(Arb.list(Arb.int()), Arb.list(Arb.string())) { a, b ->
      a.align(b).unalign() shouldBe (a to b)
    }
  }

  "align is the inverse of unalign" {
    checkAll(Arb.list(Arb.ior(Arb.int(), Arb.string()))) { xs ->
      val (a, b) = xs.unalign()
      a.align(b) shouldBe xs
    }
  }

  "unalign(fn)" {
    checkAll(Arb.list(Arb.ior(Arb.int(), Arb.string()))) { xs ->
      xs.unalign { it } shouldBe xs.unalign()
    }
  }

  "salign" {
    checkAll(Arb.list(Arb.int())) { xs ->
      xs.salign(Semigroup.int(), xs) shouldBe xs.map { it + it }
    }
  }

  "reduceOrNull is compatible with reduce from stdlib" {
    checkAll(Arb.list(Arb.string())) { xs ->

      val rs = xs.reduceOrNull({ it }) { a, b ->
        a + b
      }

      if (xs.isEmpty()) {
        rs.shouldBeNull()
      } else {
        rs shouldBe xs.reduce {
            a,b -> a +b
        }
      }
    }
  }

  "reduceRightNull is compatible with reduce from stdlib" {
    checkAll(Arb.list(Arb.string())) { xs ->

      val rs = xs.reduceRightNull({ it }) { a, b ->
        a + b
      }

      if (xs.isEmpty()) {
        rs.shouldBeNull()
      } else {
        rs shouldBe xs.reduceRight {
            a,b -> a +b
        }
      }
    }
  }
})
