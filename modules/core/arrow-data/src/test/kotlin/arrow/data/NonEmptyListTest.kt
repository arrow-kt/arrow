package arrow.data

import arrow.instances.eq
import arrow.instances.hash
import arrow.instances.nonemptylist.applicative.applicative
import arrow.instances.nonemptylist.comonad.comonad
import arrow.instances.nonemptylist.eq.eq
import arrow.instances.nonemptylist.hash.hash
import arrow.instances.nonemptylist.monad.monad
import arrow.instances.nonemptylist.semigroup.semigroup
import arrow.instances.nonemptylist.semigroupK.semigroupK
import arrow.instances.nonemptylist.show.show
import arrow.instances.nonemptylist.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListTest : UnitSpec() {
  init {

    val EQ = NonEmptyList.eq(Int.eq())
    testLaws(
      ShowLaws.laws(NonEmptyList.show(), EQ) { it.nel() },
      MonadLaws.laws(NonEmptyList.monad(), Eq.any()),
      SemigroupKLaws.laws(
        NonEmptyList.semigroupK(),
        NonEmptyList.applicative(),
        Eq.any()),
      ComonadLaws.laws(NonEmptyList.comonad(), { NonEmptyList.of(it) }, Eq.any()),
      TraverseLaws.laws(NonEmptyList.traverse(), NonEmptyList.applicative(), { n: Int -> NonEmptyList.of(n) }, Eq.any()),
      SemigroupLaws.laws(NonEmptyList.semigroup(), Nel(1, 2, 3), Nel(3, 4, 5), Nel(6, 7, 8), NonEmptyList.eq(Int.eq())),
      HashLaws.laws(NonEmptyList.hash(Int.hash()), EQ) { Nel.of(it) }
    )
  }
}
