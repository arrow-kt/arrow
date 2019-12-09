package arrow.typeclasses

interface Continuation<in T> : kotlin.coroutines.Continuation<T> {
  fun resume(value: T)

  fun resumeWithException(exception: Throwable)

  override fun resumeWith(result: Result<T>) =
    result.fold(::resume, ::resumeWithException)
}
