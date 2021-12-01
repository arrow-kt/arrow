//[arrow-optics](../../../index.md)/[arrow.optics](../index.md)/[PSetter](index.md)

# PSetter

[common]\
fun interface [PSetter](index.md)&lt;[S](index.md), [T](index.md), [A](index.md), [B](index.md)&gt;

A [Setter](../index.md#744232174%2FClasslikes%2F-617900156) is an optic that allows to see into a structure and set or modify its focus.

A (polymorphic) [PSetter](index.md) is useful when setting or modifying a value for a constructed type i.e. PSetter<Int>, List<String>, Int, String>

A [PSetter](index.md) is a generalisation of a arrow.Functor. Functor::map   (fa: Kind, f: (A) -> B): Kind PSetter::modify(s: S,         f: (A) -> B): T

## Parameters

common

| | |
|---|---|
| S | the source of a [PSetter](index.md) |
| T | the modified source of a [PSetter](index.md) |
| A | the focus of a [PSetter](index.md) |
| B | the modified focus of a [PSetter](index.md) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [choice](choice.md) | [common]<br>open infix fun &lt;[U](choice.md), [V](choice.md)&gt; [choice](choice.md)(other: [PSetter](index.md)&lt;[U](choice.md), [V](choice.md), [A](index.md), [B](index.md)&gt;): [PSetter](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[S](index.md), [U](choice.md)&gt;, [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[T](index.md), [V](choice.md)&gt;, [A](index.md), [B](index.md)&gt;<br>Join two [PSetter](index.md) with the same target |
| [compose](compose.md) | [common]<br>open infix fun &lt;[C](compose.md), [D](compose.md)&gt; [compose](compose.md)(other: [PSetter](index.md)&lt;in [A](index.md), out [B](index.md), out [C](compose.md), in [D](compose.md)&gt;): [PSetter](index.md)&lt;[S](index.md), [T](index.md), [C](compose.md), [D](compose.md)&gt;<br>Compose a [PSetter](index.md) with a [PSetter](index.md) |
| [lift](lift.md) | [common]<br>open fun [lift](lift.md)(map: ([A](index.md)) -&gt; [B](index.md)): ([S](index.md)) -&gt; [T](index.md)<br>Lift a function [map](lift.md): (A) -&gt; B to the context of S: (S) -> T` |
| [modify](modify.md) | [common]<br>abstract fun [modify](modify.md)(source: [S](index.md), map: ([A](index.md)) -&gt; [B](index.md)): [T](index.md)<br>Modify polymorphically the focus of a [PSetter](index.md) with a function [map](modify.md). |
| [plus](plus.md) | [common]<br>open operator fun &lt;[C](plus.md), [D](plus.md)&gt; [plus](plus.md)(other: [PSetter](index.md)&lt;in [A](index.md), out [B](index.md), out [C](plus.md), in [D](plus.md)&gt;): [PSetter](index.md)&lt;[S](index.md), [T](index.md), [C](plus.md), [D](plus.md)&gt; |
| [set](set.md) | [common]<br>open fun [set](set.md)(source: [S](index.md), focus: [B](index.md)): [T](index.md)<br>Set polymorphically the focus of a [PSetter](index.md) with a value b. |

## Inheritors

| Name |
|---|
| [PEvery](../-p-every/index.md) |
| [PIso](../-p-iso/index.md) |
| [PLens](../-p-lens/index.md) |
| [POptional](../-p-optional/index.md) |
| [PPrism](../-p-prism/index.md) |
| [PTraversal](../-p-traversal/index.md) |
