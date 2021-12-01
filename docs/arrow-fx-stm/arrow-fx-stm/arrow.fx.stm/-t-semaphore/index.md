//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[TSemaphore](index.md)

# TSemaphore

[common]\
data class [TSemaphore](index.md)

[TSemaphore](index.md) is the transactional Semaphore.

Semaphores are mostly used to limit concurrent access to resources by how many permits it can give out.

##  Creating a [TSemaphore](index.md)

A [TSemaphore](index.md) is created by using either [TSemaphore.new](-companion/new.md) outside of transactions or [STM.newTSem](../new-t-sem.md) inside a transaction. Both of these methods throw if the supplied initial value is negative.

##  Acquiring one or more permits

import arrow.fx.stm.TSemaphore\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tsem = TSemaphore.new(5)\
  atomically {\
    // acquire one permit\
    tsem.acquire()\
    // acquire 3 permits\
    tsem.acquire(3)\
  }\
  //sampleEnd\
  println("Permits remaining ${atomically { tsem.available() }}")\
}<!--- KNIT example-tsemaphore-01.kt -->

Should there be not enough permits the transaction will retry and wait until there are enough permits available again. [STM.tryAcquire](../-s-t-m/try-acquire.md) can be used to avoid this behaviour as it returns whether or not acquisition was successful.

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
}<!--- KNIT example-tsemaphore-02.kt -->

##  Release permits after use:

Permits can be released again using [STM.release](../-s-t-m/release.md):

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
}<!--- KNIT example-tsemaphore-03.kt -->

As you can see there is no upper limit enforced when releasing. You are free to release more or less permits than you have taken, but that may invalidate some other implicit rules so doing so is not advised.

[STM.release](../-s-t-m/release.md) will throw if given a negative number of permits.

##  Reading how many permits are currently available

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
}<!--- KNIT example-tsemaphore-04.kt -->

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |
