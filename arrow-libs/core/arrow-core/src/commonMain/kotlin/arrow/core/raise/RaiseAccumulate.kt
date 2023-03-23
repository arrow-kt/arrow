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
 * Accumulate the errors from running both [action1] and [action2].
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
 * Transform every element of [iterable] using the given [transform], or accumulate all the occurred errors using [combine].
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

  @RaiseDSL
  public inline fun <A, B> Iterable<A>.mapOrAccumulate(
    transform: RaiseAccumulate<Error>.(A) -> B
  ): List<B> = raise.mapOrAccumulate(this, transform)

  @RaiseDSL
  override fun <A> Iterable<Either<Error, A>>.bindAll(): List<A> =
    mapOrAccumulate { it.bind() }

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
