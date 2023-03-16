@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseKt")
package arrow.core.raise

import arrow.core.mapOrAccumulate
import arrow.core.Either
import arrow.core.EitherNel
import arrow.core.EmptyValue
import arrow.core.EmptyValue.combine
import arrow.core.EmptyValue.unbox
import arrow.core.NonEmptyList
import arrow.core.Validated
import arrow.core.collectionSizeOrDefault
import arrow.core.ValidatedNel
import arrow.core.nonEmptyListOf
import arrow.core.toNonEmptyListOrNull
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


/**
 * Accumulate the errors from running both [action1] and [action2] using the given [combine] function.
 */
@RaiseDSL
public inline fun <R, A, B, C> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
  @BuilderInference action5: RaiseAccumulate<R>.() -> E,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
  @BuilderInference action5: RaiseAccumulate<R>.() -> E,
  @BuilderInference action6: RaiseAccumulate<R>.() -> F,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
  @BuilderInference action5: RaiseAccumulate<R>.() -> E,
  @BuilderInference action6: RaiseAccumulate<R>.() -> F,
  @BuilderInference action7: RaiseAccumulate<R>.() -> G,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H, I> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
  @BuilderInference action5: RaiseAccumulate<R>.() -> E,
  @BuilderInference action6: RaiseAccumulate<R>.() -> F,
  @BuilderInference action7: RaiseAccumulate<R>.() -> G,
  @BuilderInference action8: RaiseAccumulate<R>.() -> H,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H, I, J> Raise<R>.zipOrAccumulate(
  combine: (R, R) -> R,
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
  @BuilderInference action5: RaiseAccumulate<R>.() -> E,
  @BuilderInference action6: RaiseAccumulate<R>.() -> F,
  @BuilderInference action7: RaiseAccumulate<R>.() -> G,
  @BuilderInference action8: RaiseAccumulate<R>.() -> H,
  @BuilderInference action9: RaiseAccumulate<R>.() -> I,
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
  return if (error !== EmptyValue) raise(unbox<R>(error))
  else block(unbox(a), unbox(b), unbox(c), unbox(d), unbox(e), unbox(f), unbox(g), unbox(h), unbox(i))
}

/**
 * Accumulate the errors from running both [action1] and [action2].
 */
@RaiseDSL
public inline fun <R, A, B, C> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
  @BuilderInference action5: RaiseAccumulate<R>.() -> E,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
  @BuilderInference action5: RaiseAccumulate<R>.() -> E,
  @BuilderInference action6: RaiseAccumulate<R>.() -> F,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
  @BuilderInference action5: RaiseAccumulate<R>.() -> E,
  @BuilderInference action6: RaiseAccumulate<R>.() -> F,
  @BuilderInference action7: RaiseAccumulate<R>.() -> G,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H, I> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
  @BuilderInference action5: RaiseAccumulate<R>.() -> E,
  @BuilderInference action6: RaiseAccumulate<R>.() -> F,
  @BuilderInference action7: RaiseAccumulate<R>.() -> G,
  @BuilderInference action8: RaiseAccumulate<R>.() -> H,
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
 */
@RaiseDSL
public inline fun <R, A, B, C, D, E, F, G, H, I, J> Raise<NonEmptyList<R>>.zipOrAccumulate(
  @BuilderInference action1: RaiseAccumulate<R>.() -> A,
  @BuilderInference action2: RaiseAccumulate<R>.() -> B,
  @BuilderInference action3: RaiseAccumulate<R>.() -> C,
  @BuilderInference action4: RaiseAccumulate<R>.() -> D,
  @BuilderInference action5: RaiseAccumulate<R>.() -> E,
  @BuilderInference action6: RaiseAccumulate<R>.() -> F,
  @BuilderInference action7: RaiseAccumulate<R>.() -> G,
  @BuilderInference action8: RaiseAccumulate<R>.() -> H,
  @BuilderInference action9: RaiseAccumulate<R>.() -> I,
  block: (A, B, C, D, E, F, G, H, I) -> J
): J {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  val error: MutableList<R> = mutableListOf()
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
 * Transform every element of [list] using the given [transform], or accumulate all the occurred errors using [combine].
 */
@RaiseDSL
public inline fun <R, A, B> Raise<R>.mapOrAccumulate(
  list: Iterable<A>,
  combine: (R, R) -> R,
  @BuilderInference transform: RaiseAccumulate<R>.(A) -> B
): List<B> {
  var error: Any? = EmptyValue
  val results = ArrayList<B>(list.collectionSizeOrDefault(10))
  for (item in list) {
    fold<NonEmptyList<R>, B, Unit>(
      { transform(RaiseAccumulate(this), item) },
      { errors -> error = combine(error, errors.reduce(combine), combine) },
      { results.add(it) }
    )
  }
  return if (error === EmptyValue) results else raise(unbox<R>(error))
}

/**
 * Accumulate the errors obtained by executing the [transform] over every element of [list].
 */
@RaiseDSL
public inline fun <R, A, B> Raise<NonEmptyList<R>>.mapOrAccumulate(
  list: Iterable<A>,
  @BuilderInference transform: RaiseAccumulate<R>.(A) -> B
): List<B> {
  val error = mutableListOf<R>()
  val results = ArrayList<B>(list.collectionSizeOrDefault(10))
  for (item in list) {
    fold<NonEmptyList<R>, B, Unit>(
      { transform(RaiseAccumulate(this), item) },
      { errors -> error.addAll(errors) },
      { results.add(it) }
    )
  }
  return error.toNonEmptyListOrNull()?.let { raise(it) } ?: results
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
  public fun <A> EitherNel<Error, A>.bindNel(): A = when (this) {
    is Either.Left -> raise.raise(value)
    is Either.Right -> value
  }

  @RaiseDSL
  public fun <A> ValidatedNel<Error, A>.bindNel(): A = when (this) {
    is Validated.Invalid -> raise.raise(value)
    is Validated.Valid -> value
  }

  @RaiseDSL
  public inline fun <A> withNel(block: Raise<NonEmptyList<Error>>.() -> A): A =
    block(raise)
}
