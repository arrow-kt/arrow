package arrow.data

import arrow.core.Function1
import arrow.core.Function1Of
import arrow.core.category
import arrow.core.invoke
import arrow.core.profunctor
import arrow.instances.ForFunction1
import arrow.test.UnitSpec
import arrow.test.laws.CategoryLaws
import arrow.test.laws.MonadLaws
import arrow.test.laws.ProfunctorLaws
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class Function1Test : UnitSpec() {
  val EQ: Eq<Function1Of<Int, Int>> = Eq { a, b ->
    a(1) == b(1)
  }

  init {
    ForFunction1<Int>() extensions {
      testLaws(
        ProfunctorLaws.laws(Function1.profunctor(), { Function1.just(it) }, EQ),
        MonadLaws.laws(this, EQ),
        CategoryLaws.laws(Function1.category(), { Function1.just(it) }, EQ)
      )
    }
  }
}
