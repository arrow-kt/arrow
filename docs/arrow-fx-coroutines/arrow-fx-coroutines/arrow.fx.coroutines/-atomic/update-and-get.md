//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Atomic](index.md)/[updateAndGet](update-and-get.md)

# updateAndGet

[common]\
abstract suspend fun [updateAndGet](update-and-get.md)(f: ([A](index.md)) -&gt; [A](index.md)): [A](index.md)

Modifies the current value using the supplied update function and returns the *new* value.

## See also

common

| | |
|---|---|
| [arrow.fx.coroutines.Atomic](update.md) | , [f](update-and-get.md) may be invoked multiple times. |
