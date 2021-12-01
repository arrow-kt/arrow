//[arrow-fx-coroutines](../../../../index.md)/[arrow.fx.coroutines](../../index.md)/[Schedule](../index.md)/[Companion](index.md)/[never](never.md)

# never

[common]\
fun &lt;[A](never.md)&gt; [never](never.md)(): [Schedule](../index.md)&lt;[A](never.md), [Nothing](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-nothing/index.html)&gt;

Creates a schedule that never retries.

Note that this will hang a program if used as a repeat/retry schedule unless cancelled.
