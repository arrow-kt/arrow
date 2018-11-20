package arrow.validation.refinedTypes.numeric

import arrow.instances.order
import arrow.test.UnitSpec
import arrow.validation.refinedTypes.numeric.validated.greaterEqual.greaterEqual
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.filter
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class GreaterEqualTest : UnitSpec() {
  init {

    val min = 100

    "Can create GreaterEqual for every number greater or equal than min defined by instance" {
      forAll(GreaterEqualGen(min)) { x: Int ->
        x.greaterEqual(Int.order(), min).isValid
      }
    }

    "Can not create GreaterEqual for any number lesser than min defined by instance" {
      forAll(LessTest.LessThanGen(min)) { x: Int ->
        x.greaterEqual(Int.order(), min).isInvalid
      }
    }
  }

  class GreaterEqualGen(private val min: Int) : Gen<Int> {
    override fun generate(): Int = Gen.int().filter { it >= min }.generate()
  }
}