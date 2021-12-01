//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[whileInput](while-input.md)

# whileInput

[common]\
fun &lt;[A](while-input.md) : [Input](index.md)&gt; [whileInput](while-input.md)(f: suspend ([A](while-input.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Schedule](index.md)&lt;[A](while-input.md), [Output](index.md)&gt;

Continues or stops the schedule based on the input.
