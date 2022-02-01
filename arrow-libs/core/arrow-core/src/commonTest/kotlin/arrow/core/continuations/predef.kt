package arrow.core.continuations

import arrow.core.identity

suspend fun <A> Effect<Nothing, A>.value(): A = fold(::identity, ::identity)

suspend fun Effect<*, *>.runCont(): Any? = fold(::identity, ::identity)
