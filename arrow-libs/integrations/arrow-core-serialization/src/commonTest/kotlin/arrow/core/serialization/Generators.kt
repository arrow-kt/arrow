package arrow.core.serialization

import arrow.core.*
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.set
import kotlin.math.max

fun <A> Arb.Companion.nonEmptyList(arb: Arb<A>, range: IntRange = 0 .. 100): Arb<NonEmptyList<A>> =
  Arb.bind(arb, Arb.list(arb, range)) { head, tail -> NonEmptyList(head, tail) }

@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
fun <A> Arb.Companion.nonEmptySet(arb: Arb<A>, range: IntRange = 0 .. 100): Arb<NonEmptySet<A>> =
  Arb.set(arb, max(range.first, 1) .. range.last).map { it.wrapAsNonEmptySetOrThrow() }

fun <B : Any> Arb.Companion.option(arb: Arb<B>): Arb<Option<B>> =
  arb.orNull().map { it.toOption() }

fun <E, A> Arb.Companion.either(arbE: Arb<E>, arbA: Arb<A>): Arb<Either<E, A>> {
  val arbLeft = arbE.map { Either.Left(it) }
  val arbRight = arbA.map { Either.Right(it) }
  return Arb.choice(arbLeft, arbRight)
}

fun <A, B> Arb.Companion.ior(arbA: Arb<A>, arbB: Arb<B>): Arb<Ior<A, B>> =
  arbA.alignWith(arbB) { it }

private fun <A, B, R> Arb<A>.alignWith(arbB: Arb<B>, transform: (Ior<A, B>) -> R): Arb<R> =
  Arb.bind(this, arbB) { a, b -> transform(Ior.Both(a, b)) }
