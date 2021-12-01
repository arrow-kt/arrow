//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parTraverseValidated](par-traverse-validated.md)

# parTraverseValidated

[common]\
suspend fun &lt;[E](par-traverse-validated.md), [A](par-traverse-validated.md), [B](par-traverse-validated.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](par-traverse-validated.md)&gt;.[parTraverseValidated](par-traverse-validated.md)(semigroup: [Semigroup](../../../arrow-core/arrow-core/arrow.typeclasses/-semigroup/index.md)&lt;[E](par-traverse-validated.md)&gt;, f: suspend CoroutineScope.([A](par-traverse-validated.md)) -&gt; [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-traverse-validated.md), [B](par-traverse-validated.md)&gt;): [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-traverse-validated.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-traverse-validated.md)&gt;&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs all mappers [f](par-traverse-validated.md) on Dispatchers.Default. If one or more of the [f](par-traverse-validated.md) returns [Validated.Invalid](../../../arrow-core/arrow-core/arrow.core/-validated/-invalid/index.md) then all the [Validated.Invalid](../../../arrow-core/arrow-core/arrow.core/-validated/-invalid/index.md) results will be combined using [semigroup](par-traverse-validated.md).

Cancelling this operation cancels all running tasks.

[common]\
suspend fun &lt;[E](par-traverse-validated.md), [A](par-traverse-validated.md), [B](par-traverse-validated.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](par-traverse-validated.md)&gt;.[parTraverseValidated](par-traverse-validated.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, semigroup: [Semigroup](../../../arrow-core/arrow-core/arrow.typeclasses/-semigroup/index.md)&lt;[E](par-traverse-validated.md)&gt;, f: suspend CoroutineScope.([A](par-traverse-validated.md)) -&gt; [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-traverse-validated.md), [B](par-traverse-validated.md)&gt;): [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-traverse-validated.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-traverse-validated.md)&gt;&gt;

Traverses this [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html) and runs all mappers [f](par-traverse-validated.md) on [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html). If one or more of the [f](par-traverse-validated.md) returns [Validated.Invalid](../../../arrow-core/arrow-core/arrow.core/-validated/-invalid/index.md) then all the [Validated.Invalid](../../../arrow-core/arrow-core/arrow.core/-validated/-invalid/index.md) results will be combined using [semigroup](par-traverse-validated.md).

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-traverse-validated.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks.

import arrow.core.*\
import arrow.typeclasses.Semigroup\
import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
object Error\
data class User(val id: Int, val createdOn: String)\
\
suspend fun main(): Unit {\
  //sampleStart\
  suspend fun getUserById(id: Int): ValidatedNel&lt;Error, User&gt; =\
    if(id % 2 == 0) Error.invalidNel()\
    else User(id, Thread.currentThread().name).validNel()\
\
  val res = listOf(1, 3, 5)\
    .parTraverseValidated(Dispatchers.IO, Semigroup.nonEmptyList()) { getUserById(it) }\
\
  val res2 = listOf(1, 2, 3, 4, 5)\
    .parTraverseValidated(Dispatchers.IO, Semigroup.nonEmptyList()) { getUserById(it) }\
 //sampleEnd\
 println(res)\
 println(res2)\
}<!--- KNIT example-partraversevalidated-02.kt -->
