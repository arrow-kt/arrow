//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[set](set.md)

# set

[common]\
open operator fun &lt;[A](set.md)&gt; [TArray](../-t-array/index.md)&lt;[A](set.md)&gt;.[set](set.md)(i: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), a: [A](set.md))

Set a variable in the [TArray](../-t-array/index.md).

import arrow.fx.stm.TArray\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tarr = TArray.new(size = 10, 2)\
  val result = atomically {\
    tarr[5] = 3\
\
    tarr[5]\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-40.kt -->

Throws if [i](set.md) is out of bounds.

This function never retries.

[common]\
open operator fun &lt;[K](set.md), [V](set.md)&gt; [TMap](../-t-map/index.md)&lt;[K](set.md), [V](set.md)&gt;.[set](set.md)(k: [K](set.md), v: [V](set.md))

Alias for [STM.insert](insert.md)

import arrow.fx.stm.TMap\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmap = TMap.new&lt;Int, String&gt;()\
  atomically {\
    tmap[1] = "Hello"\
  }\
  //sampleEnd\
}<!--- KNIT example-stm-47.kt -->
