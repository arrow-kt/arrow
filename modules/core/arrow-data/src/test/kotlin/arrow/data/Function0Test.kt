package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.instances.extensions
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
    ForFunction0 extensions {
      testLaws(
        MonadLaws.laws(this, EQ),
        ComonadLaws.laws(this, { { it }.k() }, EQ)
      )
    }
  }
}
