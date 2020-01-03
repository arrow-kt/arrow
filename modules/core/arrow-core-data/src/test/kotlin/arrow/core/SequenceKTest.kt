package arrow.core

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.sequencek.align.align
import arrow.core.extensions.sequencek.applicative.applicative
import arrow.core.extensions.sequencek.crosswalk.crosswalk
import arrow.core.extensions.sequencek.eq.eq
import arrow.core.extensions.sequencek.eqK.eqK
import arrow.core.extensions.sequencek.foldable.foldable
import arrow.core.extensions.sequencek.functor.functor
import arrow.core.extensions.sequencek.functorFilter.functorFilter
import arrow.core.extensions.sequencek.hash.hash
import arrow.core.extensions.sequencek.monad.monad
import arrow.core.extensions.sequencek.monadCombine.monadCombine
import arrow.core.extensions.sequencek.monoid.monoid
import arrow.core.extensions.sequencek.monoidK.monoidK
import arrow.core.extensions.sequencek.monoidal.monoidal
import arrow.core.extensions.sequencek.repeat.repeat
import arrow.core.extensions.sequencek.semialign.semialign
import arrow.core.extensions.sequencek.traverse.traverse
import arrow.core.extensions.sequencek.unalign.unalign
import arrow.core.extensions.sequencek.unzip.unzip
import arrow.test.UnitSpec
import arrow.test.generators.genK
import arrow.test.generators.sequenceK
import arrow.test.laws.AlignLaws
import arrow.test.laws.CrosswalkLaws
import arrow.test.laws.FunctorFilterLaws
import arrow.test.laws.HashLaws
import arrow.test.laws.MonadCombineLaws
import arrow.test.laws.MonoidKLaws
import arrow.test.laws.MonoidLaws
import arrow.test.laws.MonoidalLaws
import arrow.test.laws.RepeatLaws
import arrow.test.laws.ShowLaws
import arrow.test.laws.TraverseLaws
import arrow.test.laws.UnalignLaws
import arrow.test.laws.UnzipLaws
import arrow.typeclasses.Show
import io.kotlintest.matchers.sequences.shouldBeEmpty
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.max
import kotlin.math.min

class SequenceKTest : UnitSpec() {

  init {
    val EQ = SequenceK.eq(Int.eq())

    val show: Show<Kind<ForSequenceK, Int>> = object : Show<Kind<ForSequenceK, Int>> {
      override fun Kind<ForSequenceK, Int>.show(): String =
        fix().toList().toString()
    }

    testLaws(
      MonadCombineLaws.laws(
        SequenceK.monadCombine(),
        SequenceK.functor(),
        SequenceK.applicative(),
        SequenceK.monad(),
        SequenceK.genK(),
        SequenceK.eqK()
      ),

      MonadCombineLaws.laws(SequenceK.monadCombine(), SequenceK.genK(), SequenceK.eqK()),
      ShowLaws.laws(show, EQ, Gen.sequenceK(Gen.int())),
      MonoidKLaws.laws(SequenceK.monoidK(), SequenceK.genK(), SequenceK.eqK()),
      MonoidLaws.laws(SequenceK.monoid(), Gen.sequenceK(Gen.int()), EQ),
      MonoidalLaws.laws(SequenceK.monoidal(), SequenceK.genK(), SequenceK.eqK(), this::bijection),
      TraverseLaws.laws(SequenceK.traverse(), SequenceK.genK(), SequenceK.eqK()),
      FunctorFilterLaws.laws(SequenceK.functorFilter(), SequenceK.genK(), SequenceK.eqK()),
      HashLaws.laws(SequenceK.hash(Int.hash()), SequenceK.eq(Int.eq()), Gen.sequenceK(Gen.int())),
      AlignLaws.laws(SequenceK.align(),
        SequenceK.genK(),
        SequenceK.eqK(),
        SequenceK.foldable()
      ),
      UnalignLaws.laws(SequenceK.unalign(),
        SequenceK.genK(),
        SequenceK.eqK(),
        SequenceK.foldable()
      ),
      RepeatLaws.laws(SequenceK.repeat(),
        SequenceK.genK(),
        SequenceK.eqK(),
        SequenceK.foldable()
      ),
      UnzipLaws.laws(SequenceK.unzip(),
        SequenceK.genK(),
        SequenceK.eqK(),
        SequenceK.foldable()
      ),
      CrosswalkLaws.laws(SequenceK.crosswalk(),
        SequenceK.genK(),
        SequenceK.eqK()
      )
    )

    "can align sequences" {
      forAll(Gen.sequenceK(Gen.int()), Gen.sequenceK(Gen.string())) { a, b ->
        SequenceK.semialign().run {
          align(a, b).fix().toList().size == max(a.toList().size, b.toList().size)
        }
      }

      forAll(Gen.sequenceK(Gen.int()), Gen.sequenceK(Gen.string())) { a, b ->
        SequenceK.semialign().run {
          align(a, b).fix().take(min(a.toList().size, b.toList().size)).all {
            it.isBoth
          }
        }
      }

      forAll(Gen.sequenceK(Gen.int()), Gen.sequenceK(Gen.string())) { a, b ->
        SequenceK.semialign().run {
          val ls = a.toList()
          val rs = b.toList()

          align(a, b).fix().drop(min(ls.size, rs.size)).all {
            if (ls.size < rs.size) {
              it.isRight
            } else {
              it.isLeft
            }
          }
        }
      }
    }

    "align empty sequences" {
      val a = emptyList<String>().asSequence().k()

      SequenceK.semialign().run {
        align(a, a).fix().sequence.shouldBeEmpty()
      }
    }

    "align infinite sequences" {
      val seq1 = generateSequence("A") { it }.k()

      val seq2 = generateSequence(0) { it + 1 }.k()

      SequenceK.semialign().run {
        forAll(10, Gen.positiveIntegers().filter { it < 10_000 }) { idx: Int ->
          val element = align(seq1, seq2).fix().drop(idx).first()

          element == Ior.Both("A", idx)
        }
      }
    }
  }

  private fun bijection(from: Kind<ForSequenceK, Tuple2<Tuple2<Int, Int>, Int>>): SequenceK<Tuple2<Int, Tuple2<Int, Int>>> =
    from.fix().toList().map { Tuple2(it.a.a, Tuple2(it.a.b, it.b)) }.asSequence().k()
}
