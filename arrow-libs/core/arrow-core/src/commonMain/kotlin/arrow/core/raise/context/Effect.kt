@file:JvmMultifileClass
@file:JvmName("RaiseContextualKt")
@file:OptIn(ExperimentalTypeInference::class)
package arrow.core.raise.context

import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public typealias Effect<Error, A> = suspend context(Raise<Error>) () -> A

@Suppress("NOTHING_TO_INLINE")
public inline fun <Error, A> effect(noinline block: Effect<Error, A>): Effect<Error, A> = block

/** The same behavior and API as [Effect] except without requiring _suspend_. */
public typealias EagerEffect<Error, A> = context(Raise<Error>) () -> A

@Suppress("NOTHING_TO_INLINE")
public inline fun <Error, A> eagerEffect(noinline block: EagerEffect<Error, A>): EagerEffect<Error, A> = block

public suspend fun <A> Effect<A, A>.merge(): A = merge { invoke() }
public fun <A> EagerEffect<A, A>.merge(): A = merge(this)

public suspend fun <A> Effect<Nothing, A>.get(): A = merge()

public fun <A> EagerEffect<Nothing, A>.get(): A = merge()
