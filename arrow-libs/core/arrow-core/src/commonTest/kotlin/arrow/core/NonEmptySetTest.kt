package arrow.core

import arrow.core.test.nonEmptySet
import arrow.platform.stackSafeIteration
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.negativeInt
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.orNull
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

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
      Arb.nonEmptySet(Arb.int())
    ) { nes ->
      val s = nes.toSet()
      withClue("$nes should be equal to $s") {
        (nes == s).shouldBeTrue() // `shouldBe` doesn't use the `equals` methods on `Iterable`
        nes.hashCode() shouldBe s.hashCode()
      }
    }
  }

  @Test fun nonEmptySetEqualsNonEmptySet() = runTest {
    checkAll(
      Arb.nonEmptySet(Arb.int())
    ) { nes ->
      val s = nes.toSet().toNonEmptySetOrNull()!!
      withClue("$nes should be equal to $s") {
        (nes == s).shouldBeTrue() // `shouldBe` doesn't use the `equals` methods on `Iterable`
        nes.hashCode() shouldBe s.hashCode()
      }
    }
  }

  @Test
  fun mapOrAccumulateIsStackSafe() = runTest {
    val acc = mutableSetOf<Int>()
    val res = (0..stackSafeIteration())
      .toNonEmptySetOrNull()!!
      .mapOrAccumulate(String::plus) {
        acc.add(it)
        it
      }
    res shouldBe Either.Right(acc)
  }

  @Test
  fun mapOrAccumulateAccumulatesErrors() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int(), range = 0 .. 20)) { nes ->
      val res = nes.mapOrAccumulate { i ->
        if (i % 2 == 0) i else raise(i)
      }

      val expected = nes.filterNot { it % 2 == 0 }
        .toNonEmptyListOrNull()?.left() ?: nes.filter { it % 2 == 0 }.toNonEmptySetOrNull()!!.right()

      res shouldBe expected
    }
  }

  @Test
  fun mapOrAccumulateAccumulatesErrorsWithCombineFunction() = runTest {
    checkAll(Arb.nonEmptySet(Arb.negativeInt(), range = 0 .. 20)) { nes ->
      val res = nes.mapOrAccumulate(String::plus) { i ->
        if (i > 0) i else raise("Negative")
      }

      res shouldBe nes.map { "Negative" }.joinToString("").left()
    }
  }
}
