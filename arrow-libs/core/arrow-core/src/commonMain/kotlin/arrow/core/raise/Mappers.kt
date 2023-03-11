@file:JvmMultifileClass
@file:JvmName("RaiseKt")
package arrow.core.raise

import arrow.core.Either
import arrow.core.Ior
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/** Run the [Effect] by returning [Either.Right] of [A], or [Either.Left] of [E]. */
public suspend fun <E, A> Effect<E, A>.toEither(): Either<E, A> = either { invoke() }
public fun <E, A> EagerEffect<E, A>.toEither(): Either<E, A> = either { invoke() }

/** Run the [Effect] by returning [Ior.Right] of [A], or [Ior.Left] of [E]. */
public suspend fun <E, A> Effect<E, A>.toIor(): Ior<E, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }
public fun <E, A> EagerEffect<E, A>.toIor(): Ior<E, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }

@Deprecated(
  "orNull is being renamed to getOrNull to be more consistent with the Kotlin Standard Library naming",
  ReplaceWith("getOrNull()", "arrow.core.raise.getOrNull")
)
public suspend fun <E, A> Effect<E, A>.orNull(): A? = getOrElse { null }

@Deprecated(
  "orNull is being renamed to getOrNull to be more consistent with the Kotlin Standard Library naming",
  ReplaceWith("getOrNull()", "arrow.core.raise.getOrNull")
)
public fun <E, A> EagerEffect<E, A>.orNull(): A? = getOrElse { null }

/** Run the [Effect] by returning [A], or `null` if raised with [E]. */
public suspend fun <E, A> Effect<E, A>.getOrNull(): A? = getOrElse { null }

/** Run the [EagerEffect] by returning [A], or `null` if raised with [E]. */
public fun <E, A> EagerEffect<E, A>.getOrNull(): A? = getOrElse { null }

/** Run the [Effect] by returning [Option] of [A], [orElse] run the fallback lambda and returning its result of [Option] of [A]. */
public suspend fun <E, A> Effect<E, A>.toOption(orElse: suspend (E) -> Option<A>): Option<A> = fold(orElse) { Some(it) }
public inline fun <E, A> EagerEffect<E, A>.toOption(orElse: (E) -> Option<A>): Option<A> = fold(orElse) { Some(it) }

/** Run the [Effect] by returning [Result] of [A], [orElse] run the fallback lambda and returning its result of [Result] of [A]. */
public suspend fun <E, A> Effect<E, A>.toResult(orElse: suspend (E) -> Result<A>): Result<A> =
  fold({ Result.failure(it)  }, { orElse(it) }, { Result.success(it) })
public inline fun <E, A> EagerEffect<E, A>.toResult(orElse:  (E) -> Result<A>): Result<A> =
  fold({ Result.failure(it)  }, { orElse(it) }, { Result.success(it) })

/** Run the [Effect] by returning [Result] of [A], or [Result.Failure] if raised with [Throwable]. */
public suspend fun <A> Effect<Throwable, A>.toResult(): Result<A> = result { invoke() }
public fun <A> EagerEffect<Throwable, A>.toResult(): Result<A> = result { invoke() }
