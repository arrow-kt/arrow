//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[CircuitBreaker](../index.md)/[Companion](index.md)/[of](of.md)

# of

[common]\
suspend fun [of](of.md)(maxFailures: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), resetTimeoutNanos: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), exponentialBackoffFactor: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = 1.0, maxResetTimeout: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = Double.POSITIVE_INFINITY, onRejected: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = { }, onClosed: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = { }, onHalfOpen: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = { }, onOpen: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = { }): [CircuitBreaker](../index.md)

Attempts to create a [CircuitBreaker](../index.md).

## Parameters

common

| | |
|---|---|
| maxFailures | is the maximum count for failures before     opening the circuit breaker. |
| resetTimeoutNanos | is the timeout to wait in the Open state     before attempting a close of the circuit breaker (but without     the backoff factor applied) in nanoseconds. |
| exponentialBackoffFactor | is a factor to use for resetting     the resetTimeout when in the HalfOpen state, in case     the attempt to Close fails. |
| maxResetTimeout | is the maximum timeout the circuit breaker     is allowed to use when applying the exponentialBackoffFactor. |
| onRejected | is a callback for signaling rejected tasks, so     every time a task execution is attempted and rejected in     CircuitBreaker.Open or CircuitBreaker.HalfOpen     states. |
| onClosed | is a callback for signaling transitions to the [CircuitBreaker.State.Closed](../-state/-closed/index.md) state. |
| onHalfOpen | is a callback for signaling transitions to [CircuitBreaker.State.HalfOpen](../-state/-half-open/index.md). |
| onOpen | is a callback for signaling transitions to [CircuitBreaker.State.Open](../-state/-open/index.md). |

[common]\

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

suspend fun [of](of.md)(maxFailures: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), resetTimeout: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), exponentialBackoffFactor: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = 1.0, maxResetTimeout: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html) = Duration.INFINITE, onRejected: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = suspend { }, onClosed: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = suspend { }, onHalfOpen: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = suspend { }, onOpen: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) = suspend { }): [CircuitBreaker](../index.md)

Attempts to create a [CircuitBreaker](../index.md).

## Parameters

common

| | |
|---|---|
| maxFailures | is the maximum count for failures before     opening the circuit breaker. |
| resetTimeout | is the timeout to wait in the Open state     before attempting a close of the circuit breaker (but without     the backoff factor applied). |
| exponentialBackoffFactor | is a factor to use for resetting     the resetTimeout when in the HalfOpen state, in case     the attempt to Close fails. |
| maxResetTimeout | is the maximum timeout the circuit breaker     is allowed to use when applying the exponentialBackoffFactor. |
| onRejected | is a callback for signaling rejected tasks, so     every time a task execution is attempted and rejected in     CircuitBreaker.Open or CircuitBreaker.HalfOpen     states. |
| onClosed | is a callback for signaling transitions to the [CircuitBreaker.State.Closed](../-state/-closed/index.md) state. |
| onHalfOpen | is a callback for signaling transitions to [CircuitBreaker.State.HalfOpen](../-state/-half-open/index.md). |
| onOpen | is a callback for signaling transitions to [CircuitBreaker.State.Open](../-state/-open/index.md). |
