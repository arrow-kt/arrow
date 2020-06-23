package arrow.fx.coroutines

import arrow.fx.coroutines.stream.Pull
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.frequency
import arrow.fx.coroutines.stream.map
import io.kotest.core.spec.style.FreeSpec
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll

/**
 * A Spec that allows you to specify depth for all `Arb` used inside the spec.
 *
 * A `Int.Range` of `0..10` is equivalent to `Arb.list` as where it generates `0..100` by default.
 *
 * `Stream` is randomly generated among it's constructors, but it guarantees a depth of maxmimum `10x range.last`.
 * So for `0..10` it will generate at most a `Stream` with `10` `Chunk`s of `10` elements.
 */
abstract class StreamSpec(
  iterations: Int = 100,
  val depth: IntRange = 0..100,
  spec: StreamSpec.() -> Unit = {}
) : ArrowFxSpec(iterations) {

  inline fun <reified O, R> Arb.Companion.pull(
    arbO: Arb<O>,
    arbR: Arb<R>,
    range: IntRange = depth
  ): Arb<Pull<O, R>> =
    Arb.choice<Pull<O, R>>(
      Arb.bind(Arb.stream(arbO), arbR) { s, r ->
        s.asPull().map { r }
      },
      arbR.map { Pull.just(it) } as Arb<Pull<O, R>>,
      arbR.map { Pull.effect { it } }
    )

  fun <O> Arb.Companion.stream(
    arb: Arb<O>,
    range: IntRange = depth
  ): Arb<Stream<O>> =
    Arb.frequency(
      10 to Arb.list(arb, range).map { os ->
        Stream.iterable(os)
      },
      10 to Arb.list(arb, range).map { os ->
        Stream.iterable(os).unchunk()
      },
      5 to arb.map { fo -> Stream.effect { fo } },
      1 to Arb.bind(Arb.suspended(arb), Arb.list(arb, range), Arb.suspended(Arb.unit())) { acquire, use, release ->
        Stream.bracketCase(acquire, { _, _ -> release.invoke() }).flatMap { Stream.iterable(use) }
      }
    )

  init {
    spec()
  }
}

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
}
