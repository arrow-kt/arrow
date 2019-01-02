package arrow.validation.refinedTypes.generic

import arrow.test.UnitSpec
import arrow.validation.refinedTypes.generic.validated.nonEmpty.nonEmpty
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.filter
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyTest : UnitSpec() {
  init {
    "Should create NonEmpty for every string with length > 0" {
      forAll(NonEmptyStringGen()) { s: String ->
        s.nonEmpty("").isValid
      }
    }

    "Should not create NonEmpty for empty strings" {
      "".nonEmpty("").isInvalid
    }
  }

  class NonEmptyStringGen : Gen<String> {
    override fun generate(): String = Gen.string().filter(String::isNotEmpty).generate()
  }
}