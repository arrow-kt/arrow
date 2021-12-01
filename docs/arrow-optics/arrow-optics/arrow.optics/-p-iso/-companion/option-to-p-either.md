//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[PIso](../index.md)/[Companion](index.md)/[optionToPEither](option-to-p-either.md)

# optionToPEither

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](option-to-p-either.md), [B](option-to-p-either.md)&gt; [optionToPEither](option-to-p-either.md)(): [PIso](../index.md)&lt;[Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](option-to-p-either.md)&gt;, [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[B](option-to-p-either.md)&gt;, [Either](../../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html), [A](option-to-p-either.md)&gt;, [Either](../../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html), [B](option-to-p-either.md)&gt;&gt;

[Iso](../../index.md#1786632304%2FClasslikes%2F-617900156) that defines the equality between and [arrow.core.Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md) and [arrow.core.Either](../../../../../arrow-core/arrow-core/arrow.core/-either/index.md)
