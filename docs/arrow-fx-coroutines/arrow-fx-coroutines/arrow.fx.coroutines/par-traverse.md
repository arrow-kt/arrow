//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parTraverse](par-traverse.md)

# parTraverse

[common]\
suspend fun &lt;[A](par-traverse.md), [B](par-traverse.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](par-traverse.md)&gt;.[parTraverse](par-traverse.md)(f: suspend CoroutineScope.([A](par-traverse.md)) -&gt; [B](par-traverse.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-traverse.md)&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs all mappers [f](par-traverse.md) on Dispatchers.Default. Cancelling this operation cancels all running tasks.

import arrow.fx.coroutines.*\
\
data class User(val id: Int, val createdOn: String)\
\
suspend fun main(): Unit {\
  //sampleStart\
  suspend fun getUserById(id: Int): User =\
    User(id, Thread.currentThread().name)\
\
  val res = listOf(1, 2, 3)\
    .parTraverse { getUserById(it) }\
 //sampleEnd\
 println(res)\
}<!--- KNIT example-partraverse-03.kt -->

[common]\
suspend fun &lt;[A](par-traverse.md), [B](par-traverse.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](par-traverse.md)&gt;.[parTraverse](par-traverse.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, f: suspend CoroutineScope.([A](par-traverse.md)) -&gt; [B](par-traverse.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-traverse.md)&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs all mappers [f](par-traverse.md) on [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html).

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-traverse.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks.

import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
data class User(val id: Int, val createdOn: String)\
\
suspend fun main(): Unit {\
  //sampleStart\
  suspend fun getUserById(id: Int): User =\
    User(id, Thread.currentThread().name)\
\
  val res = listOf(1, 2, 3)\
    .parTraverse(Dispatchers.IO) { getUserById(it) }\
 //sampleEnd\
 println(res)\
}<!--- KNIT example-partraverse-04.kt -->
