//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[logOutput](log-output.md)

# logOutput

[common]\
fun &lt;[A](log-output.md)&gt; [logOutput](log-output.md)(f: suspend ([A](log-output.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Schedule](../index.md)&lt;[A](log-output.md), [A](log-output.md)&gt;

Creates a Schedule with an effectful handler on the output.
