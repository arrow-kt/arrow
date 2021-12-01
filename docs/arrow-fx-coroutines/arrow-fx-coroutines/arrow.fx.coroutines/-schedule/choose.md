//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[choose](choose.md)

# choose

[common]\
abstract infix fun &lt;[A](choose.md), [B](choose.md)&gt; [choose](choose.md)(other: [Schedule](index.md)&lt;[A](choose.md), [B](choose.md)&gt;): [Schedule](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Input](index.md), [A](choose.md)&gt;, [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Output](index.md), [B](choose.md)&gt;&gt;

Combines two schedules with different input and output and conditionally choose between the two. Continues when the chosen schedule continues and uses the chosen schedules delay.
