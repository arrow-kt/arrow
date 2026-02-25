package arrow.core

import arrow.core.test.nonEmptyList
import arrow.core.test.nonEmptySet
import arrow.platform.stackSafeIteration
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainOnlyOnce
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
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
      val s = nes.toSet().toNonEmptySetOrThrow()
      (nes == s).shouldBeTrue() // `shouldBe` doesn't use the `equals` methods on `Iterable`
      nes.hashCode() shouldBe s.hashCode()
    }
  }

  @Test
  fun mapOrAccumulateIsStackSafe() = runTest {
    val acc = mutableSetOf<Int>()
    val res = (0..stackSafeIteration())
      .toNonEmptySetOrThrow()
      .mapOrAccumulate(String::plus) {
        acc.add(it)
        it
      }
    res shouldBe Either.Right(acc)
  }

  @Test
  fun mapOrAccumulateAccumulatesErrors() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int(), range = 0..20)) { nes ->
      val res = nes.mapOrAccumulate { i ->
        if (i % 2 == 0) i else raise(i)
      }

      val expected = nes.filterNot { it % 2 == 0 }
        .toNonEmptyListOrNull()?.left() ?: nes.filter { it % 2 == 0 }.toNonEmptySetOrThrow().right()

      res shouldBe expected
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
    checkAll(Arb.set(Arb.int(), range = 1..10)) { a ->
      a.toNonEmptySetOrThrow().head shouldBe a.first()
    }
  }

  @Test
  fun lastOrNull() = runTest {
    checkAll(Arb.set(Arb.int(), range = 1..10)) { a ->
      a.toNonEmptySetOrThrow().lastOrNull() shouldBe a.last()
    }
  }

  @Test
  fun toStringContainsData() = runTest {
    checkAll(Arb.set(Arb.int(0..9), 1..9)) { a ->
      a.toNonEmptySetOrThrow().toString().also { s ->
        a.onEach { i ->
          s shouldContainOnlyOnce i.toString()
        }
      }
    }
  }

  @Test
  fun distinct() = runTest {
    checkAll(Arb.list(Arb.int(0..5), 1..30)) { a ->
      val expected = a.distinct()

      a.toNonEmptySetOrThrow().also { nes ->
        nes.distinct() shouldBe expected
        nes.toList() shouldBe expected // the same as distinct
      }
    }
  }

  @Test
  fun distinctBy() = runTest {
    checkAll(30, Arb.list(Arb.string(1, 3, Codepoint.az()), 1..50)) { a ->
      fun selector(s: String) = s[0]

      a.toNonEmptySetOrThrow()
        .distinctBy(::selector) shouldBe a.distinctBy(::selector)
    }
  }

  @Test
  fun flatMap() = runTest {
    checkAll(30, Arb.set(Arb.int(), 1..10)) { a ->
      fun transform(i: Int) = listOf(i, i + 1, i * 2)
        .map { it.toString() }
        .toNonEmptyListOrThrow()

      a.toNonEmptySetOrThrow()
        .flatMap(::transform) shouldBe a.flatMap(::transform)
    }
  }

  @Test
  fun mapIndexed() = runTest {
    checkAll(Arb.set(Arb.int(), 1..30)) { a ->
      fun transform(index: Int, i: Int) = when (index) {
        in 0..10 -> i * 2
        in 11..20 -> i * 3
        else -> i * 4
      }.toString()

      a.toNonEmptySetOrThrow()
        .mapIndexed(::transform) shouldBe a.mapIndexed(::transform)
    }
  }

  @Test
  fun zip() = runTest {
    checkAll(Arb.set(Arb.int(), 1..30), Arb.nonEmptyList(Arb.string(0..5), 1..30)) { a, b ->
      val expected = (0 until min(a.size, b.size)).map {
        a.elementAt(it) to b[it]
      }

      a.toNonEmptySetOrThrow().zip(b) shouldBe expected
    }
  }

  @OptIn(PotentiallyUnsafeNonEmptyOperation::class)
  @Test
  fun wrapAsNonEmptySetOrThrow() = runTest {
    checkAll(Arb.set(Arb.int(), 0..10)) { a ->
      runCatching {
        a.wrapAsNonEmptySetOrThrow()
      }.also {
        when (a.isEmpty()) {
          true -> {
            it.isFailure shouldBe true
          }
          false -> {
            it.getOrNull() shouldBe a.toNonEmptySetOrThrow()
          }
        }
      }
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
      it.wrapAsNonEmptySetOrThrow().toString() shouldBe it.toString()
      MyVerySpecialSet(it).wrapAsNonEmptySetOrThrow().toString() shouldBe "MyVerySpecialSet(${it.reversed()})"
    }
  }
}
