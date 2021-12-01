//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Atomic](index.md)/[update](update.md)

# update

[common]\
abstract suspend fun [update](update.md)(f: ([A](index.md)) -&gt; [A](index.md))

Updates the current value using the supplied function [f](update.md).

If another modification occurs between the time the current value is read and subsequently updated, the modification is retried using the new value. Hence, [f](update.md) may be invoked multiple times.
