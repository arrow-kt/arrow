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
import io.kotlintest.properties.forAll
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

    "Semigroup of Function0<A> is Function0<Semigroup<A>>"() {
      forAll { a: Int ->
        val left = Function0.semigroup(Int.semigroup()).run { Function0({ a }).combine(Function0({ a })) }
        val right = Int.semigroup().run { Function0({ a.combine(a) }) }

        left.invoke() == right.invoke()
      }
    }

    "Function0<A>.empty() is Function0{A.empty()}" {
      Function0.monoid(Int.monoid()).run { empty() }.invoke() == Function0({ Int.monoid().run { empty() }}).invoke()
    }
  }
}
