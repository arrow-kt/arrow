package arrow.core

/**
 * Convenience interface to bridge to old coroutine API and not deal with [Result]
 */
interface Continuation<in T> : kotlin.coroutines.Continuation<T> {
  fun resume(value: T)

  fun resumeWithException(exception: Throwable)

  override fun resumeWith(result: Result<T>) =
    result.fold(::resume, ::resumeWithException)

}
