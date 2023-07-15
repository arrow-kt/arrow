package arrow.core

import arrow.core.test.nonEmptySet
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll

class NonEmptySetTest : StringSpec({

  "iterable.toNonEmptySetOrNull should round trip" {
    checkAll(Arb.nonEmptySet(Arb.int())) { nonEmptySet ->
      nonEmptySet.toNonEmptySetOrNull().shouldNotBeNull() shouldBe nonEmptySet
    }
  }

  "iterable.toNonEmptySetOrNone should round trip" {
    checkAll(Arb.nonEmptySet(Arb.int())) { nonEmptySet ->
      nonEmptySet.toNonEmptySetOrNone() shouldBe nonEmptySet.some()
    }
  }

  "emptyList.toNonEmptySetOrNull should be null" {
    listOf<Int>().toNonEmptySetOrNull() shouldBe null
  }

  "emptyList.toNonEmptySetOrNone should be none" {
    listOf<Int>().toNonEmptySetOrNone() shouldBe none()
  }

  "adding an element already present doesn't change the set" {
    val element = Arb.int().next()
    val initialSet: NonEmptySet<Int> = nonEmptySetOf(element) + Arb.nonEmptySet(Arb.int()).next()
    initialSet.plus(element) shouldBe initialSet
  }

  "NonEmptySet equals Set" {
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

  "NonEmptySet equals NonEmptySet" {
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
})

