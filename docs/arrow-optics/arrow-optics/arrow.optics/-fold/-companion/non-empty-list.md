//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[Fold](../index.md)/[Companion](index.md)/[nonEmptyList](non-empty-list.md)

# nonEmptyList

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](non-empty-list.md)&gt; [nonEmptyList](non-empty-list.md)(): [Fold](../index.md)&lt;[NonEmptyList](../../../../../arrow-core/arrow-core/arrow.core/-non-empty-list/index.md)&lt;[A](non-empty-list.md)&gt;, [A](non-empty-list.md)&gt;

[Traversal](../../index.md#153853783%2FClasslikes%2F-617900156) for [NonEmptyList](../../../../../arrow-core/arrow-core/arrow.core/-non-empty-list/index.md) that has focus in each [A](non-empty-list.md).

#### Receiver

[PTraversal.Companion](../../-p-traversal/-companion/index.md) to make it statically available.

#### Return

[Traversal](../../index.md#153853783%2FClasslikes%2F-617900156) with source [NonEmptyList](../../../../../arrow-core/arrow-core/arrow.core/-non-empty-list/index.md) and focus every [A](non-empty-list.md) of the source.
