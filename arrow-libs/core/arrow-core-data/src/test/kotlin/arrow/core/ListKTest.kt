package arrow.core

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.list.zip.zipWith
import arrow.core.extensions.listk.align.align
import arrow.core.extensions.listk.applicative.applicative
import arrow.core.extensions.listk.crosswalk.crosswalk
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.listk.eqK.eqK
import arrow.core.extensions.listk.foldable.foldable
import arrow.core.extensions.listk.functor.functor
import arrow.core.extensions.listk.hash.hash
import arrow.core.extensions.listk.monad.monad
import arrow.core.extensions.listk.monadCombine.monadCombine
import arrow.core.extensions.listk.monadLogic.monadLogic
import arrow.core.extensions.listk.monoid.monoid
import arrow.core.extensions.listk.monoidK.monoidK
import arrow.core.extensions.listk.monoidal.monoidal
import arrow.core.extensions.listk.order.order
import arrow.core.extensions.listk.semialign.semialign
import arrow.core.extensions.listk.semigroupK.semigroupK
import arrow.core.extensions.listk.show.show
import arrow.core.extensions.listk.traverse.traverse
import arrow.core.extensions.listk.unalign.unalign
import arrow.core.extensions.listk.unzip.unzip
import arrow.core.extensions.order
import arrow.core.extensions.listk.zip.zipWith
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.listK
import arrow.core.test.laws.AlignLaws
import arrow.core.test.laws.CrosswalkLaws
import arrow.core.test.laws.EqKLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonadCombineLaws
import arrow.core.test.laws.MonadLogicLaws
import arrow.core.test.laws.MonoidKLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.MonoidalLaws
import arrow.core.test.laws.OrderLaws
import arrow.core.test.laws.SemigroupKLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.core.test.laws.UnalignLaws
import arrow.core.test.laws.UnzipLaws
import arrow.core.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import kotlin.math.max
import kotlin.math.min
import arrow.core.extensions.list.monad.flatten as monadFlatten
import arrow.core.extensions.listk.monad.flatten as kMonadFlatten

class ListKTest : UnitSpec() {

