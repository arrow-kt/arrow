package arrow.fx

import arrow.core.test.UnitSpec
import io.kotlintest.properties.Gen
import io.kotlintest.properties.PropertyContext
import io.kotlintest.properties.forAll

abstract class ArrowFxSpec(
  private val iterations: Int = 100,
  spec: ArrowFxSpec.() -> Unit = {}
) : UnitSpec() {

  init {
    spec()
  }

  fun <A> forAll(
    genA: Gen<A>,
    property: PropertyContext.(A) -> Boolean
  ): Unit =
    forAll(
      iterations,
      genA,
      property
    )

  fun <A, B> forAll(
    genA: Gen<A>,
    genB: Gen<B>,
    property: PropertyContext.(A, B) -> Boolean
  ): Unit =
    forAll(
      iterations,
      genA,
      genB,
      property
    )

  fun <A, B, C> forAll(
    genA: Gen<A>,
    genB: Gen<B>,
    genC: Gen<C>,
    property: PropertyContext.(A, B, C) -> Boolean
  ): Unit =
    forAll(
      iterations,
      genA,
      genB,
      genC,
      property
    )

  fun <A, B, C, D> forAll(
    genA: Gen<A>,
    genB: Gen<B>,
    genC: Gen<C>,
    genD: Gen<D>,
    property: PropertyContext.(A, B, C, D) -> Boolean
  ): Unit =
    forAll(
      iterations,
      genA,
      genB,
      genC,
      genD,
      property
    )

  fun <A, B, C, D, E> forAll(
    genA: Gen<A>,
    genB: Gen<B>,
    genC: Gen<C>,
    genD: Gen<D>,
    genE: Gen<E>,
    property: PropertyContext.(A, B, C, D, E) -> Boolean
  ): Unit =
    forAll(
      iterations,
      genA,
      genB,
      genC,
      genD,
      genE,
      property
    )

  fun <A, B, C, D, E, F> forAll(
    genA: Gen<A>,
    genB: Gen<B>,
    genC: Gen<C>,
    genD: Gen<D>,
    genE: Gen<E>,
    genF: Gen<F>,
    property: PropertyContext.(A, B, C, D, E, F) -> Boolean
  ): Unit =
    forAll(
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
