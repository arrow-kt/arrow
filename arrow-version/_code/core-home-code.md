---
library: core
---

{: data-executable="true"}
```kotlin
import arrow.*
import arrow.core.*

val throwsSomeStuff: (Int) -> Double = {x -> x.toDouble()}
val throwsOtherThings: (Double) -> String = {x -> x.toString()}
val moreThrowing: (String) -> List<String> = {x -> listOf(x)}
val magic = throwsSomeStuff.andThen(throwsOtherThings).andThen(moreThrowing)
magic
```
