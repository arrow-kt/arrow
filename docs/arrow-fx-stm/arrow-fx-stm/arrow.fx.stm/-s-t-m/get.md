//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[get](get.md)

# get

[common]\
open operator fun &lt;[A](get.md)&gt; [TArray](../-t-array/index.md)&lt;[A](get.md)&gt;.[get](get.md)(i: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [A](get.md)

Read a variable from the [TArray](../-t-array/index.md).

import arrow.fx.stm.TArray\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tarr = TArray.new(size = 10, 2)\
  val result = atomically {\
    tarr[5]\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-39.kt -->

Throws if [i](get.md) is out of bounds.

This function never retries.

[common]\
open operator fun &lt;[K](get.md), [V](get.md)&gt; [TMap](../-t-map/index.md)&lt;[K](get.md), [V](get.md)&gt;.[get](get.md)(k: [K](get.md)): [V](get.md)?

Alias of [STM.lookup](lookup.md)

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
    tmap[2]\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-45.kt -->

If the key is not present [STM.get](get.md) will not retry, instead it returns null.
