//[arrow-fx-coroutines](../../../../../index.md)/[arrow.fx.coroutines](../../../index.md)/[CircuitBreaker](../../index.md)/[State](../index.md)/[Open](index.md)

# Open

[common]\
class [Open](index.md) : [CircuitBreaker.State](../index.md)

When the [CircuitBreaker](../../index.md) is in the [Open](index.md) state it will short-circuit/fail-fast all requests     - All requests short-circuit/fail-fast with ExecutionRejected     - If a request is made after the configured [resetTimeout](../../../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/reset-timeout.md) passes, the [CircuitBreaker](../../index.md) is tripped into the a [HalfOpen](../-half-open/index.md) state, allowing one request to go through as a test.

## Parameters

common

| | |
|---|---|
| startedAt | is the timestamp in milliseconds since the     epoch when the transition to [Open](index.md) happened. |
| resetTimeoutNanos | is the current resetTimeout that is     applied to this Open state, to be multiplied by the     exponential backoff factor for the next transition from     HalfOpen to Open. |

## Constructors

| | |
|---|---|
| [Open](-open.md) | [common]<br>fun [Open](-open.md)(startedAt: [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html), resetTimeoutNanos: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [toString](to-string.md) | [common]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

## Properties

| Name | Summary |
|---|---|
| [expiresAt](expires-at.md) | [common]<br>val [expiresAt](expires-at.md): [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)<br>The timestamp in milliseconds since the epoch, specifying when the Open state is to transition to [HalfOpen](../-half-open/index.md). |
| [resetTimeoutNanos](reset-timeout-nanos.md) | [common]<br>val [resetTimeoutNanos](reset-timeout-nanos.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [startedAt](started-at.md) | [common]<br>val [startedAt](started-at.md): [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
