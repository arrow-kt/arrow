//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[available](available.md)

# available

[common]\
open fun [TSemaphore](../-t-semaphore/index.md).[available](available.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)

Returns the currently available number of permits in a [TSemaphore](../-t-semaphore/index.md).

import arrow.fx.stm.TSemaphore\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tsem = TSemaphore.new(5)\
  val result = atomically {\
    tsem.available()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Permits remaining ${atomically { tsem.available() }}")\
}<!--- KNIT example-stm-20.kt -->

This function never retries.
