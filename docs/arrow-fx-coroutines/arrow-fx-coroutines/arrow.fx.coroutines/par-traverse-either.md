//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parTraverseEither](par-traverse-either.md)

# parTraverseEither

[common]\
suspend fun &lt;[A](par-traverse-either.md), [B](par-traverse-either.md), [E](par-traverse-either.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](par-traverse-either.md)&gt;.[parTraverseEither](par-traverse-either.md)(f: suspend CoroutineScope.([A](par-traverse-either.md)) -&gt; [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[E](par-traverse-either.md), [B](par-traverse-either.md)&gt;): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[E](par-traverse-either.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-traverse-either.md)&gt;&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs all mappers [f](par-traverse-either.md) on Dispatchers.Default. If one of the [f](par-traverse-either.md) returns [Either.Left](../../../arrow-core/arrow-core/arrow.core/-either/-left/index.md), then it will short-circuit the operation and cancelling all this running [f](par-traverse-either.md), and returning the first encountered [Either.Left](../../../arrow-core/arrow-core/arrow.core/-either/-left/index.md).

Cancelling this operation cancels all running tasks.

[common]\
suspend fun &lt;[A](par-traverse-either.md), [B](par-traverse-either.md), [E](par-traverse-either.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](par-traverse-either.md)&gt;.[parTraverseEither](par-traverse-either.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, f: suspend CoroutineScope.([A](par-traverse-either.md)) -&gt; [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[E](par-traverse-either.md), [B](par-traverse-either.md)&gt;): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[E](par-traverse-either.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-traverse-either.md)&gt;&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs all mappers [f](par-traverse-either.md) on [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). If one of the [f](par-traverse-either.md) returns [Either.Left](../../../arrow-core/arrow-core/arrow.core/-either/-left/index.md), then it will short-circuit the operation and cancelling all this running [f](par-traverse-either.md), and returning the first encountered [Either.Left](../../../arrow-core/arrow-core/arrow.core/-either/-left/index.md).

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-traverse-either.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks.

import arrow.core.*\
import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
object Error\
data class User(val id: Int, val createdOn: String)\
\
suspend fun main(): Unit {\
  //sampleStart\
  suspend fun getUserById(id: Int): Either&lt;Error, User&gt; =\
    if(id == 4) Error.left()\
    else User(id, Thread.currentThread().name).right()\
\
  val res = listOf(1, 2, 3)\
    .parTraverseEither(Dispatchers.IO) { getUserById(it) }\
\
  val res2 = listOf(1, 4, 2, 3)\
    .parTraverseEither(Dispatchers.IO) { getUserById(it) }\
 //sampleEnd\
 println(res)\
 println(res2)\
}<!--- KNIT example-partraverseeither-02.kt -->
