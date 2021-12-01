//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[transform](transform.md)

# transform

[common]\
open fun &lt;[A](transform.md)&gt; [TArray](../-t-array/index.md)&lt;[A](transform.md)&gt;.[transform](transform.md)(f: ([A](transform.md)) -&gt; [A](transform.md))

Modify each element in a [TArray](../-t-array/index.md) by applying [f](transform.md).

import arrow.fx.stm.TArray\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tarr = TArray.new(size = 10, 2)\
  val result = atomically {\
    tarr.transform { it + 1 }\
  }\
  //sampleEnd\
}<!--- KNIT example-stm-41.kt -->

This function never retries.
