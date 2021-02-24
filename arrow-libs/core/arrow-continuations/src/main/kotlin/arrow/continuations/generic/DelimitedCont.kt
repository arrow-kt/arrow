package arrow.continuations.generic

/**
 * Base interface for a continuation
 */
interface DelimitedContinuation<A, R> {
  suspend operator fun invoke(a: A): R
}

/**
 * Base interface for our scope.
 */
interface DelimitedScope<R> {

  /**
   * Exit the [DelimitedScope] with [R]
   */
  suspend fun <A> shift(r: R): A
}

interface RestrictedScope<R> : DelimitedScope<R> {
  /**
   * Capture the continuation and pass it to [f].
   */
  suspend fun <A> shift(f: suspend RestrictedScope<R>.(DelimitedContinuation<A, R>) -> R): A

  override suspend fun <A> shift(r: R): A = shift { r }
}

interface SuspendedScope<R> : DelimitedScope<R>
