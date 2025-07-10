@file:OptIn(ExperimentalTypeInference::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")

package arrow.core.raise.context

import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.RaiseDSL
import arrow.core.raise.accumulate as accumulateExt
import arrow.core.raise.mapOrAccumulate as mapOrAccumulateExt
import arrow.core.raise.mapValuesOrAccumulate as mapValuesOrAccumulateExt
import arrow.core.raise.zipOrAccumulate as zipOrAccumulateExt
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public typealias RaiseAccumulate<A> = arrow.core.raise.RaiseAccumulate<A>

context(raise: Raise<NonEmptyList<Error>>)
@ExperimentalRaiseAccumulateApi public inline fun <Error, A> accumulate(
  block: context(RaiseAccumulate<Error>) () -> A
): A = raise.accumulateExt(block)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B> Iterable<A>.mapOrAccumulate(
  @BuilderInference transform: context(RaiseAccumulate<Error>) (A) -> B
): List<B> = raise.mapOrAccumulateExt(this, transform)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B> Sequence<A>.mapOrAccumulate(
  @BuilderInference transform: context(RaiseAccumulate<Error>) (A) -> B
): List<B> = raise.mapOrAccumulateExt(this, transform)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B> NonEmptyList<A>.mapOrAccumulate(
  @BuilderInference transform: context(RaiseAccumulate<Error>) (A) -> B
): NonEmptyList<B> = raise.mapOrAccumulateExt(this, transform)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <K, Error, A, B> Map<K, A>.mapValuesOrAccumulate(
  @BuilderInference transform: context(RaiseAccumulate<Error>) (Map.Entry<K, A>) -> B
): Map<K, B> = raise.mapValuesOrAccumulateExt(this, transform)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C> zipOrAccumulate(
  @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
  block: (A, B) -> C
): C = raise.zipOrAccumulateExt(action1, action2, block)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D> zipOrAccumulate(
  @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(RaiseAccumulate<Error>) () -> C,
  block: (A, B, C) -> D
): D = raise.zipOrAccumulateExt(action1, action2, action3, block)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E> zipOrAccumulate(
  @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(RaiseAccumulate<Error>) () -> D,
  block: (A, B, C, D) -> E
): E = raise.zipOrAccumulateExt(action1, action2, action3, action4, block)
