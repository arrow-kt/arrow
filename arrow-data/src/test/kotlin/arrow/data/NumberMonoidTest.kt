package arrow.data

import arrow.instances.*
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith
import arrow.test.UnitSpec
import arrow.typeclasses.monoid
import arrow.typeclasses.semigroup

@RunWith(KTestJUnitRunner::class)
class NumberMonoidTest : UnitSpec() {
    init {

        "instances can be resolved implicitly" {
            semigroup<Long>() shouldNotBe null
            semigroup<Int>() shouldNotBe null
            semigroup<Double>() shouldNotBe null
            semigroup<Float>() shouldNotBe null
            semigroup<Byte>() shouldNotBe null
            semigroup<Short>() shouldNotBe null
            monoid<Long>() shouldNotBe null
            monoid<Int>() shouldNotBe null
            monoid<Double>() shouldNotBe null
            monoid<Float>() shouldNotBe null
            monoid<Byte>() shouldNotBe null
            monoid<Short>() shouldNotBe null
            semigroup<Integer>() shouldNotBe null
            monoid<Integer>() shouldNotBe null
        }

        "should semigroup with the instance passed" {
            "int" {
                forAll { value: Int ->
                    val numberSemigroup = IntMonoid
                    val seen = numberSemigroup.combine(value, value)
                    val expected = value + value

                    expected == seen
                }
            }

            "float" {
                forAll { value: Float ->
                    val numberSemigroup = FloatMonoid
                    val seen = numberSemigroup.combine(value, value)
                    val expected = value + value

                    expected == seen
                }
            }

            "double" {
                forAll { value: Double ->
                    val numberSemigroup = DoubleMonoid
                    val seen = numberSemigroup.combine(value, value)
                    val expected = value + value

                    expected == seen
                }
            }

            "long" {

                forAll { value: Long ->
                    val numberSemigroup = LongMonoid
                    val seen = numberSemigroup.combine(value, value)
                    val expected = value + value

                    expected == seen
                }
            }

            "short" {
                forAll { value: Short ->
                    val numberSemigroup = ShortMonoid
                    val seen = numberSemigroup.combine(value, value)
                    val expected = (value + value).toShort()

                    expected == seen
                }
            }

            "byte" {
                forAll { value: Byte ->
                    val numberSemigroup = ByteMonoid
                    val seen = numberSemigroup.combine(value, value)
                    val expected = (value + value).toByte()

                    expected == seen
                }
            }
        }
    }
}
