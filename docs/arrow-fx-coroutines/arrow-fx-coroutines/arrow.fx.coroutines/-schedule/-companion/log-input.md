//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[logInput](log-input.md)

# logInput

[common]\
fun &lt;[A](log-input.md)&gt; [logInput](log-input.md)(f: suspend ([A](log-input.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Schedule](../index.md)&lt;[A](log-input.md), [A](log-input.md)&gt;

Creates a Schedule with an effectful handler on the input.
