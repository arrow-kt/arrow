package arrow.core.raise

public abstract class TransformingRaise<in Error, in OuterError>(@PublishedApi internal val raise: Raise<OuterError>) : Raise<Error> {
  final override fun raise(r: Error): Nothing =
    raise.raise(transform(r))

  protected abstract fun transform(r: Error): @UnsafeVariance OuterError
}

public abstract class RaiseWrapper<in Error>(raise: Raise<Error>) : TransformingRaise<Error, Error>(raise) {
  final override fun transform(r: Error): @UnsafeVariance Error = r
}
