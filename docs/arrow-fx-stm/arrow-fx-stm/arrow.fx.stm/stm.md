//[arrow-fx-stm](../../index.md)/[arrow.fx.stm](index.md)/[stm](stm.md)

# stm

[common]\
inline fun &lt;[A](stm.md)&gt; [stm](stm.md)(noinline f: [STM](-s-t-m/index.md).() -&gt; [A](stm.md)): [STM](-s-t-m/index.md).() -&gt; [A](stm.md)

Helper to create stm blocks that can be run with [STM.orElse](-s-t-m/or-else.md)

import arrow.fx.stm.atomically\
import arrow.fx.stm.stm\
\
suspend fun main() {\
  //sampleStart\
  val i = 4\
  val result = atomically {\
    stm {\
      if (i == 4) retry()\
      "Not 4"\
    } orElse { "4" }\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-55.kt -->

Equal to [suspend](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/index.html) just with an [STM](-s-t-m/index.md) receiver.
