package arrow.core

import arrow.core.test.nonEmptyList
import arrow.core.test.nonEmptySet
import arrow.platform.stackSafeIteration
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainOnlyOnce
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.negativeInt
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.math.min
import kotlin.test.Test

class NonEmptySetTest {

  @Test fun iterableToNonEmptySetOrNullShouldRoundTrip() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int())) { nonEmptySet ->
      nonEmptySet.toNonEmptySetOrNull().shouldNotBeNull() shouldBe nonEmptySet
    }
  }

  @Test fun iterableToNonEmptySetOrNullShouldReturnNullForEmptyIterable() = runTest {
    listOf<String>().toNonEmptySetOrNull().shouldBeNull()
  }

  @Test fun iterableToNonEmptySetOrNullShouldReturnWorkWhenContainingNull() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int().orNull())) { nonEmptySet ->
      nonEmptySet.toNonEmptySetOrNull().shouldNotBeNull() shouldBe nonEmptySet
    }
  }

  @Test fun iterableToNonEmptySetOrNoneShouldRoundTrip() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int())) { nonEmptySet ->
      nonEmptySet.toNonEmptySetOrNone() shouldBe nonEmptySet.some()
    }
  }

  @Test fun emptyListToNonEmptySetOrNullShouldBeNull() = runTest {
    listOf<Int>().toNonEmptySetOrNull() shouldBe null
  }

  @Test fun emptyListToNonEmptySetOrNoneShouldBeNone() = runTest {
    listOf<Int>().toNonEmptySetOrNone() shouldBe none()
  }

  @Test fun addingAnElementAlreadyPresentDoesNotChangeTheSet() = runTest {
    val element = Arb.int().next()
    val initialSet: NonEmptySet<Int> = nonEmptySetOf(element) + Arb.nonEmptySet(Arb.int()).next()
    initialSet.plus(element) shouldBe initialSet
  }

  @Test fun nonEmptySetEqualsSet() = runTest {
    checkAll(
      Arb.nonEmptySet(Arb.int()),
    ) { nes ->
      val s = nes.toSet()
      (nes == s).shouldBeTrue() // `shouldBe` doesn't use the `equals` methods on `Iterable`
      nes.hashCode() shouldBe s.hashCode()
    }
  }

  @Test fun nonEmptySetEqualsNonEmptySet() = runTest {
    checkAll(
      Arb.nonEmptySet(Arb.int()),
    ) { nes ->
      val s = nes.toNonEmptyList().toNonEmptySet()
      (nes == s).shouldBeTrue() // `shouldBe` doesn't use the `equals` methods on `Iterable`
      nes.hashCode() shouldBe s.hashCode()
    }
  }

  @Test
  fun mapOrAccumulateIsStackSafe() = runTest {
    val acc = mutableSetOf<Int>()
    NonEmptySet(0, 1..stackSafeIteration()).mapOrAccumulate(String::plus) {
      acc.add(it)
      it
    }.merge() shouldBe acc
    acc shouldBe (0..stackSafeIteration()).toSet()
  }

  @Test
  fun mapOrAccumulateAccumulatesErrors() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int(), range = 0..20)) { nes ->
      val res = nes.mapOrAccumulate { i ->
        if (i % 2 == 0) i else raise(i)
      }

      res shouldBe (nes.filterNot { it % 2 == 0 }.toNonEmptyListOrNull()?.left() ?: nes.right())
    }
  }

  @Test
  fun mapOrAccumulateAccumulatesErrorsWithCombineFunction() = runTest {
    checkAll(Arb.nonEmptySet(Arb.negativeInt(), range = 0..20)) { nes ->
      val res = nes.mapOrAccumulate(String::plus) { i ->
        if (i > 0) i else raise("Negative")
      }

      res shouldBe nes.map { "Negative" }.joinToString("").left()
    }
  }

  @Test
  fun head() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int(), range = 1..10)) { a ->
      a.head shouldBe a.elements.first()
    }
  }

  @Test
  fun lastOrNull() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int(), range = 1..10)) { a ->
      a.lastOrNull() shouldBe a.elements.last()
    }
  }

  @Test
  fun toStringContainsData() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int(0..9), 1..9)) { a ->
      val s = a.toString()
      a.forEach { i ->
        s shouldContainOnlyOnce i.toString()
      }
    }
  }

  @Test
  fun distinct() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int(0..5), 1..30)) { a ->
      val expected = a.elements.distinct()
      a.distinct() shouldBe expected
      a.toList() shouldBe expected // the same as distinct
    }
  }

  @Test
  fun distinctBy() = runTest {
    checkAll(30, Arb.nonEmptySet(Arb.string(1, 3, Codepoint.az()), 1..50)) { a ->
      fun selector(s: String) = s[0]

      a.distinctBy(::selector) shouldBe a.elements.distinctBy(::selector)
    }
  }

  @Test
  fun flatMap() = runTest {
    checkAll(30, Arb.nonEmptySet(Arb.int(), 1..10)) { a ->
      fun transform(i: Int) = nonEmptyListOf(i, i + 1, i * 2)
        .map { it.toString() }

      a.flatMap(::transform) shouldBe a.elements.flatMap(::transform)
    }
  }

  @Test
  fun mapIndexed() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int(), 1..30)) { a ->
      fun transform(index: Int, i: Int) = when (index) {
        in 0..10 -> i * 2
        in 11..20 -> i * 3
        else -> i * 4
      }.toString()

      a.mapIndexed(::transform) shouldBe a.elements.mapIndexed(::transform)
    }
  }

  @Test
  fun zip() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int(), 1..30), Arb.nonEmptyList(Arb.string(0..5), 1..30)) { a, b ->
      val expected = (0 until min(a.size, b.size)).map {
        a.elementAt(it) to b[it]
      }

      a.zip(b) shouldBe expected
    }
  }

  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  @Test
  fun wrapAsNonEmptySetOrThrowEmpty() = runTest {
    runCatching { emptySet<Int>().wrapAsNonEmptySetOrThrow() }.isFailure.shouldBeTrue()
  }

  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  @Test
  fun wrapAsNonEmptySetOrThrowNonEmpty() = runTest {
    checkAll(Arb.set(Arb.int(), 1..10)) { a ->
      a.wrapAsNonEmptySetOrThrow().elements shouldBeSameInstanceAs a
    }
  }

  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  @Test
  fun wrapAsNonEmptySetOrNull() = runTest {
    checkAll(Arb.set(Arb.int(), 0..10)) { a ->
      a.wrapAsNonEmptySetOrNull() shouldBe a.toNonEmptySetOrNull()
    }
  }

  class MyVerySpecialSet<out A>(private val other: Set<A>): Set<A> by other {
    override fun toString(): String = "MyVerySpecialSet(${other.reversed()})"
  }

  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  @Test
  fun toStringUsesUnderlyingImplementation() = runTest {
    checkAll(Arb.set(Arb.int(), 1..100)) {
      NonEmptySet(it).toString() shouldBe it.toString()
      NonEmptySet(MyVerySpecialSet(it)).toString() shouldBe "MyVerySpecialSet(${it.reversed()})"
    }
  }
}
