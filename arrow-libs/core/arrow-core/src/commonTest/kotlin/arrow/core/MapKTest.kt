package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.longSmall
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.string

class MapKTest : UnitSpec() {

  init {
    testLaws(
      MonoidLaws.laws(
        Monoid.map(Semigroup.int()),
        Arb.map(Arb.longSmall(), Arb.intSmall(), maxSize = 10)
      )
    )

    "can align maps" {
      // aligned keySet is union of a's and b's keys
      checkAll(Arb.map(Arb.long(), Arb.boolean()), Arb.map(Arb.long(), Arb.boolean())) { a, b ->
        val aligned = a.align(b)
        aligned.size shouldBe (a.keys + b.keys).size
      }

      // aligned map contains Both for all entries existing in a and b
      checkAll(Arb.map(Arb.long(), Arb.boolean()), Arb.map(Arb.long(), Arb.boolean())) { a, b ->
        val aligned = a.align(b)
        a.keys.intersect(b.keys).forEach {
          aligned[it]?.isBoth shouldBe true
        }
      }

      // aligned map contains Left for all entries existing only in a
      checkAll(Arb.map(Arb.long(), Arb.boolean()), Arb.map(Arb.long(), Arb.boolean())) { a, b ->
        val aligned = a.align(b)
        (a.keys - b.keys).forEach { key ->
          aligned[key]?.isLeft shouldBe true
        }
      }

      // aligned map contains Right for all entries existing only in b
      checkAll(Arb.map(Arb.long(), Arb.boolean()), Arb.map(Arb.long(), Arb.boolean())) { a, b ->
        val aligned = a.align(b)
        (b.keys - a.keys).forEach { key ->
          aligned[key]?.isRight shouldBe true
        }
      }
    }

    "zip2" {
      checkAll(
        Arb.map(Arb.intSmall(), Arb.intSmall()),
        Arb.map(Arb.intSmall(), Arb.intSmall())
      ) { a, b ->
        val result = a.zip(b) { _, aa, bb -> Pair(aa, bb) }
        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Pair(v, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip3" {
      checkAll(
        Arb.map(Arb.intSmall(), Arb.intSmall()),
        Arb.map(Arb.intSmall(), Arb.intSmall())
      ) { a, b ->
        val result = a.zip(b, b) { _, aa, bb, cc -> Triple(aa, bb, cc) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Triple(v, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip4" {
      checkAll(
        Arb.map(Arb.intSmall(), Arb.intSmall()),
        Arb.map(Arb.intSmall(), Arb.intSmall()),
      ) { a, b ->
        val result = a.zip(b, b, b) { _, aa, bb, cc, dd -> Tuple4(aa, bb, cc, dd) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple4(v, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip5" {
      checkAll(
        Arb.map(Arb.intSmall(), Arb.intSmall()),
        Arb.map(Arb.intSmall(), Arb.intSmall()),
      ) { a, b ->
        val result = a.zip(b, b, b, b) { _, aa, bb, cc, dd, ee -> Tuple5(aa, bb, cc, dd, ee) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple5(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip6" {
      checkAll(
        Arb.map(Arb.intSmall(), Arb.intSmall()),
        Arb.map(Arb.intSmall(), Arb.intSmall()),
      ) { a, b ->
        val result = a.zip(b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff -> Tuple6(aa, bb, cc, dd, ee, ff) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple6(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip7" {
      checkAll(
        Arb.map(Arb.intSmall(), Arb.intSmall()),
        Arb.map(Arb.intSmall(), Arb.intSmall())
      ) { a, b ->
        val result = a.zip(b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg -> Tuple7(aa, bb, cc, dd, ee, ff, gg) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple7(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip8" {
      checkAll(
        Arb.map(Arb.intSmall(), Arb.intSmall()),
        Arb.map(Arb.intSmall(), Arb.intSmall())
      ) { a, b ->
        val result =
          a.zip(b, b, b, b, b, b, b) { _, aa, bb, cc, dd, ee, ff, gg, hh -> Tuple8(aa, bb, cc, dd, ee, ff, gg, hh) }

        val expected = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Pair(k, Tuple8(v, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!, b[k]!!)) }
          .toMap()

        result shouldBe expected
      }
    }

    "zip9" {
      checkAll(
        Arb.map(Arb.intSmall(), Arb.intSmall()),
        Arb.map(Arb.intSmall(), Arb.intSmall())
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

        result shouldBe expected
      }
    }

    "zip10" {
      checkAll(
        Arb.map(Arb.intSmall(), Arb.intSmall()),
        Arb.map(Arb.intSmall(), Arb.intSmall())
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

        result shouldBe expected
      }
    }

    "flatMap" {
      checkAll(
        Arb.map(Arb.string(), Arb.intSmall()),
        Arb.map(Arb.string(), Arb.string())
      ) { a, b ->
        val result: Map<String, String> = a.flatMap { b }
        val expected: Map<String, String> = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, _) -> Pair(k, b[k]!!) }
          .toMap()
        result shouldBe expected
      }
    }
  }
}
