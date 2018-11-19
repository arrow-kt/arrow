package arrow.validation.refinedTypes.numeric

import arrow.data.Nel
import arrow.data.Validated
import arrow.data.ValidatedPartialOf
import arrow.instances.nonemptylist.semigroup.semigroup
import arrow.instances.order
import arrow.instances.validated.applicativeError.applicativeError
import arrow.test.UnitSpec
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Order
import arrow.validation.RefinedPredicateException
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
    val GREATER = { max: Int ->
      object : Greater<ValidatedPartialOf<Nel<RefinedPredicateException>>, Int> {
        override fun ORD(): Order<Int> = Int.order()
        override fun min(): Int = max

        override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
          Nel<RefinedPredicateException>> =
          Validated.applicativeError(Nel.semigroup())
      }
    }

    "Can create LessEqual for every number less or equal than min defined by instance" {
      forAll(LessEqualGen(max)) { x: Int ->
        x.lessEqual(GREATER(max)).isValid
      }
    }

    "Can not create LessEqual for any number greater than min defined by instance" {
      forAll(GreaterTest.GreaterGen(max)) { x: Int ->
        x.lessEqual(GREATER(max)).isInvalid
      }
    }

  }

  class LessEqualGen(private val max: Int) : Gen<Int> {
    override fun generate(): Int = Gen.int().filter { it <= max }.generate()
  }
}