//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[write](write.md)

# write

[common]\
abstract fun &lt;[A](write.md)&gt; [TVar](../-t-var/index.md)&lt;[A](write.md)&gt;.[write](write.md)(a: [A](write.md))

Set the value of a [TVar](../-t-var/index.md).

import arrow.fx.stm.TVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tvar = TVar.new(10)\
  val result = atomically {\
    tvar.write(20)\
  }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-stm-08.kt -->

Similarly to [read](read.md) this comes with a few guarantees:

<ul><li>For multiple writes to the same [TVar](../-t-var/index.md) in a transaction only the last will actually be performed</li><li>When committing the value inside the [TVar](../-t-var/index.md), at the time of calling [write](write.md), has to be the same as the current value otherwise the transaction will retry</li></ul>

[common]\
open fun &lt;[A](write.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](write.md)&gt;.[write](write.md)(a: [A](write.md))

Append an element to the [TQueue](../-t-queue/index.md).

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  atomically {\
    tq.write(2)\
  }\
  //sampleEnd\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-stm-27.kt -->

This function never retries.
