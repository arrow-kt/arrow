package arrow.core

import arrow.core.test.UnitSpec
import arrow.core.test.generators.sequence
import arrow.core.test.generators.sequenceK
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Monoid
import io.kotlintest.matchers.sequences.shouldBeEmpty
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.max
import kotlin.math.min

class SequenceKTest : UnitSpec() {

  init {

    testLaws(MonoidLaws.laws(Monoid.sequence(), Gen.sequence(Gen.int())) { s1, s2 -> s1.toList() == s2.toList() })

    "can align sequences" {
      forAll(Gen.sequence(Gen.int()), Gen.sequence(Gen.string())) { a, b ->
        a.align(b).toList().size == max(a.toList().size, b.toList().size)
      }

      forAll(Gen.sequence(Gen.int()), Gen.sequence(Gen.string())) { a, b ->
        a.align(b).take(min(a.toList().size, b.toList().size)).all {
          it.isBoth
        }
      }

      forAll(Gen.sequence(Gen.int()), Gen.sequence(Gen.string())) { a, b ->
        val ls = a.toList()
        val rs = b.toList()
        a.align(b).drop(min(ls.size, rs.size)).all {
          if (ls.size < rs.size) {
            it.isRight
          } else {
            it.isLeft
          }
        }
      }
    }

    "align empty sequences" {
      val a = emptyList<String>().asSequence()
      a.align(a).shouldBeEmpty()
    }

    "align infinite sequences" {
      val seq1 = generateSequence("A") { it }

      val seq2 = generateSequence(0) { it + 1 }

      forAll(10, Gen.positiveIntegers().filter { it < 10_000 }) { idx: Int ->
        val element = seq1.align(seq2).drop(idx).first()

        element == Ior.Both("A", idx)
      }
    }

    "filterMap" {
      forAll(Gen.sequenceK(Gen.int())) { a ->
        val result =
          a.filterMap {
            when (it % 2 == 0) {
              true -> Some(it.toString())
              else -> None
            }
          }

        val expected =
          a.toList()
            .mapNotNull {
              when (it % 2 == 0) {
                true -> it.toString()
                else -> null
              }
            }
            .asSequence()
            .k()

        result.toList() == expected.toList()
      }
    }

    "mapNotNull" {
      forAll(Gen.sequenceK(Gen.int())) { a ->
        val result = a.mapNotNull {
          when (it % 2 == 0) {
            true -> it.toString()
            else -> null
          }
        }
        val expected =
          a.toList()
            .mapNotNull {
              when (it % 2 == 0) {
                true -> it.toString()
                else -> null
              }
            }
            .asSequence()
            .k()

        result.toList() == expected.toList()
      }
    }
  }
}
