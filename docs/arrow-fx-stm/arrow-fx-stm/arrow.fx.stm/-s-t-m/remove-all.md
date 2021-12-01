//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[removeAll](remove-all.md)

# removeAll

[common]\
open fun &lt;[A](remove-all.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](remove-all.md)&gt;.[removeAll](remove-all.md)(pred: ([A](remove-all.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))

Filter a [TQueue](../-t-queue/index.md), removing all elements for which [pred](remove-all.md) returns false.

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  atomically {\
    tq.write(0)\
    tq.removeAll { it != 0 }\
  }\
  //sampleEnd\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-stm-37.kt -->

This function never retries.

This function has to access both [TVar](../-t-var/index.md)'s and thus may lead to increased contention, use sparingly.
