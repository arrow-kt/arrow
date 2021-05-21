package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.longSmall
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe

class MapKTest : UnitSpec() {

  init {
    testLaws(MonoidLaws.laws(Monoid.map(Semigroup.int()), Gen.map(Gen.longSmall(), Gen.intSmall())))

    "traverseEither is stacksafe" {
      val acc = mutableListOf<Int>()
      val res = (0..20_000).map { it to it }.toMap().traverseEither { v ->
        acc.add(v)
        Either.Right(v)
      }
      res shouldBe acc.map { it to it }.toMap().right()
      res shouldBe (0..20_000).map { it to it }.toMap().right()
    }

    "traverseEither short-circuit" {
      checkAll(Gen.map(Gen.int(), Gen.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverseEither {
          if (it % 2 == 0) {
            acc.add(it)
            Either.Right(it)
          } else Either.Left(it)
        }
        acc == ints.values.takeWhile { it % 2 == 0 } &&
          when (evens) {
            is Either.Right -> evens.value == ints
            is Either.Left -> evens.value == ints.values.first { it % 2 != 0 }
          }
      }
    }

    "traverseOption is stack-safe" {
      // also verifies result order and execution order (l to r)
      val acc = mutableListOf<Int>()
      val res = (0..20_000).map { it to it }.toMap().traverseOption { a ->
        acc.add(a)
        Some(a)
      }
      res shouldBe Some(acc.map { it to it }.toMap())
      res shouldBe Some((0..20_000).map { it to it }.toMap())
    }

    "traverseOption short-circuits" {
      checkAll(Gen.nonEmptyList(Gen.int())) { ints ->
        val acc = mutableListOf<Int>()
        val evens = ints.traverseOption {
          (it % 2 == 0).maybe {
            acc.add(it)
            it
          }
        }
        acc == ints.takeWhile { it % 2 == 0 } && evens.all { it == ints }
      }
    }

    "sequenceOption yields some when all entries in the list are some" {
      checkAll(Arb.list(Gen.int())) { ints ->
        val evens = ints.map { (it % 2 == 0).maybe { it } }.sequenceOption()
        evens.all { it == ints }
      }
    }

    "traverseValidated is stacksafe" {
      val acc = mutableListOf<Int>()
      val res = (0..20_000).map { it to it }.toMap().traverseValidated(Semigroup.string()) { v ->
        acc.add(v)
        Validated.Valid(v)
      }
      res shouldBe acc.map { it to it }.toMap().valid()
      res shouldBe (0..20_000).map { it to it }.toMap().valid()
    }

    "traverseValidated acummulates" {
      checkAll(Gen.map(Gen.int(), Gen.int())) { ints ->
        val res: ValidatedNel<Int, Map<Int, Int>> =
          ints.traverseValidated(Semigroup.nonEmptyList()) { i -> if (i % 2 == 0) i.validNel() else i.invalidNel() }

        val expected: ValidatedNel<Int, Map<Int, Int>> =
          NonEmptyList.fromList(ints.values.filterNot { it % 2 == 0 })
            .fold({ ints.entries.filter { (_, v) -> v % 2 == 0 }.map { (k, v) -> k to v }.toMap().validNel() }, { it.invalid() })

        res == expected
      }
    }

    "can align maps" {
      // aligned keySet is union of a's and b's keys
      checkAll(Gen.map(Gen.long(), Gen.bool()), Gen.map(Gen.long(), Gen.bool())) { a, b ->
        val aligned = a.align(b)
        aligned.size == (a.keys + b.keys).size
      }

      // aligned map contains Both for all entries existing in a and b
      checkAll(Gen.map(Gen.long(), Gen.bool()), Gen.map(Gen.long(), Gen.bool())) { a, b ->
        val aligned = a.align(b)
        a.keys.intersect(b.keys).all {
          aligned[it]?.isBoth ?: false
        }
      }

      // aligned map contains Left for all entries existing only in a
      checkAll(Gen.map(Gen.long(), Gen.bool()), Gen.map(Gen.long(), Gen.bool())) { a, b ->
        val aligned = a.align(b)
        (a.keys - b.keys).all { key ->
          aligned[key]?.let { it.isLeft } ?: false
        }
      }

      // aligned map contains Right for all entries existing only in b
      checkAll(Gen.map(Gen.long(), Gen.bool()), Gen.map(Gen.long(), Gen.bool())) { a, b ->
        val aligned = a.align(b)
        (b.keys - a.keys).all { key ->
          aligned[key]?.isRight ?: false
        }
      }
    }

    "zip2" {
      checkAll(
        Gen.map(Gen.intSmall(), Gen.intSmall()),
        Gen.map(Gen.intSmall(), Gen.intSmall())
      ) { a, b ->
        val result = a.zip(b) { _, aa, bb -> Pair(aa, bb) }
        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Pair(v, b[k]!!)) }
          .toMap()

        result == expected
      }
    }

