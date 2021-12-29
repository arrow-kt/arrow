package arrow.fx.coroutines

import kotlinx.coroutines.awaitCancellation

// TODO deprecate?
public suspend fun <A> never(): A =
  awaitCancellation()

// TODO deprecate?
public suspend fun unit(): Unit = Unit
