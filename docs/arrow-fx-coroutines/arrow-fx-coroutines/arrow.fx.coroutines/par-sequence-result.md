//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parSequenceResult](par-sequence-result.md)

# parSequenceResult

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceResultScoped")

suspend fun &lt;[A](par-sequence-result.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](par-sequence-result.md)&gt;&gt;.[parSequenceResult](par-sequence-result.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-result.md)&gt;&gt;

Sequences all tasks in parallel on [ctx](par-sequence-result.md) and returns the result. If one or more of the tasks returns [Result.failure](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/failure.html) then all the [Result.failure](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/failure.html) results will be combined using [addSuppressed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html).

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-sequence-result.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks.

[common]\
suspend fun &lt;[A](par-sequence-result.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](par-sequence-result.md)&gt;&gt;.[parSequenceResult](par-sequence-result.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-result.md)&gt;&gt;
