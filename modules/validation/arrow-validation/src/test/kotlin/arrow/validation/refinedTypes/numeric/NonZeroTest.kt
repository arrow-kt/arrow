package arrow.validation.refinedTypes.numeric

import arrow.test.UnitSpec
import arrow.test.generators.nonZeroInt
import arrow.validation.refinedTypes.numeric.validated.nonZero.nonZero
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class NonZeroTest : UnitSpec() {
  init {

    "Can create NonZero from any number except 0" {
      forAll(Gen.nonZeroInt()) { x: Int ->
        x.nonZero().isValid
      }
    }

    "Can not create NonZero from 0" {
      0.nonZero().isInvalid
    }

  }

}