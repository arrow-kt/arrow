---
library: fx
---
{: data-executable="true"}
```kotlin
import arrow.fx.IO
import arrow.fx.extensions.fx
import kotlinx.coroutines.newSingleThreadContext

//sampleStart


val Computation = newSingleThreadContext("Computation")
val BlockingIO = newSingleThreadContext("Blocking IO")
val UI = newSingleThreadContext("UI")

suspend fun main(): Unit = IO.fx {
  continueOn(Computation)
  val t1 = !effect { Thread.currentThread().name }
  continueOn(BlockingIO)
  val t2 = !effect { Thread.currentThread().name }
  continueOn(UI)
  val t3 = !effect { Thread.currentThread().name }
  !effect { println("$t1 ~> $t2 ~> $t3") }
}.suspended()
//sampleEnd
```
