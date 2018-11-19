package arrow.data

import arrow.instances.eq
import arrow.instances.hash
import arrow.instances.listk.applicative.applicative
import arrow.instances.listk.eq.eq
import arrow.instances.listk.hash.hash
import arrow.instances.listk.monoidK.monoidK
import arrow.instances.listk.semigroupK.semigroupK
import arrow.instances.listk.show.show
import arrow.instances.listk.traverse.traverse
import arrow.mtl.instances.listk.monadCombine.monadCombine
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
      ShowLaws.laws(ListK.show(), EQ) { listOf(it).k() },
      SemigroupKLaws.laws(ListK.semigroupK(), applicative, Eq.any()),
      MonoidKLaws.laws(ListK.monoidK(), applicative, Eq.any()),
      TraverseLaws.laws(ListK.traverse(), applicative, { n: Int -> ListK(listOf(n)) }, Eq.any()),
      MonadCombineLaws.laws(ListK.monadCombine(),
        { n -> ListK(listOf(n)) },
        { n -> ListK(listOf({ s: Int -> n * s })) },
        EQ),
      HashLaws.laws(ListK.hash(Int.hash()), ListK.eq(Int.eq())) { listOf(it).k() }
    )

  }
}
