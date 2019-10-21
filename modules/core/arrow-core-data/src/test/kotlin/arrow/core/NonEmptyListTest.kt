package arrow.core

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.nonemptylist.applicative.applicative
import arrow.core.extensions.nonemptylist.bimonad.bimonad
import arrow.core.extensions.nonemptylist.comonad.comonad
import arrow.core.extensions.nonemptylist.eq.eq
import arrow.core.extensions.nonemptylist.hash.hash
import arrow.core.extensions.nonemptylist.monad.monad
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.nonemptylist.semigroupK.semigroupK
import arrow.core.extensions.nonemptylist.show.show
import arrow.core.extensions.nonemptylist.traverse.traverse
import arrow.test.UnitSpec
import arrow.test.laws.BimonadLaws
import arrow.test.laws.HashLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.SemigroupLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq

class NonEmptyListTest : UnitSpec() {
  init {

    val EQ1 = NonEmptyList.eq(Int.eq())
    val EQ2: Eq<Kind<ForNonEmptyList, Kind<ForNonEmptyList, Int>>> = Eq { a, b ->
      a == b
    }

    testLaws(
      ShowLaws.laws(NonEmptyList.show(), EQ1) { it.nel() },
      SemigroupKLaws.laws(
        NonEmptyList.semigroupK(),
        NonEmptyList.applicative(),
        Eq.any()),
      BimonadLaws.laws(NonEmptyList.bimonad(), NonEmptyList.monad(), NonEmptyList.comonad(), { NonEmptyList.of(it) }, Eq.any(), EQ2, Eq.any()),
      TraverseLaws.laws(NonEmptyList.traverse(), NonEmptyList.applicative(), { n: Int -> NonEmptyList.of(n) }, Eq.any()),
      SemigroupLaws.laws(NonEmptyList.semigroup(), Nel(1, 2, 3), Nel(3, 4, 5), Nel(6, 7, 8), EQ1),
      HashLaws.laws(NonEmptyList.hash(Int.hash()), EQ1) { Nel.of(it) }
    )
  }
}
