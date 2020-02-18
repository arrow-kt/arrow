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
import arrow.core.extensions.show
import arrow.test.UnitSpec
import arrow.test.generators.genK
import arrow.test.generators.intSmall
import arrow.test.generators.longSmall
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

  val EQ: Eq<Kind2<ForMapK, Long, Int>> = object : Eq<Kind2<ForMapK, Long, Int>> {
    override fun Kind2<ForMapK, Long, Int>.eqv(b: Kind2<ForMapK, Long, Int>): Boolean =
      fix() == b.fix()
  }

  init {
    val EQ_TC = MapK.eq(Long.eq(), Int.eq())

    testLaws(
      ShowLaws.laws(MapK.show(Long.show(), Int.show()), EQ_TC, Gen.mapK(Gen.long(), Gen.int())),
      TraverseLaws.laws(MapK.traverse(), MapK.genK(Gen.long()), MapK.eqK(Long.eq())),
      MonoidLaws.laws(MapK.monoid<Long, Int>(Int.semigroup()), Gen.mapK(Gen.longSmall(), Gen.intSmall()), EQ),
      FoldableLaws.laws(MapK.foldable(), MapK.genK(Gen.long())),
      EqLaws.laws(MapK.eq(Long.eq(), Int.eq()), Gen.mapK(Gen.long(), Gen.int())),
      FunctorFilterLaws.laws(MapK.functorFilter(), MapK.genK(Gen.long()), MapK.eqK(Long.eq())),
      HashLaws.laws(MapK.hash(Long.hash(), Int.hash()), Gen.mapK(Gen.long(), Gen.int()), EQ_TC),
      AlignLaws.laws(MapK.align(),
        MapK.genK(Gen.long()),
        MapK.eqK(Long.eq()),
        MapK.foldable()
      ),
      UnalignLaws.laws(MapK.unalign(),
        MapK.genK(Gen.long()),
        MapK.eqK(Long.eq()),
        MapK.foldable()),
      UnzipLaws.laws(MapK.unzip(),
        MapK.genK(Gen.long()),
        MapK.eqK(Long.eq()),
        MapK.foldable()
      )
    )

    "can align maps" {
      // aligned keySet is union of a's and b's keys
      forAll(Gen.mapK(Gen.long(), Gen.bool()), Gen.mapK(Gen.long(), Gen.bool())) { a, b ->
        MapK.semialign<Long>().run {
          val aligned = align(a, b).fix()

          aligned.size == (a.keys + b.keys).size
        }
      }

      // aligned map contains Both for all entries existing in a and b
      forAll(Gen.mapK(Gen.long(), Gen.bool()), Gen.mapK(Gen.long(), Gen.bool())) { a, b ->
        MapK.semialign<Long>().run {
          val aligned = align(a, b).fix()
          a.keys.intersect(b.keys).all {
            aligned[it]?.isBoth ?: false
          }
        }
      }

      // aligned map contains Left for all entries existing only in a
      forAll(Gen.mapK(Gen.long(), Gen.bool()), Gen.mapK(Gen.long(), Gen.bool())) { a, b ->
        MapK.semialign<Long>().run {
          val aligned = align(a, b).fix()
          (a.keys - b.keys).all { key ->
            aligned[key]?.let { it.isLeft } ?: false
          }
        }
      }

      // aligned map contains Right for all entries existing only in b
      forAll(Gen.mapK(Gen.long(), Gen.bool()), Gen.mapK(Gen.long(), Gen.bool())) { a, b ->
        MapK.semialign<Long>().run {
          val aligned = align(a, b).fix()
          (b.keys - a.keys).all { key ->
            aligned[key]?.let { it.isRight } ?: false
          }
        }
      }
    }
  }
}
