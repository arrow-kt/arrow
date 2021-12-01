//[arrow-optics](../../../../index.md)/[arrow.optics.typeclasses](../../index.md)/[Cons](../index.md)/[Companion](index.md)

# Companion

[common]\
object [Companion](index.md)

## Functions

| Name | Summary |
|---|---|
| [fromIso](from-iso.md) | [common]<br>fun &lt;[S](from-iso.md), [A](from-iso.md), [B](from-iso.md)&gt; [fromIso](from-iso.md)(C: [Cons](../index.md)&lt;[A](from-iso.md), [B](from-iso.md)&gt;, iso: [Iso](../../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156)&lt;[S](from-iso.md), [A](from-iso.md)&gt;): [Cons](../index.md)&lt;[S](from-iso.md), [B](from-iso.md)&gt;<br>Lift an instance of [Cons](../index.md) using an [Iso](../../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156). |
| [invoke](invoke.md) | [common]<br>operator fun &lt;[S](invoke.md), [A](invoke.md)&gt; [invoke](invoke.md)(prism: [Prism](../../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156)&lt;[S](invoke.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](invoke.md), [S](invoke.md)&gt;&gt;): [Cons](../index.md)&lt;[S](invoke.md), [A](invoke.md)&gt; |
| [list](list.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](list.md)&gt; [list](list.md)(): [Cons](../index.md)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](list.md)&gt;, [A](list.md)&gt;<br>[Cons](../index.md) instance definition for [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html). |
| [string](string.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun [string](string.md)(): [Cons](../index.md)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [Char](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char/index.html)&gt;<br>[Cons](../index.md) instance for [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html). |
