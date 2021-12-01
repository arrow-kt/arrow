//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[tryPeek](try-peek.md)

# tryPeek

[common]\
open fun &lt;[A](try-peek.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](try-peek.md)&gt;.[tryPeek](try-peek.md)(): [A](try-peek.md)?

Same as [TQueue.peek](peek.md) except it returns null if the [TQueue](../-t-queue/index.md) is empty.

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.tryPeek()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-stm-33.kt -->

This function never retries.

## See also

common

| | |
|---|---|
| [arrow.fx.stm.STM](peek.md) | for a version that retries if the [TQueue](../-t-queue/index.md) is empty. |
