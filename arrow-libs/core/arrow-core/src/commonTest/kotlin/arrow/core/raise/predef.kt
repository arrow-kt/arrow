package arrow.core.raise

import arrow.core.identity

suspend fun <A> Effect<Nothing, A>.value(): A = fold(::identity, ::identity)

fun <A> EagerEffect<Nothing, A>.value(): A = fold(::identity, ::identity)
