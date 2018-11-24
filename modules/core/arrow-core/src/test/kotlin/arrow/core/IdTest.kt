package arrow.core

import arrow.core.Id
import arrow.instances.eq
import arrow.instances.hash
import arrow.instances.id.applicative.applicative
import arrow.instances.id.comonad.comonad
import arrow.instances.id.eq.eq
import arrow.instances.id.hash.hash
import arrow.instances.id.monad.monad
import arrow.instances.id.show.show
import arrow.instances.id.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class IdTest : UnitSpec() {
  init {
    testLaws(
      ShowLaws.laws(Id.show(), Eq.any()) { Id(it) },
      MonadLaws.laws(Id.monad(), Eq.any()),
      TraverseLaws.laws(Id.traverse(), Id.applicative(), ::Id, Eq.any()),
      ComonadLaws.laws(Id.comonad(), ::Id, Eq.any()),
      HashLaws.laws(Id.hash(Int.hash()), Id.eq(Int.eq())) { Id(it) }
    )
  }
}
