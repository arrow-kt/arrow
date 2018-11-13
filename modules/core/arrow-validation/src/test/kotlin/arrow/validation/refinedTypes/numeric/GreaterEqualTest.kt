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
    val LESS = { max: Int ->
      object : Less<ValidatedPartialOf<Nel<RefinedPredicateException>>, Int> {
        override fun ORD(): Order<Int> = Int.order()

        override fun max(): Int = max

        override fun applicativeError(): ApplicativeError<ValidatedPartialOf<Nel<RefinedPredicateException>>,
          Nel<RefinedPredicateException>> =
          Validated.applicativeError(Nel.semigroup())

        override fun invalidValueMsg(a: Int): String = Less.errorMsg(a, max)
      }
    }

    "Can create GreaterEqual for every number greater or equal than min defined by instance" {
      forAll(GreaterEqualGen(min)) { x: Int ->
        x.greaterEqual(LESS(min)).isValid
      }
    }

    "Can not create GreaterEqual for any number lesser than min defined by instance" {
      forAll(LessTest.LessThanGen(min)) { x: Int ->
        x.greaterEqual(LESS(min)).isInvalid
      }
    }
  }

  class GreaterEqualGen(private val min: Int) : Gen<Int> {
    override fun generate(): Int = Gen.int().filter { it >= min }.generate()
  }
}