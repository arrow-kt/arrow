//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parTraverseResultN](par-traverse-result-n.md)

# parTraverseResultN

[common]\
suspend fun &lt;[A](par-traverse-result-n.md), [B](par-traverse-result-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](par-traverse-result-n.md)&gt;.[parTraverseResultN](par-traverse-result-n.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), f: suspend CoroutineScope.([A](par-traverse-result-n.md)) -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[B](par-traverse-result-n.md)&gt;): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-traverse-result-n.md)&gt;&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs [f](par-traverse-result-n.md) in [n](par-traverse-result-n.md) parallel operations on Dispatchers.Default. If one or more of the [f](par-traverse-result-n.md) returns [Result.failure](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/failure.html) then all the [Result.failure](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/failure.html) results will be combined using [addSuppressed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html).

Cancelling this operation cancels all running tasks.

[common]\
suspend fun &lt;[A](par-traverse-result-n.md), [B](par-traverse-result-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](par-traverse-result-n.md)&gt;.[parTraverseResultN](par-traverse-result-n.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), f: suspend CoroutineScope.([A](par-traverse-result-n.md)) -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[B](par-traverse-result-n.md)&gt;): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-traverse-result-n.md)&gt;&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs [f](par-traverse-result-n.md) in [n](par-traverse-result-n.md) parallel operations on [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). If one or more of the [f](par-traverse-result-n.md) returns [Result.failure](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/failure.html) then all the [Result.failure](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/failure.html) results will be combined using [addSuppressed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html).

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-traverse-result-n.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks.
