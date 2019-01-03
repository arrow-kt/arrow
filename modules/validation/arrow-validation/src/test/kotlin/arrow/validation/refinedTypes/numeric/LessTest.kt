package arrow.validation.refinedTypes.numeric

import arrow.core.extensions.order
import arrow.test.UnitSpec
import arrow.validation.refinedTypes.numeric.validated.less.less
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.Gen
import io.kotlintest.properties.filter
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class LessTest : UnitSpec() {
  init {
    val max = 100

    "Can create Less for every number less than max defined by instance" {
      forAll(LessThanGen(max)) { x: Int ->
        x.less(Int.order(), max).isValid
      }
    }

    "Can not create Less for every number greater or equal to max defined by instance" {
      forAll(GreaterOrEqThanGen(max)) { x: Int ->
        x.less(Int.order(), max).isInvalid
      }
    }

  }

  class LessThanGen(private val max: Int) : Gen<Int> {
    override fun generate(): Int = Gen.int().filter { it < max }.generate()
  }

  class GreaterOrEqThanGen(private val max: Int) : Gen<Int> {
    override fun generate(): Int = Gen.int().filter { it >= max }.generate()
  }
}