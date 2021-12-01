//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[orElse](or-else.md)

# orElse

[common]\
abstract infix fun &lt;[A](or-else.md)&gt; [STM](index.md).() -&gt; [A](or-else.md).[orElse](or-else.md)(other: [STM](index.md).() -&gt; [A](or-else.md)): [A](or-else.md)

Run the given transaction and fallback to the other one if the first one calls [retry](retry.md).

import arrow.fx.stm.atomically\
import arrow.fx.stm.stm\
\
suspend fun main() {\
  //sampleStart\
  val result = atomically {\
    stm { retry() } orElse { "Alternative" }\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-05.kt -->
