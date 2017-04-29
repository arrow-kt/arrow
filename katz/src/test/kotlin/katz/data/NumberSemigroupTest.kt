package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NumberSemigroupTest : UnitSpec() {
    init {
        "should semigroup with the instance passed" {
            forAll { value: Int ->
                val numberSemigroup = NumberSemigroup<Int>(Int::plus)
                val seen = numberSemigroup.combine(value, value)
                val expected = value + value

                expected == seen
            }

            forAll { value: Float ->
                val numberSemigroup = NumberSemigroup<Float>(Float::plus)
                val seen = numberSemigroup.combine(value, value)
                val expected = value + value

                expected == seen
            }

            forAll { value: Double ->
                val numberSemigroup = NumberSemigroup<Double>(Double::plus)
                val seen = numberSemigroup.combine(value, value)
                val expected = value + value

                expected == seen
            }

            forAll { value: Long ->
                val numberSemigroup = NumberSemigroup<Long>(Long::plus)
                val seen = numberSemigroup.combine(value, value)
                val expected = value + value

                expected == seen
            }
        }
    }
}
