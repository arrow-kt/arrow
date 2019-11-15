package arrow.core.extensions

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Ior
import arrow.core.SequenceK
import arrow.core.extensions.sequencek.eq.eq
import arrow.core.extensions.sequencek.foldable.foldable
import arrow.core.extensions.sequencek.semialign.semialign
import arrow.core.fix
import arrow.core.k
import arrow.test.UnitSpec
import arrow.test.generators.sequenceK
import arrow.test.laws.SemialignLaws
import arrow.typeclasses.Eq
import io.kotlintest.matchers.sequences.shouldBeEmpty
import io.kotlintest.matchers.sequences.shouldContainAll
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class SequencekExtensionTest : UnitSpec() {
  init {
    testLaws(SemialignLaws.laws(SequenceK.semialign(),
      Gen.sequenceK(Gen.int()) as Gen<Kind<ForSequenceK, Int>>,
      { SequenceK.eq(it) as Eq<Kind<ForSequenceK, *>> },
      SequenceK.foldable()
    ))

    "can align sequences" {
      val a = listOf("A", "B", "C").asSequence().k()
      val b = listOf(1, 2).asSequence().k()

      SequenceK.semialign().run {
        align(a, b).fix().sequence.shouldContainAll(Ior.Both("A", 1), Ior.Both("B", 2), Ior.Left("C"))
      }
    }

    "align empty seqeuences" {
      val a = emptyList<String>().asSequence().k()

      SequenceK.semialign().run {
        align(a, a).fix().sequence.shouldBeEmpty()
      }
    }

    "align infinite sequences" {
      val seq1 = sequence {
        while (true) {
          yield("A")
        }
      }.k()

      val seq2 = sequence {
        var count = 0
        while (true) {
          yield(count++)
        }
      }.k()

      SequenceK.semialign().run {
        forAll(10, Gen.positiveIntegers().filter { it < 10_000 }) { idx: Int ->
          val element = align(seq1, seq2).fix().drop(idx).first()

          element == Ior.Both("A", idx)
        }
      }
    }
  }
}
