//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[PIso](../index.md)/[Companion](index.md)/[nullableToPOption](nullable-to-p-option.md)

# nullableToPOption

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](nullable-to-p-option.md), [B](nullable-to-p-option.md)&gt; [nullableToPOption](nullable-to-p-option.md)(): [PIso](../index.md)&lt;[A](nullable-to-p-option.md)?, [B](nullable-to-p-option.md)?, [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](nullable-to-p-option.md)&gt;, [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[B](nullable-to-p-option.md)&gt;&gt;

[PIso](../index.md) that defines the equality between the nullable platform type and [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md).
