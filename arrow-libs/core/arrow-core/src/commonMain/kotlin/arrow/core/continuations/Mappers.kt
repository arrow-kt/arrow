@file:JvmMultifileClass
@file:JvmName("Effect")

package arrow.core.continuations

import arrow.core.Either
import arrow.core.Ior
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Validated
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/** Run the [Effect] by returning [Either.Right] of [A], or [Either.Left] of [E]. */
public suspend fun <E, A> Effect<E, A>.toEither(): Either<E, A> = either { invoke() }
public fun <E, A> EagerEffect<E, A>.toEither(): Either<E, A> = either { invoke() }

/** Run the [Effect] by returning [Validated.Valid] of [A], or [Validated.Invalid] of [E]. */
public suspend fun <E, A> Effect<E, A>.toValidated(): Validated<E, A> = fold({ Validated.Invalid(it) }) { Validated.Valid(it) }
public fun <E, A> EagerEffect<E, A>.toValidated(): Validated<E, A> = fold({ Validated.Invalid(it) }) { Validated.Valid(it) }

/** Run the [Effect] by returning [Ior.Right] of [A], or [Ior.Left] of [E]. */
public suspend fun <E, A> Effect<E, A>.toIor(): Ior<E, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }
public fun <E, A> EagerEffect<E, A>.toIor(): Ior<E, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }

/** Run the [Effect] by returning [A], or `null` if raised with [E]. */
public suspend fun <E, A> Effect<E, A>.orNull(): A? = fold({ _: E -> null }) { it }
public fun <E, A> EagerEffect<E, A>.orNull(): A? = fold({ _: E -> null }) { it }

/** Run the [Effect] by returning [Option] of [A], [orElse] run the fallback lambda and returning its result of [Option] of [A]. */
public suspend fun <E, A> Effect<E, A>.toOption(orElse: suspend (E) -> Option<A>): Option<A> = fold(orElse) { Some(it) }
public fun <E, A> EagerEffect<E, A>.toOption(orElse: (E) -> Option<A>): Option<A> = fold(orElse) { Some(it) }

/** Run the [Effect] by returning [Option] of [A], or [None] if raised with [None]. */
public suspend fun <A> Effect<None, A>.toOption(): Option<A> = option { invoke() }
public fun <A> EagerEffect<None, A>.toOption(): Option<A> = option { invoke() }

/** Run the [Effect] by returning [Result] of [A], [orElse] run the fallback lambda and returning its result of [Result] of [A]. */
public suspend fun <E, A> Effect<E, A>.toResult(orElse: suspend (E) -> Result<A>): Result<A> =
  fold({ orElse(it) }, { Result.success(it) })
public fun <E, A> EagerEffect<E, A>.toResult(orElse:  (E) -> Result<A>): Result<A> =
  fold({ orElse(it) }, { Result.success(it) })

/** Run the [Effect] by returning [Result] of [A], or [Result.Failure] if raised with [Throwable]. */
public suspend fun <A> Effect<Throwable, A>.toResult(): Result<A> = result { invoke() }
public fun <A> EagerEffect<Throwable, A>.toResult(): Result<A> = result { invoke() }
