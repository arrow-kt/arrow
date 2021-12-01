//[arrow-fx-coroutines](../../../../../index.md)/[arrow.fx.coroutines](../../../index.md)/[CircuitBreaker](../../index.md)/[State](../index.md)/[Open](index.md)/[expiresAt](expires-at.md)

# expiresAt

[common]\
val [expiresAt](expires-at.md): [Long](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)

The timestamp in milliseconds since the epoch, specifying when the Open state is to transition to [HalfOpen](../-half-open/index.md).

It is calculated as: startedAt + resetTimeout
