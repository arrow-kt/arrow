package arrow.validation.refinedTypes.generic

import arrow.test.UnitSpec
import arrow.test.generators.genNonEmptyString
import arrow.validation.refinedTypes.generic.validated.nonEmpty.nonEmpty
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class NonEmptyTest : UnitSpec() {
  init {
    "Should create NonEmpty for every string with length > 0" {
      forAll(genNonEmptyString()) { s: String ->
        s.nonEmpty("").isValid
      }
    }

    "Should not create NonEmpty for empty strings" {
      "".nonEmpty("").isInvalid
    }
  }

}