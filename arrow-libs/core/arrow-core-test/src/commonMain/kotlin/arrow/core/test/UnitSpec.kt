package arrow.core.test

import arrow.core.NonEmptyList
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.test.generators.unit
import arrow.core.test.laws.Law
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.list as KList
import io.kotest.property.arbitrary.map as KMap
import io.kotest.property.checkAll
import kotlin.jvm.JvmOverloads
import kotlin.math.max

/**
 * Base class for unit tests
 */
public abstract class UnitSpec(
  public val iterations: Int = 250,
  public val maxDepth: Int = 15,
  spec: UnitSpec.() -> Unit = {}
) : StringSpec() {

  public constructor(spec: UnitSpec.() -> Unit) : this(250, 15, spec)

  public fun <A> Arb.Companion.list(gen: Gen<A>, range: IntRange = 0..maxDepth): Arb<List<A>> =
    Arb.KList(gen, range)

  public fun <A> Arb.Companion.nonEmptyList(arb: Arb<A>, depth: Int = maxDepth): Arb<NonEmptyList<A>> =
    Arb.list(arb, 1..max(1, depth)).filter(List<A>::isNotEmpty).map(NonEmptyList.Companion::fromListUnsafe)

  public fun <A> Arb.Companion.sequence(arbA: Arb<A>, range: IntRange = 0..maxDepth): Arb<Sequence<A>> =
    Arb.list(arbA, range).map { it.asSequence() }

  @JvmOverloads
  public inline fun <reified A> Arb.Companion.array(gen: Arb<A>, range: IntRange = 0..maxDepth): Arb<Array<A>> =
    Arb.list(gen, range).map { it.toTypedArray() }

  public fun <K, V> Arb.Companion.map(
    keyArb: Arb<K>,
    valueArb: Arb<V>,
    minSize: Int = 1,
    maxSize: Int = 15
  ): Arb<Map<K, V>> =
    Arb.KMap(keyArb, valueArb, minSize = minSize, maxSize = maxSize)

  init {
    spec()
  }

  public fun testLaws(vararg laws: List<Law>): Unit = laws
    .flatMap { list: List<Law> -> list.asIterable() }
    .distinctBy { law: Law -> law.name }
    .forEach { law: Law ->
      registration().addTest(TestName(law.name), xdisabled = false, law.test)
    }

  public fun testLaws(prefix: String, vararg laws: List<Law>): Unit = laws
    .flatMap { list: List<Law> -> list.asIterable() }
    .distinctBy { law: Law -> law.name }
    .forEach { law: Law ->
      registration().addTest(TestName(prefix, law.name, true), xdisabled = false, law.test)
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
