//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[PIso](../index.md)/[Companion](index.md)/[listToPOptionNel](list-to-p-option-nel.md)

# listToPOptionNel

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](list-to-p-option-nel.md), [B](list-to-p-option-nel.md)&gt; [listToPOptionNel](list-to-p-option-nel.md)(): [PIso](../index.md)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](list-to-p-option-nel.md)&gt;, [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](list-to-p-option-nel.md)&gt;, [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[NonEmptyList](../../../../../arrow-core/arrow-core/arrow.core/-non-empty-list/index.md)&lt;[A](list-to-p-option-nel.md)&gt;&gt;, [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[NonEmptyList](../../../../../arrow-core/arrow-core/arrow.core/-non-empty-list/index.md)&lt;[B](list-to-p-option-nel.md)&gt;&gt;&gt;

[PIso](../index.md) that defines equality between a [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html) and [Option](../../../../../arrow-core/arrow-core/arrow.core/-non-empty-list/index.md)
