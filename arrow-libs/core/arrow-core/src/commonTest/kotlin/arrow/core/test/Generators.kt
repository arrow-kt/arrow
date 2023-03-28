package arrow.core.test

import arrow.core.Either
import arrow.core.Endo
import arrow.core.Eval
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.Option
import arrow.core.Validated
import arrow.core.left
import arrow.core.memoize
import arrow.core.right
import arrow.core.toNonEmptySetOrNull
import arrow.core.toOption
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import kotlinx.coroutines.Dispatchers
import kotlin.math.max
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

// copied from kotest-extensions-arrow

fun <A> Arb.Companion.nonEmptyList(arb: Arb<A>, range: IntRange = 0 .. 100): Arb<NonEmptyList<A>> =
  Arb.bind(arb, Arb.list(arb, range), ::NonEmptyList)

fun <A> Arb.Companion.nonEmptySet(arb: Arb<A>, range: IntRange = 0 .. 100): Arb<NonEmptySet<A>> =
  Arb.set(arb, max(range.first, 1) .. range.last).map { it.toNonEmptySetOrNull()!! }

fun <A> Arb.Companion.sequence(arb: Arb<A>, range: IntRange = 0 .. 100): Arb<Sequence<A>> =
  Arb.list(arb, range).map { it.asSequence() }

fun <A, B> Arb.Companion.functionAToB(arb: Arb<B>): Arb<(A) -> B> =
  arb.map { b: B -> { _: A -> b } }

fun <A, B, C, D> Arb.Companion.functionABCToD(arb: Arb<D>): Arb<(A, B, C) -> D> = arbitrary { random ->
  ({ _: A, _:B, _:C -> arb.next(random)}.memoize())
}

fun Arb.Companion.throwable(): Arb<Throwable> =
  Arb.of(listOf(RuntimeException(), NoSuchElementException(), IllegalArgumentException()))

fun <A> Arb.Companion.result(arbA: Arb<A>): Arb<Result<A>> =
  Arb.choice(arbA.map(::success), throwable().map(::failure))

fun Arb.Companion.intSmall(factor: Int = 10000): Arb<Int> =
  Arb.int((Int.MIN_VALUE / factor)..(Int.MAX_VALUE / factor))

fun Arb.Companion.longSmall(): Arb<Long> =
  Arb.long((Long.MIN_VALUE / 100000L)..(Long.MAX_VALUE / 100000L))

fun <A> Arb.Companion.endo(arb: Arb<A>): Arb<Endo<A>> = arb.map { a: A -> Endo { a } }

fun <B> Arb.Companion.option(arb: Arb<B>): Arb<Option<B>> =
  arb.orNull().map { it.toOption() }

fun <E, A> Arb.Companion.either(arbE: Arb<E>, arbA: Arb<A>): Arb<Either<E, A>> {
  val arbLeft = arbE.map { Either.Left(it) }
  val arbRight = arbA.map { Either.Right(it) }
  return Arb.choice(arbLeft, arbRight)
}

fun <E, A> Arb.Companion.validated(arbE: Arb<E>, arbA: Arb<A>): Arb<Validated<E, A>> =
  Arb.either(arbE, arbA).map { Validated.fromEither(it) }

fun Arb.Companion.unit(): Arb<Unit> =
  Arb.constant(Unit)

fun <A, B> Arb.Companion.ior(arbA: Arb<A>, arbB: Arb<B>): Arb<Ior<A, B>> =
  arbA.alignWith(arbB) { it }

fun <A> Arb<A>.eval(): Arb<Eval<A>> =
  map { Eval.now(it) }

private fun <A, B, R> Arb<A>.alignWith(arbB: Arb<B>, transform: (Ior<A, B>) -> R): Arb<R> =
  Arb.bind(this, arbB) { a, b -> transform(Ior.Both(a, b)) }

fun Arb.Companion.suspendFunThatReturnsEitherAnyOrAnyOrThrows(): Arb<suspend () -> Either<Any, Any>> =
  choice(
    suspendFunThatReturnsAnyRight(),
    suspendFunThatReturnsAnyLeft(),
    suspendFunThatThrows()
  )

fun Arb.Companion.suspendFunThatReturnsAnyRight(): Arb<suspend () -> Either<Any, Any>> =
  any().map { suspend { it.right() } }

fun Arb.Companion.suspendFunThatReturnsAnyLeft(): Arb<suspend () -> Either<Any, Any>> =
  any().map { suspend { it.left() } }

