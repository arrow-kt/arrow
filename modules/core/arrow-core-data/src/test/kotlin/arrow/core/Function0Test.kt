package arrow.core

import arrow.Kind
import arrow.core.extensions.function0.applicative.applicative
import arrow.core.extensions.function0.bimonad.bimonad
import arrow.core.extensions.function0.comonad.comonad
import arrow.core.extensions.function0.functor.functor
import arrow.core.extensions.function0.monad.monad
import arrow.core.extensions.function0.monoid.monoid
import arrow.core.extensions.function0.selective.selective
import arrow.core.extensions.function0.semigroup.semigroup
import arrow.core.extensions.monoid
import arrow.core.extensions.semigroup
import arrow.test.UnitSpec
import arrow.test.generators.GenK
import arrow.test.laws.BimonadLaws
import arrow.test.laws.MonoidLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
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

  val eqk = object : EqK<ForFunction0> {
    override fun <A> Kind<ForFunction0, A>.eqK(other: Kind<ForFunction0, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let {
        EQ.run { (it.first)().eqv((it.second)()) }
      }
  }

  val genk = object : GenK<ForFunction0> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<ForFunction0, A>> =
      gen.map { { it }.k() }
  }

  init {
    testLaws(
      MonoidLaws.laws(Function0.monoid(Int.monoid()), Gen.constant({ 1 }.k()), EQ1),

      BimonadLaws.laws(
        Function0.bimonad(),
        Function0.monad(),
        Function0.comonad(),
        Function0.functor(),
        Function0.applicative(),
        Function0.selective(),
        genk, eqk
      )
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
