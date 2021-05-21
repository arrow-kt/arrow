package arrow.core

import arrow.core.test.generators.byte
import arrow.core.test.generators.short
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

data class Person(val age: Int, val name: String) : Comparable<Person> {
  companion object {
    val comparator: Comparator<Person> =
      Comparator.comparingInt(Person::age)
        .thenComparing(Person::name)
  }

  override fun compareTo(other: Person): Int =
    comparator.compare(this, other)
}

fun Gen.Companion.person(): Arb<Person> =
  bind(int(), string(), ::Person)

class ComparisonKtTest : StringSpec() {
  init {
    "Generic - sort2" {
      checkAll(Gen.person(), Gen.person()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Generic - sort3" {
      checkAll(Gen.person(), Gen.person(), Gen.person()) { a, b, c ->
        val (first, second, third) = sort(a, b, c)
        val (aa, bb, cc) = listOf(a, b, c).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
          third shouldBe cc
        }
      }
    }

    "Generic - sortAll" {
      checkAll(Gen.person(), Arb.list(Gen.person())) { a, aas ->
        val res = sort(a, *aas.toTypedArray())
        val expected = listOf(a, *aas.toTypedArray()).sorted()

        res == expected
      }
    }

    "Generic - comparator - sort2" {
      checkAll(Gen.person(), Gen.person()) { a, b ->
        val (first, second) = sort(a, b, Person.comparator)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Generic - comparator - sort3" {
      checkAll(Gen.person(), Gen.person(), Gen.person()) { a, b, c ->
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
      checkAll(Gen.byte(), Gen.byte()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Byte - sort3" {
      checkAll(Gen.byte(), Gen.byte(), Gen.byte()) { a, b, c ->
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
      checkAll(Gen.byte(), Gen.byte(), Gen.byte(), Gen.byte()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res == expected
      }
    }

    "Short - sort2" {
      checkAll(Gen.short(), Gen.short()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Short - sort3" {
      checkAll(Gen.short(), Gen.short(), Gen.short()) { a, b, c ->
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
      checkAll(Gen.short(), Gen.short(), Gen.short(), Gen.short()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res == expected
      }
    }

    "Int - sort2" {
      checkAll(Gen.int(), Gen.int()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        first == aa && second == bb
      }
    }

    "Int - sort3" {
      checkAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
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
      checkAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res == expected
      }
    }

    "Long - sort2" {
      checkAll(Gen.long(), Gen.long()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Long - sort3" {
      checkAll(Gen.long(), Gen.long(), Gen.long()) { a, b, c ->
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
      checkAll(Gen.long(), Gen.long(), Gen.long(), Gen.long()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res == expected
      }
    }

    "Float - sort2" {
      checkAll(Gen.float(), Gen.float()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        first.eqv(aa) && second.eqv(bb)
      }
    }

    "Float - sort3" {
      checkAll(Gen.float(), Gen.float(), Gen.float()) { a, b, c ->
        val (first, second, third) = sort(a, b, c)
        val (aa, bb, cc) = listOf(a, b, c).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
          third shouldBe cc
        }
      }
    }

    "Float - sortAll" {
      checkAll(Gen.float(), Gen.float(), Gen.float(), Gen.float()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res == expected
      }
    }

    "Double - sort2" {
      checkAll(Gen.double(), Gen.double()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        first.eqv(aa) && second.eqv(bb)
      }
    }

    "Double - sort3" {
      checkAll(Gen.double(), Gen.double(), Gen.double()) { a, b, c ->
        val (first, second, third) = sort(a, b, c)
        val (aa, bb, cc) = listOf(a, b, c).sorted()

        first.eqv(aa) && second.eqv(bb) && third.eqv(cc)
      }
    }

    "Double - sortAll" {
      checkAll(Gen.double(), Gen.double(), Gen.double(), Gen.double()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res == expected
      }
    }
  }
}

fun assertSoftly(f: () -> Unit): Boolean =
  io.kotlintest.assertSoftly {
    f()
    true
  }

/**
 * Equality for Float to check sorting order.
 * So we need `NaN == NaN` to be true.
 */
private fun Float.eqv(other: Float): Boolean =
  if (isNaN() && other.isNaN()) true else {
    this shouldBe other
    true
  }

/**
 * Equality for Double to check sorting order.
 * So we need `NaN == NaN` to be true.
 */
private fun Double.eqv(other: Double): Boolean =
  if (isNaN() && other.isNaN()) true else {
    this shouldBe other
    true
  }