fun Arb.Companion.suspendFunThatThrows(): Arb<suspend () -> Either<Any, Any>> =
  throwable().map { suspend { throw it } }

fun Arb.Companion.any(): Arb<Any> =
  choice(
    Arb.string() as Arb<Any>,
    Arb.int() as Arb<Any>,
    Arb.long() as Arb<Any>,
    Arb.boolean() as Arb<Any>,
    Arb.throwable() as Arb<Any>,
    Arb.unit() as Arb<Any>
  )

suspend fun Throwable.suspend(): Nothing =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { throw this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }

suspend fun <A> A.suspend(): A =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(
      Continuation(Dispatchers.Default) {
        cont.intercepted().resumeWith(it)
      }
    )

    COROUTINE_SUSPENDED
  }

private fun <A, B> value2(first: Arb<A>, second: Arb<B>): Arb<Pair<Option<A>?, Option<B>?>> =
  Arb.choice(
    Arb.bind(
      first,
      second
    ) { a, b ->
      Option.fromNullable(a) to Option.fromNullable(b)
    },
    first.map { Option.fromNullable(it) to null },
    second.map { null to Option.fromNullable(it) }
  )

private fun <A, B, C> value3(first: Arb<A>, second: Arb<B>, third: Arb<C>): Arb<Triple<Option<A>?, Option<B>?, Option<C>?>> =
  Arb.choice(
    Arb.bind(
      first,
      second,
      third
    ) { a, b,c  ->
      Triple(Option.fromNullable(a), Option.fromNullable(b), Option.fromNullable(c))
    },
    Arb.bind(
      first,
      second
    ) { a, b  ->
      Triple(Option.fromNullable(a), Option.fromNullable(b), null)
    },
    Arb.bind(
      first,
      third
    ) { a, c  ->
      Triple(Option.fromNullable(a), null, Option.fromNullable(c))
    },
    Arb.bind(
      second,
      third
    ) {  b,c  ->
      Triple(null, Option.fromNullable(b), Option.fromNullable(c))
    },
    first.map { Triple(Option.fromNullable(it), null, null )},
    second.map { Triple(null, Option.fromNullable(it), null ) },
    third.map { Triple(null, null, Option.fromNullable(it) ) }
  )

private fun <K, A, B, C> Map<K, Triple<Option<A>?, Option<B>?, Option<C>?>>.destructured(): Triple<Map<K, A>, Map<K, B>, Map<K, C>> {
  val firstMap = mutableMapOf<K, A?>()
  val secondMap = mutableMapOf<K, B?>()
  val thirdMap = mutableMapOf<K, C?>()

  this.forEach { (key, triple) ->
    val (a, b, c) = triple

    if (a != null) {
      firstMap[key] = a.getOrNull()
    }

    if (b != null) {
      secondMap[key] = b.getOrNull()
    }

    if (c != null) {
      thirdMap[key] = c.getOrNull()
    }
  }

  @Suppress("UNCHECKED_CAST")
  return Triple(firstMap, secondMap, thirdMap) as Triple<Map<K, A>, Map<K, B>, Map<K, C>>
}

private fun <K, A, B> Map<K, Pair<Option<A>?, Option<B>?>>.destructured(): Pair<Map<K, A>, Map<K, B>> {
  val firstMap = mutableMapOf<K, A?>()
  val secondMap = mutableMapOf<K, B?>()

  this.forEach { (key, pair) ->
    val (a, b) = pair

    if (a != null) {
      firstMap[key] = a.getOrNull()
    }

    if (b != null) {
      secondMap[key] = b.getOrNull()
    }
  }

  @Suppress("UNCHECKED_CAST")
  return (firstMap to secondMap) as Pair<Map<K, A>, Map<K, B>>
}

fun <K, A, B> Arb.Companion.map2(arbK: Arb<K>, arbA: Arb<A>, arbB: Arb<B>, size: IntRange = 0 .. 20): Arb<Pair<Map<K, A>, Map<K, B>>> =
  Arb.map(keyArb = arbK, valueArb = value2(arbA, arbB), minSize = size.first, maxSize = size.last)
    .map { it.destructured() }

fun <K, A, B, C> Arb.Companion.map3(
  arbK: Arb<K>,
  arbA: Arb<A>,
  arbB: Arb<B>,
  arbC: Arb<C>,
  size: IntRange = 0 .. 20
): Arb<Triple<Map<K, A>, Map<K, B>, Map<K, C>>> =
  Arb.map(arbK, value3(arbA, arbB, arbC), minSize = size.first, maxSize = size.last)
    .map { it.destructured() }
