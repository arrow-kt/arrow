@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseKt")
package arrow.core.raise

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.Ior
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.PotentiallyUnsafeNonEmptyOperation
import arrow.core.collectionSizeOrDefault
import arrow.core.getOrElse
import arrow.core.nel
import arrow.core.raise.RaiseAccumulate.Error
import arrow.core.raise.RaiseAccumulate.Ok
import arrow.core.raise.RaiseAccumulate.Value
import arrow.core.toNonEmptyListOrNull
import arrow.core.wrapAsNonEmptyListOrNull
import arrow.core.wrapAsNonEmptySetOrThrow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic
import kotlin.reflect.KProperty

/**
 * Accumulate the errors from running both [action1] and [action2] using the given [combine] function.
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C> Raise<Error>.zipOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  block: (A, B) -> C
): C {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    combine,
    action1,
    action2,
    { }) { a, b, _ ->
    block(a, b)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], and [action3] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D> Raise<Error>.zipOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  block: (A, B, C) -> D
): D {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(action3, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    combine,
    action1,
    action2,
    action3,
    { }) { a, b, c, _ ->
    block(a, b, c)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], and [action4] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E> Raise<Error>.zipOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  block: (A, B, C, D) -> E
): E {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(action3, EXACTLY_ONCE)
    callsInPlace(action4, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    combine,
    action1,
    action2,
    action3,
    action4,
    { }) { a, b, c, d, _ ->
    block(a, b, c, d)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], and [action5] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F> Raise<Error>.zipOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
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
  return zipOrAccumulate(
    combine,
    action1,
    action2,
    action3,
    action4,
    action5,
    { }) { a, b, c, d, e, _ ->
    block(a, b, c, d, e)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], and [action6] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G> Raise<Error>.zipOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
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
  return zipOrAccumulate(
    combine,
    action1,
    action2,
    action3,
    action4,
    action5,
    action6,
    { }) { a, b, c, d, e, f, _ ->
    block(a, b, c, d, e, f)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], and [action7] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H> Raise<Error>.zipOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
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
  return zipOrAccumulate(
    combine,
    action1,
    action2,
    action3,
    action4,
    action5,
    action6,
    action7,
    { }) { a, b, c, d, e, f, g, _ ->
    block(a, b, c, d, e, f, g)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], and [action8] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I> Raise<Error>.zipOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
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
  return zipOrAccumulate(
    combine,
    action1,
    action2,
    action3,
    action4,
    action5,
    action6,
    action7,
    action8,
    { }) { a, b, c, d, e, f, g, h, _ ->
    block(a, b, c, d, e, f, g, h)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], and [action9] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J> Raise<Error>.zipOrAccumulate(
  combine: (Error, Error) -> Error,
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
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
  return withError({ it.reduce(combine) }) {
    zipOrAccumulate(action1, action2, action3, action4, action5, action6, action7, action8, action9, block)
  }
}

/**
 * Accumulate the errors from running both [action1] and [action2].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  block: (A, B) -> C
): C {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    action1,
    action2,
    {}) { a, b, _ ->
    block(a, b)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], and [action3].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  block: (A, B, C) -> D
): D {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(action3, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    action1,
    action2,
    action3,
    {}) { a, b, c, _ ->
    block(a, b, c)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], and [action4].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  block: (A, B, C, D) -> E
): E {
  contract {
    callsInPlace(action1, EXACTLY_ONCE)
    callsInPlace(action2, EXACTLY_ONCE)
    callsInPlace(action3, EXACTLY_ONCE)
    callsInPlace(action4, EXACTLY_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return zipOrAccumulate(
    action1,
    action2,
    action3,
    action4,
    {}) { a, b, c, d, _ ->
    block(a, b, c, d)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], and [action5].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
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
  return zipOrAccumulate(
    action1,
    action2,
    action3,
    action4,
    action5,
    {}) { a, b, c, d, e, _ ->
    block(a, b, c, d, e)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], and [action6].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
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
  return zipOrAccumulate(
    action1,
    action2,
    action3,
    action4,
    action5,
    action6,
    {}) { a, b, c, d, e, f, _ ->
    block(a, b, c, d, e, f)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], and [action7].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
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
  return zipOrAccumulate(
    action1,
    action2,
    action3,
    action4,
    action5,
    action6,
    action7,
    {}) { a, b, c, d, e, f, g, _ ->
    block(a, b, c, d, e, f, g)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], and [action8].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
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
  return zipOrAccumulate(
    action1,
    action2,
    action3,
    action4,
    action5,
    action6,
    action7,
    action8,
    {}) { a, b, c, d, e, f, g, h, _ ->
    block(a, b, c, d, e, f, g, h)
  }
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], and [action9].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL @OptIn(ExperimentalRaiseAccumulateApi::class)
@Suppress("WRONG_INVOCATION_KIND")
public inline fun <Error, A, B, C, D, E, F, G, H, I, J> Raise<NonEmptyList<Error>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
  @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
  @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
  @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
  @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
  @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
  @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
  @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
  @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
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
  return accumulate {
    val a = accumulating { action1() }
    val b = accumulating { action2() }
    val c = accumulating { action3() }
    val d = accumulating { action4() }
    val e = accumulating { action5() }
    val f = accumulating { action6() }
    val g = accumulating { action7() }
    val h = accumulating { action8() }
    val i = accumulating { action9() }
    block(a.value, b.value, c.value, d.value, e.value, f.value, g.value, h.value, i.value)
  }
}

@RaiseDSL
public inline fun <Error, A> Raise<Error>.forEachAccumulating(
  iterable: Iterable<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulating(iterable.iterator(), combine, block)

@RaiseDSL
public inline fun <Error, A> Raise<Error>.forEachAccumulating(
  sequence: Sequence<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulating(sequence.iterator(), combine, block)

@RaiseDSL
public inline fun <Error, A> Raise<Error>.forEachAccumulating(
  iterator: Iterator<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulatingImpl(iterator, combine) { item, _ -> block(item) }

@PublishedApi @JvmSynthetic
internal inline fun <Error, A> Raise<Error>.forEachAccumulatingImpl(
  iterator: Iterator<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference block: RaiseAccumulate<Error>.(item: A, hasErrors: Boolean) -> Unit
): Unit = withError({ it.reduce(combine) }) {
  forEachAccumulatingImpl(iterator, block)
}

@RaiseDSL
public inline fun <Error, A> Raise<NonEmptyList<Error>>.forEachAccumulating(
  iterable: Iterable<A>,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulating(iterable.iterator(), block)

@RaiseDSL
public inline fun <Error, A> Raise<NonEmptyList<Error>>.forEachAccumulating(
  sequence: Sequence<A>,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulating(sequence.iterator(), block)

@RaiseDSL
public inline fun <Error, A> Raise<NonEmptyList<Error>>.forEachAccumulating(
  iterator: Iterator<A>,
  @BuilderInference block: RaiseAccumulate<Error>.(A) -> Unit
): Unit = forEachAccumulatingImpl(iterator) { item, _ -> block(item) }

/**
 * Allows to change what to do once the first error is raised.
 * Used to provide more performant [mapOrAccumulate].
 */
