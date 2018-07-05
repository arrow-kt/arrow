package arrow.data

import arrow.mtl.instances.extensions
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.KTestJUnitRunner
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ListKTest : UnitSpec() {
  val applicative = ListK.applicative()

  init {

    val EQ: Eq<ListKOf<Int>> = ListK.eq(Eq.any())

    ForListK extensions {
      testLaws(
        EqLaws.laws(EQ) { listOf(it).k() },
        ShowLaws.laws(ListK.show(), EQ) { listOf(it).k() },
        SemigroupKLaws.laws(this, applicative, Eq.any()),
        MonoidKLaws.laws(this, applicative, Eq.any()),
        TraverseLaws.laws(this, applicative, { n: Int -> ListK(listOf(n)) }, Eq.any()),
        MonadCombineLaws.laws(this,
          { n -> ListK(listOf(n)) },
          { n -> ListK(listOf({ s: Int -> n * s })) },
          EQ)
      )
    }
  }
}
