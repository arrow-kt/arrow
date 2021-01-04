package arrow.fx.coroutines

import arrow.core.Tuple2
import arrow.core.Tuple3
import io.kotest.core.spec.style.FreeSpec
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.bind
import io.kotest.property.checkAll

/**
 * Simple overwritten Kotest FreeSpec to reduce stress on tests.
 */
abstract class ArrowFxSpec(
  private val iterations: Int = 350,
  spec: ArrowFxSpec.() -> Unit = {}
) : FreeSpec() {

  init {
    spec()
  }

  suspend fun checkAll(property: suspend PropertyContext.() -> Unit): PropertyContext =
    checkAll(iterations, Arb.unit()) { property() }

  suspend fun <A> checkAll(
    genA: Arb<A>,
    property: suspend PropertyContext.(A) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      genA,
      property
    )

  suspend fun <A, B> checkAll(
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

  suspend fun <A, B, C> checkAll(
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

  suspend fun <A, B, C, D> checkAll(
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

  suspend fun <A, B, C, D, E> checkAll(
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

  suspend fun <A, B, C, D, E, F> checkAll(
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

  suspend fun <A, B, C, D, E, F, G> checkAll(
    genA: Arb<A>,
    genB: Arb<B>,
    genC: Arb<C>,
    genD: Arb<D>,
    genE: Arb<E>,
    genF: Arb<F>,
    genG: Arb<G>,
    property: suspend PropertyContext.(A, B, C, D, E, F, G) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      genA,
      genB,
      genC,
      genD,
      genE,
      Arb.bind(genF, genG, ::Tuple2)
    ) { a, b, c, d, e, (f, g) ->
      property(a, b, c, d, e, f, g)
    }

  suspend fun <A, B, C, D, E, F, G, H> checkAll(
    genA: Arb<A>,
    genB: Arb<B>,
    genC: Arb<C>,
    genD: Arb<D>,
    genE: Arb<E>,
    genF: Arb<F>,
    genG: Arb<G>,
    genH: Arb<H>,
    property: suspend PropertyContext.(A, B, C, D, E, F, G, H) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      genA,
      genB,
      genC,
      genD,
      genE,
      Arb.bind(genF, genG, genH, ::Tuple3)
    ) { a, b, c, d, e, (f, g, h) ->
      property(a, b, c, d, e, f, g, h)
    }

  suspend fun forFew(
    iterations: Int,
    property: suspend PropertyContext.(Unit) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      Arb.unit(),
      property
    )
}
