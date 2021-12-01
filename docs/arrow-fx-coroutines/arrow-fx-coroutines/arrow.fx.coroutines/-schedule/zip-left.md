//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[zipLeft](zip-left.md)

# zipLeft

[common]\
infix fun &lt;[A](zip-left.md) : [Input](index.md), [B](zip-left.md)&gt; [zipLeft](zip-left.md)(other: [Schedule](index.md)&lt;[A](zip-left.md), [B](zip-left.md)&gt;): [Schedule](index.md)&lt;[A](zip-left.md), [Output](index.md)&gt;

Combines two schedules with [and](and.md) but throws away the right schedule's result.
