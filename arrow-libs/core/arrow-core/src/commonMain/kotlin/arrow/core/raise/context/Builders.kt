@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")

package arrow.core.raise.context

import arrow.core.Either
import arrow.core.Ior
import arrow.core.IorNel
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Option
import arrow.core.raise.IorRaise
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public inline fun <Error, A> either(
  @BuilderInference block: context(Raise<Error>) () -> A
): Either<Error, A> {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return arrow.core.raise.either(block)
}

public inline fun <A> nullable(
  block: context(SingletonRaise<Nothing?>) () -> A
): A? {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return arrow.core.raise.nullable(block)
}

public inline fun <A> result(
  block: context(ResultRaise) () -> A
): Result<A> {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return arrow.core.raise.result(block)
}

public inline fun <A> option(
  block: context(SingletonRaise<None>) () -> A
): Option<A> {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return arrow.core.raise.option(block)
}

public inline fun <A> singleton(
  raise: () -> A,
  @BuilderInference block: context(SingletonRaise<A>) () -> A
): A {
  contract {
    callsInPlace(raise, AT_MOST_ONCE)
    callsInPlace(block, AT_MOST_ONCE)
  }
  return arrow.core.raise.singleton(raise, block)
}

public inline fun <Error, A> ior(
  noinline combineError: (Error, Error) -> Error,
  @BuilderInference block: context(IorRaise<Error>) () -> A
): Ior<Error, A> {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return arrow.core.raise.ior(combineError, block)
}

public inline fun <Error, A> iorNel(
  noinline combineError: (NonEmptyList<Error>, NonEmptyList<Error>) -> NonEmptyList<Error> = { a, b -> a + b },
  @BuilderInference block: context(IorRaise<NonEmptyList<Error>>) () -> A
): IorNel<Error, A> {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return arrow.core.raise.iorNel(combineError, block)
}

public inline fun impure(
  block: context(SingletonRaise<Unit>) () -> Unit
) {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  arrow.core.raise.impure(block)
}
