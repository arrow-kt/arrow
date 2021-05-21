package arrow.core.test

import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.test.laws.Law
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.createTestName
import io.kotest.property.Arb
import io.kotest.matchers.shouldBe
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.bind
import io.kotest.property.checkAll

/**
 * Base class for unit tests
 */
abstract class UnitSpec : StringSpec() {

  fun testLaws(vararg laws: List<Law>): Unit = laws
    .flatMap { list: List<Law> -> list.asIterable() }
    .distinctBy { law: Law -> law.name }
    .forEach { law: Law ->
      registration().addTest(createTestName(law.name), xdisabled = false, law.test)
    }

  suspend fun <A, B, C, D, E, F, G> checkAll(
    gena: Arb<A>,
    genb: Arb<B>,
    genc: Arb<C>,
    gend: Arb<D>,
    gene: Arb<E>,
    genf: Arb<F>,
    geng: Arb<G>,
    fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G) -> Boolean
  ) {
    checkAll(gena, genb, genc, gend, gene, Arb.bind(genf, geng, ::Pair)) { a, b, c, d, e, (f, g) ->
      fn(a, b, c, d, e, f, g) shouldBe true
    }
  }

  suspend fun <A, B, C, D, E, F, G, H> checkAll(
    gena: Arb<A>,
    genb: Arb<B>,
    genc: Arb<C>,
    gend: Arb<D>,
    gene: Arb<E>,
    genf: Arb<F>,
    geng: Arb<G>,
    genh: Arb<H>,
    fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H) -> Boolean
  ) {
    checkAll(gena, genb, genc, gend, gene, Arb.bind(genf, geng, genh, ::Triple)) { a, b, c, d, e, (f, g, h) ->
      fn(a, b, c, d, e, f, g, h) shouldBe true
    }
  }

  suspend fun <A, B, C, D, E, F, G, H, I> checkAll(
    gena: Arb<A>,
    genb: Arb<B>,
    genc: Arb<C>,
    gend: Arb<D>,
    gene: Arb<E>,
    genf: Arb<F>,
    geng: Arb<G>,
    genh: Arb<H>,
    geni: Arb<I>,
    fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I) -> Boolean
  ) {
    checkAll(gena, genb, genc, gend, gene, Arb.bind(genf, geng, genh, geni, ::Tuple4)) { a, b, c, d, e, (f, g, h, i) ->
      fn(a, b, c, d, e, f, g, h, i) shouldBe true
    }
  }

  suspend fun <A, B, C, D, E, F, G, H, I, J> checkAll(
    gena: Arb<A>,
    genb: Arb<B>,
    genc: Arb<C>,
    gend: Arb<D>,
    gene: Arb<E>,
    genf: Arb<F>,
    geng: Arb<G>,
    genh: Arb<H>,
    geni: Arb<I>,
    genj: Arb<J>,
    fn: PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J) -> Boolean
  ) {
    checkAll(
      gena,
      genb,
      genc,
      gend,
      gene,
      Arb.bind(genf, geng, genh, geni, genj, ::Tuple5)
    ) { a, b, c, d, e, (f, g, h, i, j) ->
      fn(a, b, c, d, e, f, g, h, i, j) shouldBe true
    }
  }
}
