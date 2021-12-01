//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Atomic](index.md)/[access](access.md)

# access

[common]\
abstract suspend fun [access](access.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), suspend ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)&gt;

Obtains a snapshot of the current value, and a setter for updating it.

This is useful when you need to execute effects with the original result while still ensuring an atomic update.

The setter will return false if another concurrent call invalidated the snapshot (modified the value). It will return true if setting the value was successful.

Once it has returned false or been used once, a setter never succeeds again.
