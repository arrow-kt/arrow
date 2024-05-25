package arrow

/**
 * Saga DSL derived from Scope
 */
public suspend fun <A> Finally.saga(
  action: suspend () -> A,
  compensation: suspend (A, Throwable?) -> Unit
): A = action().also { a ->
  finalise { e ->
    when (e) {
      null -> compensation(a, null)
      else -> compensation(a, e)
    }
  }
}
