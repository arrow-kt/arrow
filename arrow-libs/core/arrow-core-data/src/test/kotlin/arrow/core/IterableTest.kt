package arrow.core

import arrow.core.extensions.eq
import arrow.core.test.UnitSpec
import arrow.core.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.max
import kotlin.math.min

class IterableTest : UnitSpec() {
  init {
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

        result == left.zip(right) { l, r -> l toT r }.filter { it.b != null }
      }
    }

    "rightPadZip (without map)" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        val result = a.rightPadZip(b)

        result == left.zip(right) { l, r -> l toT r }.filter { it.a != null } &&
          result.map { it.a }.equalUnderTheLaw(a, Eq.list(Int.eq()))
      }
    }

    "rightPadZip (with map)" {
      forAll(Gen.list(Gen.int()), Gen.list(Gen.int())) { a, b ->
        val left = a.map { it } + List(max(0, b.count() - a.count())) { null }
        val right = b.map { it } + List(max(0, a.count() - b.count())) { null }

        val result = a.rightPadZip(b) { a, b -> a toT b }

        result == left.zip(right) { l, r -> l toT r }.filter { it.a != null } &&
          result.map { it.a }.equalUnderTheLaw(a, Eq.list(Int.eq()))
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

        a.padZip(b) == left.zip(right) { l, r -> l toT r }
      }
    }
  }
}
