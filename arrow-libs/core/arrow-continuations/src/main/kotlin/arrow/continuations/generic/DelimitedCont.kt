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
// TODO This should be @RestrictSuspension but that breaks because a superclass is not considered to be correct scope
// @RestrictsSuspension
interface DelimitedScope<R> {
  /**
   * Capture the continuation and pass it to [f].
   */
  suspend fun <A> shift(f: suspend DelimitedScope<R>.(DelimitedContinuation<A, R>) -> R): A

  /**
   * Manually cps transformed shift. This can be used to gain multishot without hacks, but it's not the nicest for a few reasons:
   * - It does not inherit the scope, this means it will be hard to effects offering non-det to offer the same scope again...
   * - it is manually cps transformed which means every helper between this and invoking the continuation also needs to be transformed.
   */
  suspend fun <A, B> shiftCPS(f: suspend (DelimitedContinuation<A, B>) -> R, c: suspend DelimitedScope<B>.(A) -> B): Nothing

  /**
   * Nest another scope inside the current one.
   *
   * It is important to use this over creating an unrelated scope because
   */
  suspend fun <A> reset(f: suspend DelimitedScope<A>.() -> A): A
}
