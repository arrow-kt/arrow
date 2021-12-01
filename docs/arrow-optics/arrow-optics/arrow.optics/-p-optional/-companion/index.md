//[arrow-optics](../../../../index.md)/[arrow.optics](../../index.md)/[POptional](../index.md)/[Companion](index.md)

# Companion

[common]\
object [Companion](index.md)

## Functions

| Name | Summary |
|---|---|
| [codiagonal](codiagonal.md) | [common]<br>fun &lt;[S](codiagonal.md)&gt; [codiagonal](codiagonal.md)(): [Optional](../../index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[Either](../../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[S](codiagonal.md), [S](codiagonal.md)&gt;, [S](codiagonal.md)&gt;<br>[POptional](../index.md) that takes either [S](codiagonal.md) or [S](codiagonal.md) and strips the choice of [S](codiagonal.md). |
| [id](id.md) | [common]<br>fun &lt;[S](id.md)&gt; [id](id.md)(): [PIso](../../-p-iso/index.md)&lt;[S](id.md), [S](id.md), [S](id.md), [S](id.md)&gt; |
| [invoke](invoke.md) | [common]<br>operator fun &lt;[S](invoke.md), [T](invoke.md), [A](invoke.md), [B](invoke.md)&gt; [invoke](invoke.md)(getOrModify: ([S](invoke.md)) -&gt; [Either](../../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[T](invoke.md), [A](invoke.md)&gt;, set: ([S](invoke.md), [B](invoke.md)) -&gt; [T](invoke.md)): [POptional](../index.md)&lt;[S](invoke.md), [T](invoke.md), [A](invoke.md), [B](invoke.md)&gt;<br>Invoke operator overload to create a [POptional](../index.md) of type S with focus A. Can also be used to construct [Optional](../../index.md#-1955528147%2FClasslikes%2F-617900156) |
| [listHead](list-head.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](list-head.md)&gt; [listHead](list-head.md)(): [Optional](../../index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](list-head.md)&gt;, [A](list-head.md)&gt;<br>[Optional](../../index.md#-1955528147%2FClasslikes%2F-617900156) to safely operate on the head of a list |
| [listTail](list-tail.md) | [common]<br>@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)<br>fun &lt;[A](list-tail.md)&gt; [listTail](list-tail.md)(): [Optional](../../index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](list-tail.md)&gt;, [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](list-tail.md)&gt;&gt;<br>[Optional](../../index.md#-1955528147%2FClasslikes%2F-617900156) to safely operate on the tail of a list |
| [void](void.md) | [common]<br>fun &lt;[A](void.md), [B](void.md)&gt; [void](void.md)(): [Optional](../../index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[A](void.md), [B](void.md)&gt;<br>[POptional](../index.md) that never sees its focus |
