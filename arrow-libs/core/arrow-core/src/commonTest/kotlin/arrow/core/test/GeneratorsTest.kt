package arrow.core.test

import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.assume
import io.kotest.property.checkAll

class GeneratorsTest : FreeSpec({
  "functionABCToD" - {
    "should return same result when invoked multiple times" {
      checkAll(
        Arb.string(),
        Arb.string(),
        Arb.string(),
        Arb.functionABCToD<String, String, String, Int>(Arb.int())
      ) { a, b, c, fn ->
        fn(a, b, c) shouldBe fn(a, b, c)
      }
    }

    "should return some different values" {
      Arb.functionABCToD<String, String, String, Int>(Arb.int()).take(100)
        .forAtLeastOne { fn ->
          checkAll(100, Arb.string(), Arb.string(), Arb.string()) { a, b, c ->
            assume(a != c)
            fn(a, b, c) shouldNotBe fn(c, b, a)
          }
        }
    }
  }

  "Arb.map2" - {
    val result = Arb.map2(Arb.string(), Arb.boolean(), Arb.boolean())
      .generate(RandomSource.default()).take(2000).map { it.value.first.keys.intersect(it.value.second.keys).size }.toList()

    "at least one sample should share no keys" {
      result.forAtLeastOne { it.shouldBeZero() }
    }

    "at least one sample should share some keys" {
      result.forAtLeastOne { it.shouldBeGreaterThan(0) }
    }
  }

  "Arb.map3" - {
    val result = Arb.map3(Arb.string(), Arb.boolean(), Arb.boolean(), Arb.boolean())
      .generate(RandomSource.default()).take(2000).map { it.value.first.keys.intersect(it.value.second.keys).size }.toList()

    "at least one sample should share no keys" {
      result.forAtLeastOne { it.shouldBeZero() }
    }

    "at least one sample should share some keys" {
      result.forAtLeastOne { it.shouldBeGreaterThan(0) }
    }
  }
})
