//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[exponential](exponential.md)

# exponential

[common]\
fun &lt;[A](exponential.md)&gt; [exponential](exponential.md)(base: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), factor: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = 2.0): [Schedule](../index.md)&lt;[A](exponential.md), [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt;

Creates a Schedule that increases its delay exponentially with a given factor and base. Delays can be calculated as [base](exponential.md) * factor ^ n where n is the number of executions.

## Parameters

common

| | |
|---|---|
| base | the base delay in nanoseconds |

[common]\

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

fun &lt;[A](exponential.md)&gt; [exponential](exponential.md)(base: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), factor: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = 2.0): [Schedule](../index.md)&lt;[A](exponential.md), [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)&gt;

Creates a Schedule that increases its delay exponentially with a given factor and base. Delays can be calculated as [base](exponential.md) * factor ^ n where n is the number of executions.
