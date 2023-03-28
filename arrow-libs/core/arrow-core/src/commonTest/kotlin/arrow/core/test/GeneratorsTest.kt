package arrow.core.test

import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class GeneratorsTest : FreeSpec({
  "functionABCToD: should return same result when invoked multiple times" {
    checkAll(
      Arb.string(),
      Arb.string(),
      Arb.string(),
      Arb.functionABCToD<String, String, String, Int>(Arb.int())
    ) { a, b, c, fn ->
      fn(a, b, c) shouldBe fn(a, b, c)
    }
  }

  "functionABCToD: should return some different values" {
    val random = RandomSource.seeded(0)

    val a = Arb.string().next(random)
    val a2 = Arb.string().next(random)
    val b = Arb.string().next(random)
    val c = Arb.string().next(random)

    // there should be at least one function that has a different value when the args are different
    givenSamples(Arb.functionABCToD<String, String, String, Int>(Arb.int())).find { sample ->
      val fn = sample.value
      fn(a, b, c) != fn(a2, b, c)
    }.shouldNotBeNull()
  }

  "Arb.map2: at least one sample should share no keys" {
    val result = givenSamples(
      Arb.map2(
        Arb.string(),
        Arb.boolean(),
        Arb.boolean()
      )
    ).map { it.value.first.keys.intersect(it.value.second.keys).size }

    result.forAtLeastOne { it.shouldBeZero() }
  }

  "Arb.map2: at least one sample should share some keys" {
    val result = givenSamples(
      Arb.map2(
        Arb.string(),
        Arb.boolean(),
        Arb.boolean()
      )
    ).map { it.value.first.keys.intersect(it.value.second.keys).size }

    result.forAtLeastOne { it.shouldBeGreaterThan(0) }
  }

  "Arb.map2: no null values if the arb does not produce nullables" {
    givenSamples(Arb.map2(Arb.string(), Arb.boolean(), Arb.boolean()))
      .forAll { sample ->
        sample.value.first.values.forAll { it.shouldNotBeNull() }
        sample.value.second.values.forAll { it.shouldNotBeNull() }
      }
  }

  "Arb.map2: can contain null values if the arb produces nullables" {
    givenSamples(Arb.map2(Arb.string(), Arb.boolean().orNull(), Arb.boolean().orNull()))
      .forAtLeastOne { sample -> sample.value.first.values.forAtLeastOne { it.shouldBeNull() } }
      .forAtLeastOne { sample -> sample.value.second.values.forAtLeastOne { it.shouldBeNull() } }
  }

  "Arb.map3: at least one sample should share no keys" {
    val result = givenSamples(Arb.map3(Arb.string(), Arb.boolean(), Arb.boolean(), Arb.boolean()))
      .map { it.value.first.keys.intersect(it.value.second.keys).size }.toList()

    result.forAtLeastOne { it.shouldBeZero() }
  }

  "Arb.map3: at least one sample should share some keys" {
    val result = givenSamples(Arb.map3(Arb.string(), Arb.boolean(), Arb.boolean(), Arb.boolean()))
      .map { it.value.first.keys.intersect(it.value.second.keys).size }.toList()

    result.forAtLeastOne { it.shouldBeGreaterThan(0) }
  }

  "Arb.map3: no null values if the arb does not produce nullables" {
    givenSamples(Arb.map3(Arb.string(), Arb.boolean(), Arb.boolean(), Arb.boolean()))
      .forAll { sample ->
        sample.value.first.values.forAll { it.shouldNotBeNull() }
        sample.value.second.values.forAll { it.shouldNotBeNull() }
        sample.value.third.values.forAll { it.shouldNotBeNull() }
      }
  }

  "Arb.map3: can contain null values if the arb produces nullables" {
    givenSamples(Arb.map3(Arb.string(), Arb.boolean().orNull(), Arb.boolean().orNull(), Arb.boolean().orNull()))
      .forAtLeastOne { sample -> sample.value.first.values.forAtLeastOne { it.shouldBeNull() } }
      .forAtLeastOne { sample -> sample.value.second.values.forAtLeastOne { it.shouldBeNull() } }
      .forAtLeastOne { sample -> sample.value.third.values.forAtLeastOne { it.shouldBeNull() } }
  }
})

private fun <T> givenSamples(arb: Arb<T>, count: Int = 250) =
  arb.generate(RandomSource.seeded(0)).take(count).toList()
