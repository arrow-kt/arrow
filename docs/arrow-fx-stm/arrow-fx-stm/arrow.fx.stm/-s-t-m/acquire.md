//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[acquire](acquire.md)

# acquire

[common]\
open fun [TSemaphore](../-t-semaphore/index.md).[acquire](acquire.md)()

Acquire 1 permit from a [TSemaphore](../-t-semaphore/index.md).

import arrow.fx.stm.TSemaphore\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tsem = TSemaphore.new(5)\
  atomically {\
    tsem.acquire()\
  }\
  //sampleEnd\
  println("Permits remaining ${atomically { tsem.available() }}")\
}<!--- KNIT example-stm-21.kt -->

This function will retry if there are no permits available.

## See also

common

| | |
|---|---|
| [arrow.fx.stm.STM](try-acquire.md) | for a version that does not retry. |

[common]\
open fun [TSemaphore](../-t-semaphore/index.md).[acquire](acquire.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html))

Acquire [n](acquire.md) permit from a [TSemaphore](../-t-semaphore/index.md).

import arrow.fx.stm.TSemaphore\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tsem = TSemaphore.new(5)\
  atomically {\
    tsem.acquire(3)\
  }\
  //sampleEnd\
  println("Permits remaining ${atomically { tsem.available() }}")\
}<!--- KNIT example-stm-22.kt -->

This function will retry if there are less than [n](acquire.md) permits available.

## See also

common

| | |
|---|---|
| [arrow.fx.stm.STM](try-acquire.md) | for a version that does not retry. |
