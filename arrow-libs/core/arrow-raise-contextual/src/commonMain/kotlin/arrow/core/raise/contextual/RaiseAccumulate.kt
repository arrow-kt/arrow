@file:OptIn(ExperimentalTypeInference::class)

package arrow.core.raise.contextual

import arrow.core.NonEmptyList
import arrow.core.raise.*
import kotlin.experimental.ExperimentalTypeInference

context(raise: Raise<NonEmptyList<Error>>)
@ExperimentalRaiseAccumulateApi public inline fun <Error, A> accumulate(
  block: RaiseAccumulate<Error>.() -> A
): A = raise.accumulate(block)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B> Iterable<A>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = raise.mapOrAccumulate(this, transform)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B> Sequence<A>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = raise.mapOrAccumulate(this, transform)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B> NonEmptyList<A>.mapOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): NonEmptyList<B> = raise.mapOrAccumulate(this, transform)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <K, Error, A, B> Map<K, A>.mapValuesOrAccumulate(
  @BuilderInference transform: RaiseAccumulate<Error>.(Map.Entry<K, A>) -> B
): Map<K, B> = raise.mapValuesOrAccumulate(this, transform)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C> zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  block: (A, B) -> C
): C = raise.zipOrAccumulate(action1, action2, block)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D> zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  block: (A, B, C) -> D
): D = raise.zipOrAccumulate(action1, action2, action3, block)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E> zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  block: (A, B, C, D) -> E
): E = raise.zipOrAccumulate(action1, action2, action3, action4, block)
