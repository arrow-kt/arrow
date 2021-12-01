//[arrow-optics](../../../index.md)/[arrow.optics](../index.md)/[PTraversal](index.md)

# PTraversal

[common]\
fun interface [PTraversal](index.md)&lt;[S](index.md), [T](index.md), [A](index.md), [B](index.md)&gt; : [PSetter](../-p-setter/index.md)&lt;[S](index.md), [T](index.md), [A](index.md), [B](index.md)&gt; 

A [Traversal](../index.md#153853783%2FClasslikes%2F-617900156) is an optic that allows to see into a structure with 0 to N foci.

[Traversal](../index.md#153853783%2FClasslikes%2F-617900156) is a generalisation of [kotlin.collections.map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/index.html) and can be seen as a representation of modify. all methods are written in terms of modify

## Parameters

common

| | |
|---|---|
| S | the source of a [PTraversal](index.md) |
| T | the modified source of a [PTraversal](index.md) |
| A | the target of a [PTraversal](index.md) |
| B | the modified target of a [PTraversal](index.md) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [choice](../-p-setter/choice.md) | [common]<br>open infix fun &lt;[U](../-p-setter/choice.md), [V](../-p-setter/choice.md)&gt; [choice](../-p-setter/choice.md)(other: [PSetter](../-p-setter/index.md)&lt;[U](../-p-setter/choice.md), [V](../-p-setter/choice.md), [A](index.md), [B](index.md)&gt;): [PSetter](../-p-setter/index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[S](index.md), [U](../-p-setter/choice.md)&gt;, [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[T](index.md), [V](../-p-setter/choice.md)&gt;, [A](index.md), [B](index.md)&gt;<br>Join two [PSetter](../-p-setter/index.md) with the same target<br>[common]<br>open fun &lt;[U](choice.md), [V](choice.md)&gt; [choice](choice.md)(other: [PTraversal](index.md)&lt;[U](choice.md), [V](choice.md), [A](index.md), [B](index.md)&gt;): [PTraversal](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[S](index.md), [U](choice.md)&gt;, [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[T](index.md), [V](choice.md)&gt;, [A](index.md), [B](index.md)&gt; |
| [compose](../-p-setter/compose.md) | [common]<br>open infix fun &lt;[C](../-p-setter/compose.md), [D](../-p-setter/compose.md)&gt; [compose](../-p-setter/compose.md)(other: [PSetter](../-p-setter/index.md)&lt;in [A](index.md), out [B](index.md), out [C](../-p-setter/compose.md), in [D](../-p-setter/compose.md)&gt;): [PSetter](../-p-setter/index.md)&lt;[S](index.md), [T](index.md), [C](../-p-setter/compose.md), [D](../-p-setter/compose.md)&gt;<br>Compose a [PSetter](../-p-setter/index.md) with a [PSetter](../-p-setter/index.md)<br>[common]<br>open infix fun &lt;[C](compose.md), [D](compose.md)&gt; [compose](compose.md)(other: [PTraversal](index.md)&lt;in [A](index.md), out [B](index.md), out [C](compose.md), in [D](compose.md)&gt;): [PTraversal](index.md)&lt;[S](index.md), [T](index.md), [C](compose.md), [D](compose.md)&gt;<br>Compose a [PTraversal](index.md) with a [PTraversal](index.md) |
| [lift](../-p-setter/lift.md) | [common]<br>open fun [lift](../-p-setter/lift.md)(map: ([A](index.md)) -&gt; [B](index.md)): ([S](index.md)) -&gt; [T](index.md)<br>Lift a function [map](../-p-setter/lift.md): (A) -&gt; B to the context of S: (S) -> T` |
| [modify](modify.md) | [common]<br>abstract override fun [modify](modify.md)(source: [S](index.md), map: ([A](index.md)) -&gt; [B](index.md)): [T](index.md)<br>Modify polymorphically the focus of a [PSetter](../-p-setter/index.md) with a function [map](modify.md). |
| [plus](../-p-setter/plus.md) | [common]<br>open operator fun &lt;[C](../-p-setter/plus.md), [D](../-p-setter/plus.md)&gt; [plus](../-p-setter/plus.md)(other: [PSetter](../-p-setter/index.md)&lt;in [A](index.md), out [B](index.md), out [C](../-p-setter/plus.md), in [D](../-p-setter/plus.md)&gt;): [PSetter](../-p-setter/index.md)&lt;[S](index.md), [T](index.md), [C](../-p-setter/plus.md), [D](../-p-setter/plus.md)&gt;<br>open operator fun &lt;[C](plus.md), [D](plus.md)&gt; [plus](plus.md)(other: [PTraversal](index.md)&lt;in [A](index.md), out [B](index.md), out [C](plus.md), in [D](plus.md)&gt;): [PTraversal](index.md)&lt;[S](index.md), [T](index.md), [C](plus.md), [D](plus.md)&gt; |
| [set](../-p-setter/set.md) | [common]<br>open fun [set](../-p-setter/set.md)(source: [S](index.md), focus: [B](index.md)): [T](index.md)<br>Set polymorphically the focus of a [PSetter](../-p-setter/index.md) with a value b. |

## Properties

| Name | Summary |
|---|---|
| [every](every.md) | [common]<br>open val &lt;[U](every.md), [V](every.md)&gt; [PLens](../-p-lens/index.md)&lt;[U](every.md), [V](every.md), [S](index.md), [T](index.md)&gt;.[every](every.md): [PTraversal](index.md)&lt;[U](every.md), [V](every.md), [A](index.md), [B](index.md)&gt;<br>DSL to compose [Traversal](../index.md#153853783%2FClasslikes%2F-617900156) with a [Lens](../index.md#-141055921%2FClasslikes%2F-617900156) for a structure [S](index.md) to see all its foci [A](index.md) |
| [every](every.md) | [common]<br>open val &lt;[U](every.md), [V](every.md)&gt; [PIso](../-p-iso/index.md)&lt;[U](every.md), [V](every.md), [S](index.md), [T](index.md)&gt;.[every](every.md): [PTraversal](index.md)&lt;[U](every.md), [V](every.md), [A](index.md), [B](index.md)&gt;<br>DSL to compose [Traversal](../index.md#153853783%2FClasslikes%2F-617900156) with a [Iso](../index.md#1786632304%2FClasslikes%2F-617900156) for a structure [S](index.md) to see all its foci [A](index.md) |
| [every](every.md) | [common]<br>open val &lt;[U](every.md), [V](every.md)&gt; [PPrism](../-p-prism/index.md)&lt;[U](every.md), [V](every.md), [S](index.md), [T](index.md)&gt;.[every](every.md): [PTraversal](index.md)&lt;[U](every.md), [V](every.md), [A](index.md), [B](index.md)&gt;<br>DSL to compose [Traversal](../index.md#153853783%2FClasslikes%2F-617900156) with a [Prism](../index.md#1394331700%2FClasslikes%2F-617900156) for a structure [S](index.md) to see all its foci [A](index.md) |
| [every](every.md) | [common]<br>open val &lt;[U](every.md), [V](every.md)&gt; [POptional](../-p-optional/index.md)&lt;[U](every.md), [V](every.md), [S](index.md), [T](index.md)&gt;.[every](every.md): [PTraversal](index.md)&lt;[U](every.md), [V](every.md), [A](index.md), [B](index.md)&gt;<br>DSL to compose [Traversal](../index.md#153853783%2FClasslikes%2F-617900156) with a [Optional](../index.md#-1955528147%2FClasslikes%2F-617900156) for a structure [S](index.md) to see all its foci [A](index.md) |
| [every](every.md) | [common]<br>open val &lt;[U](every.md), [V](every.md)&gt; [PSetter](../-p-setter/index.md)&lt;[U](every.md), [V](every.md), [S](index.md), [T](index.md)&gt;.[every](every.md): [PSetter](../-p-setter/index.md)&lt;[U](every.md), [V](every.md), [A](index.md), [B](index.md)&gt;<br>DSL to compose [Traversal](../index.md#153853783%2FClasslikes%2F-617900156) with a [Setter](../index.md#744232174%2FClasslikes%2F-617900156) for a structure [S](index.md) to see all its foci [A](index.md) |
| [every](every.md) | [common]<br>open val &lt;[U](every.md), [V](every.md)&gt; [PTraversal](index.md)&lt;[U](every.md), [V](every.md), [S](index.md), [T](index.md)&gt;.[every](every.md): [PTraversal](index.md)&lt;[U](every.md), [V](every.md), [A](index.md), [B](index.md)&gt;<br>DSL to compose [Traversal](../index.md#153853783%2FClasslikes%2F-617900156) with a [Traversal](../index.md#153853783%2FClasslikes%2F-617900156) for a structure [S](index.md) to see all its foci [A](index.md) |
| [every](every.md) | [common]<br>open val &lt;[U](every.md), [V](every.md)&gt; [PEvery](../-p-every/index.md)&lt;[U](every.md), [V](every.md), [S](index.md), [T](index.md)&gt;.[every](every.md): [PTraversal](index.md)&lt;[U](every.md), [V](every.md), [A](index.md), [B](index.md)&gt;<br>DSL to compose [Traversal](../index.md#153853783%2FClasslikes%2F-617900156) with a [PEvery](../-p-every/index.md) for a structure [S](index.md) to see all its foci [A](index.md) |

## Inheritors

| Name |
|---|
| [PEvery](../-p-every/index.md) |
| [PIso](../-p-iso/index.md) |
| [PLens](../-p-lens/index.md) |
| [POptional](../-p-optional/index.md) |
| [PPrism](../-p-prism/index.md) |