@PublishedApi @JvmSynthetic @OptIn(ExperimentalRaiseAccumulateApi::class)
internal inline fun <Error, A> Raise<NonEmptyList<Error>>.forEachAccumulatingImpl(
  iterator: Iterator<A>,
  @BuilderInference block: RaiseAccumulate<Error>.(item: A, hasErrors: Boolean) -> Unit
): Unit = accumulate {
  var error: Value<Nothing>? = null
  iterator.forEach {
    error = accumulating {
      block(it, error != null)
      return@forEach // continue to next iteration since there were no errors
    }
  }
  error?.value
}

/**
 * Transform every element of [iterable] using the given [transform], or accumulate all the occurred errors using [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> Raise<Error>.mapOrAccumulate(
  iterable: Iterable<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = buildList(iterable.collectionSizeOrDefault(10)) {
  forEachAccumulatingImpl(iterable.iterator(), combine) { item, hasErrors ->
    transform(item).also { if (!hasErrors) add(it) }
  }
}

/**
 * Accumulate the errors obtained by executing the [transform] over every element of [iterable].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> Raise<NonEmptyList<Error>>.mapOrAccumulate(
  iterable: Iterable<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = buildList(iterable.collectionSizeOrDefault(10)) {
  forEachAccumulatingImpl(iterable.iterator()) { item, hasErrors ->
    transform(item).also { if (!hasErrors) add(it) }
  }
}

/**
 * Transform every element of [sequence] using the given [transform], or accumulate all the occurred errors using [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> Raise<Error>.mapOrAccumulate(
  sequence: Sequence<A>,
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = buildList {
  forEachAccumulatingImpl(sequence.iterator(), combine) { item, hasErrors ->
    transform(item).also { if (!hasErrors) add(it) }
  }
}

/**
 * Accumulate the errors obtained by executing the [transform] over every element of [sequence].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> Raise<NonEmptyList<Error>>.mapOrAccumulate(
  sequence: Sequence<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): List<B> = buildList {
  forEachAccumulatingImpl(sequence.iterator()) { item, hasErrors ->
    transform(item).also { if (!hasErrors) add(it) }
  }
}

/**
 * Accumulate the errors obtained by executing the [transform] over every element of [NonEmptyList].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@OptIn(ExperimentalRaiseAccumulateApi::class)
@RaiseDSL
@Suppress("WRONG_INVOCATION_KIND")
public inline fun <Error, A, B> Raise<NonEmptyList<Error>>.mapOrAccumulate(
  nonEmptyList: NonEmptyList<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): NonEmptyList<B> {
  // For a NonEmptyList to be returned, there must be a B, which can only be produced by transform
  // thus transform must be called at least once (or alternatively an error is raised or an exception is thrown etc)
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return mapOrAccumulate(nonEmptyList.all) { transform(it) }.let(::NonEmptyList)
}

/**
 * Accumulate the errors obtained by executing the [transform] over every element of [NonEmptySet].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
@RaiseDSL
@Suppress("WRONG_INVOCATION_KIND")
public inline fun <Error, A, B> Raise<NonEmptyList<Error>>.mapOrAccumulate(
  nonEmptySet: NonEmptySet<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): NonEmptySet<B> {
  contract { callsInPlace(transform, AT_LEAST_ONCE) }
  return buildSet(nonEmptySet.size) {
    forEachAccumulatingImpl(nonEmptySet.iterator()) { item, hasErrors ->
      transform(item).also { if (!hasErrors) add(it) }
    }
  }.wrapAsNonEmptySetOrThrow()
}

@RaiseDSL
@Deprecated(
  message = "Deprecated to allow for future alignment with stdlib Map#map returning List",
  replaceWith = ReplaceWith("mapValuesOrAccumulate(map, combine, transform)"),
)
public inline fun <K, Error, A, B> Raise<Error>.mapOrAccumulate(
  map: Map<K, A>,
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(Map.Entry<K, A>) -> B
): Map<K, B> = mapValuesOrAccumulate(map, combine, transform)

@RaiseDSL
@Deprecated(
  message = "Deprecated to allow for future alignment with stdlib Map#map returning List",
  replaceWith = ReplaceWith("mapValuesOrAccumulate(map, transform)")
)
public inline fun <K, Error, A, B> Raise<NonEmptyList<Error>>.mapOrAccumulate(
  map: Map<K, A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(Map.Entry<K, A>) -> B
): Map<K, B> = mapValuesOrAccumulate(map, transform)

@RaiseDSL
public inline fun <K, Error, A, B> Raise<Error>.mapValuesOrAccumulate(
  map: Map<K, A>,
  combine: (Error, Error) -> Error,
  @BuilderInference transform: RaiseAccumulate<Error>.(Map.Entry<K, A>) -> B
): Map<K, B> = buildMap(map.size) {
  forEachAccumulatingImpl(map.entries.iterator(), combine) { item, hasErrors ->
    transform(item).also { if (!hasErrors) put(item.key, it) }
  }
}

@RaiseDSL
public inline fun <K, Error, A, B> Raise<NonEmptyList<Error>>.mapValuesOrAccumulate(
  map: Map<K, A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(Map.Entry<K, A>) -> B
): Map<K, B> = buildMap(map.size) {
  forEachAccumulatingImpl(map.entries.iterator()) { item, hasErrors ->
    transform(item).also { if (!hasErrors) put(item.key, it) }
  }
}

@RequiresOptIn(level = RequiresOptIn.Level.WARNING, message = "This API is work-in-progress and is subject to change.")
@Retention(AnnotationRetention.BINARY)
@Target(
  AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CONSTRUCTOR,
  AnnotationTarget.CLASS
)
public annotation class ExperimentalRaiseAccumulateApi

@ExperimentalRaiseAccumulateApi
public inline fun <Error, A> Raise<NonEmptyList<Error>>.accumulate(
  block: RaiseAccumulate<Error>.() -> A
): A {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  val (raiseAccumulate, raiseErrorsIfAvailable) = RaiseAccumulate()
  val result = block(raiseAccumulate)
  raiseErrorsIfAvailable()
  return result
}

@ExperimentalRaiseAccumulateApi
public inline fun <Error, A, R> accumulate(
  raise: (Raise<NonEmptyList<Error>>.() -> A) -> R,
  crossinline block: RaiseAccumulate<Error>.() -> A
): R {
  contract {
    callsInPlace(raise, EXACTLY_ONCE)
  }
  return raise { accumulate(block) }
}

/**
 * Receiver type belonging to [mapOrAccumulate].
 * Allows binding both [Either] and [EitherNel] values for [Either.Left] types of [Error].
 * It extends [Raise] of [Error], and allows working over [Raise] of [NonEmptyList] of [Error] as well.
 */
