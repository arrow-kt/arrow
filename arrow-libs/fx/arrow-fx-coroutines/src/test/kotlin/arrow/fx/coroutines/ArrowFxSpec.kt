package arrow.fx.coroutines

import io.kotest.core.spec.style.FreeSpec
import io.kotest.property.Gen
import io.kotest.property.PropertyContext
import io.kotest.property.checkAll

/**
 * Simple overwritten Kotest FreeSpec to reduce stress on tests.
 */
abstract class ArrowFxSpec(
  private val iterations: Int = 100,
  spec: ArrowFxSpec.() -> Unit = {}
) : FreeSpec() {

  init {
    spec()
  }

  suspend fun <A> checkAll(
    genA: Gen<A>,
    property: suspend PropertyContext.(A) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      genA,
      property
    )

  suspend fun <A, B> checkAll(
    genA: Gen<A>,
    genB: Gen<B>,
    property: suspend PropertyContext.(A, B) -> Unit
  ): PropertyContext =
    checkAll(
      iterations,
      genA,
      genB,
      property
    )

  suspend fun <A, B, C> checkAll(
    genA: Gen<A>,
    genB: Gen<B>,
    genC: Gen<C>,
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
    genA: Gen<A>,
    genB: Gen<B>,
    genC: Gen<C>,
    genD: Gen<D>,
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
    genA: Gen<A>,
    genB: Gen<B>,
    genC: Gen<C>,
    genD: Gen<D>,
    genE: Gen<E>,
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
    genA: Gen<A>,
    genB: Gen<B>,
    genC: Gen<C>,
    genD: Gen<D>,
    genE: Gen<E>,
    genF: Gen<F>,
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
}
