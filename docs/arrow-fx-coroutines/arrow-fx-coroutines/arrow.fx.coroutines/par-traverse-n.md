//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parTraverseN](par-traverse-n.md)

# parTraverseN

[common]\
suspend fun &lt;[A](par-traverse-n.md), [B](par-traverse-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](par-traverse-n.md)&gt;.[parTraverseN](par-traverse-n.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), f: suspend CoroutineScope.([A](par-traverse-n.md)) -&gt; [B](par-traverse-n.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-traverse-n.md)&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs [f](par-traverse-n.md) in [n](par-traverse-n.md) parallel operations on Dispatchers.Default. Cancelling this operation cancels all running tasks.

[common]\
suspend fun &lt;[A](par-traverse-n.md), [B](par-traverse-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](par-traverse-n.md)&gt;.[parTraverseN](par-traverse-n.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), f: suspend CoroutineScope.([A](par-traverse-n.md)) -&gt; [B](par-traverse-n.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-traverse-n.md)&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs [f](par-traverse-n.md) in [n](par-traverse-n.md) parallel operations on [ctx](par-traverse-n.md).

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-traverse-n.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks.
