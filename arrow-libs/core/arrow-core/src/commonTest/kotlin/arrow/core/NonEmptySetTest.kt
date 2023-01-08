package arrow.core

import arrow.core.test.nonEmptySet
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class NonEmptySetTest : StringSpec({

  "iterable.toNonEmptySetOrNull should round trip" {
    checkAll(Arb.nonEmptySet(Arb.int())) { nonEmptySet ->
      nonEmptySet.elements.toNonEmptySetOrNull().shouldNotBeNull() shouldBe nonEmptySet
    }
  }

  "iterable.toNonEmptySetOrNone should round trip" {
    checkAll(Arb.nonEmptySet(Arb.int())) { nonEmptySet ->
      nonEmptySet.elements.toNonEmptySetOrNone() shouldBe nonEmptySet.some()
    }
  }
})

