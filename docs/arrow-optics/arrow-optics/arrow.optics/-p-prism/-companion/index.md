//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[PPrism](../index.md)/[Companion](index.md)

# Companion

[common]\
object [Companion](index.md)

## Functions

| Name | Summary |
|---|---|
| [id](id.md) | [common]<br>fun &lt;[S](id.md)&gt; [id](id.md)(): [PIso](../../-p-iso/index.md)&lt;[S](id.md), [S](id.md), [S](id.md), [S](id.md)&gt; |
| [invoke](invoke.md) | [common]<br>operator fun &lt;[S](invoke.md), [T](invoke.md), [A](invoke.md), [B](invoke.md)&gt; [invoke](invoke.md)(getOrModify: ([S](invoke.md)) -&gt; [Either](../../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[T](invoke.md), [A](invoke.md)&gt;, reverseGet: ([B](invoke.md)) -&gt; [T](invoke.md)): [PPrism](../index.md)&lt;[S](invoke.md), [T](invoke.md), [A](invoke.md), [B](invoke.md)&gt;<br>Invoke operator overload to create a [PPrism](../index.md) of type S with focus A. Can also be used to construct [Prism](../../index.md#1394331700%2FClasslikes%2F-617900156) |
| [none](none.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](none.md)&gt; [none](none.md)(): [Prism](../../index.md#1394331700%2FClasslikes%2F-617900156)&lt;[Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](none.md)&gt;, [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt;<br>[Prism](../../index.md#1394331700%2FClasslikes%2F-617900156) to focus into an [arrow.core.None](../../../../../arrow-core/arrow-core/arrow.core/-none/index.md) |
| [only](only.md) | [common]<br>fun &lt;[A](only.md)&gt; [only](only.md)(a: [A](only.md), eq: ([A](only.md), [A](only.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = { aa, b -&gt; aa == b }): [Prism](../../index.md#1394331700%2FClasslikes%2F-617900156)&lt;[A](only.md), [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt;<br>A [PPrism](../index.md) that checks for equality with a given value [a](only.md) |
| [pSome](p-some.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](p-some.md), [B](p-some.md)&gt; [pSome](p-some.md)(): [PPrism](../index.md)&lt;[Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](p-some.md)&gt;, [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[B](p-some.md)&gt;, [A](p-some.md), [B](p-some.md)&gt;<br>[PPrism](../index.md) to focus into an [arrow.core.Some](../../../../../arrow-core/arrow-core/arrow.core/-some/index.md) |
| [some](some.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](some.md)&gt; [some](some.md)(): [Prism](../../index.md#1394331700%2FClasslikes%2F-617900156)&lt;[Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](some.md)&gt;, [A](some.md)&gt;<br>[Prism](../../index.md#1394331700%2FClasslikes%2F-617900156) to focus into an [arrow.core.Some](../../../../../arrow-core/arrow-core/arrow.core/-some/index.md) |
