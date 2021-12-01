//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[take](take.md)

# take

[common]\
open fun &lt;[A](take.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](take.md)&gt;.[take](take.md)(): [A](take.md)

Read the value from a [TMVar](../-t-m-var/index.md) and empty it.

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.new(10)\
  val result = atomically {\
    tmvar.take()\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-stm-11.kt -->

This retries if the [TMVar](../-t-m-var/index.md) is empty and leaves the [TMVar](../-t-m-var/index.md) empty if it succeeded.

## See also

common

| | |
|---|---|
| [arrow.fx.stm.STM](read.md) | for a version that does not remove the value after reading. |
