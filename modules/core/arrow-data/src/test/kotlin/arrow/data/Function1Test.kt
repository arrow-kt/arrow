package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.instances.ForFunction1
import arrow.instances.monoid
import arrow.instances.semigroup
import arrow.test.UnitSpec
import arrow.test.laws.ContravariantLaws
import arrow.test.laws.CategoryLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.SemigroupLaws
import arrow.test.laws.ProfunctorLaws
import arrow.typeclasses.*
import io.kotlintest.KTestJUnitRunner
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
    ForFunction1<Int>() extensions {
      testLaws(
        SemigroupLaws.laws(Function1.semigroup<Int, Int>(Int.semigroup()), Function1 { 1 }, Function1 { 2 }, Function1 { 3 }, EQ),
        MonoidLaws.laws(Function1.monoid<Int, Int>(Int.monoid()), Function1 { it }, EQ),
        ContravariantLaws.laws(Function1.contravariant(), { Function1.just<Int, Int>(it).conest() }, ConestedEQ),
        ProfunctorLaws.laws(Function1.profunctor(), { Function1.just(it) }, EQ),
        MonadLaws.laws(this, EQ),
        CategoryLaws.laws(Function1.category(), { Function1.just(it) }, EQ)
      )
    }
  }
}
