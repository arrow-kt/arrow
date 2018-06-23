package arrow.data

import arrow.core.*
import arrow.instances.extensions
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
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
