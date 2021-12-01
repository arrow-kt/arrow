//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[CircuitBreaker](../index.md)/[State](index.md)

# State

[common]\
sealed class [State](index.md)

The initial state when initializing a [CircuitBreaker](../index.md) is [Closed](-closed/index.md).

The available states are:

<ul><li>[Closed](-closed/index.md) in case tasks are allowed to go through</li><li>[Open](-open/index.md) in case the circuit breaker is active and rejects incoming tasks</li><li>[HalfOpen](-half-open/index.md) in case a reset attempt was triggered and it is waiting for     the result in order to evolve in [Closed](-closed/index.md), or back to [Open](-open/index.md)</li></ul>

## Types

| Name | Summary |
|---|---|
| [Closed](-closed/index.md) | [common]<br>class [Closed](-closed/index.md)(failures: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [CircuitBreaker.State](index.md)<br>[Closed](-closed/index.md) is the normal state of the [CircuitBreaker](../index.md), where requests are being made. The state in which [CircuitBreaker](../index.md) starts.     - When an exceptions occurs it increments the failure counter     - A successful request will reset the failure counter to zero     - When the failure counter reaches the [maxFailures](../../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/max-failures.md) threshold, the breaker is tripped into the [Open](-open/index.md) state |
| [HalfOpen](-half-open/index.md) | [common]<br>class [HalfOpen](-half-open/index.md) : [CircuitBreaker.State](index.md)<br>The [CircuitBreaker](../index.md) is in [HalfOpen](-half-open/index.md) state while it's allowing a test request to go through. |
| [Open](-open/index.md) | [common]<br>class [Open](-open/index.md) : [CircuitBreaker.State](index.md)<br>When the [CircuitBreaker](../index.md) is in the [Open](-open/index.md) state it will short-circuit/fail-fast all requests     - All requests short-circuit/fail-fast with ExecutionRejected     - If a request is made after the configured [resetTimeout](../../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/reset-timeout.md) passes, the [CircuitBreaker](../index.md) is tripped into the a [HalfOpen](-half-open/index.md) state, allowing one request to go through as a test. |

## Inheritors

| Name |
|---|
| [CircuitBreaker.State](-closed/index.md) |
| [CircuitBreaker.State](-open/index.md) |
| [CircuitBreaker.State](-half-open/index.md) |
