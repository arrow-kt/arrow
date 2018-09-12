package arrow.data

import arrow.core.Function1
import arrow.core.Function1Of
import arrow.core.category
import arrow.core.invoke
import arrow.core.profunctor
import arrow.Kind
import arrow.core.*
import arrow.instances.ForFunction1
import arrow.test.UnitSpec
import arrow.test.laws.ContravariantLaws
import arrow.test.laws.CategoryLaws
import arrow.test.laws.MonadLaws
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
        ContravariantLaws.laws(Function1.contravariant(), { Function1.just<Int, Int>(it).conest() }, ConestedEQ),
        ProfunctorLaws.laws(Function1.profunctor(), { Function1.just(it) }, EQ),
        MonadLaws.laws(this, EQ),
        CategoryLaws.laws(Function1.category(), { Function1.just(it) }, EQ)
      )
    }
  }
}
