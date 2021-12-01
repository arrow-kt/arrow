//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[modify](modify.md)

# modify

[common]\

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

fun [modify](modify.md)(f: suspend ([Output](index.md), [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)) -&gt; [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)): [Schedule](index.md)&lt;[Input](index.md), [Output](index.md)&gt;

Changes the delay of a resulting [Decision](-decision/index.md) based on the [Output](index.md) and the produced delay.
