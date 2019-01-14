package arrow.extensions

import arrow.core.extensions.monoid
import arrow.test.UnitSpec
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class NumberMonoidTest : UnitSpec() {
  init {

    "should semigroup with the instance passed" {
      "int" {
        forAll { value: Int ->
          val seen = Int.monoid().run { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "float" {
        forAll { value: Float ->
          val seen = Float.monoid().run { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "double" {
        forAll { value: Double ->
          val seen = Double.monoid().run { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "long" {

        forAll { value: Long ->
          val seen = Long.monoid().run  { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "short" {
        forAll { value: Short ->
          val seen = Short.monoid().run  { value.combine(value) }
          val expected = (value + value).toShort()

          expected == seen
        }
      }

      "byte" {
        forAll { value: Byte ->
          val seen = Byte.monoid().run { value.combine(value) }
          val expected = (value + value).toByte()

          expected == seen
        }
      }
    }
  }
}
