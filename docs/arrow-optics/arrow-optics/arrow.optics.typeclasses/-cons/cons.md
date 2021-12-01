//[arrow-optics](../../../index.md)/[arrow.optics.typeclasses](../index.md)/[Cons](index.md)/[cons](cons.md)

# cons

[common]\
abstract fun [cons](cons.md)(): [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156)&lt;[S](index.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [S](index.md)&gt;&gt;

Provides a [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) between [S](index.md) and its first element [A](index.md) and tail [S](index.md).

[common]\
open infix fun [A](index.md).[cons](cons.md)(tail: [S](index.md)): [S](index.md)

Prepend an element [A](index.md) to the first element of [S](index.md).

#### Receiver

[A](index.md) element to prepend or attach on left side of [tail](cons.md).
