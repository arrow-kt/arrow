//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Atomic](index.md)/[tryModify](try-modify.md)

# tryModify

[common]\
abstract suspend fun &lt;[B](try-modify.md)&gt; [tryModify](try-modify.md)(f: ([A](index.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](try-modify.md)&gt;): [B](try-modify.md)?

Attempts to inspect the state, uptade it, and extract a different state.

[tryModify](try-modify.md) behaves as [tryUpdate](try-update.md) but allows the update function to return an output value of type [B](try-modify.md).
