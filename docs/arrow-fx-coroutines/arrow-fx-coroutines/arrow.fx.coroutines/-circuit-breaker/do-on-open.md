//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[CircuitBreaker](index.md)/[doOnOpen](do-on-open.md)

# doOnOpen

[common]\
fun [doOnOpen](do-on-open.md)(callback: suspend () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [CircuitBreaker](index.md)

Returns a new circuit breaker that wraps the state of the source and that will fire the given callback upon the circuit breaker transitioning to the CircuitBreaker.Open state.

It is useful for gathering stats.

NOTE: calling this method multiple times will create a circuit breaker that will call multiple callbacks, thus the callback given is cumulative with other specified callbacks.

#### Return

a new circuit breaker wrapping the state of the source

## Parameters

common

| | |
|---|---|
| callback | will be executed when the state evolves into CircuitBreaker.Open |
