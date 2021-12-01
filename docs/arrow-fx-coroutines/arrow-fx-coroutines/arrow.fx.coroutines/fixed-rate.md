//[arrow-fx-coroutines](../../index.md)/[arrow.fx.coroutines](index.md)/[fixedRate](fixed-rate.md)

# fixedRate

[common]\

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

fun [fixedRate](fixed-rate.md)(period: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), dampen: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, timeStampInMillis: () -&gt; [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) = { timeInMillis() }): Flow&lt;[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt;

[common]\
fun [fixedRate](fixed-rate.md)(period: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html), dampen: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, timeStampInMillis: () -&gt; [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) = { timeInMillis() }): Flow&lt;[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt;

Flow that emits [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) every [period](fixed-rate.md) while taking into account how much time it takes downstream to consume the emission. If downstream takes longer to process than [period](fixed-rate.md) than it immediately emits another [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html), if you set [dampen](fixed-rate.md) to false it will send n = downstreamTime / period[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) elements immediately.

Use onEach { delay(timeMillis) } for an alternative that sleeps [period](fixed-rate.md) between every element. This is different in that the time between every element is equal to the specified period, regardless of how much time it takes to process that tick downstream.

i.e, for a period of 1 second and a delay(100), the timestamps of the emission would be 1s, 2s, 3s, ... when using [fixedRate](fixed-rate.md). Whereas with onEach { delay(timeMillis) } it would run at timestamps 1s, 2.1s, 3.2s, ...

## Parameters

common

| | |
|---|---|
| period | period between [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) emits of the resulting Flow. |
| dampen | if you set [dampen](fixed-rate.md) to false it will send n times [period](fixed-rate.md) time it took downstream to process the emission. |
| timeStampInMillis | allows for supplying a different timestamp function, useful to override with runBlockingTest |
