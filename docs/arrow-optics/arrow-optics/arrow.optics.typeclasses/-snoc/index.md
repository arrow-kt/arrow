//[arrow-optics](../../../index.md)/[arrow.optics.typeclasses](../index.md)/[Snoc](index.md)

# Snoc

[common]\
fun interface [Snoc](index.md)&lt;[S](index.md), [A](index.md)&gt;

[Snoc](index.md) defines a [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) between a [S](index.md) and its [init](index.md) and last element [A](index.md) and thus can be seen as the reverse of [Cons](../-cons/index.md). It provides a way to attach or detach elements on the end side of a structure.

## Parameters

common

| | |
|---|---|
| S | source of [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) and init of [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) target. |
| A | last of [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) focus, [A](index.md) is supposed to be unique for a given [S](index.md). |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [initOption](init-option.md) | [common]<br>open fun [initOption](init-option.md)(): [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[S](index.md), [S](index.md)&gt;<br>Provides an [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) between [S](index.md) and its init [S](index.md). |
| [lastOption](last-option.md) | [common]<br>open fun [lastOption](last-option.md)(): [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[S](index.md), [A](index.md)&gt;<br>Provides an [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) between [S](index.md) and its last element [A](index.md). |
| [snoc](snoc.md) | [common]<br>abstract fun [snoc](snoc.md)(): [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156)&lt;[S](index.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[S](index.md), [A](index.md)&gt;&gt;<br>Provides a [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) between a [S](index.md) and its [init](index.md) and last element [A](index.md).<br>[common]<br>open infix fun [S](index.md).[snoc](snoc.md)(last: [A](index.md)): [S](index.md)<br>Append an element [A](index.md) to [S](index.md). |
| [unsnoc](unsnoc.md) | [common]<br>open fun [S](index.md).[unsnoc](unsnoc.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[S](index.md), [A](index.md)&gt;?<br>Deconstruct an [S](index.md) between its [init](init.md) and last element. |

## Properties

| Name | Summary |
|---|---|
| [init](init.md) | [common]<br>open val [S](index.md).[init](init.md): [S](index.md)?<br>Selects all elements except the last. |
