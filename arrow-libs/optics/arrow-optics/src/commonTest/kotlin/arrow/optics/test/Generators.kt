package arrow.optics.test

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.PotentiallyUnsafeNonEmptyOperation
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.toOption
import arrow.core.wrapAsNonEmptyListOrThrow
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import kotlin.math.max

@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
fun <A> Arb.Companion.nonEmptyList(arb: Arb<A>, range: IntRange = 0 .. 100): Arb<NonEmptyList<A>> =
  Arb.list(arb, max(range.first, 1) .. range.last).map { it.wrapAsNonEmptyListOrThrow() }

fun <A> Arb.Companion.sequence(arb: Arb<A>, range: IntRange = 0 .. 100): Arb<Sequence<A>> =
  Arb.list(arb, range).map { it.asSequence() }

fun <A, B> Arb.Companion.functionAToB(arb: Arb<B>): Arb<(A) -> B> =
  arb.map { b: B -> { _: A -> b } }

fun <B> Arb.Companion.option(arb: Arb<B>): Arb<Option<B>> =
  arb.orNull().map { it.toOption() }

fun <E, A> Arb.Companion.either(arbE: Arb<E>, arbA: Arb<A>): Arb<Either<E, A>> {
  val arbLeft = arbE.map { Either.Left(it) }
  val arbRight = arbA.map { Either.Right(it) }
  return Arb.choice(arbLeft, arbRight)
}

fun <A, B, C, D> Arb.Companion.tuple4(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>
): Arb<Tuple4<A, B, C, D>> =
  Arb.bind(arbA, arbB, arbC, arbD, ::Tuple4)

fun <A, B, C, D, E> Arb.Companion.tuple5(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>
): Arb<Tuple5<A, B, C, D, E>> =
  Arb.bind(arbA, arbB, arbC, arbD, arbE, ::Tuple5)

fun <A, B, C, D, E, F> Arb.Companion.tuple6(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>
): Arb<Tuple6<A, B, C, D, E, F>> =
  Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, ::Tuple6)

fun <A, B, C, D, E, F, G> Arb.Companion.tuple7(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>,
  arbG: Arb<G>
): Arb<Tuple7<A, B, C, D, E, F, G>> =
  Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, ::Tuple7)

fun <A, B, C, D, E, F, G, H> Arb.Companion.tuple8(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>,
  arbG: Arb<G>,
  arbH: Arb<H>
): Arb<Tuple8<A, B, C, D, E, F, G, H>> =
  Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH, ::Tuple8)

fun <A, B, C, D, E, F, G, H, I> Arb.Companion.tuple9(
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  arbD: Arb<D>,
  arbE: Arb<E>,
  arbF: Arb<F>,
  arbG: Arb<G>,
  arbH: Arb<H>,
  arbI: Arb<I>
): Arb<Tuple9<A, B, C, D, E, F, G, H, I>> =
  Arb.bind(arbA, arbB, arbC, arbD, arbE, arbF, arbG, arbH, arbI, ::Tuple9)
