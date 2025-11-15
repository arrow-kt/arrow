@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")
@file:Suppress("LEAKED_IN_PLACE_LAMBDA")

package arrow.core.raise

import arrow.core.NonEmptyList
import arrow.core.raise.context.Raise
import kotlin.contracts.ExperimentalContracts
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import arrow.core.raise.zipOrAccumulate as zipOrAccumulateExt

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
): E = raise.zipOrAccumulateExt(action1, action2, action3, action4, block)

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], and [action5].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E, F> zipOrAccumulate(
  @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(RaiseAccumulate<Error>) () -> D,
  @BuilderInference action5: context(RaiseAccumulate<Error>) () -> E,
  block: (A, B, C, D, E) -> F
): F = raise.zipOrAccumulateExt(action1, action2, action3, action4, action5, block)

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], and [action6].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E, F, G> zipOrAccumulate(
  @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(RaiseAccumulate<Error>) () -> D,
  @BuilderInference action5: context(RaiseAccumulate<Error>) () -> E,
  @BuilderInference action6: context(RaiseAccumulate<Error>) () -> F,
  block: (A, B, C, D, E, F) -> G
): G = raise.zipOrAccumulateExt(action1, action2, action3, action4, action5, action6, block)

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], and [action7].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E, F, G, H> zipOrAccumulate(
  @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(RaiseAccumulate<Error>) () -> D,
  @BuilderInference action5: context(RaiseAccumulate<Error>) () -> E,
  @BuilderInference action6: context(RaiseAccumulate<Error>) () -> F,
  @BuilderInference action7: context(RaiseAccumulate<Error>) () -> G,
  block: (A, B, C, D, E, F, G) -> H
): H = raise.zipOrAccumulateExt(action1, action2, action3, action4, action5, action6, action7, block)

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], and [action8].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E, F, G, H, I> zipOrAccumulate(
  @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(RaiseAccumulate<Error>) () -> D,
  @BuilderInference action5: context(RaiseAccumulate<Error>) () -> E,
  @BuilderInference action6: context(RaiseAccumulate<Error>) () -> F,
  @BuilderInference action7: context(RaiseAccumulate<Error>) () -> G,
  @BuilderInference action8: context(RaiseAccumulate<Error>) () -> H,
  block: (A, B, C, D, E, F, G, H) -> I
): I = raise.zipOrAccumulateExt(action1, action2, action3, action4, action5, action6, action7, action8, block)

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], and [action9].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
context(raise: Raise<NonEmptyList<Error>>)
@RaiseDSL public inline fun <Error, A, B, C, D, E, F, G, H, I, J> zipOrAccumulate(
  @BuilderInference action1: context(RaiseAccumulate<Error>) () -> A,
  @BuilderInference action2: context(RaiseAccumulate<Error>) () -> B,
  @BuilderInference action3: context(RaiseAccumulate<Error>) () -> C,
  @BuilderInference action4: context(RaiseAccumulate<Error>) () -> D,
  @BuilderInference action5: context(RaiseAccumulate<Error>) () -> E,
  @BuilderInference action6: context(RaiseAccumulate<Error>) () -> F,
  @BuilderInference action7: context(RaiseAccumulate<Error>) () -> G,
  @BuilderInference action8: context(RaiseAccumulate<Error>) () -> H,
  @BuilderInference action9: context(RaiseAccumulate<Error>) () -> I,
  block: (A, B, C, D, E, F, G, H, I) -> J
): J = raise.zipOrAccumulateExt(action1, action2, action3, action4, action5, action6, action7, action8, action9, block)
