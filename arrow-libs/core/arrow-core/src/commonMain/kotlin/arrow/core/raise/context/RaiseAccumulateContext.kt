@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")
@file:Suppress("LEAKED_IN_PLACE_LAMBDA")

package arrow.core.raise.context

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.getOrElse
import arrow.core.raise.Accumulate
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.RaiseAccumulate.Value
import arrow.core.raise.RaiseDSL
import arrow.core.raise.RaiseNel
import arrow.core.raise.accumulating
import arrow.core.raise.ensureNotNullOrAccumulate
import arrow.core.raise.ensureOrAccumulate
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import arrow.core.raise.accumulate as accumulateExt
import arrow.core.raise.mapOrAccumulate as mapOrAccumulateExt
import arrow.core.raise.mapValuesOrAccumulate as mapValuesOrAccumulateExt
import arrow.core.raise.zipOrAccumulate as zipOrAccumulateExt

@Deprecated("RaiseAccumulate has been split into Raise and Accumulate. Use the new APIs instead.")
public typealias RaiseAccumulate<A> = arrow.core.raise.RaiseAccumulate<A>

// without the (this, this) we get an internal compiler error. May be fixed in 2.2.20?
context(raise: Raise<NonEmptyList<Error>>)
@ExperimentalRaiseAccumulateApi @RaiseDSL public inline fun <Error, A> accumulate(
  block: context(Raise<Error>, Accumulate<Error>) () -> A
): A = raise.accumulateExt { block(this, this) }

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B> Iterable<A>.mapOrAccumulate(
  @BuilderInference transform: context(Raise<Error>, Accumulate<Error>) (A) -> B
): List<B> = raise.mapOrAccumulateExt(this) { transform(this, this, it) }

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B> Sequence<A>.mapOrAccumulate(
  @BuilderInference transform: context(Raise<Error>, Accumulate<Error>) (A) -> B
): List<B> = raise.mapOrAccumulateExt(this) { transform(this, this, it) }

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B> NonEmptyList<A>.mapOrAccumulate(
  @BuilderInference transform: context(Raise<Error>, Accumulate<Error>) (A) -> B
): NonEmptyList<B> {
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return raise.mapOrAccumulateExt(this) { transform(this, this, it) }
}

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL
public inline fun <Error, A, B> NonEmptySet<A>.mapOrAccumulate(
  @BuilderInference transform: context(Raise<Error>, Accumulate<Error>) (A) -> B
): NonEmptySet<B> {
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return raise.mapOrAccumulateExt(this) { transform(this, this, it) }
}

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <K, Error, A, B> Map<K, A>.mapValuesOrAccumulate(
  @BuilderInference transform: context(Raise<Error>, Accumulate<Error>) (Map.Entry<K, A>) -> B
): Map<K, B> = raise.mapValuesOrAccumulateExt(this) { transform(this, this, it) }

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C> zipOrAccumulate(
  @BuilderInference action1: context(Raise<Error>, Accumulate<Error>) () -> A,
  @BuilderInference action2: context(Raise<Error>, Accumulate<Error>) () -> B,
  block: (A, B) -> C
): C = raise.zipOrAccumulateExt({ action1(this, this) }, { action2(this, this) }, block)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D> zipOrAccumulate(
  @BuilderInference action1: context(Raise<Error>, Accumulate<Error>) () -> A,
  @BuilderInference action2: context(Raise<Error>, Accumulate<Error>) () -> B,
  @BuilderInference action3: context(Raise<Error>, Accumulate<Error>) () -> C,
  block: (A, B, C) -> D
): D = raise.zipOrAccumulateExt({ action1(this, this) }, { action2(this, this) }, { action3(this, this) }, block)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E> zipOrAccumulate(
  @BuilderInference action1: context(Raise<Error>, Accumulate<Error>) () -> A,
  @BuilderInference action2: context(Raise<Error>, Accumulate<Error>) () -> B,
  @BuilderInference action3: context(Raise<Error>, Accumulate<Error>) () -> C,
  @BuilderInference action4: context(Raise<Error>, Accumulate<Error>) () -> D,
  block: (A, B, C, D) -> E
): E = raise.zipOrAccumulateExt({ action1(this, this) }, { action2(this, this) }, { action3(this, this) }, { action4(this, this) }, block)

@RaiseDSL
context(_: Raise<Error>, _: Accumulate<Error>)
public inline fun <Error, A, B> Iterable<A>.mapAccumulating(
  transform: context(Raise<Error>, Accumulate<Error>) (A) -> B
): List<B> = withNel { mapOrAccumulate(transform) }

@RaiseDSL
context(_: Raise<Error>, _: Accumulate<Error>)
public inline fun <A, B> NonEmptyList<A>.mapAccumulating(
  transform: context(Raise<Error>, Accumulate<Error>) (A) -> B
): NonEmptyList<B> {
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return withNel { mapOrAccumulate(transform) }
}

