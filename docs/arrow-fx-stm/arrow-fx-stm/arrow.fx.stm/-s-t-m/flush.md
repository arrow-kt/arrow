//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[flush](flush.md)

# flush

[common]\
open fun &lt;[A](flush.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](flush.md)&gt;.[flush](flush.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](flush.md)&gt;

Drains all entries of a [TQueue](../-t-queue/index.md) into a single list.

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.write(2)\
    tq.write(4)\
\
    tq.flush()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-stm-31.kt -->

This function never retries.
