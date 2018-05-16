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
          val seen = Int syntax { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "float" {
        forAll { value: Float ->
          val seen = Float syntax { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "double" {
        forAll { value: Double ->
          val seen = Double syntax { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "long" {

        forAll { value: Long ->
          val seen = Long syntax  { value.combine(value) }
          val expected = value + value

          expected == seen
        }
      }

      "short" {
        forAll { value: Short ->
          val seen = Short syntax  { value.combine(value) }
          val expected = (value + value).toShort()

          expected == seen
        }
      }

      "byte" {
        forAll { value: Byte ->
          val seen = Byte.syntax { value.combine(value) }
          val expected = (value + value).toByte()

          expected == seen
        }
      }
    }
  }
}
