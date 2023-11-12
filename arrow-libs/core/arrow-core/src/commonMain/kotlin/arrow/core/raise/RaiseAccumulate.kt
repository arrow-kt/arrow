@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseKt")
package arrow.core.raise

import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.EmptyValue
import arrow.core.EmptyValue.combine
import arrow.core.EmptyValue.unbox
import arrow.core.NonEmptyList
import arrow.core.NonEmptySet
import arrow.core.Validated
import arrow.core.ValidatedDeprMsg
import arrow.core.collectionSizeOrDefault
import arrow.core.ValidatedNel
import arrow.core.mapOrAccumulate
import arrow.core.nonEmptyListOf
import arrow.core.toNonEmptyListOrNull
import arrow.core.toNonEmptySetOrNull
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  var error: Any? = EmptyValue
  val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
  val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
  val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
  val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
  val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
  val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
  val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
  val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
  val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
  return if (error !== EmptyValue) raise(unbox<Error>(error))
  else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], and [action10] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K> Raise<Error>.zipOrAccumulate(
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
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    block: (A, B, C, D, E, F, G, H, I, J) -> K
): K {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    var error: Any? = EmptyValue
    val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    return if (error !== EmptyValue) raise(unbox<Error>(error))
    else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], and [action11] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L> Raise<Error>.zipOrAccumulate(
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
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    block: (A, B, C, D, E, F, G, H, I, J, K) -> L
): L {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    var error: Any? = EmptyValue
    val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    return if (error !== EmptyValue) raise(unbox<Error>(error))
    else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], and [action12] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M> Raise<Error>.zipOrAccumulate(
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
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    block: (A, B, C, D, E, F, G, H, I, J, K, L) -> M
): M {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    var error: Any? = EmptyValue
    val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    return if (error !== EmptyValue) raise(unbox<Error>(error))
    else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], and [action13] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N> Raise<Error>.zipOrAccumulate(
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
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> N
): N {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    var error: Any? = EmptyValue
    val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    return if (error !== EmptyValue) raise(unbox<Error>(error))
    else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], and [action14] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Raise<Error>.zipOrAccumulate(
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
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> O
): O {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    var error: Any? = EmptyValue
    val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    return if (error !== EmptyValue) raise(unbox<Error>(error))
    else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], and [action15] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Raise<Error>.zipOrAccumulate(
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
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> P
): P {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    var error: Any? = EmptyValue
    val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    return if (error !== EmptyValue) raise(unbox<Error>(error))
    else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], [action15], and [action16] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Raise<Error>.zipOrAccumulate(
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
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    @BuilderInference action16: RaiseAccumulate<Error>.() -> P,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Q
): Q {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    var error: Any? = EmptyValue
    val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val p = recover({ action16(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    return if (error !== EmptyValue) raise(unbox<Error>(error))
    else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o), unbox(p))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], [action15], and [action16] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Raise<Error>.zipOrAccumulate(
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
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    @BuilderInference action16: RaiseAccumulate<Error>.() -> P,
    @BuilderInference action17: RaiseAccumulate<Error>.() -> Q,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> R
): R {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    var error: Any? = EmptyValue
    val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val p = recover({ action16(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val q = recover({ action17(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    return if (error !== EmptyValue) raise(unbox<Error>(error))
    else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o), unbox(p), unbox(q))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], [action15], [action16], [action17], and [action18] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Raise<Error>.zipOrAccumulate(
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
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    @BuilderInference action16: RaiseAccumulate<Error>.() -> P,
    @BuilderInference action17: RaiseAccumulate<Error>.() -> Q,
    @BuilderInference action18: RaiseAccumulate<Error>.() -> R,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) -> S
): S {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    var error: Any? = EmptyValue
    val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val p = recover({ action16(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val q = recover({ action17(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val r = recover({ action18(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    return if (error !== EmptyValue) raise(unbox<Error>(error))
    else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o), unbox(p), unbox(q), unbox(r))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], [action15], [action16], [action17], [action18], and [action19] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Raise<Error>.zipOrAccumulate(
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
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    @BuilderInference action16: RaiseAccumulate<Error>.() -> P,
    @BuilderInference action17: RaiseAccumulate<Error>.() -> Q,
    @BuilderInference action18: RaiseAccumulate<Error>.() -> R,
    @BuilderInference action19: RaiseAccumulate<Error>.() -> S,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> T
): T {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    var error: Any? = EmptyValue
    val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val p = recover({ action16(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val q = recover({ action17(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val r = recover({ action18(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val s = recover({ action19(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    return if (error !== EmptyValue) raise(unbox<Error>(error))
    else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o), unbox(p), unbox(q), unbox(r), unbox(s))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], [action15], [action16], [action17], [action18], [action19], and [action20] using the given [combine].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Raise<Error>.zipOrAccumulate(
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
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    @BuilderInference action16: RaiseAccumulate<Error>.() -> P,
    @BuilderInference action17: RaiseAccumulate<Error>.() -> Q,
    @BuilderInference action18: RaiseAccumulate<Error>.() -> R,
    @BuilderInference action19: RaiseAccumulate<Error>.() -> S,
    @BuilderInference action20: RaiseAccumulate<Error>.() -> T,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) -> U
): U {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    var error: Any? = EmptyValue
    val a = recover({ action1(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val p = recover({ action16(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val q = recover({ action17(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val r = recover({ action18(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val s = recover({ action19(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    val t = recover({ action20(RaiseAccumulate(this)) }) { error = combine(error, it.reduce(combine), combine); EmptyValue }
    return if (error !== EmptyValue) raise(unbox<Error>(error))
    else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o), unbox(p), unbox(q), unbox(r), unbox(s), unbox(t))
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
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
@RaiseDSL
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
  contract { callsInPlace(block, AT_MOST_ONCE) }
  val error: MutableList<Error> = mutableListOf()
  val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
  error.toNonEmptyListOrNull()?.let { raise(it) }
  return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9] and [action10].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    block: (A, B, C, D, E, F, G, H, I, J) -> K
): K {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val error: MutableList<Error> = mutableListOf()
    val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    error.toNonEmptyListOrNull()?.let { raise(it) }
    return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10] and [action11].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    block: (A, B, C, D, E, F, G, H, I, J, K) -> L
): L {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val error: MutableList<Error> = mutableListOf()
    val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    error.toNonEmptyListOrNull()?.let { raise(it) }
    return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], and [action12].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    block: (A, B, C, D, E, F, G, H, I, J, K, L) -> M
): M {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val error: MutableList<Error> = mutableListOf()
    val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    error.toNonEmptyListOrNull()?.let { raise(it) }
    return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], and [action13].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M) -> N
): N {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val error: MutableList<Error> = mutableListOf()
    val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    error.toNonEmptyListOrNull()?.let { raise(it) }
    return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], and [action14].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N) -> O
): O {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val error: MutableList<Error> = mutableListOf()
    val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    error.toNonEmptyListOrNull()?.let { raise(it) }
    return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], and [action15].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O) -> P
): P {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val error: MutableList<Error> = mutableListOf()
    val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    error.toNonEmptyListOrNull()?.let { raise(it) }
    return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], [action15], and [action16].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    @BuilderInference action16: RaiseAccumulate<Error>.() -> P,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P) -> Q
): Q {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val error: MutableList<Error> = mutableListOf()
    val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val p = recover({ action16(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    error.toNonEmptyListOrNull()?.let { raise(it) }
    return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o), unbox(p))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], [action15], [action16], and [action17].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    @BuilderInference action16: RaiseAccumulate<Error>.() -> P,
    @BuilderInference action17: RaiseAccumulate<Error>.() -> Q,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q) -> R
): R {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val error: MutableList<Error> = mutableListOf()
    val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val p = recover({ action16(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val q = recover({ action17(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    error.toNonEmptyListOrNull()?.let { raise(it) }
    return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o), unbox(p), unbox(q))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], [action15], [action16], [action17], and [action18].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    @BuilderInference action16: RaiseAccumulate<Error>.() -> P,
    @BuilderInference action17: RaiseAccumulate<Error>.() -> Q,
    @BuilderInference action18: RaiseAccumulate<Error>.() -> R,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R) -> S
): S {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val error: MutableList<Error> = mutableListOf()
    val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val p = recover({ action16(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val q = recover({ action17(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val r = recover({ action18(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    error.toNonEmptyListOrNull()?.let { raise(it) }
    return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o), unbox(p), unbox(q), unbox(r))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], [action15], [action16], [action17], [action18], and [action19].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    @BuilderInference action16: RaiseAccumulate<Error>.() -> P,
    @BuilderInference action17: RaiseAccumulate<Error>.() -> Q,
    @BuilderInference action18: RaiseAccumulate<Error>.() -> R,
    @BuilderInference action19: RaiseAccumulate<Error>.() -> S,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S) -> T
): T {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val error: MutableList<Error> = mutableListOf()
    val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val p = recover({ action16(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val q = recover({ action17(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val r = recover({ action18(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val s = recover({ action19(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    error.toNonEmptyListOrNull()?.let { raise(it) }
    return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o), unbox(p), unbox(q), unbox(r), unbox(s))
}

/**
 * Accumulate the errors from running [action1], [action2], [action3], [action4], [action5], [action6], [action7], [action8], [action9], [action10], [action11], [action12], [action13], [action14], [action15], [action16], [action17], [action18], [action19] and [action20].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U> Raise<NonEmptyList<Error>>.zipOrAccumulate(
    @BuilderInference action1: RaiseAccumulate<Error>.() -> A,
    @BuilderInference action2: RaiseAccumulate<Error>.() -> B,
    @BuilderInference action3: RaiseAccumulate<Error>.() -> C,
    @BuilderInference action4: RaiseAccumulate<Error>.() -> D,
    @BuilderInference action5: RaiseAccumulate<Error>.() -> E,
    @BuilderInference action6: RaiseAccumulate<Error>.() -> F,
    @BuilderInference action7: RaiseAccumulate<Error>.() -> G,
    @BuilderInference action8: RaiseAccumulate<Error>.() -> H,
    @BuilderInference action9: RaiseAccumulate<Error>.() -> I,
    @BuilderInference action10: RaiseAccumulate<Error>.() -> J,
    @BuilderInference action11: RaiseAccumulate<Error>.() -> K,
    @BuilderInference action12: RaiseAccumulate<Error>.() -> L,
    @BuilderInference action13: RaiseAccumulate<Error>.() -> M,
    @BuilderInference action14: RaiseAccumulate<Error>.() -> N,
    @BuilderInference action15: RaiseAccumulate<Error>.() -> O,
    @BuilderInference action16: RaiseAccumulate<Error>.() -> P,
    @BuilderInference action17: RaiseAccumulate<Error>.() -> Q,
    @BuilderInference action18: RaiseAccumulate<Error>.() -> R,
    @BuilderInference action19: RaiseAccumulate<Error>.() -> S,
    @BuilderInference action20: RaiseAccumulate<Error>.() -> T,
    block: (A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T) -> U
): U {
    contract { callsInPlace(block, AT_MOST_ONCE) }
    val error: MutableList<Error> = mutableListOf()
    val a = recover({ action1(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val b = recover({ action2(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val c = recover({ action3(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val d = recover({ action4(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val e = recover({ action5(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val f = recover({ action6(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val g = recover({ action7(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val h = recover({ action8(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val i = recover({ action9(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val j = recover({ action10(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val k = recover({ action11(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val l = recover({ action12(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val m = recover({ action13(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val n = recover({ action14(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val o = recover({ action15(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val p = recover({ action16(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val q = recover({ action17(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val r = recover({ action18(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val s = recover({ action19(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    val t = recover({ action20(RaiseAccumulate(this)) }) { error.addAll(it); EmptyValue }
    error.toNonEmptyListOrNull()?.let { raise(it) }
    return block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i), unbox(j), unbox(k), unbox(l), unbox(m), unbox(n), unbox(o), unbox(p), unbox(q), unbox(r), unbox(s), unbox(t))
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
): List<B> {
  var error: Any? = EmptyValue
  val results = ArrayList<B>(iterable.collectionSizeOrDefault(10))
  for (item in iterable) {
    fold<NonEmptyList<Error>, B, Unit>(
      { transform(RaiseAccumulate(this), item) },
      { errors -> error = combine(error, errors.reduce(combine), combine) },
      { results.add(it) }
    )
  }
  return if (error === EmptyValue) results else raise(unbox<Error>(error))
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
): List<B> {
  val error = mutableListOf<Error>()
  val results = ArrayList<B>(iterable.collectionSizeOrDefault(10))
  for (item in iterable) {
    fold<NonEmptyList<Error>, B, Unit>(
      { transform(RaiseAccumulate(this), item) },
      { errors -> error.addAll(errors) },
      { results.add(it) }
    )
  }
  return error.toNonEmptyListOrNull()?.let { raise(it) } ?: results
}

/**
 * Accumulate the errors obtained by executing the [transform] over every element of [NonEmptyList].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> Raise<NonEmptyList<Error>>.mapOrAccumulate(
  nonEmptyList: NonEmptyList<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): NonEmptyList<B> = requireNotNull(mapOrAccumulate(nonEmptyList.all, transform).toNonEmptyListOrNull())

/**
 * Accumulate the errors obtained by executing the [transform] over every element of [NonEmptySet].
 *
 * See the Arrow docs for more information over
 * [error accumulation](https://arrow-kt.io/learn/typed-errors/working-with-typed-errors/#accumulating-errors)
 * and how to use it in [validation](https://arrow-kt.io/learn/typed-errors/validation/).
 */
@RaiseDSL
public inline fun <Error, A, B> Raise<NonEmptyList<Error>>.mapOrAccumulate(
  nonEmptySet: NonEmptySet<A>,
  @BuilderInference transform: RaiseAccumulate<Error>.(A) -> B
): NonEmptySet<B> {
  val error = mutableListOf<Error>()
  val results = HashSet<B>(nonEmptySet.collectionSizeOrDefault(10))
  for (item in nonEmptySet) {
    fold<NonEmptyList<Error>, B, Unit>(
      { transform(RaiseAccumulate(this), item) },
      { errors -> error.addAll(errors) },
      { results.add(it) }
    )
  }
  return error.toNonEmptyListOrNull()?.let { raise(it) } ?: requireNotNull(results.toNonEmptySetOrNull())
}

/**
 * Receiver type belonging to [mapOrAccumulate].
 * Allows binding both [Either] and [EitherNel] values for [Either.Left] types of [Error].
 * It extends [Raise] of [Error], and allows working over [Raise] of [NonEmptyList] of [Error] as well.
 */
public open class RaiseAccumulate<Error>(
  public val raise: Raise<NonEmptyList<Error>>
) : Raise<Error> {

  @RaiseDSL
  public override fun raise(r: Error): Nothing =
    raise.raise(nonEmptyListOf(r))

  public override fun <K, A> Map<K, Either<Error, A>>.bindAll(): Map<K, A> =
    mapOrAccumulate { (_, a) -> a.bind() }.bindNel()

  @RaiseDSL
  public inline fun <A, B> Iterable<A>.mapOrAccumulate(
    transform: RaiseAccumulate<Error>.(A) -> B
  ): List<B> = raise.mapOrAccumulate(this, transform)

  @RaiseDSL
  public inline fun <A, B> NonEmptyList<A>.mapOrAccumulate(
    transform: RaiseAccumulate<Error>.(A) -> B
  ): NonEmptyList<B> = raise.mapOrAccumulate(this, transform)

  @RaiseDSL
  public inline fun <A, B> NonEmptySet<A>.mapOrAccumulate(
    transform: RaiseAccumulate<Error>.(A) -> B
  ): NonEmptySet<B> = raise.mapOrAccumulate(this, transform)

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
  ): NonEmptyList<B> = raise.mapOrAccumulate(list, transform)

  @RaiseDSL
  @JvmName("_mapOrAccumulate")
  public inline fun <A, B> mapOrAccumulate(
    set: NonEmptySet<A>,
    transform: RaiseAccumulate<Error>.(A) -> B
  ): NonEmptySet<B> = raise.mapOrAccumulate(set, transform)

  @RaiseDSL
  override fun <A> Iterable<Either<Error, A>>.bindAll(): List<A> =
    mapOrAccumulate { it.bind() }

  override fun <A> NonEmptyList<Either<Error, A>>.bindAll(): NonEmptyList<A> =
    mapOrAccumulate { it.bind() }

  override fun <A> NonEmptySet<Either<Error, A>>.bindAll(): NonEmptySet<A> =
    mapOrAccumulate { it.bind() }

  @RaiseDSL
  public fun <A> EitherNel<Error, A>.bindNel(): A = when (this) {
    is Either.Left -> raise.raise(value)
    is Either.Right -> value
  }

  @Deprecated(ValidatedDeprMsg, ReplaceWith("toEither().bindNel()"))
  @RaiseDSL
  public fun <A> ValidatedNel<Error, A>.bindNel(): A = when (this) {
    is Validated.Invalid -> raise.raise(value)
    is Validated.Valid -> value
  }

  @RaiseDSL
  public inline fun <A> withNel(block: Raise<NonEmptyList<Error>>.() -> A): A =
    block(raise)
}
