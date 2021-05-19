package arrow.core

import arrow.core.test.generators.byte
import arrow.core.test.generators.short
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
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

fun Gen.Companion.person(): Gen<Person> =
  bind(int(), string(), ::Person)

class ComparisonKtTest : StringSpec() {
  init {
    "Generic - sort2" {
      forAll(Gen.person(), Gen.person()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Generic - sort3" {
      forAll(Gen.person(), Gen.person(), Gen.person()) { a, b, c ->
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
      forAll(Gen.person(), Gen.list(Gen.person())) { a, aas ->
        val res = sort(a, *aas.toTypedArray())
        val expected = listOf(a, *aas.toTypedArray()).sorted()

        res == expected
      }
    }

    "Generic - comparator - sort2" {
      forAll(Gen.person(), Gen.person()) { a, b ->
        val (first, second) = sort(a, b, Person.comparator)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Generic - comparator - sort3" {
      forAll(Gen.person(), Gen.person(), Gen.person()) { a, b, c ->
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
      forAll(Gen.byte(), Gen.byte()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Byte - sort3" {
      forAll(Gen.byte(), Gen.byte(), Gen.byte()) { a, b, c ->
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
      forAll(Gen.byte(), Gen.byte(), Gen.byte(), Gen.byte()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res == expected
      }
    }

    "Short - sort2" {
      forAll(Gen.short(), Gen.short()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Short - sort3" {
      forAll(Gen.short(), Gen.short(), Gen.short()) { a, b, c ->
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
      forAll(Gen.short(), Gen.short(), Gen.short(), Gen.short()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res == expected
      }
    }

    "Int - sort2" {
      forAll(Gen.int(), Gen.int()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        first == aa && second == bb
      }
    }

    "Int - sort3" {
      forAll(Gen.int(), Gen.int(), Gen.int()) { a, b, c ->
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
      forAll(Gen.int(), Gen.int(), Gen.int(), Gen.int()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res == expected
      }
    }

    "Long - sort2" {
      forAll(Gen.long(), Gen.long()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    "Long - sort3" {
      forAll(Gen.long(), Gen.long(), Gen.long()) { a, b, c ->
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
      forAll(Gen.long(), Gen.long(), Gen.long(), Gen.long()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res == expected
      }
    }

    "Float - sort2" {
      forAll(Gen.float(), Gen.float()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        first.eqv(aa) && second.eqv(bb)
      }
    }

    "Float - sort3" {
      forAll(Gen.float(), Gen.float(), Gen.float()) { a, b, c ->
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
      forAll(Gen.float(), Gen.float(), Gen.float(), Gen.float()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res == expected
      }
    }

    "Double - sort2" {
      forAll(Gen.double(), Gen.double()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        first.eqv(aa) && second.eqv(bb)
      }
    }

    "Double - sort3" {
      forAll(Gen.double(), Gen.double(), Gen.double()) { a, b, c ->
        val (first, second, third) = sort(a, b, c)
        val (aa, bb, cc) = listOf(a, b, c).sorted()

        first.eqv(aa) && second.eqv(bb) && third.eqv(cc)
      }
    }

    "Double - sortAll" {
      forAll(Gen.double(), Gen.double(), Gen.double(), Gen.double()) { a, b, c, d ->
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
