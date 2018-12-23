package arrow.core

import arrow.Kind
import arrow.core.*
import arrow.instances.extensions
import arrow.instances.function0.comonad.comonad
import arrow.instances.function0.monad.monad
import arrow.instances.function0.monoid.monoid
import arrow.instances.function0.semigroup.semigroup
import arrow.instances.monoid
import arrow.instances.semigroup
import arrow.test.UnitSpec
import arrow.test.laws.ComonadLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.SemigroupLaws
import arrow.test.laws.MonoidLaws
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
        SemigroupLaws.laws(Function0.semigroup(Int.semigroup()), { 1 }.k(), { 2 }.k(), { 3 }.k(), EQ),
        MonoidLaws.laws(Function0.monoid(Int.monoid()), { 1 }.k(), EQ),
        MonadLaws.laws(Function0.monad(), EQ),
        ComonadLaws.laws(Function0.comonad(), { { it }.k() }, EQ)
      )
  }
}
