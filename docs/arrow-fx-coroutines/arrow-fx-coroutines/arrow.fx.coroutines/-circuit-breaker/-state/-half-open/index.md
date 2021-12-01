//[arrow-fx-coroutines](../../../../../index.md)/[arrow.fx.coroutines](../../../index.md)/[CircuitBreaker](../../index.md)/[State](../index.md)/[HalfOpen](index.md)

# HalfOpen

[common]\
class [HalfOpen](index.md) : [CircuitBreaker.State](../index.md)

The [CircuitBreaker](../../index.md) is in [HalfOpen](index.md) state while it's allowing a test request to go through.

<ul><li>All other requests made while the test request is still running will short-circuit/fail-fast.</li><li>If the test request succeeds, then the [CircuitBreaker](../../index.md) is tripped back into [Closed](../-closed/index.md), with the reset timeout, and the failures count also reset to their initial values.</li><li>If the test request fails, then the [CircuitBreaker](../../index.md) is tripped back into [Open](../-open/index.md), the [resetTimeout](../../../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/reset-timeout.md) is multiplied by the [exponentialBackoffFactor](../../../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/exponential-backoff-factor.md), up to the configured [maxResetTimeout](../../../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/max-reset-timeout.md).</li></ul>

## Parameters

common

| | |
|---|---|
| resetTimeoutNanos | is the current reset timeout that the [CircuitBreaker](../../index.md) has to stay in [Open](../-open/index.md) state. When the reset timeout lapsed, than the [CircuitBreaker](../../index.md) will allow a test request to go through in [HalfOpen](index.md). If the test request failed, the [CircuitBreaker](../../index.md) will go back into [Open](../-open/index.md) and it'll multiply the [resetTimeoutNanos](reset-timeout-nanos.md) with the the exponential backoff factor. |

## Constructors

| | |
|---|---|
| [HalfOpen](-half-open.md) | [common]<br>fun [HalfOpen](-half-open.md)(resetTimeoutNanos: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [toString](to-string.md) | [common]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

## Properties

| Name | Summary |
|---|---|
| [resetTimeoutNanos](reset-timeout-nanos.md) | [common]<br>val [resetTimeoutNanos](reset-timeout-nanos.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
