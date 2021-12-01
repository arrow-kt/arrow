//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[fold](fold.md)

# fold

[common]\
fun &lt;[C](fold.md)&gt; [fold](fold.md)(initial: [C](fold.md), f: suspend ([C](fold.md), [Output](index.md)) -&gt; [C](fold.md)): [Schedule](index.md)&lt;[Input](index.md), [C](fold.md)&gt;

Non-effectful version of foldM.
