package arrow

import arrow.data.NonEmptyList
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListSemigroupTest : UnitSpec() {
    init {
        "should semigroup with the instance passed" {
            forAll { value: Int ->
                val nonEmptyListSemigroup = NonEmptyList.semigroup<Int>()
                val seen = nonEmptyListSemigroup.combine(NonEmptyList.of(value), NonEmptyList.of(value))
                val expected = NonEmptyList.of(value, value)

                expected == seen
            }
        }
    }
}
