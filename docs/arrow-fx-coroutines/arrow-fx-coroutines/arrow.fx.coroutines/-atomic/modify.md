//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Atomic](index.md)/[modify](modify.md)

# modify

[common]\
abstract suspend fun &lt;[B](modify.md)&gt; [modify](modify.md)(f: ([A](index.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](modify.md)&gt;): [B](modify.md)

Modify allows to inspect the state [A](index.md) of the [AtomicRef](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-atomic-ref/index.md), update it and extract a different state [B](modify.md).

import arrow.fx.coroutines.*\
\
typealias Id = Int\
data class Job(val description: String)\
\
val initialState = (0 until 10).map { i -&gt; Pair(i, Job("Task #$i")) }\
\
suspend fun main(): Unit {\
  val jobs = Atomic(initialState)\
\
  val batch = jobs.modify { j -&gt;\
    val batch = j.take(5)\
    Pair(j.drop(5), batch)\
  }\
\
  batch.forEach { (id, job) -&gt;\
    println("Going to work on $job with id $id\n")\
  }\
\
  println("Remaining: ${jobs.get()}")\
}<!--- KNIT example-atomic-02.kt -->
