//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[logOutput](log-output.md)

# logOutput

[common]\
abstract fun [logOutput](log-output.md)(f: suspend ([Output](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;

Runs an effectful handler on every output. Does not alter the decision.
