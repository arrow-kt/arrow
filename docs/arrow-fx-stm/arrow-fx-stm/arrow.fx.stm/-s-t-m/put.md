//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[put](put.md)

# put

[common]\
open fun &lt;[A](put.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](put.md)&gt;.[put](put.md)(a: [A](put.md))

Put a value into an empty [TMVar](../-t-m-var/index.md).

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.empty&lt;Int&gt;()\
  atomically {\
    tmvar.put(20)\
  }\
  //sampleEnd\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-stm-12.kt -->

This retries if the [TMVar](../-t-m-var/index.md) is not empty.

For a version of [TMVar.put](put.md) that does not retry see [TMVar.tryPut](try-put.md)
