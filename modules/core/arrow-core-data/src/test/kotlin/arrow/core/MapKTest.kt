package arrow.core

import arrow.Kind2
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.mapk.align.align
import arrow.core.extensions.mapk.eq.eq
import arrow.core.extensions.mapk.eqK.eqK
import arrow.core.extensions.mapk.foldable.foldable
import arrow.core.extensions.mapk.functorFilter.functorFilter
import arrow.core.extensions.mapk.hash.hash
import arrow.core.extensions.mapk.monoid.monoid
import arrow.core.extensions.mapk.semialign.semialign
import arrow.core.extensions.mapk.show.show
import arrow.core.extensions.mapk.traverse.traverse
import arrow.core.extensions.mapk.unalign.unalign
import arrow.core.extensions.mapk.unzip.unzip
import arrow.core.extensions.semigroup
import arrow.test.UnitSpec
import arrow.test.generators.genK
import arrow.test.generators.mapK
import arrow.test.laws.AlignLaws
import arrow.test.laws.EqLaws
import arrow.test.laws.FoldableLaws
import arrow.test.laws.FunctorFilterLaws
import arrow.test.laws.HashLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.test.laws.UnalignLaws
import arrow.test.laws.UnzipLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class MapKTest : UnitSpec() {

  val EQ: Eq<Kind2<ForMapK, String, Int>> = object : Eq<Kind2<ForMapK, String, Int>> {
    override fun Kind2<ForMapK, String, Int>.eqv(b: Kind2<ForMapK, String, Int>): Boolean =
      fix()["key"] == b.fix()["key"]
  }

  init {
    val EQ_TC = MapK.eq(String.eq(), Int.eq())

    val testLaws = testLaws(
      ShowLaws.laws(MapK.show(), EQ_TC, Gen.mapK(Gen.string(), Gen.int())),
      TraverseLaws.laws(MapK.traverse(), MapK.genK(Gen.string()), MapK.eqK(String.eq())),
      MonoidLaws.laws(MapK.monoid<String, Int>(Int.semigroup()), Gen.mapK(Gen.string(), Gen.int()), EQ),
      FoldableLaws.laws(MapK.foldable(), MapK.genK(Gen.string())),
      EqLaws.laws(MapK.eq(String.eq(), Int.eq()), Gen.mapK(Gen.string(), Gen.int())),
      FunctorFilterLaws.laws(MapK.functorFilter(), MapK.genK(Gen.string()), MapK.eqK(String.eq())),
      HashLaws.laws(MapK.hash(String.hash(), Int.hash()), EQ_TC, Gen.mapK(Gen.string(), Gen.int())),
      AlignLaws.laws(MapK.align(),
        MapK.genK(Gen.string()),
        MapK.eqK(String.eq()),
        MapK.foldable()
      ),
      UnalignLaws.laws(MapK.unalign(),
        MapK.genK(Gen.string()),
        MapK.eqK(String.eq()),
        MapK.foldable()),
      UnzipLaws.laws(MapK.unzip(),
        MapK.genK(Gen.string()),
        MapK.eqK(String.eq()),
        MapK.foldable()
      )
    )

    "can align maps" {
      // aligned keySet is union of a's and b's keys
      forAll(Gen.mapK(Gen.string(), Gen.bool()), Gen.mapK(Gen.string(), Gen.bool())) { a, b ->
        MapK.semialign<String>().run {
          val aligned = align(a, b).fix()

          aligned.size == (a.keys + b.keys).size
        }
      }

      // aligned map contains Both for all entries existing in a and b
      forAll(Gen.mapK(Gen.string(), Gen.bool()), Gen.mapK(Gen.string(), Gen.bool())) { a, b ->
        MapK.semialign<String>().run {
          val aligned = align(a, b).fix()
          a.keys.intersect(b.keys).all {
            aligned[it]?.isBoth ?: false
          }
        }
      }

      // aligned map contains Left for all entries existing only in a
      forAll(Gen.mapK(Gen.string(), Gen.bool()), Gen.mapK(Gen.string(), Gen.bool())) { a, b ->
        MapK.semialign<String>().run {
          val aligned = align(a, b).fix()
          (a.keys - b.keys).all { key ->
            aligned[key]?.let { it.isLeft } ?: false
          }
        }
      }

      // aligned map contains Right for all entries existing only in b
      forAll(Gen.mapK(Gen.string(), Gen.bool()), Gen.mapK(Gen.string(), Gen.bool())) { a, b ->
        MapK.semialign<String>().run {
          val aligned = align(a, b).fix()
          (b.keys - a.keys).all { key ->
            aligned[key]?.let { it.isRight } ?: false
          }
        }
      }
    }
  }
}
