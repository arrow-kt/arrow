package arrow.data

import arrow.instances.syntax.listk.applicative.applicative
import arrow.instances.syntax.listk.eq.eq
import arrow.instances.syntax.listk.monoidK.monoidK
import arrow.instances.syntax.listk.semigroupK.semigroupK
import arrow.instances.syntax.listk.show.show
import arrow.instances.syntax.listk.traverse.traverse
import arrow.mtl.instances.syntax.listk.monadCombine.monadCombine
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

    testLaws(
      EqLaws.laws(EQ) { listOf(it).k() },
      ShowLaws.laws(ListK.show(), EQ) { listOf(it).k() },
      SemigroupKLaws.laws(ListK.semigroupK(), applicative, Eq.any()),
      MonoidKLaws.laws(ListK.monoidK(), applicative, Eq.any()),
      TraverseLaws.laws(ListK.traverse(), applicative, { n: Int -> ListK(listOf(n)) }, Eq.any()),
      MonadCombineLaws.laws(ListK.monadCombine(),
        { n -> ListK(listOf(n)) },
        { n -> ListK(listOf({ s: Int -> n * s })) },
        EQ)
    )

  }
}
