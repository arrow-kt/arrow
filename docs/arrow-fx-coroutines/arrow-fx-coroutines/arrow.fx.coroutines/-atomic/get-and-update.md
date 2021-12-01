//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Atomic](index.md)/[getAndUpdate](get-and-update.md)

# getAndUpdate

[common]\
abstract suspend fun [getAndUpdate](get-and-update.md)(f: ([A](index.md)) -&gt; [A](index.md)): [A](index.md)

Modifies the current value using the supplied update function and returns the *old* value.

## See also

common

| | |
|---|---|
| [arrow.fx.coroutines.Atomic](update.md) | , [f](get-and-update.md) may be invoked multiple times. |
