@file:JvmMultifileClass
@file:JvmName("RaiseKt")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)

package arrow.core.raise.context

import arrow.core.raise.ExperimentalTraceApi
import arrow.core.raise.Trace
import arrow.core.raise.withErrorTraced as withErrorTracedOriginal
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public suspend fun <Error, A, B> Effect<Error, A>.fold(
  catch: suspend (throwable: Throwable) -> B,
  recover: suspend (error: Error) -> B,
  transform: suspend (value: A) -> B,
): B {
  contract {
    callsInPlace(catch, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold({ bind() }, { catch(it) }, { recover(it) }, { transform(it) })
}

public suspend fun <Error, A, B> Effect<Error, A>.fold(
  recover: suspend (error: Error) -> B,
  transform: suspend (value: A) -> B,
): B {
  contract {
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold({ throw it }, recover, transform)
}

public inline fun <Error, A, B> EagerEffect<Error, A>.fold(
  catch: (throwable: Throwable) -> B,
  recover: (error: Error) -> B,
  transform: (value: A) -> B,
): B {
  contract {
    callsInPlace(catch, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold({ bind() }, catch, recover, transform)
}

public inline fun <Error, A, B> EagerEffect<Error, A>.fold(recover: (error: Error) -> B, transform: (value: A) -> B): B {
  contract {
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold({ throw it }, recover, transform)
}

@JvmName("_foldOrThrow")
public inline fun <Error, A, B> fold(
  @BuilderInference block: Raise<Error>.() -> A,
  recover: (error: Error) -> B,
  transform: (value: A) -> B,
): B {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return fold(block, { throw it }, recover, transform)
}

@JvmName("_fold")
public inline fun <Error, A, B> fold(
  block: context(Raise<Error>) () -> A,
  catch: (throwable: Throwable) -> B,
  recover: (error: Error) -> B,
  transform: (value: A) -> B,
): B {
  contract {
    callsInPlace(block, AT_MOST_ONCE)
    callsInPlace(catch, AT_MOST_ONCE)
    callsInPlace(recover, AT_MOST_ONCE)
    callsInPlace(transform, AT_MOST_ONCE)
  }
  return arrow.core.raise.fold(block, catch, recover, transform)
}

@ExperimentalTraceApi
context(raise: Raise<Error>)
public inline fun <Error, A> traced(
  block: context(Raise<Error>) () -> A,
  trace: (trace: Trace, error: Error) -> Unit
): A {
  contract {
    callsInPlace(trace, AT_MOST_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return withErrorTraced({ t, error -> error.also { trace(t, error) } }, block)
}

@ExperimentalTraceApi
context(raise: Raise<Error>)
public inline fun <Error, OtherError, A> withErrorTraced(
  transform: (Trace, OtherError) -> Error,
  block: context(Raise<OtherError>) () -> A
): A {
  contract {
    callsInPlace(transform, AT_MOST_ONCE)
    callsInPlace(block, EXACTLY_ONCE)
  }
  return raise.withErrorTracedOriginal(transform, block)
}
