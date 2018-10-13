package arrow.data

import arrow.instances.eq
import arrow.instances.syntax.nonemptylist.applicative.applicative
import arrow.instances.syntax.nonemptylist.comonad.comonad
import arrow.instances.syntax.nonemptylist.eq.eq
import arrow.instances.syntax.nonemptylist.monad.monad
import arrow.instances.syntax.nonemptylist.semigroupK.semigroupK
import arrow.instances.syntax.nonemptylist.show.show
import arrow.instances.syntax.nonemptylist.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListTest : UnitSpec() {
  init {

    val EQ = NonEmptyList.eq(Int.eq())
    testLaws(
      EqLaws.laws(EQ) { it.nel() },
      ShowLaws.laws(NonEmptyList.show(), EQ) { it.nel() },
      MonadLaws.laws(NonEmptyList.monad(), Eq.any()),
      SemigroupKLaws.laws(
        NonEmptyList.semigroupK(),
        NonEmptyList.applicative(),
        Eq.any()),
      ComonadLaws.laws(NonEmptyList.comonad(), { NonEmptyList.of(it) }, Eq.any()),
      TraverseLaws.laws(NonEmptyList.traverse(), NonEmptyList.applicative(), { n: Int -> NonEmptyList.of(n) }, Eq.any())
    )

  }
}
