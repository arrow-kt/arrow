package arrow.validation.refinedTypes.numeric

import arrow.instances.order
import arrow.test.UnitSpec
import arrow.validation.refinedTypes.numeric.validated.lessEqual.lessEqual
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.filter
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class LessEqualTest : UnitSpec() {
  init {

    val max = 100

    "Can create LessEqual for every number less or equal than min defined by instance" {
      forAll(LessEqualGen(max)) { x: Int ->
        x.lessEqual(Int.order(), max).isValid
      }
    }

    "Can not create LessEqual for any number greater than min defined by instance" {
      forAll(GreaterTest.GreaterGen(max)) { x: Int ->
        x.lessEqual(Int.order(), max).isInvalid
      }
    }

  }

  class LessEqualGen(private val max: Int) : Gen<Int> {
    override fun generate(): Int = Gen.int().filter { it <= max }.generate()
  }
}