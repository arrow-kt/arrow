//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[CircuitBreaker](index.md)/[protectEither](protect-either.md)

# protectEither

[common]\
suspend fun &lt;[A](protect-either.md)&gt; [protectEither](protect-either.md)(fa: suspend () -&gt; [A](protect-either.md)): [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[CircuitBreaker.ExecutionRejected](-execution-rejected/index.md), [A](protect-either.md)&gt;

Returns a new task that upon execution will execute the given task, but with the protection of this circuit breaker. If an exception in [fa](protect-either.md) occurs, other than an [ExecutionRejected](-execution-rejected/index.md) exception, it will be rethrown.