@OptIn(ExperimentalSubclassOptIn::class, ExperimentalRaiseAccumulateApi::class)
@Suppress("DEPRECATION")
@SubclassOptInRequired(ExperimentalRaiseAccumulateApi::class)
public open class RaiseAccumulate<Error> @ExperimentalRaiseAccumulateApi constructor(
  private val accumulate: Accumulate<Error>,
  @Deprecated("use withNel instead", level = DeprecationLevel.WARNING)
  public val raise: Raise<NonEmptyList<Error>>,
  private val raiseErrorsWith: (Error) -> Nothing
) : Accumulate<Error> by accumulate, Raise<Error> {
  @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
  @ExperimentalRaiseAccumulateApi
  public constructor(accumulate: Accumulate<Error>, raiseErrorsWith: (Error) -> Nothing): this(accumulate, RaiseNel(accumulate), raiseErrorsWith)

  @OptIn(ExperimentalRaiseAccumulateApi::class)
  public constructor(raise: Raise<NonEmptyList<Error>>) : this(ListAccumulate(raise))

  @ExperimentalRaiseAccumulateApi
  internal constructor(listAccumulate: ListAccumulate<Error>) : this(listAccumulate, listAccumulate, listAccumulate::raiseSingle)

  override fun raise(r: Error): Nothing = raiseErrorsWith(r)

  public override fun <K, A> Map<K, Either<Error, A>>.bindAll(): Map<K, A> =
    raise.mapValuesOrAccumulate(this) { it.value.bind() }

  @RaiseDSL
  public inline fun <A, B> Iterable<A>.mapOrAccumulate(
    transform: RaiseAccumulate<Error>.(A) -> B
  ): List<B> = raise.mapOrAccumulate(this, transform)

  @RaiseDSL
  public inline fun <A, B> NonEmptyList<A>.mapOrAccumulate(
    transform: RaiseAccumulate<Error>.(A) -> B
  ): NonEmptyList<B> {
    contract { callsInPlace(transform, AT_LEAST_ONCE) }
    return raise.mapOrAccumulate(this, transform)
  }

  @RaiseDSL
  public inline fun <A, B> NonEmptySet<A>.mapOrAccumulate(
    transform: RaiseAccumulate<Error>.(A) -> B
  ): NonEmptySet<B> {
    contract { callsInPlace(transform, AT_LEAST_ONCE) }
    return raise.mapOrAccumulate(this, transform)
  }

  @RaiseDSL
  public inline fun <K, A, B> Map<K, A>.mapOrAccumulate(
    transform: RaiseAccumulate<Error>.(Map.Entry<K, A>) -> B
  ): List<B> = raise.mapOrAccumulate(entries, transform)

  @RaiseDSL
  public inline fun <K, A, B> Map<K, A>.mapValuesOrAccumulate(
    transform: RaiseAccumulate<Error>.(Map.Entry<K, A>) -> B
  ): Map<K, B> = raise.mapValuesOrAccumulate(this, transform)

  @RaiseDSL
  @JvmName("_mapOrAccumulate")
  public inline fun <A, B> mapOrAccumulate(
    iterable: Iterable<A>,
    transform: RaiseAccumulate<Error>.(A) -> B
  ): List<B> = raise.mapOrAccumulate(iterable, transform)

  @RaiseDSL
  @JvmName("_mapOrAccumulate")
  public inline fun <A, B> mapOrAccumulate(
    list: NonEmptyList<A>,
    transform: RaiseAccumulate<Error>.(A) -> B
  ): NonEmptyList<B> {
    contract { callsInPlace(transform, AT_LEAST_ONCE) }
    return raise.mapOrAccumulate(list, transform)
  }

  @RaiseDSL
  @JvmName("_mapOrAccumulate")
  public inline fun <A, B> mapOrAccumulate(
    set: NonEmptySet<A>,
    transform: RaiseAccumulate<Error>.(A) -> B
  ): NonEmptySet<B> {
    contract { callsInPlace(transform, AT_LEAST_ONCE) }
    return raise.mapOrAccumulate(set, transform)
  }

  @RaiseDSL
  override fun <A> Iterable<Either<Error, A>>.bindAll(): List<A> =
    mapOrAccumulate { it.bind() }

  override fun <A> NonEmptyList<Either<Error, A>>.bindAll(): NonEmptyList<A> =
    mapOrAccumulate { it.bind() }

  override fun <A> NonEmptySet<Either<Error, A>>.bindAll(): NonEmptySet<A> =
    mapOrAccumulate { it.bind() }

  @RaiseDSL
  public fun <A> EitherNel<Error, A>.bindNel(): A = with(raise) { bind() }

  @RaiseDSL
  public inline fun <A> withNel(block: Raise<NonEmptyList<Error>>.() -> A): A {
    contract {
      callsInPlace(block, EXACTLY_ONCE)
    }
    return block(raise)
  }

  @PublishedApi
  @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
  internal fun addErrors(newErrors: Iterable<Error>) {
    newErrors.toNonEmptyListOrNull()?.let(::accumulateAll)
  }

  private val underlyingListAccumulate: ListAccumulate<Error> get() = accumulate as? ListAccumulate<Error>
    ?: error("Underlying Accumulate is not a ListAccumulate. " +
      "This should never happen since it is only called by " +
      "old inlined bytecode from accumulate, which always uses ListAccumulate.")

  @PublishedApi
  @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
  internal fun hasErrors(): Boolean = underlyingListAccumulate.hasErrors()

  @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
  override val hasAccumulatedErrors: Boolean
    get() = underlyingListAccumulate.hasErrors()

  @PublishedApi
  @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
  internal fun raiseErrors(): Nothing = underlyingListAccumulate.raiseErrors()

  @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
  override val latestError: Value<Nothing>?
    get() = if (underlyingListAccumulate.hasErrors()) Error { underlyingListAccumulate.raiseErrors() } else null

  @Suppress("NOTHING_TO_INLINE")
  @Deprecated(message = "Deprecated in favor of member", level = DeprecationLevel.HIDDEN)
  public inline operator fun <A> Value<A>.getValue(thisRef: Nothing?, property: KProperty<*>): A = value

  public sealed class Value<out A> {
    public abstract val value: A

    @Suppress("NOTHING_TO_INLINE")
    public inline operator fun getValue(thisRef: Nothing?, property: KProperty<*>): A = value
  }

  @PublishedApi
  internal class Error(private val raise: () -> Nothing) : Value<Nothing>() {
    @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
    constructor(raiseAccumulate: RaiseAccumulate<*>) : this({
      raiseAccumulate.underlyingListAccumulate.raiseErrors()
    })
    // WARNING: do not turn this into a property with initializer!!
    //          'raiseErrors' is then executed eagerly, and leads to wrong behavior!!
    override val value get(): Nothing = raise()
  }

  @PublishedApi internal class Ok<out A>(override val value: A): Value<A>()


  @ExperimentalRaiseAccumulateApi
  public inline fun <A> accumulating(block: RaiseAccumulate<Error>.() -> A): Value<A> {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    return (this as Accumulate<Error>).accumulating(block)
  }

  @ExperimentalRaiseAccumulateApi
  @RaiseDSL
  public inline fun <A> recover(
    block: RaiseAccumulate<Error>.() -> A,
    recover: (error: Error) -> A,
  ): A {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val accumulate = this
    return arrow.core.raise.recover({
      withNel {
        block(RaiseAccumulate(accumulate, this, ::raise))
      }
    }, recover)
  }

  @ExperimentalRaiseAccumulateApi
  public inline fun ensureOrAccumulate(condition: Boolean, raise: () -> Error): Value<Unit> {
    contract { callsInPlace(raise, AT_MOST_ONCE) }
    return (this as Accumulate<Error>).ensureOrAccumulate(condition, raise)
  }

  @ExperimentalRaiseAccumulateApi
  public inline fun <B : Any> ensureNotNullOrAccumulate(value: B?, raise: () -> Error): Value<B> {
    contract { callsInPlace(raise, AT_MOST_ONCE) }
    return (this as Accumulate<Error>).ensureNotNullOrAccumulate(value, raise)
  }

  // IorRaise methods
  @RaiseDSL
  @JvmName("bindAllIor")
  @ExperimentalRaiseAccumulateApi
  public fun <A> Iterable<Ior<Error, A>>.bindAll(): List<A> =
    mapOrAccumulate { it.bind() }

  @RaiseDSL
  @JvmName("bindAllIor")
  @ExperimentalRaiseAccumulateApi
  public fun <A> NonEmptyList<Ior<Error, A>>.bindAll(): NonEmptyList<A> =
    mapOrAccumulate { it.bind() }

  @RaiseDSL
  @JvmName("bindAllIor")
  @ExperimentalRaiseAccumulateApi
  public fun <A> NonEmptySet<Ior<Error, A>>.bindAll(): NonEmptySet<A> =
    mapOrAccumulate { it.bind() }

  @RaiseDSL
  @ExperimentalRaiseAccumulateApi
  public fun <A> Ior<Error, A>.bind(): A =
    when (this) {
      is Ior.Left -> raise(value)
      is Ior.Right -> value
      is Ior.Both -> {
        accumulate(leftValue)
        rightValue
      }
    }

  @JvmName("bindAllIor")
  @ExperimentalRaiseAccumulateApi
  public fun <K, V> Map<K, Ior<Error, V>>.bindAll(): Map<K, V> =
    mapValuesOrAccumulate { (_, v) -> v.bind() }
}

