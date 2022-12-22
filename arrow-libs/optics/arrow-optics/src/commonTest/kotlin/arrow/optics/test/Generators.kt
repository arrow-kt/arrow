package arrow.optics.test

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.Validated
import arrow.core.toOption
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.orNull

public fun <A> Arb.Companion.nonEmptyList(arb: Arb<A>, range: IntRange = 0 .. 100): Arb<NonEmptyList<A>> =
  Arb.bind(arb, Arb.list(arb, range), ::NonEmptyList)

public fun <A> Arb.Companion.sequence(arb: Arb<A>, range: IntRange = 0 .. 100): Arb<Sequence<A>> =
  Arb.list(arb, range).map { it.asSequence() }

public fun <A, B> Arb.Companion.functionAToB(arb: Arb<B>): Arb<(A) -> B> =
  arb.map { b: B -> { _: A -> b } }

public fun <B> Arb.Companion.option(arb: Arb<B>): Arb<Option<B>> =
  arb.orNull().map { it.toOption() }

public fun <E, A> Arb.Companion.either(arbE: Arb<E>, arbA: Arb<A>): Arb<Either<E, A>> {
  val arbLeft = arbE.map { Either.Left(it) }
  val arbRight = arbA.map { Either.Right(it) }
  return Arb.choice(arbLeft, arbRight)
}

public fun <E, A> Arb.Companion.validated(arbE: Arb<E>, arbA: Arb<A>): Arb<Validated<E, A>> =
  Arb.either(arbE, arbA).map { Validated.fromEither(it) }
