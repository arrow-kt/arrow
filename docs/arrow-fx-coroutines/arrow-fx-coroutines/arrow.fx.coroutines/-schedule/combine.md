//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[combine](combine.md)

# combine

[common]\

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

fun &lt;[A](combine.md) : [Input](index.md), [B](combine.md), [C](combine.md)&gt; [combine](combine.md)(other: [Schedule](index.md)&lt;[A](combine.md), [B](combine.md)&gt;, zipContinue: (cont: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), otherCont: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), zipDuration: (duration: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), otherDuration: [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)) -&gt; [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), zip: ([Output](index.md), [B](combine.md)) -&gt; [C](combine.md)): [Schedule](index.md)&lt;[A](combine.md), [C](combine.md)&gt;

Combines with another schedule by combining the result and the delay of the [Decision](-decision/index.md) with the [zipContinue](combine.md), [zipDuration](combine.md) and a [zip](combine.md) functions
