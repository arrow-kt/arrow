//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[tryTake](try-take.md)

# tryTake

[common]\
open fun &lt;[A](try-take.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](try-take.md)&gt;.[tryTake](try-take.md)(): [A](try-take.md)?

Same as [TMVar.take](take.md) except it returns null if the [TMVar](../-t-m-var/index.md) is empty and thus never retries.

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.empty&lt;Int&gt;()\
  val result = atomically {\
    tmvar.tryTake()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-stm-14.kt -->
