//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Atomic](index.md)/[tryUpdate](try-update.md)

# tryUpdate

[common]\
abstract suspend fun [tryUpdate](try-update.md)(f: ([A](index.md)) -&gt; [A](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Attempts to modify the current value once, in contrast to [update](update.md) which calls [f](try-update.md) until it succeeds.
