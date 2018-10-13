package arrow.data

import arrow.core.Id
import arrow.instances.syntax.id.applicative.applicative
import arrow.instances.syntax.id.comonad.comonad
import arrow.instances.syntax.id.eq.eq
import arrow.instances.syntax.id.monad.monad
import arrow.instances.syntax.id.show.show
import arrow.instances.syntax.id.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IdTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(Id.eq(Eq.any())) { Id(it) },
      ShowLaws.laws(Id.show(), Eq.any()) { Id(it) },
      MonadLaws.laws(Id.monad(), Eq.any()),
      TraverseLaws.laws(Id.traverse(), Id.applicative(), ::Id, Eq.any()),
      ComonadLaws.laws(Id.comonad(), ::Id, Eq.any())
    )
  }
}
