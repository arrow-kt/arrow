//[arrow-fx-coroutines](../../../../../index.md)/[arrow.fx.coroutines](../../../index.md)/[CircuitBreaker](../../index.md)/[State](../index.md)/[Closed](index.md)

# Closed

[common]\
class [Closed](index.md)(failures: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [CircuitBreaker.State](../index.md)

[Closed](index.md) is the normal state of the [CircuitBreaker](../../index.md), where requests are being made. The state in which [CircuitBreaker](../../index.md) starts.     - When an exceptions occurs it increments the failure counter     - A successful request will reset the failure counter to zero     - When the failure counter reaches the [maxFailures](../../../../../../arrow-fx-coroutines/arrow.fx.coroutines/-circuit-breaker/max-failures.md) threshold, the breaker is tripped into the [Open](../-open/index.md) state

## Parameters

common

| | |
|---|---|
| failures | is the current failures count |

## Constructors

| | |
|---|---|
| [Closed](-closed.md) | [common]<br>fun [Closed](-closed.md)(failures: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) |

## Functions

| Name | Summary |
|---|---|
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [toString](to-string.md) | [common]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

## Properties

| Name | Summary |
|---|---|
| [failures](failures.md) | [common]<br>val [failures](failures.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
