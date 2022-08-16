@file:JvmMultifileClass
@file:JvmName("Effect")

package arrow.core.continuations

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public suspend fun <E, A> Effect<E, A>.toEither(): Either<E, A> =
  fold({ Either.Left(it) }, { Either.Right(it) })

public fun <E, A> EagerEffect<E, A>.toEither(): Either<E, A> =
  fold({ Either.Left(it) }, { Either.Right(it) })


public suspend fun <E, A> Effect<E, A>.orNull(): A? =
  fold({ null }, { it })

public fun <E, A> EagerEffect<E, A>.orNull(): A? =
  fold({ null }, { it })

public suspend fun <A> Effect<None, A>.toOption(): Option<A> =
  fold(::identity) { Some(it) }

public fun <A> EagerEffect<None, A>.toOption(): Option<A> =
  fold(::identity) { Some(it) }

public suspend fun <A> Effect<Throwable, A>.toResult(): Result<A> =
  fold({ Result.failure(it) }) { Result.success(it) }

public fun <A> EagerEffect<Throwable, A>.toResult(): Result<A> =
  fold({ Result.failure(it) }) { Result.success(it) }
