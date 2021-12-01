//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[doUntil](do-until.md)

# doUntil

[common]\
fun &lt;[A](do-until.md)&gt; [doUntil](do-until.md)(f: suspend ([A](do-until.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Schedule](../index.md)&lt;[A](do-until.md), [A](do-until.md)&gt;

Creates a Schedule that continues until [f](do-until.md) returns true.
