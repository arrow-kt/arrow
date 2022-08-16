@file:JvmMultifileClass
@file:JvmName("Effect")

package arrow.core.continuations

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public suspend fun <E, A> Effect<E, A>.toEither(): Either<E, A> = either { invoke() }

public fun <E, A> EagerEffect<E, A>.toEither(): Either<E, A> = either { invoke() }

public suspend fun <E, A> Effect<E, A>.orNull(): A? = fold({ _: E -> null }) { it }

public fun <E, A> EagerEffect<E, A>.orNull(): A? = fold({ _: E -> null }) { it }

public suspend fun <A> Effect<None, A>.toOption(): Option<A> = option { invoke() }

public fun <A> EagerEffect<None, A>.toOption(): Option<A> = option { invoke() }

public suspend fun <A> Effect<Throwable, A>.toResult(): Result<A> = result { invoke() }

public fun <A> EagerEffect<Throwable, A>.toResult(): Result<A> = result { invoke() }

public suspend fun <E, A> Effect<E, A>.toOption(orElse: suspend (E) -> Option<A>): Option<A> =
  fold(orElse) { Some(it) }

public fun <E, A> EagerEffect<E, A>.toOption(orElse: (E) -> Option<A>): Option<A> =
  fold(orElse) { Some(it) }
