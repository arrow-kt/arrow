//[arrow-fx-stm](../../index.md)/[arrow.fx.stm](index.md)/[check](check.md)

# check

[common]\
fun [STM](-s-t-m/index.md).[check](check.md)(b: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html))

Retry if [b](check.md) is false otherwise does nothing.

import arrow.fx.stm.atomically\
import arrow.fx.stm.stm\
\
suspend fun main() {\
  //sampleStart\
  val i = 4\
  val result = atomically {\
    stm {\
      check(i &lt;= 5) // This calls retry and aborts if i &lt;= 5\
      "Larger than 5"\
    } orElse { "Smaller than or equal to 5" }\
  }\
  //sampleEnd\
  println("Result $result")\
}<!--- KNIT example-stm-56.kt -->

check(b) = if (b.not()) retry() else Unit
