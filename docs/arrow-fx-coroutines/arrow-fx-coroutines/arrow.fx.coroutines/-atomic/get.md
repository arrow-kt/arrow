//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Atomic](index.md)/[get](get.md)

# get

[common]\
abstract suspend fun [get](get.md)(): [A](index.md)

Obtains the current value. Since [AtomicRef](../../../../arrow-continuations/arrow-continuations/arrow.continuations.generic/-atomic-ref/index.md) is always guaranteed to have a value, the returned action completes immediately after being bound.
