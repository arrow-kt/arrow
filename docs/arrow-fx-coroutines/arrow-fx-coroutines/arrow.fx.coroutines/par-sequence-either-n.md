//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parSequenceEitherN](par-sequence-either-n.md)

# parSequenceEitherN

[common]\
suspend fun &lt;[A](par-sequence-either-n.md), [B](par-sequence-either-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either-n.md), [B](par-sequence-either-n.md)&gt;&gt;.[parSequenceEitherN](par-sequence-either-n.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either-n.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-sequence-either-n.md)&gt;&gt;

Sequences all tasks in [n](par-sequence-either-n.md) parallel processes on Dispatchers.Default and return the result.

Cancelling this operation cancels all running tasks

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceEitherNScoped")

suspend fun &lt;[A](par-sequence-either-n.md), [B](par-sequence-either-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either-n.md), [B](par-sequence-either-n.md)&gt;&gt;.[parSequenceEitherN](par-sequence-either-n.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either-n.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-sequence-either-n.md)&gt;&gt;

suspend fun &lt;[A](par-sequence-either-n.md), [B](par-sequence-either-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either-n.md), [B](par-sequence-either-n.md)&gt;&gt;.[parSequenceEitherN](par-sequence-either-n.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either-n.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-sequence-either-n.md)&gt;&gt;

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceEitherNScoped")

suspend fun &lt;[A](par-sequence-either-n.md), [B](par-sequence-either-n.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either-n.md), [B](par-sequence-either-n.md)&gt;&gt;.[parSequenceEitherN](par-sequence-either-n.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either-n.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-sequence-either-n.md)&gt;&gt;

Sequences all tasks in [n](par-sequence-either-n.md) parallel processes on [ctx](par-sequence-either-n.md) and return the result.

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-sequence-either-n.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks
