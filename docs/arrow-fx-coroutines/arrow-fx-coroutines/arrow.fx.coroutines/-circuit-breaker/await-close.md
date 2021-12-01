//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[CircuitBreaker](index.md)/[awaitClose](await-close.md)

# awaitClose

[common]\
suspend fun [awaitClose](await-close.md)()

Awaits for this CircuitBreaker to be [CircuitBreaker.State.Closed](-state/-closed/index.md).

If this CircuitBreaker is already in a closed state, then it returns immediately, otherwise it will wait (asynchronously) until the CircuitBreaker switches to the CircuitBreaker.Closed state again.
