//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[spaced](spaced.md)

# spaced

[common]\
fun &lt;[A](spaced.md)&gt; [spaced](spaced.md)(interval: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Schedule](../index.md)&lt;[A](spaced.md), [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)&gt;

Creates a Schedule that continues with a fixed delay.

## Parameters

common

| | |
|---|---|
| interval | fixed delay in nanoseconds |

[common]\

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

fun &lt;[A](spaced.md)&gt; [spaced](spaced.md)(interval: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)): [Schedule](../index.md)&lt;[A](spaced.md), [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)&gt;

Creates a Schedule that continues with a fixed delay.

## Parameters

common

| | |
|---|---|
| interval | fixed delay in [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html) |
