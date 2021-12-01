//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[plusAssign](plus-assign.md)

# plusAssign

[common]\
open operator fun &lt;[A](plus-assign.md)&gt; [TQueue](../-t-queue/index.md)&lt;[A](plus-assign.md)&gt;.[plusAssign](plus-assign.md)(a: [A](plus-assign.md))

Append an element to the [TQueue](../-t-queue/index.md). Alias for [STM.write](write.md).

import arrow.fx.stm.TQueue\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tq = TQueue.new&lt;Int&gt;()\
  atomically {\
    tq += 2\
  }\
  //sampleEnd\
  println("Items in queue ${atomically { tq.flush() }}")\
}<!--- KNIT example-stm-28.kt -->

This function never retries.

[common]\
open operator fun &lt;[K](plus-assign.md), [V](plus-assign.md)&gt; [TMap](../-t-map/index.md)&lt;[K](plus-assign.md), [V](plus-assign.md)&gt;.[plusAssign](plus-assign.md)(kv: [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[K](plus-assign.md), [V](plus-assign.md)&gt;)

Add a key value pair to the map

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  atomically {\
    tmap += (1 to "Hello")\
  }\
  //sampleEnd\
}<!--- KNIT example-stm-48.kt -->

[common]\
open operator fun &lt;[A](plus-assign.md)&gt; [TSet](../-t-set/index.md)&lt;[A](plus-assign.md)&gt;.[plusAssign](plus-assign.md)(a: [A](plus-assign.md))

Adds an element to the set. Alias of [STM.insert](insert.md).

import arrow.fx.stm.TSet\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tset = TSet.new&lt;String&gt;()\
  atomically {\
    tset += "Hello"\
  }\
  //sampleEnd\
}<!--- KNIT example-stm-53.kt -->
