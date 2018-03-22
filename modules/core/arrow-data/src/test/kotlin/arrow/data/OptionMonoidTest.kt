package arrow.data

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.monoid
import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionMonoidTest : UnitSpec() {
    init {
        "should semigroup with the instance passed" {
            forAll { _: Int ->
                val optionMonoid = Option.monoid(NonEmptyList.semigroup<Int>())
                val seen = optionMonoid.run { None.combine(None) }
                val expected = None

                expected == seen
            }
        }

        "should semigroup with the instance passed" {
            forAll { value: Int ->
                val optionMonoid = Option.monoid(NonEmptyList.semigroup<Int>())
                val seen = optionMonoid.run { Some(NonEmptyList.of(value)).combine(None) }
                val expected = None

                expected == seen
            }
        }

        "should semigroup with the instance passed" {
            forAll { value: Int ->
                val optionMonoid = Option.monoid(NonEmptyList.semigroup<Int>())
                val seen = optionMonoid.run { None.combine(Some(NonEmptyList.of(value))) }
                val expected = None

                expected == seen
            }
        }

        "should semigroup with the instance passed" {
            forAll { value: Int ->
                val optionMonoid = Option.monoid(NonEmptyList.semigroup<Int>())
                val seen = optionMonoid.run { Some(NonEmptyList.of(value)).combine(Some(NonEmptyList.of(value))) }
                val expected = Some(NonEmptyList.of(value, value))

                expected == seen
            }
        }
    }
}
