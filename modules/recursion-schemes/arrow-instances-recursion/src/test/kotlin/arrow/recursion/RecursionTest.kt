package arrow.recursion

import arrow.core.Option
import arrow.instances.option.functor.functor
import arrow.test.UnitSpec
import arrow.test.generators.fromGNatAlgebra
import arrow.test.generators.toGNatCoalgebra
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class RecursionTest : UnitSpec() {
  init {
    "Hylo should be stack safe" {
      Option.functor().hylo(fromGNatAlgebra(), toGNatCoalgebra(), 100000)
    }

    "Hylo == id if alg . coalg == id" {
      forAll(Gen.choose(0, 1000)) {
        Option.functor().hylo(fromGNatAlgebra(), toGNatCoalgebra(), it) == it
      }
    }
  }
}