@RaiseDSL
context(_: Raise<Error>, _: Accumulate<Error>)
public inline fun <A, B> NonEmptySet<A>.mapAccumulating(
  transform: context(Raise<Error>, Accumulate<Error>) (A) -> B
): NonEmptySet<B> {
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return withNel { mapOrAccumulate(transform) }
}

@RaiseDSL
context(_: Raise<Error>, _: Accumulate<Error>)
public inline fun <K, A, B> Map<K, A>.mapAccumulating(
  transform: context(Raise<Error>, Accumulate<Error>) (Map.Entry<K, A>) -> B
): List<B> = withNel { entries.mapOrAccumulate(transform) }

@RaiseDSL
context(_: Raise<Error>, _: Accumulate<Error>)
public inline fun <K, A, B> Map<K, A>.mapValuesAccumulating(
  transform: context(Raise<Error>, Accumulate<Error>) (Map.Entry<K, A>) -> B
): Map<K, B> = withNel { mapValuesOrAccumulate(transform) }

context(_: Raise<Error>, _: Accumulate<Error>)
public fun <K, A> Map<K, Either<Error, A>>.bindAllAccumulating(): Map<K, A> =
  mapValuesAccumulating { it.value.bind() }

@RaiseDSL
context(_: Raise<Error>, _: Accumulate<Error>)
public fun <A> Iterable<Either<Error, A>>.bindAllAccumulating(): List<A> =
  mapAccumulating { it.bind() }

context(_: Raise<Error>, _: Accumulate<Error>)
public fun <A> NonEmptyList<Either<Error, A>>.bindAllAccumulating(): NonEmptyList<A> =
  mapAccumulating { it.bind() }

context(_: Raise<Error>, _: Accumulate<Error>)
public fun <A> NonEmptySet<Either<Error, A>>.bindAllAccumulating(): NonEmptySet<A> =
  mapAccumulating { it.bind() }

@RaiseDSL
context(_: Raise<Error>, _: Accumulate<Error>)
public fun <Error, A> EitherNel<Error, A>.bindNel(): A = withNel { bind() }

@OptIn(ExperimentalRaiseAccumulateApi::class)
context(raise: Raise<Error>, accumulate: Accumulate<Error>)
@PublishedApi
internal val <Error> raiseNel: Raise<NonEmptyList<Error>> get() = RaiseNel(raise, accumulate)

@RaiseDSL
context(_: Raise<Error>, _: Accumulate<Error>)
public inline fun <Error, A> withNel(block: context(Raise<NonEmptyList<Error>>) () -> A): A {
  contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
  return block(raiseNel)
}

@ExperimentalRaiseAccumulateApi
context(accumulate: Accumulate<Error>)
public fun <Error> accumulate(error: Error): Value<Nothing> = accumulate.accumulate(error)

@ExperimentalRaiseAccumulateApi
context(accumulate: Accumulate<Error>)
public fun <Error> accumulateAll(errors: NonEmptyList<Error>): Value<Nothing> = accumulate.accumulateAll(errors)

@ExperimentalRaiseAccumulateApi
context(accumulate: Accumulate<*>)
public val hasAccumulatedErrors: Boolean get() = accumulate.hasAccumulatedErrors

@ExperimentalRaiseAccumulateApi
context(accumulate: Accumulate<*>)
public val latestError: Value<Nothing>? get() = accumulate.latestError

@ExperimentalRaiseAccumulateApi
context(accumulate: Accumulate<Error>)
public fun <Error, A> Either<Error, A>.getOrAccumulate(recover: (Error) -> A): A = getOrElse {
  accumulate(it)
  recover(it)
}

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(accumulate: Accumulate<Error>)
public fun <Error, A> Either<Error, A>.bindOrAccumulate(): Value<A> =
  with(accumulate) { this@bindOrAccumulate.bindOrAccumulate() }

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(accumulate: Accumulate<Error>)
public fun <Error, A> Iterable<Either<Error, A>>.bindAllOrAccumulate(): Value<List<A>> =
  with(accumulate) { this@bindAllOrAccumulate.bindAllOrAccumulate() }

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(accumulate: Accumulate<Error>)
public fun <Error, A> EitherNel<Error, A>.bindNelOrAccumulate(): Value<A> =
  with(accumulate) { this@bindNelOrAccumulate.bindNelOrAccumulate() }

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(accumulate: Accumulate<Error>)
public inline fun <Error> ensureOrAccumulate(condition: Boolean, error: () -> Error) {
  contract { callsInPlace(error, AT_MOST_ONCE) }
  accumulate.ensureOrAccumulate(condition, error)
}

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(accumulate: Accumulate<Error>)
public inline fun <Error, B: Any> ensureNotNullOrAccumulate(value: B?, error: () -> Error): Value<B> {
  contract { callsInPlace(error, AT_MOST_ONCE) }
  return accumulate.ensureNotNullOrAccumulate(value, error)
}

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(accumulate: Accumulate<Error>)
public inline fun <Error, A> accumulating(block: context(Raise<Error>, Accumulate<Error>) () -> A): Value<A> {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return accumulate.accumulating { block(this, this) }
}
