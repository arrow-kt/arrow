package arrow.core

import arrow.core.test.nonEmptySet
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class NonEmptySetTest {

  @Test fun iterableToNonEmptySetOrNullShouldRoundTrip() = runTest {
    checkAll(Arb.nonEmptySet(Arb.int())) { nonEmptySet ->
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
}

