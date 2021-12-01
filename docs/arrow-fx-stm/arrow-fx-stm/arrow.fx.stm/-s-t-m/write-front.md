//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[writeFront](write-front.md)

# writeFront

[common]\
open fun &lt;[A](write-front.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](write-front.md)&gt;.[writeFront](write-front.md)(a: [A](write-front.md))

Prepend an element to the [TQueue](../-t-queue/index.md).

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  atomically {\
    tq.write(1)\
    tq.writeFront(2)\
  }\
  //sampleEnd\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-stm-34.kt -->

Mainly used to implement [TQueue.peek](peek.md) and since this writes to the read variable of a [TQueue](../-t-queue/index.md) excessive use can lead to contention on consumers. Prefer appending to a [TQueue](../-t-queue/index.md) if possible.

This function never retries.
