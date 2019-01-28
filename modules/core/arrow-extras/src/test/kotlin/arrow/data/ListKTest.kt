package arrow.data

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.identity
import arrow.core.toT
import arrow.data.extensions.list.fx.fx
import arrow.data.extensions.listk.applicative.applicative
import arrow.data.extensions.listk.eq.eq
import arrow.data.extensions.listk.hash.hash
import arrow.data.extensions.listk.monoidK.monoidK
import arrow.data.extensions.listk.semigroupK.semigroupK
import arrow.data.extensions.listk.show.show
import arrow.data.extensions.listk.traverse.traverse
import arrow.mtl.extensions.listk.monadCombine.monadCombine
import arrow.test.UnitSpec
import arrow.test.laws.*
import arrow.typeclasses.Eq
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
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

    "List exports a commutative Fx instance" {
      val ef1 = suspend { 1 }
      val ef2 = suspend { 2 }
      fx(ef1, ef2, ::identity) shouldBe listOf(1 toT 2)
    }

  }
}
