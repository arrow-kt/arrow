//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[PPrism](../index.md)/[Companion](index.md)/[only](only.md)

# only

[common]\
fun &lt;[A](only.md)&gt; [only](only.md)(a: [A](only.md), eq: ([A](only.md), [A](only.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = { aa, b -&gt; aa == b }): [Prism](../../index.md#1394331700%2FClasslikes%2F-617900156)&lt;[A](only.md), [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt;

A [PPrism](../index.md) that checks for equality with a given value [a](only.md)
