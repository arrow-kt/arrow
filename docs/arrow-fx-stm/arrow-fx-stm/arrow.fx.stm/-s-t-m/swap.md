//[arrow-fx-stm](../../../index.md)/[arrow.fx.stm](../index.md)/[STM](index.md)/[swap](swap.md)

# swap

[common]\
open fun &lt;[A](swap.md)&gt; [TVar](../-t-var/index.md)&lt;[A](swap.md)&gt;.[swap](swap.md)(a: [A](swap.md)): [A](swap.md)

Swap the content of the [TVar](../-t-var/index.md)

import arrow.fx.stm.TVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tvar = TVar.new(10)\
  val result = atomically {\
    tvar.swap(20)\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${tvar.unsafeRead()}")\
}<!--- KNIT example-stm-10.kt -->

#### Return

The previous value stored inside the [TVar](../-t-var/index.md)

[common]\
open fun &lt;[A](swap.md)&gt; [TMVar](../-t-m-var/index.md)&lt;[A](swap.md)&gt;.[swap](swap.md)(a: [A](swap.md)): [A](swap.md)

Swap the content of a [TMVar](../-t-m-var/index.md) or retry if it is empty.

import arrow.fx.stm.TMVar\
import arrow.fx.stm.atomically\
\
suspend fun main() {\
  //sampleStart\
  val tmvar = TMVar.new(30)\
  val result = atomically {\
    tmvar.swap(40)\
  }\
  //sampleEnd\
  println("Result $result")\
  println("New value ${atomically { tmvar.tryTake() } }")\
}<!--- KNIT example-stm-19.kt -->
