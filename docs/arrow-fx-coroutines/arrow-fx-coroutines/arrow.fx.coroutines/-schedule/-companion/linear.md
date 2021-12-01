//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[linear](linear.md)

# linear

[common]\
fun &lt;[A](linear.md)&gt; [linear](linear.md)(base: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)): [Schedule](../index.md)&lt;[A](linear.md), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;

Creates a Schedule which increases its delay linearly, by n * base where n is the number of executions.

## Parameters

common

| | |
|---|---|
| base | the base delay in nanoseconds |

[common]\

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

fun &lt;[A](linear.md)&gt; [linear](linear.md)(base: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)): [Schedule](../index.md)&lt;[A](linear.md), [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)&gt;

Creates a Schedule which increases its delay linearly, by n * base where n is the number of executions.
