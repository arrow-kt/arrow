@file:JvmMultifileClass
@file:JvmName("RaiseKt")
@file:OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
package arrow.core.raise.context

import arrow.core.nonFatalOrThrow
import arrow.core.raise.catch
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public infix fun <Error, OtherError, A> Effect<Error, A>.recover(recover: suspend context(Raise<OtherError>) (error: Error) -> A): Effect<OtherError, A> =
  effect { recover({ bind() }) { recover(it) } }

public infix fun <Error, A> Effect<Error, A>.catch(catch: suspend context(Raise<Error>) (throwable: Throwable) -> A): Effect<Error, A> =
  effect { catch({ bind() }) { catch(it) } }

@JvmName("catchReified")
public inline infix fun <reified T : Throwable, Error, A> Effect<Error, A>.catch(
  @BuilderInference crossinline catch: suspend context(Raise<Error>) (t: T) -> A,
): Effect<Error, A> =
  effect { catch({ bind() }) { t: T -> catch(t) } }

/** Runs the [Effect] and captures any [nonFatalOrThrow] exception into [Result]. */
public fun <Error, A> Effect<Error, A>.catch(): Effect<Error, Result<A>> =
  effect {
    catch({ Result.success(bind()) }, Result.Companion::failure)
  }

public suspend inline infix fun <Error, A> Effect<Error, A>.getOrElse(recover: (error: Error) -> A): A {
  contract { callsInPlace(recover, InvocationKind.AT_MOST_ONCE) }
  return recover({ bind() }, recover)
}

public infix fun <Error, OtherError, A> Effect<Error, A>.mapError(transform: suspend (error: Error) -> OtherError): Effect<OtherError, A> =
  effect { withError({ transform(it) }) { bind() } }

public infix fun <Error, OtherError, A> EagerEffect<Error, A>.recover(recover: context(Raise<OtherError>) (error: Error) -> A): EagerEffect<OtherError, A> =
  eagerEffect { recover(this) { recover(it) } }

public infix fun <Error, A> EagerEffect<Error, A>.catch(catch: context(Raise<Error>) (throwable: Throwable) -> A): EagerEffect<Error, A> =
  eagerEffect { catch({ bind() }) { catch(it) } }

@JvmName("catchReified")
public inline infix fun <reified T : Throwable, Error, A> EagerEffect<Error, A>.catch(
  @BuilderInference crossinline catch: context(Raise<Error>) (t: T) -> A,
): EagerEffect<Error, A> =
  eagerEffect { catch({ bind() }) { t: T -> catch(t) } }

public inline infix fun <Error, A> EagerEffect<Error, A>.getOrElse(recover: (error: Error) -> A): A {
  contract { callsInPlace(recover, InvocationKind.AT_MOST_ONCE) }
  return recover({ bind() }, recover)
}

public infix fun <Error, OtherError, A> EagerEffect<Error, A>.mapError(transform: (error: Error) -> OtherError): EagerEffect<OtherError, A> =
  eagerEffect { withError({ transform(it) }) { bind() } }
