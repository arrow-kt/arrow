package arrow.core.test

import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.test.generators.unit
import arrow.core.test.laws.Law
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.createTestName
import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.bind
import io.kotest.property.checkAll

/**
 * Base class for unit tests
 */
public abstract class UnitSpec(
  private val iterations: Int = 250,
  spec: UnitSpec.() -> Unit = {}
) : StringSpec() {

  public constructor(spec: UnitSpec.() -> Unit): this(250, spec)

  init {
      spec()
  }

  public fun testLaws(vararg laws: List<Law>): Unit = laws
    .flatMap { list: List<Law> -> list.asIterable() }
    .distinctBy { law: Law -> law.name }
    .forEach { law: Law ->
      registration().addTest(createTestName(law.name), xdisabled = false, law.test)
    }

  public fun testLaws(prefix: String, vararg laws: List<Law>): Unit = laws
    .flatMap { list: List<Law> -> list.asIterable() }
    .distinctBy { law: Law -> law.name }
    .forEach { law: Law ->
      registration().addTest(createTestName(prefix, law.name, true), xdisabled = false, law.test)
    }

  public suspend fun checkAll(property: suspend PropertyContext.() -> Unit): PropertyContext =
    checkAll(iterations, Arb.unit()) { property() }

  public suspend fun <A> checkAll(
    genA: Arb<A>,
    property: suspend PropertyContext.(A) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      genA,
      property
    )

  public suspend fun <A, B> checkAll(
    genA: Arb<A>,
    genB: Arb<B>,
    property: suspend PropertyContext.(A, B) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      genA,
      genB,
      property
    )

  public suspend fun <A, B, C> checkAll(
    genA: Arb<A>,
    genB: Arb<B>,
    genC: Arb<C>,
    property: suspend PropertyContext.(A, B, C) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      genA,
      genB,
      genC,
      property
    )

  public suspend fun <A, B, C, D> checkAll(
    genA: Arb<A>,
    genB: Arb<B>,
    genC: Arb<C>,
    genD: Arb<D>,
    property: suspend PropertyContext.(A, B, C, D) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      genA,
      genB,
      genC,
      genD,
      property
    )

  public suspend fun <A, B, C, D, E> checkAll(
    genA: Arb<A>,
    genB: Arb<B>,
    genC: Arb<C>,
    genD: Arb<D>,
    genE: Arb<E>,
    property: suspend PropertyContext.(A, B, C, D, E) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      genA,
      genB,
      genC,
      genD,
      genE,
      property
    )

  public suspend fun <A, B, C, D, E, F> checkAll(
    genA: Arb<A>,
    genB: Arb<B>,
    genC: Arb<C>,
    genD: Arb<D>,
    genE: Arb<E>,
    genF: Arb<F>,
    property: suspend PropertyContext.(A, B, C, D, E, F) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      genA,
      genB,
      genC,
      genD,
      genE,
      genF,
      property
    )

  public suspend fun <A, B, C, D, E, F, G> checkAll(
    gena: Arb<A>,
    genb: Arb<B>,
    genc: Arb<C>,
    gend: Arb<D>,
    gene: Arb<E>,
    genf: Arb<F>,
    geng: Arb<G>,
    fn: suspend PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G) -> Unit
  ) {
    checkAll(gena, genb, genc, gend, gene, Arb.bind(genf, geng, ::Pair)) { a, b, c, d, e, (f, g) ->
      fn(a, b, c, d, e, f, g)
    }
  }

  public suspend fun <A, B, C, D, E, F, G, H> checkAll(
    gena: Arb<A>,
    genb: Arb<B>,
    genc: Arb<C>,
    gend: Arb<D>,
    gene: Arb<E>,
    genf: Arb<F>,
    geng: Arb<G>,
    genh: Arb<H>,
    fn: suspend PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H) -> Unit
  ) {
    checkAll(gena, genb, genc, gend, gene, Arb.bind(genf, geng, genh, ::Triple)) { a, b, c, d, e, (f, g, h) ->
      fn(a, b, c, d, e, f, g, h)
    }
  }

  public suspend fun <A, B, C, D, E, F, G, H, I> checkAll(
    gena: Arb<A>,
    genb: Arb<B>,
    genc: Arb<C>,
    gend: Arb<D>,
    gene: Arb<E>,
    genf: Arb<F>,
    geng: Arb<G>,
    genh: Arb<H>,
    geni: Arb<I>,
    fn: suspend PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I) -> Unit
  ) {
    checkAll(gena, genb, genc, gend, gene, Arb.bind(genf, geng, genh, geni, ::Tuple4)) { a, b, c, d, e, (f, g, h, i) ->
      fn(a, b, c, d, e, f, g, h, i)
    }
  }

  public suspend fun <A, B, C, D, E, F, G, H, I, J> checkAll(
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
    fn: suspend PropertyContext.(a: A, b: B, c: C, d: D, e: E, f: F, g: G, h: H, i: I, j: J) -> Unit
  ) {
    checkAll(
      gena,
      genb,
      genc,
      gend,
      gene,
      Arb.bind(genf, geng, genh, geni, genj, ::Tuple5)
    ) { a, b, c, d, e, (f, g, h, i, j) ->
      fn(a, b, c, d, e, f, g, h, i, j)
    }
  }

  public suspend fun forFew(
    iterations: Int,
    property: suspend PropertyContext.(Unit) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      Arb.unit(),
      property
    )
}
