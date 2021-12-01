//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[size](size.md)

# size

[common]\
open fun &lt;[A](size.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](size.md)&gt;.[size](size.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

Return the current number of elements in a [TQueue](../-t-queue/index.md)

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.size()\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-38.kt -->

This function never retries.

This function has to access both [TVar](../-t-var/index.md)'s and thus may lead to increased contention, use sparingly.