@ExperimentalRaiseAccumulateApi
private class RaiseNel<Error>(private val accumulate: Accumulate<Error>) : Raise<NonEmptyList<Error>> {
  override fun raise(r: NonEmptyList<Error>): Nothing {
    accumulate.accumulateAll(r).value
  }
}

@OptIn(PotentiallyUnsafeNonEmptyOperation::class)
@ExperimentalRaiseAccumulateApi
@PublishedApi
internal fun <Error> Raise<NonEmptyList<Error>>.RaiseAccumulate(): Pair<RaiseAccumulate<Error>, () -> Unit> {
  val errors = mutableListOf<Error>()
  return RaiseAccumulate(this, errors) to { errors.wrapAsNonEmptyListOrNull()?.let(::raise) }
}

@ExperimentalRaiseAccumulateApi
private class ListAccumulate<Error>(private val raise: Raise<NonEmptyList<Error>>) : Accumulate<Error>, Raise<NonEmptyList<Error>> {
  private val list: MutableList<Error> = mutableListOf()

  fun raiseSingle(r: Error): Nothing = raise.raise(NonEmptyList(list + r))
  override fun raise(r: NonEmptyList<Error>) = raise.raise(NonEmptyList(list + r.all))

  // only valid if list is not empty
  // errors are never removed from `list`, so once this is valid, it stays valid
  private val error = Error { raise.raise(NonEmptyList(list)) }

