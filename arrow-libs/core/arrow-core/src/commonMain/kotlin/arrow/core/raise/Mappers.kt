@file:JvmMultifileClass
@file:JvmName("RaiseKt")
package arrow.core.raise

import arrow.core.Either
import arrow.core.Ior
import arrow.core.Option
import arrow.core.Some
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/** Run the [Effect] by returning [Either.Right] of [A], or [Either.Left] of [Error]. */
public suspend fun <Error, A> Effect<Error, A>.toEither(): Either<Error, A> = either { invoke() }
public fun <Error, A> EagerEffect<Error, A>.toEither(): Either<Error, A> = either { invoke() }

/** Run the [Effect] by returning [Ior.Right] of [A], or [Ior.Left] of [Error]. */
public suspend fun <Error, A> Effect<Error, A>.toIor(): Ior<Error, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }
public fun <Error, A> EagerEffect<Error, A>.toIor(): Ior<Error, A> = fold({ Ior.Left(it) }) { Ior.Right(it) }

/** Run the [Effect] by returning [A], or `null` if raised with [Error]. */
public suspend fun <Error, A> Effect<Error, A>.getOrNull(): A? = getOrElse { null }

/** Run the [EagerEffect] by returning [A], or `null` if raised with [Error]. */
public fun <Error, A> EagerEffect<Error, A>.getOrNull(): A? = getOrElse { null }

/** Run the [Effect] by returning [Option] of [A], [recover] run the fallback lambda and returning its result of [Option] of [A]. */
public suspend fun <Error, A> Effect<Error, A>.toOption(recover: suspend (error: Error) -> Option<A>): Option<A> = fold(recover) { Some(it) }
public inline fun <Error, A> EagerEffect<Error, A>.toOption(recover: (error: Error) -> Option<A>): Option<A> = fold(recover) { Some(it) }

/** Run the [Effect] by returning [Result] of [A], [recover] run the fallback lambda and returning its result of [Result] of [A]. */
public suspend fun <Error, A> Effect<Error, A>.toResult(recover: suspend (error: Error) -> Result<A>): Result<A> =
  fold({ Result.failure(it)  }, { recover(it) }, { Result.success(it) })
public inline fun <Error, A> EagerEffect<Error, A>.toResult(recover: (error: Error) -> Result<A>): Result<A> =
  fold({ Result.failure(it)  }, { recover(it) }, { Result.success(it) })

/** Run the [Effect] by returning [Result] of [A], or [Result.Failure] if raised with [Throwable]. */
public suspend fun <A> Effect<Throwable, A>.toResult(): Result<A> = result { invoke() }
public fun <A> EagerEffect<Throwable, A>.toResult(): Result<A> = result { invoke() }
