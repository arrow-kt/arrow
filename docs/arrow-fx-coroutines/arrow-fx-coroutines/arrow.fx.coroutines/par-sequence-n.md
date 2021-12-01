//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parSequenceN](par-sequence-n.md)

# parSequenceN

[common]\
suspend fun &lt;[A](par-sequence-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [A](par-sequence-n.md)&gt;.[parSequenceN](par-sequence-n.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-n.md)&gt;

suspend fun &lt;[A](par-sequence-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [A](par-sequence-n.md)&gt;.[parSequenceN](par-sequence-n.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-n.md)&gt;

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceNScoped")

suspend fun &lt;[A](par-sequence-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [A](par-sequence-n.md)&gt;.[parSequenceN](par-sequence-n.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-n.md)&gt;

Sequences all tasks in [n](par-sequence-n.md) parallel processes on Dispatchers.Default and return the result.

Cancelling this operation cancels all running tasks

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceNScoped")

suspend fun &lt;[A](par-sequence-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [A](par-sequence-n.md)&gt;.[parSequenceN](par-sequence-n.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-n.md)&gt;

Sequences all tasks in [n](par-sequence-n.md) parallel processes and return the result.

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-sequence-n.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks
