//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[doWhile](do-while.md)

# doWhile

[common]\
fun &lt;[A](do-while.md)&gt; [doWhile](do-while.md)(f: suspend ([A](do-while.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Schedule](../index.md)&lt;[A](do-while.md), [A](do-while.md)&gt;

Creates a Schedule that continues as long as [f](do-while.md) returns true.
