//[arrow-optics](../../../index.md)/[arrow.optics.typeclasses](../index.md)/[Cons](index.md)

# Cons

[common]\
fun interface [Cons](index.md)&lt;[S](index.md), [A](index.md)&gt;

[Cons](index.md) provides a [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) between [S](index.md) and its first element [A](index.md) and tail [S](index.md). It provides a convenient way to attach or detach elements to the left side of a structure [S](index.md).

## Parameters

common

| | |
|---|---|
| S | source of [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) and tail of [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) focus. |
| A | first element of [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) focus, [A](index.md) is supposed to be unique for a given [S](index.md). |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [cons](cons.md) | [common]<br>abstract fun [cons](cons.md)(): [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156)&lt;[S](index.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [S](index.md)&gt;&gt;<br>Provides a [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) between [S](index.md) and its first element [A](index.md) and tail [S](index.md).<br>[common]<br>open infix fun [A](index.md).[cons](cons.md)(tail: [S](index.md)): [S](index.md)<br>Prepend an element [A](index.md) to the first element of [S](index.md). |
| [firstOption](first-option.md) | [common]<br>open fun [firstOption](first-option.md)(): [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[S](index.md), [A](index.md)&gt;<br>Provides an [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) between [S](index.md) and its first element [A](index.md). |
| [tailOption](tail-option.md) | [common]<br>open fun [tailOption](tail-option.md)(): [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[S](index.md), [S](index.md)&gt;<br>Provides an [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) between [S](index.md) and its tail [S](index.md). |
| [uncons](uncons.md) | [common]<br>open fun [S](index.md).[uncons](uncons.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [S](index.md)&gt;?<br>Deconstruct an [S](index.md) to its optional first element [A](index.md) and tail [S](index.md). |
