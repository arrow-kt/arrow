package arrow.typeclasses

interface Continuation<in T> : kotlin.coroutines.Continuation<T> {
  fun resume(value: T)

  fun resumeWithException(exception: Throwable)

  override fun resumeWith(result: SuccessOrFailure<T>) =
    result.fold(::resume, ::resumeWithException)

}

inline fun <A> A.success(): SuccessOrFailure<A> =
  SuccessOrFailure.success(this)

inline fun <A> Throwable.failure(): SuccessOrFailure<A> =
  SuccessOrFailure.failure(this)

inline fun <A, B> SuccessOrFailure<A>.flatMap(f: (A) -> SuccessOrFailure<B>): SuccessOrFailure<B> =
  fold(f, { it.failure() })
