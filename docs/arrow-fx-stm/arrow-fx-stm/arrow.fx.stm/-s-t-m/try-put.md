//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[tryPut](try-put.md)

# tryPut

[common]\
open fun &lt;[A](try-put.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](try-put.md)&gt;.[tryPut](try-put.md)(a: [A](try-put.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Same as [TMVar.put](put.md) except that it returns true or false if was successful or it retried.

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.new(20)\
  val result = atomically {\
    tmvar.tryPut(30)\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-stm-15.kt -->

This function never retries.

## See also

common

| | |
|---|---|
| [arrow.fx.stm.STM](put.md) | for a function that retries if the [TMVar](../-t-m-var/index.md) is not empty. |
