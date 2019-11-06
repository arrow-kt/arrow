package arrow.core

import arrow.Kind
import arrow.core.extensions.function1.category.category
import arrow.core.extensions.function1.divisible.divisible
import arrow.core.extensions.function1.monad.monad
import arrow.core.extensions.function1.monoid.monoid
import arrow.core.extensions.function1.profunctor.profunctor
import arrow.core.extensions.function1.semigroup.semigroup
import arrow.core.extensions.monoid
import arrow.core.extensions.semigroup
import arrow.test.UnitSpec
import arrow.test.laws.CategoryLaws
import arrow.test.laws.DivisibleLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.ProfunctorLaws
import arrow.typeclasses.Conested
import arrow.typeclasses.Eq
import arrow.typeclasses.conest
import arrow.typeclasses.counnest
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class Function1Test : UnitSpec() {
  val ConestedEQ: Eq<Kind<Conested<ForFunction1, Int>, Int>> = Eq { a, b ->
    a.counnest().invoke(1) == b.counnest().invoke(1)
  }

  val EQ: Eq<Function1Of<Int, Int>> = Eq { a, b ->
    a(1) == b(1)
  }

  init {
    testLaws(
      MonoidLaws.laws(Function1.monoid<Int, Int>(Int.monoid()), Gen.constant({ a: Int -> a + 1 }.k()), EQ),
      DivisibleLaws.laws(Function1.divisible(Int.monoid()), { Function1.just<Int, Int>(it).conest() }, ConestedEQ),
      ProfunctorLaws.laws(Function1.profunctor(), { Function1.just(it) }, EQ),
      MonadLaws.laws(Function1.monad(), EQ),
      CategoryLaws.laws(Function1.category(), { Function1.just(it) }, EQ)
    )

    "Semigroup of Function1<A> is Function1<Semigroup<A>>" {
      forAll { a: Int ->
        val left = Function1.semigroup<Int, Int>(Int.semigroup()).run {
          Function1<Int, Int> { it }.combine(Function1 { it })
        }

        val right = Function1<Int, Int> { Int.monoid().run { it.combine(it) } }

        left.invoke(a) == right.invoke(a)
      }
    }

    "Function1<A>.empty() is Function1{A.empty()}" {
      forAll { a: Int, b: Int ->
        val left = Function1.monoid<Int, Int>(Int.monoid()).run { empty() }
        val right = Function1<Int, Int> { Int.monoid().run { empty() } }
        left.invoke(a) == right.invoke(b)
      }
    }
  }
}
