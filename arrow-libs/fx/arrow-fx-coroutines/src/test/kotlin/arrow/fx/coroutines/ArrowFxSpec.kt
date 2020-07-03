package arrow.fx.coroutines

import arrow.fx.coroutines.stream.Pull
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.map
import io.kotest.core.spec.style.FreeSpec
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.Shrinker
import io.kotest.property.arbitrary.arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.choose
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import kotlin.math.abs

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
    Arb.choose(
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

  fun Arb.Companion.long(range: LongRange = Long.MIN_VALUE..Long.MAX_VALUE): Arb<Long> {
    val edgecases = listOf(0L, 1, -1, Long.MAX_VALUE, Long.MIN_VALUE).filter { it in range }
    return arb(LongShrinker(range), edgecases) { it.random.nextLong(range.first, range.last) }
  }

  class LongShrinker(private val range: LongRange) : Shrinker<Long> {
    override fun shrink(value: Long): List<Long> =
      when (value) {
        0L -> emptyList()
        1L, -1L -> listOf(0)
        else -> {
          val a = listOf(abs(value), value / 3, value / 2, value * 2 / 3)
          val b = (1..5L).map { value - it }.reversed().filter { it > 0 }
          (a + b).distinct().filter { it in range && it != value }
        }
      }
  }

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
}
