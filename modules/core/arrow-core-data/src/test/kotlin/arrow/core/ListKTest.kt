package arrow.core

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.listk.align.align
import arrow.core.extensions.listk.applicative.applicative
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.listk.eqK.eqK
import arrow.core.extensions.listk.foldable.foldable

import arrow.core.extensions.listk.hash.hash
import arrow.core.extensions.listk.monadCombine.monadCombine
import arrow.core.extensions.listk.monoid.monoid
import arrow.core.extensions.listk.monoidK.monoidK
import arrow.core.extensions.listk.monoidal.monoidal
import arrow.core.extensions.listk.semialign.semialign
import arrow.core.extensions.listk.semigroupK.semigroupK
import arrow.core.extensions.listk.show.show
import arrow.core.extensions.listk.traverse.traverse
import arrow.core.extensions.listk.unalign.unalign
import arrow.core.extensions.tuple2.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.listK
import arrow.test.laws.AlignLaws
import arrow.test.laws.EqKLaws
import arrow.test.laws.HashLaws
import arrow.test.laws.MonadCombineLaws
import arrow.test.laws.MonoidKLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.MonoidalLaws
import arrow.test.laws.SemigroupKLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.test.laws.UnalignLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.max
import kotlin.math.min

class ListKTest : UnitSpec() {
  val applicative = ListK.applicative()

  init {

    val eq: Eq<ListKOf<Int>> = ListK.eq(Eq.any())
    val associativeSemigroupalEq: Eq<ListKOf<Tuple2<Int, Tuple2<Int, Int>>>> = ListK.eq(Tuple2.eq(Int.eq(), Tuple2.eq(Int.eq(), Int.eq())))

    testLaws(
      MonadCombineLaws.laws(ListK.monadCombine(), { listOf(it).k() }, { i -> listOf({ j: Int -> j + i }).k() }, eq),
      ShowLaws.laws(ListK.show(), eq) { listOf(it).k() },
      MonoidLaws.laws(ListK.monoid(), Gen.listK(Gen.int()), ListK.eq(Int.eq())),
      SemigroupKLaws.laws(ListK.semigroupK(), applicative, Eq.any()),
      MonoidalLaws.laws(ListK.monoidal(), applicative, ListK.eq(Tuple2.eq(Int.eq(), Int.eq())), this::bijection, associativeSemigroupalEq),
      MonoidKLaws.laws(ListK.monoidK(), applicative, Eq.any()),
      TraverseLaws.laws(ListK.traverse(), applicative, { n: Int -> ListK(listOf(n)) }, Eq.any()),
      MonadCombineLaws.laws(ListK.monadCombine(),
        { n -> ListK(listOf(n)) },
        { n -> ListK(listOf({ s: Int -> n * s })) },
        eq),
      HashLaws.laws(ListK.hash(Int.hash()), ListK.eq(Int.eq())) { listOf(it).k() },
      EqKLaws.laws(
        ListK.eqK(),
        ListK.eq(Int.eq()) as Eq<Kind<ForListK, Int>>,
        Gen.listK(Gen.int()) as Gen<Kind<ForListK, Int>>
      ) {
        ListK.just(it)
      },
      AlignLaws.laws(ListK.align(),
        Gen.listK(Gen.int()) as Gen<Kind<ForListK, Int>>,
        ListK.eqK(),
        ListK.foldable()
      ),
      UnalignLaws.laws(ListK.unalign(),
        Gen.listK(Gen.int()) as Gen<Kind<ForListK, Int>>,
        ListK.eqK()
      )
    )

    "can align lists with different lengths" {
      forAll(Gen.listK(Gen.bool()), Gen.listK(Gen.bool())) { a, b ->
        ListK.semialign().run {
          align(a, b).fix().size == max(a.size, b.size)
        }
      }

      forAll(Gen.listK(Gen.bool()), Gen.listK(Gen.bool())) { a, b ->
        ListK.semialign().run {
          align(a, b).fix().take(min(a.size, b.size)).all {
            it.isBoth
          }
        }
      }

      forAll(Gen.listK(Gen.bool()), Gen.listK(Gen.bool())) { a, b ->
        ListK.semialign().run {
          align(a, b).fix().drop(min(a.size, b.size)).all {
            if (a.size < b.size) {
              it.isRight
            } else {
              it.isLeft
            }
          }
        }
      }
    }
  }

  private fun bijection(from: Kind<ForListK, Tuple2<Tuple2<Int, Int>, Int>>): ListK<Tuple2<Int, Tuple2<Int, Int>>> =
    from.fix().map { Tuple2(it.a.a, Tuple2(it.a.b, it.b)) }.k()
}
