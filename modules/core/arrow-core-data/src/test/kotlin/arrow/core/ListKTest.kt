package arrow.core

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.hash
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
import arrow.core.extensions.listk.monoid.monoid
import arrow.core.extensions.listk.monoidK.monoidK
import arrow.core.extensions.listk.monoidal.monoidal
import arrow.core.extensions.listk.semialign.semialign
import arrow.core.extensions.listk.semigroupK.semigroupK
import arrow.core.extensions.listk.show.show
import arrow.core.extensions.listk.traverse.traverse
import arrow.core.extensions.listk.unalign.unalign
import arrow.core.extensions.listk.unzip.unzip
import arrow.core.extensions.tuple2.eq.eq
import arrow.test.UnitSpec
import arrow.test.generators.genK
import arrow.test.generators.listK
import arrow.test.laws.AlignLaws
import arrow.test.laws.CrosswalkLaws
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
import arrow.test.laws.UnzipLaws
import arrow.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.max
import kotlin.math.min

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
      ShowLaws.laws(ListK.show(), EQ, Gen.listK(Gen.int())),
      MonoidLaws.laws(ListK.monoid(), Gen.listK(Gen.int()), ListK.eq(Int.eq())),
      SemigroupKLaws.laws(ListK.semigroupK(), ListK.genK(), ListK.eqK()),
      MonoidalLaws.laws(ListK.monoidal(),
        ListK.genK(10),
        ListK.eqK(),
        this::bijection),
      MonoidKLaws.laws(ListK.monoidK(), ListK.genK(), ListK.eqK()),
      TraverseLaws.laws(ListK.traverse(), ListK.genK(), ListK.eqK()),

      HashLaws.laws(ListK.hash(Int.hash()), ListK.eq(Int.eq()), Gen.listK(Gen.int())),
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

    "rpadzip" {
      forAll(Gen.listK(Gen.int()), Gen.listK(Gen.int())) { a, b ->

        val result =
          a.rpadZip(b)

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
  }

  private fun bijection(from: Kind<ForListK, Tuple2<Tuple2<Int, Int>, Int>>): ListK<Tuple2<Int, Tuple2<Int, Int>>> =
    from.fix().map { Tuple2(it.a.a, Tuple2(it.a.b, it.b)) }.k()
}
