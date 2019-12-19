package arrow.core

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.nonemptylist.applicative.applicative
import arrow.core.extensions.nonemptylist.bimonad.bimonad
import arrow.core.extensions.nonemptylist.comonad.comonad
import arrow.core.extensions.nonemptylist.eq.eq
import arrow.core.extensions.nonemptylist.eqK.eqK
import arrow.core.extensions.nonemptylist.foldable.foldable
import arrow.core.extensions.nonemptylist.functor.functor
import arrow.core.extensions.nonemptylist.hash.hash
import arrow.core.extensions.nonemptylist.monad.monad
import arrow.core.extensions.nonemptylist.semialign.semialign
import arrow.core.extensions.nonemptylist.semigroup.semigroup
import arrow.core.extensions.nonemptylist.semigroupK.semigroupK
import arrow.core.extensions.nonemptylist.show.show
import arrow.core.extensions.nonemptylist.traverse.traverse
import arrow.core.extensions.nonemptylist.unzip.unzip
import arrow.test.UnitSpec
import arrow.test.generators.genK
import arrow.test.generators.nonEmptyList
import arrow.test.laws.BimonadLaws
import arrow.test.laws.EqKLaws
import arrow.test.laws.HashLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.SemigroupLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.test.laws.UnzipLaws
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.max
import kotlin.math.min

class NonEmptyListTest : UnitSpec() {
  init {

    val EQ1 = NonEmptyList.eq(Int.eq())

    testLaws(
      ShowLaws.laws(NonEmptyList.show(), EQ1, Gen.nonEmptyList(Gen.int())),
      SemigroupKLaws.laws(
        NonEmptyList.semigroupK(),
        NonEmptyList.genK(),
        NonEmptyList.eqK()),
      BimonadLaws.laws(
        NonEmptyList.bimonad(),
        NonEmptyList.monad(),
        NonEmptyList.comonad(),
        NonEmptyList.functor(),
        NonEmptyList.applicative(),
        NonEmptyList.monad(),
        NonEmptyList.genK(),
        NonEmptyList.eqK()
      ),
      TraverseLaws.laws(NonEmptyList.traverse(), NonEmptyList.genK(), NonEmptyList.eqK()),
      SemigroupLaws.laws(NonEmptyList.semigroup(), Nel(1, 2, 3), Nel(3, 4, 5), Nel(6, 7, 8), EQ1),
      HashLaws.laws(NonEmptyList.hash(Int.hash()), EQ1, Gen.nonEmptyList(Gen.int())),
      EqKLaws.laws(
        NonEmptyList.eqK(),
        NonEmptyList.genK()
      ),
      UnzipLaws.laws(NonEmptyList.unzip(),
        NonEmptyList.genK(),
        NonEmptyList.eqK(),
        NonEmptyList.foldable()
      )
    )

    "can align lists with different lengths" {
      forAll(Gen.nonEmptyList(Gen.bool()), Gen.nonEmptyList(Gen.bool())) { a, b ->
        NonEmptyList.semialign().run {
          align(a, b).fix().size == max(a.size, b.size)
        }
      }

      forAll(Gen.nonEmptyList(Gen.bool()), Gen.nonEmptyList(Gen.bool())) { a, b ->
        NonEmptyList.semialign().run {
          align(a, b).fix().all.take(min(a.size, b.size)).all {
            it.isBoth
          }
        }
      }
    }
  }
}
