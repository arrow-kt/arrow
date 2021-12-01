//[arrow-optics](../../../index.md)/[arrow.optics.typeclasses](../index.md)/[At](index.md)

# At

[common]\
fun interface [At](index.md)&lt;[S](index.md), [I](index.md), [A](index.md)&gt;

[At](index.md) provides a [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in [A](index.md) at a given index [I](index.md).

## Parameters

common

| | |
|---|---|
| S | source of [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) |
| I | index that uniquely identifies the focus of the [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) |
| A | focus that is supposed to be unique for a given pair [S](index.md) and [I](index.md). |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [at](at.md) | [common]<br>abstract fun [at](at.md)(i: [I](index.md)): [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156)&lt;[S](index.md), [A](index.md)&gt;<br>Get a [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) for a structure [S](index.md) with focus in [A](index.md) at index [i](at.md).<br>[common]<br>open fun &lt;[T](at.md)&gt; [Fold](../../arrow.optics/-fold/index.md)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Fold](../../arrow.optics/-fold/index.md)&lt;[T](at.md), [A](index.md)&gt;<br>DSL to compose [At](index.md) with a [Fold](../../arrow.optics/-fold/index.md) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).<br>[common]<br>open fun &lt;[T](at.md)&gt; [Getter](../../arrow.optics/-getter/index.md)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Getter](../../arrow.optics/-getter/index.md)&lt;[T](at.md), [A](index.md)&gt;<br>DSL to compose [At](index.md) with a [Getter](../../arrow.optics/-getter/index.md) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).<br>[common]<br>open fun &lt;[T](at.md)&gt; [Iso](../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;<br>DSL to compose [At](index.md) with an [Iso](../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).<br>[common]<br>open fun &lt;[T](at.md)&gt; [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;<br>DSL to compose [At](index.md) with a [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).<br>[common]<br>open fun &lt;[T](at.md)&gt; [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;<br>DSL to compose [At](index.md) with an [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).<br>[common]<br>open fun &lt;[T](at.md)&gt; [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;<br>DSL to compose [At](index.md) with a [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).<br>[common]<br>open fun &lt;[T](at.md)&gt; [Setter](../../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Setter](../../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;<br>DSL to compose [At](index.md) with a [Setter](../../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).<br>[common]<br>open fun &lt;[T](at.md)&gt; [Traversal](../../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Traversal](../../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;<br>DSL to compose [At](index.md) with a [Traversal](../../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md). |

## Extensions

| Name | Summary |
|---|---|
| [remove](../remove.md) | [common]<br>fun &lt;[S](../remove.md), [I](../remove.md), [A](../remove.md)&gt; [At](index.md)&lt;[S](../remove.md), [I](../remove.md), [Option](../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](../remove.md)&gt;&gt;.[remove](../remove.md)(s: [S](../remove.md), i: [I](../remove.md)): [S](../remove.md)<br>Delete a value associated with a key in a Map-like container<br>[common]<br>fun &lt;[S](../remove.md), [I](../remove.md), [A](../remove.md)&gt; [At](index.md)&lt;[S](../remove.md), [I](../remove.md), [Option](../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](../remove.md)&gt;&gt;.[remove](../remove.md)(i: [I](../remove.md)): ([S](../remove.md)) -&gt; [S](../remove.md)<br>Lift deletion of a value associated with a key in a Map-like container |
