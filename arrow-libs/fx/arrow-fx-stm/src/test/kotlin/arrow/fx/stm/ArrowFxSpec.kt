package arrow.fx.stm

import io.kotest.core.spec.style.FreeSpec
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.Shrinker
import io.kotest.property.arbitrary.arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.checkAll
import kotlin.math.abs

fun Arb.Companion.unit(): Arb<Unit> =
  Arb.constant(Unit)

/**
 * Simple overwritten Kotest FreeSpec to reduce stress on tests.
 */
abstract class ArrowFxSpec(
  private val iterations: Int = 350,
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
