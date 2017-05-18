package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyVectorSemigroupTest : UnitSpec() {
    init {
        "should semigroup with the instance passed" {
            forAll { value: Int ->
                val nonEmptyVectorSemigroup = NonEmptyVectorSemigroup<Int>()
                val seen = nonEmptyVectorSemigroup.combine(NonEmptyVector.of(value), NonEmptyVector.of(value))
                val expected = NonEmptyVector.of(value, value)

                expected == seen
            }
        }
    }
}
