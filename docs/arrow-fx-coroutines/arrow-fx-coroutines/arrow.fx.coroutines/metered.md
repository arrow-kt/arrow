//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[metered](metered.md)

# metered

[common]\

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

fun &lt;[A](metered.md)&gt; Flow&lt;[A](metered.md)&gt;.[metered](metered.md)(period: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)): Flow&lt;[A](metered.md)&gt;

Flow that emits [A](metered.md) every [period](metered.md) while taking into account how much time it takes downstream to consume the emission. If downstream takes longer to process than [period](metered.md) than it immediately emits another [A](metered.md).

Use onEach { delay(timeMillis) } for an alternative that sleeps [period](metered.md) between every element. This is different in that the time between every element is equal to the specified period, regardless of how much time it takes to process that tick downstream.

i.e, for a period of 1 second and a delay(100), the timestamps of the emission would be 1s, 2s, 3s, ... when using [fixedRate](fixed-rate.md). Whereas with onEach { delay(timeMillis) } it would run at timestamps 1s, 2.1s, 3.2s, ...

## Parameters

common

| | |
|---|---|
| period | period between [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) emits of the resulting Flow. |

[common]\
fun &lt;[A](metered.md)&gt; Flow&lt;[A](metered.md)&gt;.[metered](metered.md)(period: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)): Flow&lt;[A](metered.md)&gt;
