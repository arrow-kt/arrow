//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[zip](zip.md)

# zip

[common]\
infix fun &lt;[A](zip.md), [B](zip.md)&gt; [zip](zip.md)(other: [Schedule](index.md)&lt;[A](zip.md), [B](zip.md)&gt;): [Schedule](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Input](index.md), [A](zip.md)&gt;, [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Output](index.md), [B](zip.md)&gt;&gt;

abstract fun &lt;[A](zip.md), [B](zip.md), [C](zip.md)&gt; [zip](zip.md)(other: [Schedule](index.md)&lt;[A](zip.md), [B](zip.md)&gt;, f: ([Output](index.md), [B](zip.md)) -&gt; [C](zip.md)): [Schedule](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Input](index.md), [A](zip.md)&gt;, [C](zip.md)&gt;

Combines two with different input and output using and. Continues when both continue and uses the maximum delay.
