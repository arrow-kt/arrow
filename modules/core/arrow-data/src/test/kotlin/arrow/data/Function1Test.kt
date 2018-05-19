package arrow.data

import arrow.core.*
import arrow.test.UnitSpec
import arrow.test.laws.ContravariantLaws
import arrow.test.laws.MonadLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function1Test : UnitSpec() {
  val EQ: Eq<Function1Of<Int, Int>> = Eq { a, b ->
    a(1) == b(1)
  }

  init {

    testLaws(
      ContravariantLaws.laws(Function1.contravariant(), { Function1.just(it) }, EQ),
      MonadLaws.laws(Function1.monad<Int>(), EQ))
  }
}
