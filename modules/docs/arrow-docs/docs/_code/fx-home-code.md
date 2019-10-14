---
library: fx
---
{: data-executable="true"}
```kotlin
import arrow.core.Tuple2
import arrow.fx.IO
import arrow.unsafe
import arrow.fx.extensions.io.unsafeRun.runBlocking
import arrow.fx.extensions.fx

suspend fun threadName(): String =
    Thread.currentThread().name
//sampleStart


val program = IO.fx {
    val (threadA: String, threadB: String) =
    !dispatchers().default().parMapN(
        effect { threadName() },
        effect { threadName() },
        ::Tuple2
    )
    !effect { println("Visited $threadA, $threadB") }
}
//sampleEnd
fun main() { // The edge of our world
  unsafe { runBlocking { program } }
}
```
