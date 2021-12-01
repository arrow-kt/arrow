//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[CircuitBreaker](../index.md)/[Companion](index.md)

# Companion

[common]\
object [Companion](index.md)

## Functions

| Name | Summary |
|---|---|
| [of](of.md) | [common]<br>suspend fun [of](of.md)(maxFailures: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), resetTimeoutNanos: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), exponentialBackoffFactor: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = 1.0, maxResetTimeout: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = Double.POSITIVE_INFINITY, onRejected: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = { }, onClosed: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = { }, onHalfOpen: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = { }, onOpen: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = { }): [CircuitBreaker](../index.md)<br>@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)<br>suspend fun [of](of.md)(maxFailures: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), resetTimeout: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), exponentialBackoffFactor: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = 1.0, maxResetTimeout: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html) = Duration.INFINITE, onRejected: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = suspend { }, onClosed: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = suspend { }, onHalfOpen: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = suspend { }, onOpen: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = suspend { }): [CircuitBreaker](../index.md)<br>Attempts to create a [CircuitBreaker](../index.md). |