  override fun accumulateAll(errors: NonEmptyList<Error>): Value<Nothing> {
    list.addAll(errors)
    return error
  }

  fun hasErrors() = list.isNotEmpty()
  fun raiseErrors(): Nothing = error.value
}

@ExperimentalRaiseAccumulateApi
private class TolerantAccumulate<Error>(
  private val underlying: Accumulate<Error>,
  private val raise: Raise<Value<Nothing>>
) : Accumulate<Error> {
  override fun accumulateAll(errors: NonEmptyList<Error>): Value<Nothing> {
    val error = underlying.accumulateAll(errors)
    return Error { raise.raise(error) }
  }
}

@ExperimentalRaiseAccumulateApi
@PublishedApi internal fun <Error> Accumulate<Error>.tolerant(raise: Raise<Value<Nothing>>): Accumulate<Error> =
  TolerantAccumulate(this, raise)

@Suppress("NOTHING_TO_INLINE")
public inline operator fun <A> Value<A>.getValue(thisRef: Nothing?, property: KProperty<*>): A = value

@ExperimentalRaiseAccumulateApi
public interface Accumulate<Error> {
  @ExperimentalRaiseAccumulateApi
  public fun accumulate(error: Error): Value<Nothing> = accumulateAll(error.nel())

  @ExperimentalRaiseAccumulateApi
  public fun accumulateAll(errors: NonEmptyList<Error>): Value<Nothing>

