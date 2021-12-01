//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[member](member.md)

# member

[common]\
open fun &lt;[K](member.md), [V](member.md)&gt; [TMap](../-t-map/index.md)&lt;[K](member.md), [V](member.md)&gt;.[member](member.md)(k: [K](member.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Check if a key [k](member.md) is in the map

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  atomically {\
    tmap[1] = "Hello"\
\
    tmap.remove(1)\
  }\
  //sampleEnd\
}<!--- KNIT example-stm-43.kt -->

This function never retries.

[common]\
open fun &lt;[A](member.md)&gt; [TSet](../-t-set/index.md)&lt;[A](member.md)&gt;.[member](member.md)(a: [A](member.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Check if an element is already in the set

import arrow.fx.stm.TSet\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tset = TSet.new&lt;String&gt;()\
  val result = atomically {\
    tset.insert("Hello")\
    tset.member("Hello")\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-51.kt -->
