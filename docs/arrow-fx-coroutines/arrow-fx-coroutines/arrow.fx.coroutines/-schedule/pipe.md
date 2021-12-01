//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[pipe](pipe.md)

# pipe

[common]\
abstract infix fun &lt;[B](pipe.md)&gt; [pipe](pipe.md)(other: [Schedule](index.md)&lt;[Output](index.md), [B](pipe.md)&gt;): [Schedule](index.md)&lt;[Input](index.md), [B](pipe.md)&gt;

Composes this schedule with the other schedule by piping the output of this schedule into the input of the other.
