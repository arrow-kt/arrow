//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[contramap](contramap.md)

# contramap

[common]\
abstract fun &lt;[B](contramap.md)&gt; [contramap](contramap.md)(f: suspend ([B](contramap.md)) -&gt; [Input](index.md)): [Schedule](index.md)&lt;[B](contramap.md), [Output](index.md)&gt;

Changes the input of the schedule. May alter a schedule's decision if it is based on input.
