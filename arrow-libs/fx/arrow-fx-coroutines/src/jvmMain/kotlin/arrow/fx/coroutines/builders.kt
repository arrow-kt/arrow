package arrow.fx.coroutines

import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun <A> never(): A =
  suspendCancellableCoroutine<Nothing> {}

suspend fun unit(): Unit = Unit
