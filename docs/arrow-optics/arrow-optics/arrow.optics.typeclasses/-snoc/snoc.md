//[arrow-optics](../../../index.md)/[arrow.optics.typeclasses](../index.md)/[Snoc](index.md)/[snoc](snoc.md)

# snoc

[common]\
abstract fun [snoc](snoc.md)(): [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156)&lt;[S](index.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[S](index.md), [A](index.md)&gt;&gt;

Provides a [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) between a [S](index.md) and its [init](index.md) and last element [A](index.md).

[common]\
open infix fun [S](index.md).[snoc](snoc.md)(last: [A](index.md)): [S](index.md)

Append an element [A](index.md) to [S](index.md).
