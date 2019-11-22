package arrow.core

import arrow.Kind
import arrow.Kind2
import arrow.core.extensions.eq
import arrow.core.extensions.foldable
import arrow.core.extensions.functor
import arrow.core.extensions.hash
import arrow.core.extensions.monoid
import arrow.core.extensions.semialign
import arrow.core.extensions.show
import arrow.core.extensions.sortedmapk.eq.eq
import arrow.core.extensions.sortedmapk.hash.hash
import arrow.core.extensions.traverse
import arrow.test.UnitSpec
import arrow.test.generators.sortedMapK
import arrow.test.laws.HashLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.SemialignLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class SortedMapKTest : UnitSpec() {

  val EQ: Eq<Kind2<ForSortedMapK, String, Int>> = object : Eq<Kind2<ForSortedMapK, String, Int>> {
    override fun Kind2<ForSortedMapK, String, Int>.eqv(b: Kind2<ForSortedMapK, String, Int>): Boolean =
      fix()["key"] == b.fix()["key"]
  }

  val EQK = object : EqK<SortedMapKPartialOf<String>> {
    override fun <A> Kind<SortedMapKPartialOf<String>, A>.eqK(other: Kind<SortedMapKPartialOf<String>, A>, EQ: Eq<A>): Boolean =
      SortedMapK.eq(String.eq(), EQ).run { this@eqK.fix().eqv(other.fix()) }
  }

  init {
    testLaws(
      HashLaws.laws(SortedMapK.hash(String.hash(), Int.hash()), EQ) { sortedMapOf("key" to it).k() },
      ShowLaws.laws(SortedMapK.show(), EQ) { sortedMapOf("key" to 1).k() },
      MonoidLaws.laws(SortedMapK.monoid<String, Int>(Int.monoid()), Gen.sortedMapK(Gen.string(), Gen.int()), EQ),
      TraverseLaws.laws(
        SortedMapK.traverse<String>(),
        SortedMapK.functor<String>(),
        { a: Int -> sortedMapOf("key" to a).k() },
        EQ),
      SemialignLaws.laws(SortedMapK.semialign<String>(),
        Gen.sortedMapK(Gen.string(), Gen.int()) as Gen<Kind<SortedMapKPartialOf<String>, Int>>,
        EQK,
        SortedMapK.foldable<String>()
      ))

    "can align maps" {
      val gen = Gen.sortedMapK(Gen.string(), Gen.bool())

      // aligned keySet is union of a's and b's keys
      forAll(gen, gen) { a, b ->
        SortedMapK.semialign<String>().run {
          val aligned = align(a, b).fix()

          aligned.size == (a.keys + b.keys).size
        }
      }

      // aligned map contains Both for all entries existing in a and b
      forAll(gen, gen) { a, b ->
        SortedMapK.semialign<String>().run {
          val aligned = align(a, b).fix()
          a.keys.intersect(b.keys).all {
            aligned[it]?.isBoth ?: false
          }
        }
      }

      // aligned map contains Left for all entries existing only in a
      forAll(gen, gen) { a, b ->
        SortedMapK.semialign<String>().run {
          val aligned = align(a, b).fix()
          (a.keys - b.keys).all { key ->
            aligned[key]?.let { it.isLeft } ?: false
          }
        }
      }

      // aligned map contains Right for all entries existing only in b
      forAll(gen, gen) { a, b ->
        SortedMapK.semialign<String>().run {
          val aligned = align(a, b).fix()
          (b.keys - a.keys).all { key ->
            aligned[key]?.let { it.isRight } ?: false
          }
        }
      }
    }
  }
}
