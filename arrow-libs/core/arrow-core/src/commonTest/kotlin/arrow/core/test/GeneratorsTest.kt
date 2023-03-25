package arrow.core.test

import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
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

    "generates maps with shared keys" - {
      val result = givenSamples(
        Arb.map2(
          Arb.string(),
          Arb.boolean(),
          Arb.boolean()
        )
      ).map { it.value.first.keys.intersect(it.value.second.keys).size }

      "at least one sample should share no keys" {
        result.forAtLeastOne { it.shouldBeZero() }
      }

      "at least one sample should share some keys" {
        result.forAtLeastOne { it.shouldBeGreaterThan(0) }
      }
    }

    "nullable values" - {
      "no null values if the arb does not produce nullables" {
        givenSamples(Arb.map2(Arb.string(), Arb.boolean(), Arb.boolean()))
          .forAll { sample ->
            sample.value.first.values.forAll { it.shouldNotBeNull() }
            sample.value.second.values.forAll { it.shouldNotBeNull() }
          }
      }

      "can have null values if the arb produces nullables" {
        givenSamples(Arb.map2(Arb.string(), Arb.boolean().orNull(), Arb.boolean().orNull()))
          .forAtLeastOne { sample -> sample.value.first.values.forAtLeastOne { it.shouldBeNull() } }
          .forAtLeastOne { sample -> sample.value.second.values.forAtLeastOne { it.shouldBeNull() } }
      }
    }
  }

  "Arb.map3" - {

    "generates maps with shared keys" - {
      val result = givenSamples(Arb.map3(Arb.string(), Arb.boolean(), Arb.boolean(), Arb.boolean()))
        .map { it.value.first.keys.intersect(it.value.second.keys).size }.toList()
      "at least one sample should share no keys" {
        result.forAtLeastOne { it.shouldBeZero() }
      }

      "at least one sample should share some keys" {
        result.forAtLeastOne { it.shouldBeGreaterThan(0) }
      }
    }

    "nullable values" - {
      "no null values if the arb does not produce nullables" {
        givenSamples(Arb.map3(Arb.string(), Arb.boolean(), Arb.boolean(), Arb.boolean()))
          .forAll { sample ->
            sample.value.first.values.forAll { it.shouldNotBeNull() }
            sample.value.second.values.forAll { it.shouldNotBeNull() }
            sample.value.third.values.forAll { it.shouldNotBeNull() }
          }
      }

      "can have null values if the arb produces nullables" {
        givenSamples(Arb.map3(Arb.string(), Arb.boolean().orNull(), Arb.boolean().orNull(), Arb.boolean().orNull()))
          .forAtLeastOne { sample -> sample.value.first.values.forAtLeastOne { it.shouldBeNull() } }
          .forAtLeastOne { sample -> sample.value.second.values.forAtLeastOne { it.shouldBeNull() } }
          .forAtLeastOne { sample -> sample.value.third.values.forAtLeastOne { it.shouldBeNull() } }
      }
    }
  }
})

private fun <T> givenSamples(arb: Arb<T>) =
  arb.generate(RandomSource.seeded(0)).take(2000).toList()
