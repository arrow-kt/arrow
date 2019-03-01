package arrow.data

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.tuple2.eq.eq
import arrow.data.extensions.list.fx.fx
import arrow.data.extensions.listk.applicative.applicative
import arrow.data.extensions.listk.eq.eq
import arrow.data.extensions.listk.hash.hash
import arrow.data.extensions.listk.monoidK.monoidK
import arrow.data.extensions.listk.semigroupK.semigroupK
import arrow.data.extensions.listk.semigroupal.semigroupal
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

    val eq: Eq<ListKOf<Int>> = ListK.eq(Eq.any())
    val associativeSemigroupalEq: Eq<ListKOf<Tuple2<Int, Tuple2<Int, Int>>>> = ListK.eq(Tuple2.eq(Int.eq(), Tuple2.eq(Int.eq(), Int.eq())))

    testLaws(
      ShowLaws.laws(ListK.show(), eq) { listOf(it).k() },
      SemigroupKLaws.laws(ListK.semigroupK(), applicative, Eq.any()),
      SemigroupalLaws.laws(ListK.semigroupal(), { ListK.just(it) }, this::bijection, associativeSemigroupalEq),
      MonoidKLaws.laws(ListK.monoidK(), applicative, Eq.any()),
      TraverseLaws.laws(ListK.traverse(), applicative, { n: Int -> ListK(listOf(n)) }, Eq.any()),
      MonadCombineLaws.laws(ListK.monadCombine(),
        { n -> ListK(listOf(n)) },
        { n -> ListK(listOf({ s: Int -> n * s })) },
        eq),
      HashLaws.laws(ListK.hash(Int.hash()), ListK.eq(Int.eq())) { listOf(it).k() }
    )

    "List exports a commutative Fx instance" {
      fx(listOf(1), listOf(2), ::identity) shouldBe listOf(1 toT 2)
    }

  }

  private fun bijection(from: Kind<ForListK, Tuple2<Tuple2<Int, Int>, Int>>): ListK<Tuple2<Int, Tuple2<Int, Int>>> =
          from.fix().map { Tuple2(it.a.a, Tuple2(it.a.b, it.b)) }.k()

}
