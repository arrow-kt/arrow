package arrow.core

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string

data class Person(val age: Int, val name: String) : Comparable<Person> {
  companion object {
    val comparator: Comparator<Person> = Comparator { a, b ->
      val res = a.age.compareTo(b.age)
      if (res != 0) res
      else a.name.compareTo(b.name)
    }
  }

  override fun compareTo(other: Person): Int =
    comparator.compare(this, other)
}

fun Arb.Companion.person(): Arb<Person> =
  Arb.bind(Arb.int(), Arb.string(), ::Person)

class ComparisonKtTest : StringSpec({
    "Arberic - sort2" {
      checkAll(Arb.person(), Arb.person()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Arberic - sort3" {
      checkAll(Arb.person(), Arb.person(), Arb.person()) { a, b, c ->
        val (first, second, third) = sort(a, b, c)
        val (aa, bb, cc) = listOf(a, b, c).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
          third shouldBe cc
        }
      }
    }

    "Arberic - sortAll" {
      checkAll(Arb.person(), Arb.array(Arb.person(), 0..50)) { a, aas ->
        val res = sort(a, *aas)
        val expected = listOf(a, *aas).sorted()

        res shouldBe expected
      }
    }

    "Arberic - comparator - sort2" {
      checkAll(Arb.person(), Arb.person()) { a, b ->
        val (first, second) = sort(a, b, Person.comparator)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Arberic - comparator - sort3" {
      checkAll(Arb.person(), Arb.person(), Arb.person()) { a, b, c ->
        val (first, second, third) = sort(a, b, c, Person.comparator)
        val (aa, bb, cc) = listOf(a, b, c).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
          third shouldBe cc
        }
      }
    }

    "Byte - sort2" {
      checkAll(Arb.byte(), Arb.byte()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Byte - sort3" {
      checkAll(Arb.byte(), Arb.byte(), Arb.byte()) { a, b, c ->
        val (first, second, third) = sort(a, b, c)
        val (aa, bb, cc) = listOf(a, b, c).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
          third shouldBe cc
        }
      }
    }

    "Byte - sortAll" {
      checkAll(Arb.byte(), Arb.byte(), Arb.byte(), Arb.byte()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res shouldBe expected
      }
    }

    "Short - sort2" {
      checkAll(Arb.short(), Arb.short()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Short - sort3" {
      checkAll(Arb.short(), Arb.short(), Arb.short()) { a, b, c ->
        val (first, second, third) = sort(a, b, c)
        val (aa, bb, cc) = listOf(a, b, c).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
          third shouldBe cc
        }
      }
    }

    "Short - sortAll" {
      checkAll(Arb.short(), Arb.short(), Arb.short(), Arb.short()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res shouldBe expected
      }
    }

    "Int - sort2" {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        first shouldBe aa
        second shouldBe bb
      }
    }

    "Int - sort3" {
      checkAll(Arb.int(), Arb.int(), Arb.int()) { a, b, c ->
        val (first, second, third) = sort(a, b, c)
        val (aa, bb, cc) = listOf(a, b, c).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
          third shouldBe cc
        }
      }
    }

    "Int - sortAll" {
      checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res shouldBe expected
      }
    }

    "Long - sort2" {
      checkAll(Arb.long(), Arb.long()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Long - sort3" {
      checkAll(Arb.long(), Arb.long(), Arb.long()) { a, b, c ->
        val (first, second, third) = sort(a, b, c)
        val (aa, bb, cc) = listOf(a, b, c).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
          third shouldBe cc
        }
      }
    }

    "Long - sortAll" {
      checkAll(Arb.long(), Arb.long(), Arb.long(), Arb.long()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res shouldBe expected
      }
    }
})
