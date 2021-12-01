//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[modify](modify.md)

# modify

[common]\
open fun &lt;[A](modify.md)&gt; [TVar](../-t-var/index.md)&lt;[A](modify.md)&gt;.[modify](modify.md)(f: ([A](modify.md)) -&gt; [A](modify.md))

Modify the value of a [TVar](../-t-var/index.md)

import arrow.fx.stm.TVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tvar = TVar.new(10)\
  val result = atomically {\
    tvar.modify { it * 2 }\
  }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-stm-09.kt -->

modify(f) = write(f(read()))
