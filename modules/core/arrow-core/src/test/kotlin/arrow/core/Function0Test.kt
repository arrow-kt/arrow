package arrow.core

import arrow.Kind
import arrow.core.*
import arrow.instances.extensions
import arrow.instances.function0.comonad.comonad
import arrow.instances.function0.monad.monad
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.test.laws.MonadLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function0Test : UnitSpec() {
  val EQ: Eq<Kind<ForFunction0, Int>> = Eq { a, b ->
    a() == b()
  }

  init {
      testLaws(
        MonadLaws.laws(Function0.monad(), EQ),
        ComonadLaws.laws(Function0.comonad(), { { it }.k() }, EQ)
      )
  }
}
