---
library: analysis
---
{: data-executable="true"}
```kotlin
import arrow.analysis.*

fun increment(x: Int): Int {
  pre(x > 0) { "value must be positive" }
  return (x + 1).post({ it > 0 }) { "result is positive" }
}

class Positive(val value: Int) {
  init { require(value > 0) }

  fun add(other: Positive) =
    Positive(this.value + other.value)
}
```
