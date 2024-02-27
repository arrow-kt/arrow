package arrow.core.raise

import arrow.core.identity

suspend fun <A> Effect<Nothing, A>.value(): A = fold(::identity, ::identity)

suspend fun <E, A> Effect<E, A>.runCont(): Any? = fold(::identity, ::identity)

fun <A> EagerEffect<Nothing, A>.value(): A = fold(::identity, ::identity)

fun <E, A> EagerEffect<E, A>.runCont(): Any? = fold(::identity, ::identity)
