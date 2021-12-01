//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[CircuitBreaker](index.md)/[protectOrThrow](protect-or-throw.md)

# protectOrThrow

[common]\
suspend tailrec fun &lt;[A](protect-or-throw.md)&gt; [protectOrThrow](protect-or-throw.md)(fa: suspend () -&gt; [A](protect-or-throw.md)): [A](protect-or-throw.md)

Returns a new task that upon execution will execute the given task, but with the protection of this circuit breaker. If an exception in [fa](protect-or-throw.md) occurs it will be rethrown
