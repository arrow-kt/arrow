package arrow.scoped

/**
 * Saga DSL derived from Scope
 */
public suspend fun <A> ScopingScope.saga(
  action: suspend () -> A,
  compensation: suspend (A, Throwable?) -> Unit
): A = action().also { a ->
  closing { e ->
    when (e) {
      null -> compensation(a, null)
      else -> compensation(a, e)
    }
  }
}
