package arrow.validation.refinedTypes.string

import arrow.test.UnitSpec
import arrow.validation.refinedTypes.string.validated.nonEmptyString.nonEmptyString
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.filter
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyStringTest : UnitSpec() {
  init {
    "Should create NonEmptyString for every string with length > 0" {
      forAll(NonEmptyStringGen()) { s: String ->
        s.nonEmptyString().isValid
      }
    }

    "Should not create NonEmptyString for empty strings" {
      "".nonEmptyString().isInvalid
    }
  }

  class NonEmptyStringGen : Gen<String> {
    override fun generate(): String = Gen.string().filter(String::isNotEmpty).generate()
  }
}