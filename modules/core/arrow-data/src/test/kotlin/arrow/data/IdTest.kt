package arrow.data

import arrow.core.*
import arrow.instances.extensions
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IdTest : UnitSpec() {
  init {

    ForId extensions {
      testLaws(
        EqLaws.laws(Id.eq(Eq.any())) { Id(it) },
        ShowLaws.laws(Id.show(), Eq.any()) { Id(it) },
        MonadLaws.laws(this, Eq.any()),
        TraverseLaws.laws(Id.traverse(), this, ::Id, Eq.any()),
        ComonadLaws.laws(this, ::Id, Eq.any())
      )
    }
  }
}
