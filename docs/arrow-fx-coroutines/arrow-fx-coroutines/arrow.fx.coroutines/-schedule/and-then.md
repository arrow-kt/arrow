//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[andThen](and-then.md)

# andThen

[common]\
abstract infix fun &lt;[A](and-then.md) : [Input](index.md), [B](and-then.md)&gt; [andThen](and-then.md)(other: [Schedule](index.md)&lt;[A](and-then.md), [B](and-then.md)&gt;): [Schedule](index.md)&lt;[A](and-then.md), [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Output](index.md), [B](and-then.md)&gt;&gt;

Executes one schedule after the other. When the first schedule ends, it continues with the second.
