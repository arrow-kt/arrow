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
                    val numberSemigroup = IntMonoidInstance
                    val seen = numberSemigroup.run { value.combine(value) }
                    val expected = value + value

                    expected == seen
                }
            }

            "float" {
                forAll { value: Float ->
                    val numberSemigroup = FloatMonoid
                    val seen = numberSemigroup.run { value.combine(value) }
                    val expected = value + value

                    expected == seen
                }
            }

            "double" {
                forAll { value: Double ->
                    val numberSemigroup = DoubleMonoid
                    val seen = numberSemigroup.run { value.combine(value) }
                    val expected = value + value

                    expected == seen
                }
            }

            "long" {

                forAll { value: Long ->
                    val numberSemigroup = LongMonoidInstance
                    val seen = numberSemigroup.run { value.combine(value) }
                    val expected = value + value

                    expected == seen
                }
            }

            "short" {
                forAll { value: Short ->
                    val numberSemigroup = ShortMonoid
                    val seen = numberSemigroup.run { value.combine(value) }
                    val expected = (value + value).toShort()

                    expected == seen
                }
            }

            "byte" {
                forAll { value: Byte ->
                    val numberSemigroup = ByteMonoidInstance
                    val seen = numberSemigroup.run { value.combine(value) }
                    val expected = (value + value).toByte()

                    expected == seen
                }
            }
        }
    }
}
