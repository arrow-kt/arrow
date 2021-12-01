//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[tryAcquire](try-acquire.md)

# tryAcquire

[common]\
open fun [TSemaphore](../-t-semaphore/index.md).[tryAcquire](try-acquire.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Like [TSemaphore.acquire](acquire.md) except that it returns whether or not acquisition was successful.

import arrow.fx.stm.TSemaphore\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tsem = TSemaphore.new(0)\
  val result = atomically {\
    tsem.tryAcquire()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Permits remaining ${atomically { tsem.available() }}")\
}<!--- KNIT example-stm-23.kt -->

This function never retries.

## See also

common

| | |
|---|---|
| [arrow.fx.stm.STM](acquire.md) | for a version that retries if there are not enough permits. |

[common]\
open fun [TSemaphore](../-t-semaphore/index.md).[tryAcquire](try-acquire.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Like [TSemaphore.acquire](acquire.md) except that it returns whether or not acquisition was successful.

import arrow.fx.stm.TSemaphore\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tsem = TSemaphore.new(0)\
  val result = atomically {\
    tsem.tryAcquire(3)\
  }\
  //sampleEnd\
  println("Result $result")\
  println("Permits remaining ${atomically { tsem.available() }}")\
}<!--- KNIT example-stm-24.kt -->

This function never retries.

## See also

common

| | |
|---|---|
| [arrow.fx.stm.STM](acquire.md) | for a version that retries if there are not enough permits. |
