package arrow.core.test

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
import io.kotest.property.checkAll
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class GeneratorsTest {
  @Test fun functionAToBShouldReturnSameResultWhenInvokedMultipleTimes() = runTest {
    checkAll(
      Arb.int(),
      Arb.functionAToB<Int, Int>(Arb.int())
    ) { a, fn ->
      fn(a) shouldBe fn(a)
    }
  }

  @Test fun functionAToBShouldReturnSomeDifferentValues() = runTest {
    val a = Arb.int().next(fixedRandom)
    val a2 = Arb.int().next(fixedRandom)

    // there should be at least one function that has a different value when the args are different
    givenSamples(Arb.functionAToB<Int, Int>(Arb.int())).find { sample ->
      val fn = sample.value
      fn(a) != fn(a2)
    }.shouldNotBeNull()
  }

  @Test fun functionABCToDShouldReturnSameResultWhenInvokedMultipleTimes() = runTest {
    checkAll(
      Arb.int(),
      Arb.int(),
      Arb.int(),
      Arb.functionABCToD<Int, Int, Int, Int>(Arb.int())
    ) { a, b, c, fn ->
      fn(a, b, c) shouldBe fn(a, b, c)
    }
  }

  @Test fun functionABCToDShouldReturnSomeDifferentValues() = runTest {
    val a = Arb.int().next(fixedRandom)
    val a2 = Arb.int().next(fixedRandom)
    val b = Arb.int().next(fixedRandom)
    val c = Arb.int().next(fixedRandom)

    // there should be at least one function that has a different value when the args are different
    givenSamples(Arb.functionABCToD<Int, Int, Int, Int>(Arb.int())).find { sample ->
      val fn = sample.value
      fn(a, b, c) != fn(a2, b, c)
    }.shouldNotBeNull()
  }

  @Test fun arbMap2AtLeastOneSampleShouldShareNoKeys() = runTest {
    val result = givenSamples(
      Arb.map2(
        Arb.int(),
        Arb.boolean(),
        Arb.boolean()
      )
    ).map { it.value.first.keys.intersect(it.value.second.keys).size }

    result.forAtLeastOne { it.shouldBeZero() }
  }

  @Test fun arbMap2AtLeastOneSampleShouldShareSomeKeys() = runTest {
    val result = givenSamples(
      Arb.map2(
        Arb.int(),
        Arb.boolean(),
        Arb.boolean()
      )
    ).map { it.value.first.keys.intersect(it.value.second.keys).size }

    result.forAtLeastOne { it.shouldBeGreaterThan(0) }
  }

  @Test fun arbMap2NoNullValuesIfTheArbDoesNotProduceNullables() = runTest {
    givenSamples(Arb.map2(Arb.int(), Arb.boolean(), Arb.boolean()))
      .forAll { sample ->
        sample.value.first.values.forAll { it.shouldNotBeNull() }
        sample.value.second.values.forAll { it.shouldNotBeNull() }
      }
  }

  @Test fun arbMap2CanContainNullValuesIfTheArbProducesNullables() = runTest {
    givenSamples(Arb.map2(Arb.int(), Arb.boolean().orNull(), Arb.boolean().orNull()))
      .forAtLeastOne { sample -> sample.value.first.values.forAtLeastOne { it.shouldBeNull() } }
      .forAtLeastOne { sample -> sample.value.second.values.forAtLeastOne { it.shouldBeNull() } }
  }

  @Test fun arbMap3AtLeastOneSampleShouldShareNoKeys() = runTest {
    val result = givenSamples(Arb.map3(Arb.int(), Arb.boolean(), Arb.boolean(), Arb.boolean()))
      .map { it.value.first.keys.intersect(it.value.second.keys).size }.toList()

    result.forAtLeastOne { it.shouldBeZero() }
  }

  @Test fun ArbMap3AtLeastOneSampleShouldShareSomeKeys() = runTest {
    val result = givenSamples(Arb.map3(Arb.int(), Arb.boolean(), Arb.boolean(), Arb.boolean()))
      .map { it.value.first.keys.intersect(it.value.second.keys).size }.toList()

    result.forAtLeastOne { it.shouldBeGreaterThan(0) }
  }

  @Test fun arbMap3NoNullValuesIfTheArbDoesNotProduceNullables() = runTest {
    givenSamples(Arb.map3(Arb.int(), Arb.boolean(), Arb.boolean(), Arb.boolean()))
      .forAll { sample ->
        sample.value.first.values.forAll { it.shouldNotBeNull() }
        sample.value.second.values.forAll { it.shouldNotBeNull() }
        sample.value.third.values.forAll { it.shouldNotBeNull() }
      }
  }

  @Test fun arbMap3CanContainNullValuesIfTheArbProducesNullables() = runTest {
    givenSamples(Arb.map3(Arb.int(), Arb.boolean().orNull(), Arb.boolean().orNull(), Arb.boolean().orNull()))
      .forAtLeastOne { sample -> sample.value.first.values.forAtLeastOne { it.shouldBeNull() } }
      .forAtLeastOne { sample -> sample.value.second.values.forAtLeastOne { it.shouldBeNull() } }
      .forAtLeastOne { sample -> sample.value.third.values.forAtLeastOne { it.shouldBeNull() } }
  }
}

private fun <T> givenSamples(arb: Arb<T>, count: Int = 250) =
  arb.generate(fixedRandom).take(count).toList()

private val fixedRandom = RandomSource.seeded(0)
