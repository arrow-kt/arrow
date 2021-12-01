//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[zipRight](zip-right.md)

# zipRight

[common]\
infix fun &lt;[A](zip-right.md) : [Input](index.md), [B](zip-right.md)&gt; [zipRight](zip-right.md)(other: [Schedule](index.md)&lt;[A](zip-right.md), [B](zip-right.md)&gt;): [Schedule](index.md)&lt;[A](zip-right.md), [B](zip-right.md)&gt;

Combines two schedules with [and](and.md) but throws away the left schedule's result.
