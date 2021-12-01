//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[and](and.md)

# and

[common]\
infix fun &lt;[A](and.md) : [Input](index.md), [B](and.md)&gt; [and](and.md)(other: [Schedule](index.md)&lt;[A](and.md), [B](and.md)&gt;): [Schedule](index.md)&lt;[A](and.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Output](index.md), [B](and.md)&gt;&gt;

Combines two schedules. Continues only when both continue and chooses the maximum delay.
