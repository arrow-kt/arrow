package arrow.fx.coroutines

import kotlinx.coroutines.awaitCancellation

@Deprecated("Use awaitCancellation", ReplaceWith("awaitCancellation()", "kotlinx.coroutines.awaitCancellation"))
public suspend fun <A> never(): A =
  awaitCancellation()

@Deprecated("Use Unit directly instead", ReplaceWith("Unit"))
public suspend fun unit(): Unit = Unit
