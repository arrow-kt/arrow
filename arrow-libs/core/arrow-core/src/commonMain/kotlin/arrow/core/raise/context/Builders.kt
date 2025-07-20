@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")

package arrow.core.raise.context

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.raise.RaiseDSL
import arrow.core.some
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.*
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public inline fun <Error, A> either(@BuilderInference block: context(Raise<Error>) () -> A): Either<Error, A> =
  arrow.core.raise.either(block)

public inline fun <A> nullable(block: context(SingletonRaise) () -> A): A? =
  recover(block) { null }

public inline fun <A> result(block: context(ResultRaise) () -> A): Result<A> =
  arrow.core.raise.result(block)

public inline fun <A> option(block: context(SingletonRaise) () -> A): Option<A> =
  recover<Unit, _>({ block().some()}) { None }

public inline fun impure(block: SingletonRaise.() -> Unit) {
  contract { callsInPlace(block, AT_MOST_ONCE) }
  return merge(block)
}

@RaiseDSL
public inline fun <A> singleton(
  raise: () -> A,
  @BuilderInference block: context(SingletonRaise) () -> A,
): A {
  contract {
    callsInPlace(raise, AT_MOST_ONCE)
    callsInPlace(block, AT_MOST_ONCE)
  }
  return recover(block) { raise() }
}

/**
 * Introduces a scope where you can [bind] errors of any type,
 * but no information is saved in the [raise] case.
 */
context(_: Raise<Unit>)
@RaiseDSL
public inline fun <A> ignoreErrors(
  block: Raise<Any?>.() -> A,
): A {
  contract { callsInPlace(block, EXACTLY_ONCE) }
  return withError({}, block)
}

@RaiseDSL
public inline fun <Error, A> recover(
  @BuilderInference block: context(Raise<Error>) () -> A,
  @BuilderInference recover: (error: Error) -> A,
): A {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
  }
  return arrow.core.raise.recover(block, recover)
}

@RaiseDSL
public inline fun <Error, A> recover(
  @BuilderInference block: context(Raise<Error>) () -> A,
  @BuilderInference recover: (error: Error) -> A,
  @BuilderInference catch: (throwable: Throwable) -> A,
): A {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(catch, AT_MOST_ONCE)
  }
  return arrow.core.raise.recover(block, recover, catch)
}

@RaiseDSL
@JvmName("recoverReified")
public inline fun <reified T : Throwable, Error, A> recover(
  @BuilderInference block: arrow.core.raise.Raise<Error>.() -> A,
  @BuilderInference recover: (error: Error) -> A,
  @BuilderInference catch: (t: T) -> A,
): A {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(catch, AT_MOST_ONCE)
  }
  return arrow.core.raise.recover(block, recover, catch)
}

@RaiseDSL
@JvmName("_merge")
public inline fun <A> merge(
  @BuilderInference block: arrow.core.raise.Raise<A>.() -> A,
): A {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
  }
  return arrow.core.raise.merge(block)
}
