package arrow.fx

import arrow.fx.extensions.io.functor.functor
import arrow.test.UnitSpec
import arrow.test.laws.FunctorLaws

@kotlinx.coroutines.ObsoleteCoroutinesApi
class IOTest2 : UnitSpec() {

  init {
    testLaws(FunctorLaws.laws(IO.functor(), IO.genK(), IO.eqK()))
  }
}

