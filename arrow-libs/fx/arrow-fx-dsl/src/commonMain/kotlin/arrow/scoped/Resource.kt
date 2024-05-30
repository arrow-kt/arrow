package arrow.scoped

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

public suspend fun <A> ScopingScope.resource(
  action: suspend CoroutineScope.() -> A,
  compensation: suspend (A, Throwable?) -> Unit
): A =
  withContext(NonCancellable, action)
    .also { a -> closing { e -> compensation(a, e) } }
