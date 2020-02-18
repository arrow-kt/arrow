package arrow.core.extensions

import arrow.test.UnitSpec
import arrow.test.generators.byte
import arrow.test.generators.byteSmall
import arrow.test.generators.doubleSmall
import arrow.test.generators.floatSmall
import arrow.test.generators.intSmall
import arrow.test.generators.longSmall
import arrow.test.generators.short
import arrow.test.generators.shortSmall
import arrow.test.laws.MonoidLaws
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class NumberMonoidTest : UnitSpec() {
  init {
    testLaws(MonoidLaws.laws(Byte.monoid(), Gen.byteSmall(), Byte.eq()))
    testLaws(MonoidLaws.laws(Double.monoid(), Gen.doubleSmall(), Double.eq()))
    testLaws(MonoidLaws.laws(Int.monoid(), Gen.intSmall(), Int.eq()))
    testLaws(MonoidLaws.laws(Short.monoid(), Gen.shortSmall(), Short.eq()))
    testLaws(MonoidLaws.laws(Float.monoid(), Gen.floatSmall(), Float.eq()))
    testLaws(MonoidLaws.laws(Long.monoid(), Gen.longSmall(), Long.eq()))

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
