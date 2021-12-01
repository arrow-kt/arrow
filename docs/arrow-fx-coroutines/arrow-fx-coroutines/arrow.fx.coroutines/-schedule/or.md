//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[or](or.md)

# or

[common]\
infix fun &lt;[A](or.md) : [Input](index.md), [B](or.md)&gt; [or](or.md)(other: [Schedule](index.md)&lt;[A](or.md), [B](or.md)&gt;): [Schedule](index.md)&lt;[A](or.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Output](index.md), [B](or.md)&gt;&gt;

Combines two schedules. Continues if one continues and chooses the minimum delay.
