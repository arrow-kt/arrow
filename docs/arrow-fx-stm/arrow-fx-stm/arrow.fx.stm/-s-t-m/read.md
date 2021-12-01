//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[read](read.md)

# read

[common]\
abstract fun &lt;[A](read.md)&gt; [TVar](../-t-var/index.md)&lt;[A](read.md)&gt;.[read](read.md)(): [A](read.md)

Read the value from a [TVar](../-t-var/index.md).

import arrow.fx.stm.TVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tvar = TVar.new(10)\
  val result = atomically {\
    tvar.read()\
  }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-stm-07.kt -->

This comes with a few guarantees:

<ul><li>Any given [TVar](../-t-var/index.md) is only ever read once during a transaction.</li><li>When committing the transaction the value read has to be equal to the current value otherwise the transaction will retry</li></ul>

[common]\
open fun &lt;[A](read.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](read.md)&gt;.[read](read.md)(): [A](read.md)

Read a value from a [TMVar](../-t-m-var/index.md) without removing it.

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.new(30)\
  val result = atomically {\
    tmvar.read()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-stm-13.kt -->

This retries if the [TMVar](../-t-m-var/index.md) is empty but does not take the value out if it succeeds.

## See also

common

| | |
|---|---|
| [arrow.fx.stm.STM](take.md) | for a version that leaves the [TMVar](../-t-m-var/index.md) empty after reading. |

[common]\
open fun &lt;[A](read.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](read.md)&gt;.[read](read.md)(): [A](read.md)

Remove the front element from the [TQueue](../-t-queue/index.md) or retry if the [TQueue](../-t-queue/index.md) is empty.

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  val result = atomically {\
    tq.write(2)\
    tq.read()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-stm-29.kt -->

## See also

common

| | |
|---|---|
| [arrow.fx.stm.STM](peek.md) | for a version that does not remove the element. |
