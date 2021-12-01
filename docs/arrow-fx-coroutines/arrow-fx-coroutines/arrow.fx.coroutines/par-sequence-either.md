//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parSequenceEither](par-sequence-either.md)

# parSequenceEither

[common]\
suspend fun &lt;[A](par-sequence-either.md), [B](par-sequence-either.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either.md), [B](par-sequence-either.md)&gt;&gt;.[parSequenceEither](par-sequence-either.md)(): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-sequence-either.md)&gt;&gt;

Sequences all tasks in parallel on Dispatchers.Default and return the result. If one of the tasks returns [Either.Left](../../../arrow-core/arrow-core/arrow.core/-either/-left/index.md), then it will short-circuit the operation and cancelling all this running tasks, and returning the first encountered [Either.Left](../../../arrow-core/arrow-core/arrow.core/-either/-left/index.md).

Cancelling this operation cancels all running tasks.

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceEitherScoped")

suspend fun &lt;[A](par-sequence-either.md), [B](par-sequence-either.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either.md), [B](par-sequence-either.md)&gt;&gt;.[parSequenceEither](par-sequence-either.md)(): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-sequence-either.md)&gt;&gt;

suspend fun &lt;[A](par-sequence-either.md), [B](par-sequence-either.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either.md), [B](par-sequence-either.md)&gt;&gt;.[parSequenceEither](par-sequence-either.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-sequence-either.md)&gt;&gt;

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceEitherScoped")

suspend fun &lt;[A](par-sequence-either.md), [B](par-sequence-either.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either.md), [B](par-sequence-either.md)&gt;&gt;.[parSequenceEither](par-sequence-either.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext): [Either](../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](par-sequence-either.md), [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](par-sequence-either.md)&gt;&gt;

Sequences all tasks in parallel on [ctx](par-sequence-either.md) and return the result. If one of the tasks returns [Either.Left](../../../arrow-core/arrow-core/arrow.core/-either/-left/index.md), then it will short-circuit the operation and cancelling all this running tasks, and returning the first encountered [Either.Left](../../../arrow-core/arrow-core/arrow.core/-either/-left/index.md).

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-sequence-either.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks.

import arrow.core.*\
import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
object Error\
typealias Task = suspend () -&gt; Either&lt;Throwable, Unit&gt;\
\
suspend fun main(): Unit {\
  //sampleStart\
  fun getTask(id: Int): Task =\
    suspend { Either.catch { println("Working on task $id on ${Thread.currentThread().name}") } }\
\
  val res = listOf(1, 2, 3)\
    .map(::getTask)\
    .parSequenceEither(Dispatchers.IO)\
  //sampleEnd\
  println(res)\
}<!--- KNIT example-partraverseeither-01.kt -->
