//[arrow-fx-coroutines](../../../index.md)/[arrow.fx.coroutines](../index.md)/[Schedule](index.md)/[check](check.md)

# check

[common]\
abstract fun &lt;[A](check.md) : [Input](index.md)&gt; [check](check.md)(pred: suspend ([A](check.md), [Output](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Schedule](index.md)&lt;[A](check.md), [Output](index.md)&gt;

Conditionally checks on both the input and the output whether or not to continue.
