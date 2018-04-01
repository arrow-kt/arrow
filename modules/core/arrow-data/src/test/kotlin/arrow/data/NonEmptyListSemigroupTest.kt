package arrow.data

import arrow.test.UnitSpec
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListSemigroupTest : UnitSpec() {
  init {
    "should semigroup with the instance passed" {
      forAll { value: Int ->
        with(NonEmptyList.semigroup<Int>()) {

          val seen = NonEmptyList.of(value).combine(NonEmptyList.of(value))
          val expected = NonEmptyList.of(value, value)

          expected == seen
        }
      }
    }
  }
}
