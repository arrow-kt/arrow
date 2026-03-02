@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")
@file:Suppress("LEAKED_IN_PLACE_LAMBDA")

package arrow.core.raise.context

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.RaiseDSL
import arrow.core.raise.accumulate as accumulateExt
import arrow.core.raise.forEachAccumulating as forEachAccumulatingExt
import arrow.core.raise.mapOrAccumulate as mapOrAccumulateExt
import arrow.core.raise.zipOrAccumulate as zipOrAccumulateExt

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
@RaiseDSL public inline fun <Error, A, B> NonEmptySet<A>.mapOrAccumulate(
  @BuilderInference transform: context(RaiseAccumulate<Error>) (A) -> B
): NonEmptySet<B> {
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return raise.mapOrAccumulateExt(this, transform)
}

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL
public inline fun <Error, A> forEachAccumulating(
  iterable: Iterable<A>,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = raise.forEachAccumulatingExt(iterable, block)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL
public inline fun <Error, A> forEachAccumulating(
  sequence: Sequence<A>,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = raise.forEachAccumulatingExt(sequence, block)

context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL
public inline fun <Error, A> forEachAccumulating(
  iterator: Iterator<A>,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = raise.forEachAccumulatingExt(iterator, block)

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
