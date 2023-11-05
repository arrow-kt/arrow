package arrow.core

import io.kotest.assertions.assertSoftly
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

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

class ComparisonKtTest {
    @Test fun arbericSort2() = runTest {
      checkAll(Arb.person(), Arb.person()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    @Test fun arbericSort3() = runTest {
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

    @Test fun arbericSortAll() = runTest {
      checkAll(Arb.person(), Arb.list(Arb.person(), 0..50)) { a, lst ->
        val aas = lst.toTypedArray()
        val res = sort(a, *aas)
        val expected = listOf(a, *aas).sorted()

        res shouldBe expected
      }
    }

    @Test fun arbericComparatorSort2() = runTest {
      checkAll(Arb.person(), Arb.person()) { a, b ->
        val (first, second) = sort(a, b, Person.comparator)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    @Test fun arbericComparatorSort3() = runTest {
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

    @Test fun byteSort2() = runTest {
      checkAll(Arb.byte(), Arb.byte()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    @Test fun byteSort3() = runTest {
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

    @Test fun byteSortAll() = runTest {
      checkAll(Arb.byte(), Arb.byte(), Arb.byte(), Arb.byte()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res shouldBe expected
      }
    }

    @Test fun shortSort2() = runTest {
      checkAll(Arb.short(), Arb.short()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    @Test fun shortSort3() = runTest {
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

    @Test fun shortSortAll() = runTest {
      checkAll(Arb.short(), Arb.short(), Arb.short(), Arb.short()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res shouldBe expected
      }
    }
    
    @Test fun intSort2() = runTest {
      checkAll(Arb.int(), Arb.int()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        first shouldBe aa
        second shouldBe bb
      }
    }

    @Test fun intSort3() = runTest {
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

    @Test fun intSortAll() = runTest {
      checkAll(Arb.int(), Arb.int(), Arb.int(), Arb.int()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res shouldBe expected
      }
    }

    @Test fun longSort2() = runTest {
      checkAll(Arb.long(), Arb.long()) { a, b ->
        val (first, second) = sort(a, b)
        val (aa, bb) = listOf(a, b).sorted()

        assertSoftly {
          first shouldBe aa
          second shouldBe bb
        }
      }
    }

    @Test fun longSort3() = runTest {
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

    @Test fun longSortAll() = runTest {
      checkAll(Arb.long(), Arb.long(), Arb.long(), Arb.long()) { a, b, c, d ->
        val res = sort(a, b, c, d)
        val expected = listOf(a, b, c, d).sorted()

        res shouldBe expected
      }
    }
}
