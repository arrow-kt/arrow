//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Decision](index.md)/[combine](combine.md)

# combine

[common]\

@[ExperimentalTime](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-experimental-time/index.html)

fun &lt;[C](combine.md), [D](combine.md), [E](combine.md)&gt; [combine](combine.md)(other: [Schedule.Decision](index.md)&lt;[C](combine.md), [D](combine.md)&gt;, f: ([Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html), g: ([Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html)) -&gt; [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/index.html), zip: ([B](index.md), [D](combine.md)) -&gt; [E](combine.md)): [Schedule.Decision](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [C](combine.md)&gt;, [E](combine.md)&gt;
