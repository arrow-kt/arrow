package arrow.continuations.generic

/**
 * Base interface for a continuation
 */
@Deprecated(deprecateArrowContinuation)
public interface DelimitedContinuation<A, R> {
  public suspend operator fun invoke(a: A): R
}

/**
 * Base interface for our scope.
 */
@Deprecated(deprecateArrowContinuation)
public interface DelimitedScope<R> {

  /**
   * Exit the [DelimitedScope] with [R]
   */
  public suspend fun <A> shift(r: R): A
}

@Deprecated(deprecateArrowContinuation)
public interface RestrictedScope<R> : DelimitedScope<R> {
  /**
   * Capture the continuation and pass it to [f].
   */
  public suspend fun <A> shift(f: suspend RestrictedScope<R>.(DelimitedContinuation<A, R>) -> R): A

  public override suspend fun <A> shift(r: R): A = shift { r }
}

@Deprecated(deprecateArrowContinuation)
public interface SuspendedScope<R> : DelimitedScope<R>
