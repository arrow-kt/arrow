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
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.longSmall
import arrow.core.test.generators.mapK
import arrow.core.test.laws.AlignLaws
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.FoldableLaws
import arrow.core.test.laws.FunctorFilterLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.core.test.laws.UnalignLaws
import arrow.core.test.laws.UnzipLaws
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

    "map2" {
      forAll(
        Gen.mapK(Gen.intSmall(), Gen.intSmall()),
        Gen.mapK(Gen.intSmall(), Gen.intSmall())
      ) { a, b ->
        val result = a.map2(b) { left, right -> left + right }
        val expected: MapK<Int, Int> = a.filter { (k, v) -> b.containsKey(k) }
          .map { (k, v) -> Tuple2(k, v + b[k]!!) }
          .let { mapOf(*it.toTypedArray()) }
        result == expected
      }
    }

    "ap2" {
      forAll(
        Gen.mapK(Gen.intSmall(), Gen.intSmall()),
        Gen.mapK(Gen.intSmall(), Gen.intSmall())
      ) { a, b ->
        val result = a.ap2(
          a.map { {x: Int, y: Int -> x + y } },
          b
        )
        val expected: MapK<Int, Int> = a.filter { (k, v) -> b.containsKey(k) }
          .map { (k, v) -> Tuple2(k, v + b[k]!!) }
          .let { mapOf(*it.toTypedArray()) }
        result == expected
      }
    }

    "flatMap" {
      forAll(
        Gen.mapK(Gen.string(), Gen.intSmall()),
        Gen.mapK(Gen.string(), Gen.string())
      ) { a, b ->
        val result: MapK<String, String> = a.flatMap { b }
        val expected: MapK<String, String> = a.filter { (k, _) -> b.containsKey(k) }
          .map { (k, v) -> Tuple2(k, b[k]!!) }
          .let { mapOf(*it.toTypedArray()) }
        result == expected
      }
    }
  }
}
