package arrow.validation.refinedTypes.numeric

import arrow.core.extensions.order
import arrow.test.UnitSpec
import arrow.test.generators.genGreaterOrEqThan
import arrow.test.generators.genLessThan
import arrow.validation.refinedTypes.numeric.validated.less.less
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class LessTest : UnitSpec() {
  init {
    val max = 100

    "Can create Less for every number less than max defined by instance" {
      forAll(genLessThan(max)) { x: Int ->
        x.less(Int.order(), max).isValid
      }
    }

    "Can not create Less for every number greater or equal to max defined by instance" {
      forAll(genGreaterOrEqThan(max)) { x: Int ->
        x.less(Int.order(), max).isInvalid
      }
    }

  }
}