//[arrow-fx-coroutines-test](../../index.md)/[arrow.fx.coroutines](index.md)/[assertThrowable](assert-throwable.md)

# assertThrowable

[common]\
inline fun &lt;[A](assert-throwable.md)&gt; [assertThrowable](assert-throwable.md)(executable: () -&gt; [A](assert-throwable.md)): [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)

Example usage:

import arrow.fx.coroutines.assertThrowable\
\
fun main() {\
  val exception = assertThrowable&lt;IllegalArgumentException&gt; {\
    throw IllegalArgumentException("Talk to a duck")\
  }\
  require("Talk to a duck" == exception.message)\
}<!--- KNIT example-predef-test-01.kt -->

## See also

common

| | |
|---|---|
| Assertions.assertThrows |  |
