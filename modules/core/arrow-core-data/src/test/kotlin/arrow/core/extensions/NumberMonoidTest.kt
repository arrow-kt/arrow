package arrow.core.extensions

import arrow.test.UnitSpec
import arrow.test.generators.byte
import arrow.test.generators.short
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class NumberMonoidTest : UnitSpec() {
  init {

    "should semigroup with the instance passed - int" {
      forAll { value: Int ->
        val seen = Int.monoid().run { value.combine(value) }
        val expected = value + value

        expected == seen
      }
    }

    "should semigroup with the instance passed - float" {
      forAll(Gen.numericFloats()) { value: Float ->
        val seen = Float.monoid().run { value.combine(value) }
        val expected = value + value

        expected == seen
      }
    }

    "should semigroup with the instance passed - double" {
      forAll(Gen.numericDoubles()) { value: Double ->
        val seen = Double.monoid().run { value.combine(value) }
        val expected = value + value

        expected == seen
      }
    }

    "should semigroup with the instance passed - long" {
      forAll { value: Long ->
        val seen = Long.monoid().run { value.combine(value) }
        val expected = value + value

        expected == seen
      }
    }

    "should semigroup with the instance passed - short" {
      forAll(Gen.short()) { value: Short ->
        val seen = Short.monoid().run { value.combine(value) }
        val expected = (value + value).toShort()

        expected == seen
      }
    }

    "should semigroup with the instance passed - byte" {
      forAll(Gen.byte()) { value: Byte ->
        val seen = Byte.monoid().run { value.combine(value) }
        val expected = (value + value).toByte()

        expected == seen
      }
    }
  }
}
