//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[update](update.md)

# update

[common]\
open fun &lt;[K](update.md), [V](update.md)&gt; [TMap](../-t-map/index.md)&lt;[K](update.md), [V](update.md)&gt;.[update](update.md)(k: [K](update.md), fn: ([V](update.md)) -&gt; [V](update.md))

Update a value at a key if it exists.

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  val result = atomically {\
    tmap[2] = "Hello"\
    tmap.update(2) { it.reversed() }\
    tmap[2]\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-49.kt -->
