//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[fibonacci](fibonacci.md)

# fibonacci

[common]\
fun &lt;[A](fibonacci.md)&gt; [fibonacci](fibonacci.md)(one: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Schedule](../index.md)&lt;[A](fibonacci.md), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;

Creates a Schedule that continues with increasing delay by adding the last two delays.

## Parameters

common

| | |
|---|---|
| one | initial delay in nanoseconds |

[common]\

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

fun &lt;[A](fibonacci.md)&gt; [fibonacci](fibonacci.md)(one: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)): [Schedule](../index.md)&lt;[A](fibonacci.md), [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)&gt;

Creates a Schedule that continues with increasing delay by adding the last two delays.
