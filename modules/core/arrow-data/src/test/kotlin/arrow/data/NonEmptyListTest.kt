package arrow.data

import arrow.instances.IntEqInstance
import arrow.instances.eq
import arrow.instances.extensions
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListTest : UnitSpec() {
  init {

    val EQ = NonEmptyList.eq(Int.eq())
    ForNonEmptyList extensions {
      testLaws(
        EqLaws.laws(EQ) { it.nel() },
        ShowLaws.laws(NonEmptyList.show(), EQ) { it.nel() },
        MonadLaws.laws(this, Eq.any()),
        SemigroupKLaws.laws(
          this,
          this,
          Eq.any()),
        ComonadLaws.laws(this, { NonEmptyList.of(it) }, Eq.any()),
        TraverseLaws.laws(this, this, { n: Int -> NonEmptyList.of(n) }, Eq.any())
      )
    }

  }
}
