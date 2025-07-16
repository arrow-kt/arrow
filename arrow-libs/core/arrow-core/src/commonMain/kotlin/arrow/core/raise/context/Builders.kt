@file:OptIn(ExperimentalTypeInference::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")

package arrow.core.raise.context

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public inline fun <Error, A> either(@BuilderInference block: context(Raise<Error>) () -> A): Either<Error, A> =
  arrow.core.raise.either(block)

public inline fun <A> nullable(block: context(SingletonRaise<Nothing?>) () -> A): A? =
  arrow.core.raise.nullable(block)

public inline fun <A> result(block: context(ResultRaise) () -> A): Result<A> =
  arrow.core.raise.result(block)

public inline fun <A> option(block: context(SingletonRaise<None>) () -> A): Option<A> =
  arrow.core.raise.option(block)
