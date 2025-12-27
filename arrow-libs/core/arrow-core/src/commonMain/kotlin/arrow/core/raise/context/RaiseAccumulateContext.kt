@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")
@file:Suppress("LEAKED_IN_PLACE_LAMBDA")

package arrow.core.raise.context

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.NonEmptyList
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.RaiseAccumulate.Value
import arrow.core.raise.RaiseDSL
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import arrow.core.raise.accumulate as accumulateExt
import arrow.core.raise.mapOrAccumulate as mapOrAccumulateExt
import arrow.core.raise.mapValuesOrAccumulate as mapValuesOrAccumulateExt
import arrow.core.raise.zipOrAccumulate as zipOrAccumulateExt
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public typealias RaiseAccumulate<A> = arrow.core.raise.RaiseAccumulate<A>

context(raise: Raise<NonEmptyList<Error>>)
@ExperimentalRaiseAccumulateApi @RaiseDSL public inline fun <Error, A> accumulate(
  block: context(RaiseAccumulate<Error>) () -> A
): A {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return raise.accumulateExt(block)
}

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
): NonEmptyList<B> {
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return raise.mapOrAccumulateExt(this, transform)
}

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <K, Error, A, B> Map<K, A>.mapValuesOrAccumulate(
  @BuilderInference transform: context(RaiseAccumulate<Error>) (Map.Entry<K, A>) -> B
): Map<K, B> = raise.mapValuesOrAccumulateExt(this, transform)

