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
          val seen = Int extensions { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "float" {
        forAll { value: Float ->
          val seen = Float extensions { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "double" {
        forAll { value: Double ->
          val seen = Double extensions { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "long" {

        forAll { value: Long ->
          val seen = Long extensions  { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "short" {
        forAll { value: Short ->
          val seen = Short extensions  { value.combine(value) }
          val expected = (value + value).toShort()

          expected == seen
        }
      }

      "byte" {
        forAll { value: Byte ->
          val seen = Byte.extensions { value.combine(value) }
          val expected = (value + value).toByte()

          expected == seen
        }
      }
    }
  }
}
