package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.max
import kotlin.math.min

class IterableTest : UnitSpec() {
  init {
    "zip3" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int()), Gen.list(Gen.int())) { a, b, c ->
        val result = a.zip(b, c, ::Triple)
        val expected = a.zip(b, ::Pair).zip(c) { (a, b), c -> Triple(a, b, c) }
        result == expected
      }
    }

    "zip4" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int()), Gen.list(Gen.int()), Gen.list(Gen.int())) { a, b, c, d ->
        val result = a.zip(b, c, d, ::Tuple4)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }

        result == expected
      }
    }

    "zip5" {
      forAll(
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int())
      ) { a, b, c, d, e ->
        val result = a.zip(b, c, d, e, ::Tuple5)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }

        result == expected
      }
    }

    "zip6" {
      forAll(
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int())
      ) { a, b, c, d, e, f ->
        val result = a.zip(b, c, d, e, f, ::Tuple6)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }

        result == expected
      }
    }

    "zip7" {
      forAll(
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int())
      ) { a, b, c, d, e, f, g ->
        val result = a.zip(b, c, d, e, f, g, ::Tuple7)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
          .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }

        result == expected
      }
    }

    "zip8" {
      forAll(
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int())
      ) { a, b, c, d, e, f, g, h ->
        val result = a.zip(b, c, d, e, f, g, h, ::Tuple8)
        val expected = a.zip(b, ::Pair)
          .zip(c) { (a, b), c -> Triple(a, b, c) }
          .zip(d) { (a, b, c), d -> Tuple4(a, b, c, d) }
          .zip(e) { (a, b, c, d), e -> Tuple5(a, b, c, d, e) }
          .zip(f) { (a, b, c, d, e), f -> Tuple6(a, b, c, d, e, f) }
          .zip(g) { (a, b, c, d, e, f), g -> Tuple7(a, b, c, d, e, f, g) }
          .zip(h) { (a, b, c, d, e, f, g), h -> Tuple8(a, b, c, d, e, f, g, h) }

        result == expected
      }
    }

    "zip9" {
      forAll(
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int())
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

        result == expected
      }
    }

    "zip10" {
      forAll(
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int()),
        Gen.list(Gen.int())
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

        result == expected
      }
    }

    "can align lists with different lengths" {
      forAll(Gen.list(Gen.bool()), Gen.list(Gen.bool())) { a, b ->
        a.align(b).size == max(a.size, b.size)
      }

      forAll(Gen.list(Gen.bool()), Gen.list(Gen.bool())) { a, b ->
        a.align(b).take(min(a.size, b.size)).all {
          it.isBoth
        }
      }

      forAll(Gen.list(Gen.bool()), Gen.list(Gen.bool())) { a, b ->
        a.align(b).drop(min(a.size, b.size)).all {
          if (a.size < b.size) {
            it.isRight
          } else {
            it.isLeft
          }
        }
      }
    }

    "leftPadZip (with map)" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        val result = a.leftPadZip(b) { a, b -> a toT b }

        result == left.zip(right) { l, r -> l toT r }.filter { it.b != null }
      }
    }

    "leftPadZip (without map)" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        val result = a.leftPadZip(b)

        result == left.zip(right) { l, r -> l to r }.filter { it.second != null }
      }
    }

    "rightPadZip (without map)" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        val result = a.rightPadZip(b)

        result == left.zip(right) { l, r -> l to r }.filter { it.first != null } &&
          result.map { it.first }.equalUnderTheLaw(a, Eq.any())
      }
    }

    "rightPadZip (with map)" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        val result = a.rightPadZip(b) { a, b -> a to b }

        result == left.zip(right) { l, r -> l to r }.filter { it.first != null } &&
          result.map { it.first }.equalUnderTheLaw(a, Eq.any())
      }
    }

    "padZip" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }
        a.padZip(b) { l, r -> Ior.fromNullables(l, r) } == left.zip(right) { l, r -> Ior.fromNullables(l, r) }
      }
    }

    "padZipWithNull" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        a.padZip(b) == left.zip(right) { l, r -> l to r }
      }
    }
  }
}
