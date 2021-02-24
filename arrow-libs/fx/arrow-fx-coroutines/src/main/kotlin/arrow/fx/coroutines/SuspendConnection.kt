package arrow.fx.coroutines

/**
 * Inline marker to mark a [CancelToken],
 * This allows for clearer APIs in functions that expect a [CancelToken] to be returned.
 */
@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class CancelToken(val cancel: suspend () -> Unit) {

  suspend fun invoke(): Unit = cancel.invoke()

  override fun toString(): String = "CancelToken(..)"

  companion object {
    val unit = CancelToken { Unit }
  }
}

typealias Disposable = () -> Unit
