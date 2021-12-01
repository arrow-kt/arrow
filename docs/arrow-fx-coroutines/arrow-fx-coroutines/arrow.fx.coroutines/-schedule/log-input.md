//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[logInput](log-input.md)

# logInput

[common]\
abstract fun [logInput](log-input.md)(f: suspend ([Input](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;

Runs an effectful handler on every input. Does not alter the decision.
