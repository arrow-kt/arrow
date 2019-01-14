package arrow.validation.refinedTypes.numeric

import arrow.core.extensions.order
import arrow.test.UnitSpec
import arrow.test.generators.genGreaterEqual
import arrow.test.generators.genLessThan
import arrow.validation.refinedTypes.numeric.validated.greaterEqual.greaterEqual
import io.kotlintest.properties.forAll
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class GreaterEqualTest : UnitSpec() {
  init {

    val min = 100

    "Can create GreaterEqual for every number greater or equal than min defined by instance" {
      forAll(genGreaterEqual(min)) { x: Int ->
        x.greaterEqual(Int.order(), min).isValid
      }
    }

    "Can not create GreaterEqual for any number lesser than min defined by instance" {
      forAll(genLessThan(min)) { x: Int ->
        x.greaterEqual(Int.order(), min).isInvalid
      }
    }
  }

}