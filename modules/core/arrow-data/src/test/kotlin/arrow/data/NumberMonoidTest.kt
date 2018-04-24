package arrow.data

import arrow.instances.*
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NumberMonoidTest : UnitSpec() {
  init {

    "should semigroup with the instance passed" {
      "int" {
        forAll { value: Int ->
          val numberSemigroup = Int.monoid()
          val seen = numberSemigroup.run { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "float" {
        forAll { value: Float ->
          val numberSemigroup = Float.monoid()
          val seen = numberSemigroup.run { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "double" {
        forAll { value: Double ->
          val numberSemigroup = Double.monoid()
          val seen = numberSemigroup.run { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "long" {

        forAll { value: Long ->
          val numberSemigroup = Long.monoid()
          val seen = numberSemigroup.run { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "short" {
        forAll { value: Short ->
          val numberSemigroup = Short.monoid()
          val seen = numberSemigroup.run { value.combine(value) }
          val expected = (value + value).toShort()

          expected == seen
        }
      }

      "byte" {
        forAll { value: Byte ->
          val numberSemigroup = Byte.monoid()
          val seen = numberSemigroup.run { value.combine(value) }
          val expected = (value + value).toByte()

          expected == seen
        }
      }
    }
  }
}
