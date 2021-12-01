//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[lookup](lookup.md)

# lookup

[common]\
open fun &lt;[K](lookup.md), [V](lookup.md)&gt; [TMap](../-t-map/index.md)&lt;[K](lookup.md), [V](lookup.md)&gt;.[lookup](lookup.md)(k: [K](lookup.md)): [V](lookup.md)?

Lookup a value at the specific key [k](lookup.md)

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  val result = atomically {\
    tmap[1] = "Hello"\
    tmap[2] = "World"\
\
    tmap.lookup(1)\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-44.kt -->

If the key is not present [STM.lookup](lookup.md) will not retry, instead it returns null.
