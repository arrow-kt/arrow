//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Resource](../index.md)/[Companion](index.md)/[invoke](invoke.md)

# invoke

[common]\
operator fun &lt;[A](invoke.md)&gt; [invoke](invoke.md)(acquire: suspend () -&gt; [A](invoke.md), release: suspend ([A](invoke.md), [ExitCase](../../-exit-case/index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Resource](../index.md)&lt;[A](invoke.md)&gt;

Construct a [Resource](../index.md) from a allocating function [acquire](invoke.md) and a release function [release](invoke.md).

import arrow.fx.coroutines.*\
\
suspend fun acquireResource(): Int = 42.also { println("Getting expensive resource") }\
suspend fun releaseResource(r: Int, exitCase: ExitCase): Unit = println("Releasing expensive resource: $r, exit: $exitCase")\
\
suspend fun main(): Unit {\
  //sampleStart\
  val resource = Resource(::acquireResource, ::releaseResource)\
  resource.use {\
    println("Expensive resource under use! $it")\
  }\
  //sampleEnd\
}<!--- KNIT example-resource-08.kt -->
