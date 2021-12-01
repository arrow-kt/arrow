//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[insert](insert.md)

# insert

[common]\
open fun &lt;[K](insert.md), [V](insert.md)&gt; [TMap](../-t-map/index.md)&lt;[K](insert.md), [V](insert.md)&gt;.[insert](insert.md)(k: [K](insert.md), v: [V](insert.md))

Add a key value pair to the map

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  atomically {\
    tmap.insert(10, "Hello")\
  }\
  //sampleEnd\
}<!--- KNIT example-stm-46.kt -->

[common]\
open fun &lt;[A](insert.md)&gt; [TSet](../-t-set/index.md)&lt;[A](insert.md)&gt;.[insert](insert.md)(a: [A](insert.md))

Adds an element to the set.

import arrow.fx.stm.TSet\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tset = TSet.new&lt;String&gt;()\
  atomically {\
    tset.insert("Hello")\
  }\
  //sampleEnd\
}<!--- KNIT example-stm-52.kt -->
