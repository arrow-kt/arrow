@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package arrow.core.raise

import arrow.core.Either
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference

public inline fun <Error, A> result4k(@BuilderInference block: Raise<Error>.() -> A): Result<A, Error> {
  contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
  return fold(block, { Failure(it) }, { Success(it) })
}

context(raise: Raise<Error>)
public fun <Error, A> Result<A, Error>.bind(): A = when (this) {
  is Success -> value
  is Failure -> raise.raise(reason)
}

public fun <E, A> Either<E, A>.toResult4k(): Result<A, E> = when (this) {
  is Either.Left -> Failure(value)
  is Either.Right -> Success(value)
}

public fun <E, A> Result<A, E>.toEither(): Either<E, A> = when (this) {
  is Success -> Either.Right(value)
  is Failure -> Either.Left(reason)
}
