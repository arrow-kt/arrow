//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[remove](remove.md)

# remove

[common]\
open fun &lt;[K](remove.md), [V](remove.md)&gt; [TMap](../-t-map/index.md)&lt;[K](remove.md), [V](remove.md)&gt;.[remove](remove.md)(k: [K](remove.md))

Remove a key value pair from a map

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  atomically {\
    tmap[1] = "Hello"\
    tmap.remove(1)\
  }\
  //sampleEnd\
}<!--- KNIT example-stm-50.kt -->

[common]\
open fun &lt;[A](remove.md)&gt; [TSet](../-t-set/index.md)&lt;[A](remove.md)&gt;.[remove](remove.md)(a: [A](remove.md))

Remove an element from the set.

import arrow.fx.stm.TSet\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tset = TSet.new&lt;String&gt;()\
  atomically {\
    tset.insert("Hello")\
    tset.remove("Hello")\
  }\
  //sampleEnd\
}<!--- KNIT example-stm-54.kt -->