  init {

    val EQ: Eq<ListKOf<Int>> = ListK.eq(Eq.any())

    testLaws(
      MonadCombineLaws.laws(
        ListK.monadCombine(),
        ListK.functor(),
        ListK.applicative(),
        ListK.monad(),
        ListK.genK(),
        ListK.eqK()
      ),
      ShowLaws.laws(ListK.show(Int.show()), EQ, Gen.listK(Gen.int())),
      MonoidLaws.laws(ListK.monoid(), Gen.listK(Gen.int()), ListK.eq(Int.eq())),
      SemigroupKLaws.laws(ListK.semigroupK(), ListK.genK(), ListK.eqK()),
      MonoidalLaws.laws(ListK.monoidal(),
        ListK.genK(10),
        ListK.eqK(),
        this::bijection),
      MonoidKLaws.laws(ListK.monoidK(), ListK.genK(), ListK.eqK()),
      TraverseLaws.laws(ListK.traverse(), ListK.applicative(), ListK.genK(), ListK.eqK()),

      HashLaws.laws(ListK.hash(Int.hash()), Gen.listK(Gen.int()), ListK.eq(Int.eq())),
      OrderLaws.laws(ListK.order(Int.order()), Gen.listK(Gen.int()).map { it as ListKOf<Int> }),
      EqKLaws.laws(
        ListK.eqK(),
        ListK.genK()
      ),
      AlignLaws.laws(ListK.align(),
        ListK.genK(),
        ListK.eqK(),
        ListK.foldable()
      ),
      UnalignLaws.laws(ListK.unalign(),
        ListK.genK(),
        ListK.eqK(),
        ListK.foldable()
      ),
      UnzipLaws.laws(ListK.unzip(),
        ListK.genK(),
        ListK.eqK(),
        ListK.foldable()
      ),
      CrosswalkLaws.laws(ListK.crosswalk(),
        ListK.genK(),
        ListK.eqK()
      ),
      MonadLogicLaws.laws(ListK.monadLogic(), ListK.genK(), ListK.eqK())
    )

    "stdlib list can flatten" {
      val a: List<List<Int>> = listOf(listOf(0, 1), listOf(2), listOf(3, 4), listOf(5))

      a.monadFlatten() shouldBe listOf(0, 1, 2, 3, 4, 5)
    }

    "can flatten" {
      val a: ListK<ListK<Int>> = listOf(listOf(0, 1).k(), listOf(2).k(), listOf(3, 4).k(), listOf(5).k()).k()

      a.kMonadFlatten() shouldBe listOf(0, 1, 2, 3, 4, 5)
    }

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

    "lpadzip" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->

        val result =
          a.lpadZip(b)

        result.map { it.b }.equalUnderTheLaw(b, ListK.eq(Int.eq()))
      }
    }

    "lpadzipwith" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->

        val result =
          a.lpadZipWith(b) { a, b ->
            a toT b
          }

        result.map { it.b }.equalUnderTheLaw(b, ListK.eq(Int.eq()))
      }
    }

    "leftPadZip (with map)" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->
        val left = a.map { it }.k() + List(max(0, b.count() - a.count())) { null }.k()
        val right = b.map { it }.k() + List(max(0, a.count() - b.count())) { null }.k()

        val result =
          a.leftPadZip(b) { a, b ->
            a toT b
          }

        result == left.zipWith(right) { l, r -> l toT r }.filter { it.b != null }
      }
    }

    "leftPadZip (without map)" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->
        val left = a.map { it }.k() + List(max(0, b.count() - a.count())) { null }.k()
        val right = b.map { it }.k() + List(max(0, a.count() - b.count())) { null }.k()

        val result = a.leftPadZip(b)

        result == left.zipWith(right) { l, r -> l toT r }.filter { it.b != null }
      }
    }

    "rpadzip" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->

        val result =
          a.rpadZip(b)

        result.map { it.a }.equalUnderTheLaw(a, ListK.eq(Int.eq()))
      }
    }

    "rightPadZip (without map)" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->
        val left = a.map { it }.k() + List(max(0, b.count() - a.count())) { null }.k()
        val right = b.map { it }.k() + List(max(0, a.count() - b.count())) { null }.k()

        val result = a.rightPadZip(b)

        result == left.zipWith(right) { l, r -> l toT r }.filter { it.a != null } &&
          result.map { it.a }.equalUnderTheLaw(a, ListK.eq(Int.eq()))
      }
    }

    "rpadzipwith" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->

        val result =
          a.rpadZipWith(b) { a, b ->
            a toT b
          }

        result.map { it.a }.equalUnderTheLaw(a, ListK.eq(Int.eq()))
      }
    }

    "rightPadZip (with map)" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->
        val left = a.map { it }.k() + List(max(0, b.count() - a.count())) { null }.k()
        val right = b.map { it }.k() + List(max(0, a.count() - b.count())) { null }.k()

        val result =
          a.rightPadZip(b) { a, b ->
            a toT b
          }

        result == left.zipWith(right) { l, r -> l toT r }.filter { it.a != null } &&
          result.map { it.a }.equalUnderTheLaw(a, ListK.eq(Int.eq()))
      }
    }

    "padZip" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->
        val left = a.map { Some(it) }.k() + List(max(0, b.count() - a.count())) { None }.k()
        val right = b.map { Some(it) }.k() + List(max(0, a.count() - b.count())) { None }.k()

        a.padZip(b) == left.zipWith(right) { l, r -> l toT r }
      }
    }

    "padZipWith" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->
        val left = a.map { Some(it) }.k() + List(max(0, b.count() - a.count())) { None }.k()
        val right = b.map { Some(it) }.k() + List(max(0, a.count() - b.count())) { None }.k()
        a.padZipWith(b) { l, r -> Ior.fromOptions(l, r) } == left.zipWith(right) { l, r -> Ior.fromOptions(l, r) }
      }
    }

    "padZip (with map)" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->
        val left = a.map { it }.k() + List(max(0, b.count() - a.count())) { null }.k()
        val right = b.map { it }.k() + List(max(0, a.count() - b.count())) { null }.k()
        a.padZip(b) { l, r -> Ior.fromNullables(l, r) } == left.zipWith(right) { l, r -> Ior.fromNullables(l, r) }
      }
    }

    "padZipWithNull" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->
        val left = a.map { it }.k() + List(max(0, b.count() - a.count())) { null }.k()
        val right = b.map { it }.k() + List(max(0, a.count() - b.count())) { null }.k()

        a.padZipWithNull(b) == left.zipWith(right) { l, r -> l toT r }
      }
    }

    "filterMap() should map list and filter out None values" {
      forAll(Gen.listK(Gen.int())) { listk ->
          listk.filterMap {
            when (it % 2 == 0) {
              true -> it.toString().toOption()
              else -> None
            }
          } == listk.toList().filter { it % 2 == 0 }.map { it.toString() }.k()
      }
    }

    "mapNotNull() should map list and filter out null values" {
      forAll(Gen.listK(Gen.int())) { listk ->
        listk.mapNotNull {
          when (it % 2 == 0) {
            true -> it.toString()
            else -> null
          }
        } == listk.toList().filter { it % 2 == 0 }.map { it.toString() }.k()
      }
    }
  }

  private fun bijection(from: Kind<ForListK, Tuple2<Tuple2<Int, Int>, Int>>): ListK<Tuple2<Int, Tuple2<Int, Int>>> =
    from.fix().map { Tuple2(it.a.a, Tuple2(it.a.b, it.b)) }.k()
}
