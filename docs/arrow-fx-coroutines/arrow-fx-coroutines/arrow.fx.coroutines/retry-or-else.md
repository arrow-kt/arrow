//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[retryOrElse](retry-or-else.md)

# retryOrElse

[common]\
suspend fun &lt;[A](retry-or-else.md), [B](retry-or-else.md)&gt; [Schedule](-schedule/index.md)&lt;[Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](retry-or-else.md)&gt;.[retryOrElse](retry-or-else.md)(fa: suspend () -&gt; [A](retry-or-else.md), orElse: suspend ([Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html), [B](retry-or-else.md)) -&gt; [A](retry-or-else.md)): [A](retry-or-else.md)

Runs an effect and, if it fails, decide using the provided policy if the effect should be retried and if so, with how much delay. Also offers a function to handle errors if they are encountered during retrial.
