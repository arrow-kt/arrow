//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[PEvery](../index.md)/[Companion](index.md)/[option](option.md)

# option

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](option.md)&gt; [option](option.md)(): [Every](../../index.md#176863642%2FClasslikes%2F-617900156)&lt;[Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](option.md)&gt;, [A](option.md)&gt;

[Traversal](../../index.md#153853783%2FClasslikes%2F-617900156) for [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md) that has focus in each [arrow.core.Some](../../../../../arrow-core/arrow-core/arrow.core/-some/index.md).

#### Receiver

[PTraversal.Companion](../../-p-traversal/-companion/index.md) to make it statically available.

#### Return

[Traversal](../../index.md#153853783%2FClasslikes%2F-617900156) with source [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md) and focus in every [arrow.core.Some](../../../../../arrow-core/arrow-core/arrow.core/-some/index.md) of the source.
