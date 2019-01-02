package arrow.validation.refinedTypes.numeric

import arrow.core.extensions.order
import arrow.test.UnitSpec
import arrow.validation.refinedTypes.numeric.validated.greater.greater
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.filter
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class GreaterTest : UnitSpec() {
  init {
    val min = 100

    "Can create Greater for every number greater than the min defined by instace" {
      forAll(GreaterGen(min)) { x: Int ->
        x.greater(Int.order(), min).isValid
      }
    }

    "Can not create Greater for any number less or equal than the min defined by instance" {
      forAll(LessEqualTest.LessEqualGen(min)) { x: Int ->
        x.greater(Int.order(), min).isInvalid
      }
    }
  }

  class GreaterGen(private val min: Int) : Gen<Int> {
    override fun generate(): Int = Gen.int().filter { it > min }.generate()
  }
}