/**
 * Accumulate the errors from running [action1] and [action2].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C> zipOrAccumulate(
  @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
  block: (A, B) -> C
): C {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return raise.zipOrAccumulateExt(action1, action2, block)
}

/**
 * Accumulate the errors from running [action1], [action2], and [action3]
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D> zipOrAccumulate(
  @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(RaiseAccumulate<Error>) () -> C,
  block: (A, B, C) -> D
): D {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(action3, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return raise.zipOrAccumulateExt(action1, action2, action3, block)
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], and [action4].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E> zipOrAccumulate(
  @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(RaiseAccumulate<Error>) () -> D,
  block: (A, B, C, D) -> E
): E {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(action3, EXACTLY_ONCE)
    callsInPlace(action4, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return raise.zipOrAccumulateExt(action1, action2, action3, action4, block)
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], and [action5].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E, F> zipOrAccumulate(
  @BuilderInference action1: context(arrow.core.raise.RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(arrow.core.raise.RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(arrow.core.raise.RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(arrow.core.raise.RaiseAccumulate<Error>) () -> D,
  @BuilderInference action5: context(arrow.core.raise.RaiseAccumulate<Error>) () -> E,
  block: (A, B, C, D, E) -> F
): F {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(action3, EXACTLY_ONCE)
    callsInPlace(action4, EXACTLY_ONCE)
    callsInPlace(action5, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return raise.zipOrAccumulateExt(action1, action2, action3, action4, action5, block)
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], and [action6].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E, F, G> zipOrAccumulate(
  @BuilderInference action1: context(arrow.core.raise.RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(arrow.core.raise.RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(arrow.core.raise.RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(arrow.core.raise.RaiseAccumulate<Error>) () -> D,
  @BuilderInference action5: context(arrow.core.raise.RaiseAccumulate<Error>) () -> E,
  @BuilderInference action6: context(arrow.core.raise.RaiseAccumulate<Error>) () -> F,
  block: (A, B, C, D, E, F) -> G
): G {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(action3, EXACTLY_ONCE)
    callsInPlace(action4, EXACTLY_ONCE)
    callsInPlace(action5, EXACTLY_ONCE)
    callsInPlace(action6, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return raise.zipOrAccumulateExt(action1, action2, action3, action4, action5, action6, block)
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], and [action7].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E, F, G, H> zipOrAccumulate(
  @BuilderInference action1: context(arrow.core.raise.RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(arrow.core.raise.RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(arrow.core.raise.RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(arrow.core.raise.RaiseAccumulate<Error>) () -> D,
  @BuilderInference action5: context(arrow.core.raise.RaiseAccumulate<Error>) () -> E,
  @BuilderInference action6: context(arrow.core.raise.RaiseAccumulate<Error>) () -> F,
  @BuilderInference action7: context(arrow.core.raise.RaiseAccumulate<Error>) () -> G,
  block: (A, B, C, D, E, F, G) -> H
): H {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(action3, EXACTLY_ONCE)
    callsInPlace(action4, EXACTLY_ONCE)
    callsInPlace(action5, EXACTLY_ONCE)
    callsInPlace(action6, EXACTLY_ONCE)
    callsInPlace(action7, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return raise.zipOrAccumulateExt(action1, action2, action3, action4, action5, action6, action7, block)
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], and [action8].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E, F, G, H, I> zipOrAccumulate(
  @BuilderInference action1: context(arrow.core.raise.RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(arrow.core.raise.RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(arrow.core.raise.RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(arrow.core.raise.RaiseAccumulate<Error>) () -> D,
  @BuilderInference action5: context(arrow.core.raise.RaiseAccumulate<Error>) () -> E,
  @BuilderInference action6: context(arrow.core.raise.RaiseAccumulate<Error>) () -> F,
  @BuilderInference action7: context(arrow.core.raise.RaiseAccumulate<Error>) () -> G,
  @BuilderInference action8: context(arrow.core.raise.RaiseAccumulate<Error>) () -> H,
  block: (A, B, C, D, E, F, G, H) -> I
): I {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(action3, EXACTLY_ONCE)
    callsInPlace(action4, EXACTLY_ONCE)
    callsInPlace(action5, EXACTLY_ONCE)
    callsInPlace(action6, EXACTLY_ONCE)
    callsInPlace(action7, EXACTLY_ONCE)
    callsInPlace(action8, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return raise.zipOrAccumulateExt(action1, action2, action3, action4, action5, action6, action7, action8, block)
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], and [action9].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E, F, G, H, I, J> zipOrAccumulate(
  @BuilderInference action1: context(arrow.core.raise.RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(arrow.core.raise.RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(arrow.core.raise.RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(arrow.core.raise.RaiseAccumulate<Error>) () -> D,
  @BuilderInference action5: context(arrow.core.raise.RaiseAccumulate<Error>) () -> E,
  @BuilderInference action6: context(arrow.core.raise.RaiseAccumulate<Error>) () -> F,
  @BuilderInference action7: context(arrow.core.raise.RaiseAccumulate<Error>) () -> G,
  @BuilderInference action8: context(arrow.core.raise.RaiseAccumulate<Error>) () -> H,
  @BuilderInference action9: context(arrow.core.raise.RaiseAccumulate<Error>) () -> I,
  block: (A, B, C, D, E, F, G, H, I) -> J
): J {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(action3, EXACTLY_ONCE)
    callsInPlace(action4, EXACTLY_ONCE)
    callsInPlace(action5, EXACTLY_ONCE)
    callsInPlace(action6, EXACTLY_ONCE)
    callsInPlace(action7, EXACTLY_ONCE)
    callsInPlace(action8, EXACTLY_ONCE)
    callsInPlace(action9, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return raise.zipOrAccumulateExt(action1, action2, action3, action4, action5, action6, action7, action8, action9, block)
}

@RaiseDSL
context(raise: RaiseAccumulate<Error>)
public fun <Error, A> EitherNel<Error, A>.bindNel(): A =
  with(raise) { this@bindNel.bindNel() }

@RaiseDSL
context(raise: RaiseAccumulate<Error>)
public inline fun <Error, A> withNel(block: context(Raise<NonEmptyList<Error>>) () -> A): A {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return raise.withNel { block() }
}

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public fun <Error, A> Either<Error, A>.bindOrAccumulate(): Value<A> =
  with(raise) { this@bindOrAccumulate.bindOrAccumulate() }

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public fun <Error, A> Iterable<Either<Error, A>>.bindAllOrAccumulate(): Value<List<A>> =
  with(raise) { this@bindAllOrAccumulate.bindAllOrAccumulate() }

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public fun <Error, A> EitherNel<Error, A>.bindNelOrAccumulate(): Value<A> =
  with(raise) { this@bindNelOrAccumulate.bindNelOrAccumulate() }

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public inline fun <Error> ensureOrAccumulate(condition: Boolean, error: () -> Error) {
  contract { callsInPlace(error, AT_MOST_ONCE) }
  with(raise) { ensureOrAccumulate(condition, error) }
}

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public inline fun <Error, B: Any> ensureNotNullOrAccumulate(value: B?, error: () -> Error): Value<B> {
  contract { callsInPlace(error, AT_MOST_ONCE) }
  return with(raise) { ensureNotNullOrAccumulate(value, error) }
}

@ExperimentalRaiseAccumulateApi @RaiseDSL
context(raise: RaiseAccumulate<Error>)
public inline fun <Error, A> accumulating(block: context(RaiseAccumulate<Error>) () -> A): Value<A> {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return with(raise) { accumulating(block) }
}