    "zip3" {
      checkAll(
        Gen.map(Gen.intSmall(), Gen.intSmall()),
        Gen.map(Gen.intSmall(), Gen.intSmall())
      ) { a, b ->
        val result = a.zip(b, b) { _, aa, bb, cc -> Triple(aa, bb, cc) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Triple(v, b[k]!!, b[k]!!)) }
          .toMap()

        result == expected
      }
    }

    "zip4" {
      checkAll(
        Gen.map(Gen.intSmall(), Gen.intSmall()),
        Gen.map(Gen.intSmall(), Gen.intSmall()),
      ) { a, b ->
        val result = a.zip(b, b, b) { _, aa, bb, cc, dd -> Tuple4(aa, bb, cc, dd) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple4(v, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result == expected
      }
    }

    "zip5" {
      checkAll(
        Gen.map(Gen.intSmall(), Gen.intSmall()),
        Gen.map(Gen.intSmall(), Gen.intSmall()),
      ) { a, b ->
        val result = a.zip(b, b, b, b) { _, aa, bb, cc, dd, ee -> Tuple5(aa, bb, cc, dd, ee) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple5(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result == expected
      }
    }

    "zip6" {
      checkAll(
        Gen.map(Gen.intSmall(), Gen.intSmall()),
        Gen.map(Gen.intSmall(), Gen.intSmall()),
      ) { a, b ->
        val result = a.zip(b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff -> Tuple6(aa, bb, cc, dd, ee, ff) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple6(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result == expected
      }
    }

    "zip7" {
      checkAll(
        Gen.map(Gen.intSmall(), Gen.intSmall()),
        Gen.map(Gen.intSmall(), Gen.intSmall())
      ) { a, b ->
        val result = a.zip(b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg -> Tuple7(aa, bb, cc, dd, ee, ff, gg) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple7(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result == expected
      }
    }

    "zip8" {
      checkAll(
        Gen.map(Gen.intSmall(), Gen.intSmall()),
        Gen.map(Gen.intSmall(), Gen.intSmall())
      ) { a, b ->
        val result =
          a.zip(b, b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg, hh -> Tuple8(aa, bb, cc, dd, ee, ff, gg, hh) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple8(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result == expected
      }
    }

    "zip9" {
      checkAll(
        Gen.map(Gen.intSmall(), Gen.intSmall()),
        Gen.map(Gen.intSmall(), Gen.intSmall())
      ) { a, b ->
        val result = a.zip(b, b, b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg, hh, ii ->
          Tuple9(
            aa,
            bb,
            cc,
            dd,
            ee,
            ff,
            gg,
            hh,
            ii
          )
        }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple9(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result == expected
      }
    }

    "zip10" {
      checkAll(
        Gen.map(Gen.intSmall(), Gen.intSmall()),
        Gen.map(Gen.intSmall(), Gen.intSmall())
      ) { a, b ->
        val result = a.zip(b, b, b, b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg, hh, ii, jj ->
          Tuple10(
            aa,
            bb,
            cc,
            dd,
            ee,
            ff,
            gg,
            hh,
            ii,
            jj
          )
        }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple10(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result == expected
      }
    }

    "flatMap" {
      checkAll(
        Gen.map(Gen.string(), Gen.intSmall()),
        Gen.map(Gen.string(), Gen.string())
      ) { a, b ->
        val result: Map<String, String> = a.flatMap { b }
        val expected: Map<String, String> = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, b[k]!!) }
          .toMap()
        result == expected
      }
    }
  }
}
