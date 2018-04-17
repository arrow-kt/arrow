package arrow.data

import arrow.core.Function1
import arrow.core.Function1Of
import arrow.core.invoke
import arrow.core.monad
import arrow.test.UnitSpec
import arrow.test.laws.MonadLaws
import arrow.typeclasses.Eq

import org.junit.runner.RunWith


class Function1Test : UnitSpec() {
  val EQ: Eq<Function1Of<Int, Int>> = Eq { a, b ->
    a(1) == b(1)
  }

  init {

    testLaws(MonadLaws.laws(Function1.monad<Int>(), EQ))
  }
}
