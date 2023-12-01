package arrow.core.raise

suspend fun <A> Effect<Nothing, A>.value(): A = merge()

suspend fun <E, A> Effect<E, A>.runCont(): Any? = merge()

fun <A> EagerEffect<Nothing, A>.value(): A = merge()

fun <E, A> EagerEffect<E, A>.runCont(): Any? = merge()
