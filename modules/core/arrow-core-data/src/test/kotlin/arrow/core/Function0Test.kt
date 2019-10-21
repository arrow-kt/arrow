package arrow.core

import arrow.Kind
import arrow.core.extensions.function0.bimonad.bimonad
import arrow.core.extensions.function0.comonad.comonad
import arrow.core.extensions.function0.monad.monad
import arrow.core.extensions.function0.monoid.monoid
import arrow.core.extensions.function0.semigroup.semigroup
import arrow.core.extensions.monoid
import arrow.core.extensions.semigroup
import arrow.test.UnitSpec
import arrow.test.laws.BimonadLaws
import arrow.test.laws.MonoidLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class Function0Test : UnitSpec() {
  val EQ1: Eq<Kind<ForFunction0, Int>> = Eq { a, b ->
    a() == b()
  }

  val EQ2: Eq<Kind<ForFunction0, Kind<ForFunction0, Int>>> = Eq { a, b ->
    a()() == b()()
  }

  init {
    testLaws(
      MonoidLaws.laws(Function0.monoid(Int.monoid()), Gen.constant({ 1 }.k()), EQ1),
      BimonadLaws.laws(Function0.bimonad(), Function0.monad(), Function0.comonad(), { { it }.k() }, EQ1, EQ2, Eq.any())
    )

    "Semigroup of Function0<A> is Function0<Semigroup<A>>" {
      forAll { a: Int ->
        val left = Function0.semigroup(Int.semigroup()).run { Function0 { a }.combine(Function0 { a }) }
        val right = Int.semigroup().run { Function0 { a.combine(a) } }

        left.invoke() == right.invoke()
      }
    }

    "Function0<A>.empty() is Function0{A.empty()}" {
      Function0.monoid(Int.monoid()).run { empty() }.invoke() shouldBe Function0 { Int.monoid().run { empty() } }.invoke()
    }
  }
}