  @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
  @ExperimentalRaiseAccumulateApi
  public val hasAccumulatedErrors: Boolean get() = false

  @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
  @ExperimentalRaiseAccumulateApi
  public val latestError: Value<Nothing>? get() = null

  @ExperimentalRaiseAccumulateApi
  public fun <A> Either<Error, A>.bindOrAccumulate(): Value<A> = accumulating { bind() }

  @ExperimentalRaiseAccumulateApi
  public fun <A> Iterable<Either<Error, A>>.bindAllOrAccumulate(): Value<List<A>> = accumulating { bindAll() }

  @ExperimentalRaiseAccumulateApi
  public fun <A> EitherNel<Error, A>.bindNelOrAccumulate(): Value<A> = accumulating { bindNel() }

  // IorRaise methods
  @ExperimentalRaiseAccumulateApi
  public fun <A> Either<Error, A>.getOrAccumulate(recover: (Error) -> A): A = getOrElse {
    accumulate(it)
    recover(it)
  }
}

@ExperimentalRaiseAccumulateApi
public inline fun <Error, A> Accumulate<Error>.accumulating(block: RaiseAccumulate<Error>.() -> A): Value<A> {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return merge {
    recover({ Ok(block(RaiseAccumulate(tolerant(this@merge), this) { raise(it.nel()) })) }, ::accumulateAll)
  }
}

@ExperimentalRaiseAccumulateApi
public inline fun <Error> Accumulate<Error>.ensureOrAccumulate(condition: Boolean, raise: () -> Error): Value<Unit> {
  contract { callsInPlace(raise, AT_MOST_ONCE) }
  return if (condition) Ok(Unit) else accumulate(raise())
}

@ExperimentalRaiseAccumulateApi
public inline fun <Error, B : Any> Accumulate<Error>.ensureNotNullOrAccumulate(value: B?, raise: () -> Error): Value<B> {
  contract { callsInPlace(raise, AT_MOST_ONCE) }
  return if (value != null) Ok(value) else accumulate(raise())
}
