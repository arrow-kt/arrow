//[arrow-optics](../../../../index.md)/[arrow.optics.typeclasses](../../index.md)/[At](../index.md)/[Companion](index.md)

# Companion

[common]\
object [Companion](index.md)

## Functions

| Name | Summary |
|---|---|
| [fromIso](from-iso.md) | [common]<br>fun &lt;[S](from-iso.md), [U](from-iso.md), [I](from-iso.md), [A](from-iso.md)&gt; [fromIso](from-iso.md)(AT: [At](../index.md)&lt;[U](from-iso.md), [I](from-iso.md), [A](from-iso.md)&gt;, iso: [Iso](../../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156)&lt;[S](from-iso.md), [U](from-iso.md)&gt;): [At](../index.md)&lt;[S](from-iso.md), [I](from-iso.md), [A](from-iso.md)&gt;<br>Lift an instance of [At](../index.md) using an [Iso](../../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156). |
| [map](map.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[K](map.md), [V](map.md)&gt; [map](map.md)(): [At](../index.md)&lt;[Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](map.md), [V](map.md)&gt;, [K](map.md), [Option](../../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[V](map.md)&gt;&gt; |
| [set](set.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](set.md)&gt; [set](set.md)(): [At](../index.md)&lt;[Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html)&lt;[A](set.md)&gt;, [A](set.md), [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)&gt;<br>[At](../index.md) instance definition for [Set](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-set/index.html). |
