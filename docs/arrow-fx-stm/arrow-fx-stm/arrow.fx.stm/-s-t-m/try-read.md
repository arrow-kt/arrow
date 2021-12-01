//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[tryRead](try-read.md)

# tryRead

[common]\
open fun &lt;[A](try-read.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](try-read.md)&gt;.[tryRead](try-read.md)(): [A](try-read.md)?

Same as [TMVar.read](read.md) except that it returns null if the [TMVar](../-t-m-var/index.md) is empty and thus never retries.

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.empty&lt;Int&gt;()\
  val result = atomically {\
    tmvar.tryRead()\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-16.kt -->

## See also

common

| | |
|---|---|
| [arrow.fx.stm.STM](try-take.md) | for a function that leaves the [TMVar](../-t-m-var/index.md) empty after reading. |

[common]\
open fun &lt;[A](try-read.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](try-read.md)&gt;.[tryRead](try-read.md)(): [A](try-read.md)?

Same as [TQueue.read](read.md) except it returns null if the [TQueue](../-t-queue/index.md) is empty.

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.tryRead()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-stm-30.kt -->

This function never retries.
