package arrow.core

import arrow.Kind
import arrow.core.ForFunction1
import arrow.core.Function1
import arrow.core.Function1Of
import arrow.core.invoke
import arrow.instances.function1.category.category
import arrow.instances.function1.contravariant.contravariant
import arrow.instances.function1.monad.monad
import arrow.instances.function1.profunctor.profunctor
import arrow.test.UnitSpec
import arrow.test.laws.CategoryLaws
import arrow.test.laws.ContravariantLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.ProfunctorLaws
import arrow.typeclasses.Conested
import arrow.typeclasses.Eq
import arrow.typeclasses.conest
import arrow.typeclasses.counnest
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
    testLaws(
      ContravariantLaws.laws(Function1.contravariant(), { Function1.just<Int, Int>(it).conest() }, ConestedEQ),
      ProfunctorLaws.laws(Function1.profunctor(), { Function1.just(it) }, EQ),
      MonadLaws.laws(Function1.monad(), EQ),
      CategoryLaws.laws(Function1.category(), { Function1.just(it) }, EQ)
    )
  }
}
