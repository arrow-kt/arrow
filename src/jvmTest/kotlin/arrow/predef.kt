package arrow

import arrow.core.identity

suspend fun <A> Cont<Nothing, A>.value(): A = fold(::identity, ::identity)

suspend fun Cont<*, *>.runCont(): Any? = fold(::identity, ::identity)
