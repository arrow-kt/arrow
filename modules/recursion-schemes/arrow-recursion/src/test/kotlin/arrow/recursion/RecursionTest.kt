package arrow.recursion

import arrow.core.Option
import arrow.core.functor
import arrow.recursion.laws.fromGNatAlgebra
import arrow.recursion.laws.intGen
import arrow.recursion.laws.toGNatCoalgebra
import arrow.test.UnitSpec
import io.kotlintest.properties.forAll

class RecursionTest : UnitSpec() {
  init {
    "Hylo should be stack safe" {
      hylo(Option.functor(), fromGNatAlgebra(), toGNatCoalgebra(), 100000)
    }

    "Hylo == id if alg . coalg == id" {
      forAll(intGen) {
        hylo(Option.functor(), fromGNatAlgebra(), toGNatCoalgebra(), it) == it
      }
    }
  }
}
