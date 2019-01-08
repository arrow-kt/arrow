package arrow.validation.refinedTypes.numeric

import arrow.test.UnitSpec
import arrow.validation.refinedTypes.numeric.validated.nonZero.nonZero
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.filter
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonZeroTest : UnitSpec() {
  init {

    "Can create NonZero from any number except 0" {
      forAll(NonZeroIntGen()) { x: Int ->
        x.nonZero().isValid
      }
    }

    "Can not create NonZero from 0" {
      0.nonZero().isInvalid
    }

  }

  class NonZeroIntGen : Gen<Int> {
    override fun generate(): Int = Gen.int().filter { it != 0 }.generate()
  }
}