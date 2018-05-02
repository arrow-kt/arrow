package arrow.data

import arrow.instances.IntEqInstance
import arrow.instances.eq
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListTest : UnitSpec() {
  init {

    val applicative = NonEmptyList.applicative()

    val EQ = NonEmptyList.eq(Int.eq())
    testLaws(
      EqLaws.laws(EQ) { it.nel() },
      ShowLaws.laws(NonEmptyList.show(), EQ) { it.nel() },
      MonadLaws.laws(NonEmptyList.monad(), Eq.any()),
      SemigroupKLaws.laws(
        NonEmptyList.semigroupK(),
        applicative,
        Eq.any()),
      ComonadLaws.laws(NonEmptyList.comonad(), { NonEmptyList.of(it) }, Eq.any()),
      TraverseLaws.laws(NonEmptyList.traverse(), applicative, { n: Int -> NonEmptyList.of(n) }, Eq.any())
    )

  }
}
