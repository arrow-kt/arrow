//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[PEvery](../index.md)/[Companion](index.md)/[either](either.md)

# either

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[L](either.md), [R](either.md)&gt; [either](either.md)(): [Every](../../index.md#176863642%2FClasslikes%2F-617900156)&lt;[Either](../../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[L](either.md), [R](either.md)&gt;, [R](either.md)&gt;

[Traversal](../../index.md#153853783%2FClasslikes%2F-617900156) for [Either](../../../../../arrow-core/arrow-core/arrow.core/-either/index.md) that has focus in each [Either.Right](../../../../../arrow-core/arrow-core/arrow.core/-either/-right/index.md).

#### Receiver

Traversal.Companion to make it statically available.

#### Return

[Traversal](../../index.md#153853783%2FClasslikes%2F-617900156) with source [Either](../../../../../arrow-core/arrow-core/arrow.core/-either/index.md) and focus every [Either.Right](../../../../../arrow-core/arrow-core/arrow.core/-either/-right/index.md) of the source.
