//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[release](release.md)

# release

[common]\
open fun [TSemaphore](../-t-semaphore/index.md).[release](release.md)()

Release a permit back to the [TSemaphore](../-t-semaphore/index.md).

import arrow.fx.stm.TSemaphore\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tsem = TSemaphore.new(5)\
  atomically {\
    tsem.release()\
  }\
  //sampleEnd\
  println("Permits remaining ${atomically { tsem.available() }}")\
}<!--- KNIT example-stm-25.kt -->

This function never retries.

[common]\
open fun [TSemaphore](../-t-semaphore/index.md).[release](release.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))

Release [n](release.md) permits back to the [TSemaphore](../-t-semaphore/index.md).

import arrow.fx.stm.TSemaphore\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tsem = TSemaphore.new(5)\
  atomically {\
    tsem.release(2)\
  }\
  //sampleEnd\
  println("Permits remaining ${atomically { tsem.available() }}")\
}<!--- KNIT example-stm-26.kt -->

[n](release.md) must be non-negative.

This function never retries.
