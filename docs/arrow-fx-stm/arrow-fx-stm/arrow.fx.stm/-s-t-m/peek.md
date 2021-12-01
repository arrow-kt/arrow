//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[peek](peek.md)

# peek

[common]\
open fun &lt;[A](peek.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](peek.md)&gt;.[peek](peek.md)(): [A](peek.md)

Read the front element of a [TQueue](../-t-queue/index.md) without removing it.

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.write(2)\
\
    tq.peek()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-stm-32.kt -->

This function retries if the [TQueue](../-t-queue/index.md) is empty.

## See also

common

| | |
|---|---|
| [arrow.fx.stm.STM](try-peek.md) | for a version that does not retry. |
