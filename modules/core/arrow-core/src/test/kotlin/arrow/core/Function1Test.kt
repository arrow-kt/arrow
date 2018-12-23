package arrow.core

import arrow.Kind
import arrow.core.ForFunction1
import arrow.core.Function1
import arrow.core.Function1Of
import arrow.core.invoke
import arrow.instances.function1.category.category
import arrow.instances.function1.contravariant.contravariant
import arrow.instances.function1.monad.monad
import arrow.instances.function1.monoid.monoid
import arrow.instances.function1.profunctor.profunctor
import arrow.instances.function1.semigroup.semigroup
import arrow.instances.monoid
import arrow.instances.semigroup
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Conested
import arrow.typeclasses.Eq
import arrow.typeclasses.conest
import arrow.typeclasses.counnest
import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function1Test : UnitSpec() {
  val ConestedEQ: Eq<Kind<Conested<ForFunction1, Int>, Int>> = Eq { a, b ->
    a.counnest().invoke(1) == b.counnest().invoke(1)
  }

  val EQ: Eq<Function1Of<Int, Int>> = Eq { a, b ->
    a(1) == b(1)
  }

  init {
    testLaws(
      SemigroupLaws.laws(Function1.semigroup<Int, Int>(Int.semigroup()), { a: Int -> a + 1 }.k(), { a: Int -> a + 2 }.k(), { a: Int -> a + 3 }.k(), EQ),
      MonoidLaws.laws(Function1.monoid<Int, Int>(Int.monoid()), { a: Int -> a + 1 }.k(), EQ),
      ContravariantLaws.laws(Function1.contravariant(), { Function1.just<Int, Int>(it).conest() }, ConestedEQ),
      ProfunctorLaws.laws(Function1.profunctor(), { Function1.just(it) }, EQ),
      MonadLaws.laws(Function1.monad(), EQ),
      CategoryLaws.laws(Function1.category(), { Function1.just(it) }, EQ)
    )

    "Semigroup of Function1<A> is Function1<Semigroup<A>>"() {
      forAll { a: Int ->
        val left = Function1.semigroup<Int, Int>(Int.semigroup()).run {
          Function1<Int, Int>({ it }).combine(Function1<Int, Int>({ it }))
        }

        val right = Function1<Int, Int>({ Int.monoid().run { it.combine(it) } })

        left.invoke(a) == right.invoke(a)
      }
    }

    "Function1<A>.empty() is Function1{A.empty()}" {
      forAll { a: Int, b: Int ->
        val left = Function1.monoid<Int, Int>(Int.monoid()).run { empty() }
        val right = Function1<Int, Int>({ Int.monoid().run { empty() } })
        left.invoke(a) == right.invoke(b)
      }
    }
  }
}
