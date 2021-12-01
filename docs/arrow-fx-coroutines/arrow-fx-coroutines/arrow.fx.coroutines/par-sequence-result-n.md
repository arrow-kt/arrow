//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parSequenceResultN](par-sequence-result-n.md)

# parSequenceResultN

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceResultNScoped")

suspend fun &lt;[A](par-sequence-result-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](par-sequence-result-n.md)&gt;&gt;.[parSequenceResultN](par-sequence-result-n.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-result-n.md)&gt;&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs suspend CoroutineScope.() -&gt; Result&lt;A&gt; in [n](par-sequence-result-n.md) parallel operations on [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). If one or more of the tasks returns [Result.failure](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/failure.html) then all the [Result.failure](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/failure.html) results will be combined using [addSuppressed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html).

Cancelling this operation cancels all running tasks.

[common]\
suspend fun &lt;[A](par-sequence-result-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](par-sequence-result-n.md)&gt;&gt;.[parSequenceResultN](par-sequence-result-n.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-result-n.md)&gt;&gt;

suspend fun &lt;[A](par-sequence-result-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](par-sequence-result-n.md)&gt;&gt;.[parSequenceResultN](par-sequence-result-n.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-result-n.md)&gt;&gt;

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceResultNScoped")

suspend fun &lt;[A](par-sequence-result-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[A](par-sequence-result-n.md)&gt;&gt;.[parSequenceResultN](par-sequence-result-n.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Result](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-result-n.md)&gt;&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs suspend CoroutineScope.() -&gt; Result&lt;A&gt; in [n](par-sequence-result-n.md) parallel operations on [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). If one or more of the tasks returns [Result.failure](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/failure.html) then all the [Result.failure](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-result/failure.html) results will be combined using [addSuppressed](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html).

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-sequence-result-n.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks.
