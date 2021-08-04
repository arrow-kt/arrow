package arrow.fx.coroutines

import kotlinx.coroutines.suspendCancellableCoroutine

public suspend fun <A> never(): A =
  suspendCancellableCoroutine<Nothing> {}

public suspend fun unit(): Unit = Unit
