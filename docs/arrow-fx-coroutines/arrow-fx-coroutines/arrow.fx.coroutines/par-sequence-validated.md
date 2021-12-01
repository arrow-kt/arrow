//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parSequenceValidated](par-sequence-validated.md)

# parSequenceValidated

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceValidatedScoped")

suspend fun &lt;[E](par-sequence-validated.md), [A](par-sequence-validated.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-sequence-validated.md), [A](par-sequence-validated.md)&gt;&gt;.[parSequenceValidated](par-sequence-validated.md)(semigroup: [Semigroup](../../../arrow-core/arrow-core/arrow.typeclasses/-semigroup/index.md)&lt;[E](par-sequence-validated.md)&gt;): [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-sequence-validated.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-validated.md)&gt;&gt;

Sequences all tasks in parallel on Dispatchers.Default and returns the result. If one or more of the tasks returns [Validated.Invalid](../../../arrow-core/arrow-core/arrow.core/-validated/-invalid/index.md) then all the [Validated.Invalid](../../../arrow-core/arrow-core/arrow.core/-validated/-invalid/index.md) results will be combined using [semigroup](par-sequence-validated.md).

Cancelling this operation cancels all running tasks.

[common]\
suspend fun &lt;[E](par-sequence-validated.md), [A](par-sequence-validated.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-sequence-validated.md), [A](par-sequence-validated.md)&gt;&gt;.[parSequenceValidated](par-sequence-validated.md)(semigroup: [Semigroup](../../../arrow-core/arrow-core/arrow.typeclasses/-semigroup/index.md)&lt;[E](par-sequence-validated.md)&gt;): [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-sequence-validated.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-validated.md)&gt;&gt;

suspend fun &lt;[E](par-sequence-validated.md), [A](par-sequence-validated.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-sequence-validated.md), [A](par-sequence-validated.md)&gt;&gt;.[parSequenceValidated](par-sequence-validated.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, semigroup: [Semigroup](../../../arrow-core/arrow-core/arrow.typeclasses/-semigroup/index.md)&lt;[E](par-sequence-validated.md)&gt;): [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-sequence-validated.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-validated.md)&gt;&gt;

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceValidatedScoped")

suspend fun &lt;[E](par-sequence-validated.md), [A](par-sequence-validated.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-sequence-validated.md), [A](par-sequence-validated.md)&gt;&gt;.[parSequenceValidated](par-sequence-validated.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext, semigroup: [Semigroup](../../../arrow-core/arrow-core/arrow.typeclasses/-semigroup/index.md)&lt;[E](par-sequence-validated.md)&gt;): [Validated](../../../arrow-core/arrow-core/arrow.core/-validated/index.md)&lt;[E](par-sequence-validated.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence-validated.md)&gt;&gt;

Sequences all tasks in parallel on [ctx](par-sequence-validated.md) and returns the result. If one or more of the tasks returns [Validated.Invalid](../../../arrow-core/arrow-core/arrow.core/-validated/-invalid/index.md) then all the [Validated.Invalid](../../../arrow-core/arrow-core/arrow.core/-validated/-invalid/index.md) results will be combined using [semigroup](par-sequence-validated.md).

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-sequence-validated.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks.

import arrow.core.*\
import arrow.typeclasses.Semigroup\
import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
typealias Task = suspend () -&gt; ValidatedNel&lt;Throwable, Unit&gt;\
\
suspend fun main(): Unit {\
  //sampleStart\
  fun getTask(id: Int): Task =\
    suspend { Validated.catchNel { println("Working on task $id on ${Thread.currentThread().name}") } }\
\
  val res = listOf(1, 2, 3)\
    .map(::getTask)\
    .parSequenceValidated(Dispatchers.IO, Semigroup.nonEmptyList())\
  //sampleEnd\
  println(res)\
}<!--- KNIT example-partraversevalidated-01.kt -->
