package arrow.core.test

import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.test.laws.Law
import io.kotlintest.TestCase
import io.kotlintest.TestType
import io.kotlintest.properties.Gen
import io.kotlintest.properties.PropertyContext
import io.kotlintest.properties.assertAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractStringSpec

/**
 * Base class for unit tests
 */
abstract class UnitSpec : AbstractStringSpec() {

  private val lawTestCases = mutableListOf<TestCase>()

  fun testLaws(vararg laws: List<Law>): List<TestCase> = laws
    .flatMap { list: List<Law> -> list.asIterable() }
    .distinctBy { law: Law -> law.name }
    .map { law: Law ->
      val lawTestCase = createTestCase(law.name, law.test, defaultTestCaseConfig, TestType.Test)
      lawTestCases.add(lawTestCase)
      lawTestCase
    }

  override fun testCases(): List<TestCase> = super.testCases() + lawTestCases

  fun <A, B, C, D, E, F, G> forAll(
    gena: Gen<A>,
    genb: Gen<B>,
    genc: Gen<C>,
    gend: Gen<D>,
    gene: Gen<E>,
    genf: Gen<F>,
    geng: Gen<G>,
    fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G) -> Boolean
  ) {
    assertAll(gena, genb, genc, gend, gene, Gen.bind(genf, geng, ::Pair)) { a, b, c, d, e, (f, g) ->
      fn(a, b, c, d, e, f, g) shouldBe true
    }
  }

  fun <A, B, C, D, E, F, G, H> forAll(
    gena: Gen<A>,
    genb: Gen<B>,
    genc: Gen<C>,
    gend: Gen<D>,
    gene: Gen<E>,
    genf: Gen<F>,
    geng: Gen<G>,
    genh: Gen<H>,
    fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H) -> Boolean
  ) {
    assertAll(gena, genb, genc, gend, gene, Gen.bind(genf, geng, genh, ::Triple)) { a, b, c, d, e, (f, g, h) ->
      fn(a, b, c, d, e, f, g, h) shouldBe true
    }
  }

  fun <A, B, C, D, E, F, G, H, I> forAll(
    gena: Gen<A>,
    genb: Gen<B>,
    genc: Gen<C>,
    gend: Gen<D>,
    gene: Gen<E>,
    genf: Gen<F>,
    geng: Gen<G>,
    genh: Gen<H>,
    geni: Gen<I>,
    fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I) -> Boolean
  ) {
    assertAll(gena, genb, genc, gend, gene, Gen.bind(genf, geng, genh, geni, ::Tuple4)) { a, b, c, d, e, (f, g, h, i) ->
      fn(a, b, c, d, e, f, g, h, i) shouldBe true
    }
  }

  fun <A, B, C, D, E, F, G, H, I, J> forAll(
    gena: Gen<A>,
    genb: Gen<B>,
    genc: Gen<C>,
    gend: Gen<D>,
    gene: Gen<E>,
    genf: Gen<F>,
    geng: Gen<G>,
    genh: Gen<H>,
    geni: Gen<I>,
    genj: Gen<J>,
    fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J) -> Boolean
  ) {
    assertAll(gena, genb, genc, gend, gene, Gen.bind(genf, geng, genh, geni, genj, ::Tuple5)) { a, b, c, d, e, (f, g, h, i, j) ->
      fn(a, b, c, d, e, f, g, h, i, j) shouldBe true
    }
  }
}
