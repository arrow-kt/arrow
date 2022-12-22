package arrow.core.test

import arrow.core.Const
import arrow.core.Either
import arrow.core.Endo
import arrow.core.Eval
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.Tuple10
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.Validated
import arrow.core.left
import arrow.core.right
import arrow.core.toOption
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.byte
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.numericDoubles
import io.kotest.property.arbitrary.numericFloats
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.Dispatchers
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine
import kotlin.math.abs

// copied from kotest-extensions-arrow

public fun <A> Arb.Companion.nonEmptyList(arb: Arb<A>, range: IntRange = 0 .. 100): Arb<NonEmptyList<A>> =
  Arb.bind(arb, Arb.list(arb, range), ::NonEmptyList)

public fun <A> Arb.Companion.sequence(arb: Arb<A>, range: IntRange = 0 .. 100): Arb<Sequence<A>> =
  Arb.list(arb, range).map { it.asSequence() }

public fun <A, B> Arb.Companion.functionAToB(arb: Arb<B>): Arb<(A) -> B> =
  arb.map { b: B -> { _: A -> b } }

public fun Arb.Companion.throwable(): Arb<Throwable> =
  Arb.of(listOf(RuntimeException(), NoSuchElementException(), IllegalArgumentException()))

public fun <A> Arb.Companion.result(arbA: Arb<A>): Arb<Result<A>> =
  Arb.choice(arbA.map(::success), throwable().map(::failure))

public fun Arb.Companion.intSmall(factor: Int = 10000): Arb<Int> =
  Arb.int((Int.MIN_VALUE / factor)..(Int.MAX_VALUE / factor))

public fun Arb.Companion.longSmall(): Arb<Long> =
  Arb.long((Long.MIN_VALUE / 100000L)..(Long.MAX_VALUE / 100000L))

public fun <A> Arb.Companion.endo(arb: Arb<A>): Arb<Endo<A>> = arb.map { a: A -> Endo<A> { a } }

public fun <B> Arb.Companion.option(arb: Arb<B>): Arb<Option<B>> =
  arb.orNull().map { it.toOption() }

public fun <E, A> Arb.Companion.either(arbE: Arb<E>, arbA: Arb<A>): Arb<Either<E, A>> {
  val arbLeft = arbE.map { Either.Left(it) }
  val arbRight = arbA.map { Either.Right(it) }
  return Arb.choice(arbLeft, arbRight)
}

public fun <E, A> Arb<E>.or(arbA: Arb<A>): Arb<Either<E, A>> = Arb.either(this, arbA)

public fun <E, A> Arb.Companion.validated(arbE: Arb<E>, arbA: Arb<A>): Arb<Validated<E, A>> =
  Arb.either(arbE, arbA).map { Validated.fromEither(it) }

public fun Arb.Companion.unit(): Arb<Unit> =
  Arb.constant(Unit)

public fun <A, B> Arb.Companion.ior(arbA: Arb<A>, arbB: Arb<B>): Arb<Ior<A, B>> =
  arbA.alignWith(arbB) { it }

public fun <A> Arb<A>.eval(): Arb<Eval<A>> =
  map { Eval.now(it) }

private fun <A, B, R> Arb<A>.alignWith(arbB: Arb<B>, transform: (Ior<A, B>) -> R): Arb<R> =
  Arb.bind(this, arbB) { a, b -> transform(Ior.Both(a, b)) }

public fun Arb.Companion.suspendFunThatReturnsEitherAnyOrAnyOrThrows(): Arb<suspend () -> Either<Any, Any>> =
  choice(
    suspendFunThatReturnsAnyRight(),
    suspendFunThatReturnsAnyLeft(),
    suspendFunThatThrows()
  )

public fun Arb.Companion.suspendFunThatReturnsAnyRight(): Arb<suspend () -> Either<Any, Any>> =
  any().map { suspend { it.right() } }

public fun Arb.Companion.suspendFunThatReturnsAnyLeft(): Arb<suspend () -> Either<Any, Any>> =
  any().map { suspend { it.left() } }

public fun Arb.Companion.suspendFunThatThrows(): Arb<suspend () -> Either<Any, Any>> =
  throwable().map { suspend { throw it } } as Arb<suspend () -> Either<Any, Any>>

public fun Arb.Companion.any(): Arb<Any> =
  choice(
    Arb.string() as Arb<Any>,
    Arb.int() as Arb<Any>,
    Arb.long() as Arb<Any>,
    Arb.boolean() as Arb<Any>,
    Arb.throwable() as Arb<Any>,
    Arb.unit() as Arb<Any>
  )

public suspend fun Throwable.suspend(): Nothing =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { throw this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }

public suspend fun <A> A.suspend(): A =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }
