//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[parSequence](par-sequence.md)

# parSequence

[common]\
suspend fun &lt;[A](par-sequence.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [A](par-sequence.md)&gt;.[parSequence](par-sequence.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence.md)&gt;

suspend fun &lt;[A](par-sequence.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend () -&gt; [A](par-sequence.md)&gt;.[parSequence](par-sequence.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence.md)&gt;

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceScoped")

suspend fun &lt;[A](par-sequence.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [A](par-sequence.md)&gt;.[parSequence](par-sequence.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence.md)&gt;

Sequences all tasks in parallel on Dispatchers.Default and return the result

Cancelling this operation cancels all running tasks.

import arrow.fx.coroutines.*\
\
typealias Task = suspend () -&gt; Unit\
\
suspend fun main(): Unit {\
  //sampleStart\
  fun getTask(id: Int): Task =\
    suspend { println("Working on task $id on ${Thread.currentThread().name}") }\
\
  val res = listOf(1, 2, 3)\
    .map(::getTask)\
    .parSequence()\
  //sampleEnd\
  println(res)\
}<!--- KNIT example-partraverse-01.kt -->

[common]\

@[JvmName](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-name/index.html)(name = "parSequenceScoped")

suspend fun &lt;[A](par-sequence.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;suspend CoroutineScope.() -&gt; [A](par-sequence.md)&gt;.[parSequence](par-sequence.md)(ctx: [CoroutineContext](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-coroutine-context/index.html) = EmptyCoroutineContext): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](par-sequence.md)&gt;

Sequences all tasks in parallel and return the result

Coroutine context is inherited from a CoroutineScope, additional context elements can be specified with [ctx](par-sequence.md) argument. If the combined context does not have any dispatcher nor any other [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), then Dispatchers.Default is used. **WARNING** If the combined context has a single threaded [ContinuationInterceptor](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.coroutines/-continuation-interceptor/index.html), this function will not run in parallel.

Cancelling this operation cancels all running tasks.

import arrow.fx.coroutines.*\
import kotlinx.coroutines.Dispatchers\
\
typealias Task = suspend () -&gt; Unit\
\
suspend fun main(): Unit {\
  //sampleStart\
  fun getTask(id: Int): Task =\
    suspend { println("Working on task $id on ${Thread.currentThread().name}") }\
\
  val res = listOf(1, 2, 3)\
    .map(::getTask)\
    .parSequence(Dispatchers.IO)\
  //sampleEnd\
  println(res)\
}<!--- KNIT example-partraverse-02.kt -->
