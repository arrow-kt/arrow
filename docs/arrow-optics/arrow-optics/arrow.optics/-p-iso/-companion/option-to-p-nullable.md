//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[PIso](../index.md)/[Companion](index.md)/[optionToPNullable](option-to-p-nullable.md)

# optionToPNullable

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](option-to-p-nullable.md), [B](option-to-p-nullable.md)&gt; [optionToPNullable](option-to-p-nullable.md)(): [PIso](../index.md)&lt;[Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](option-to-p-nullable.md)&gt;, [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[B](option-to-p-nullable.md)&gt;, [A](option-to-p-nullable.md)?, [B](option-to-p-nullable.md)?&gt;

[PIso](../index.md) that defines the equality between [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md) and the nullable platform type.
