//[arrow-optics](../../index.md)/[arrow.optics.typeclasses](index.md)

# Package arrow.optics.typeclasses

## Types

| Name | Summary |
|---|---|
| [At](-at/index.md) | [common]<br>fun interface [At](-at/index.md)&lt;[S](-at/index.md), [I](-at/index.md), [A](-at/index.md)&gt;<br>[At](-at/index.md) provides a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) for a structure [S](-at/index.md) to focus in [A](-at/index.md) at a given index [I](-at/index.md). |
| [Conj](index.md#1066020643%2FClasslikes%2F-617900156) | [common]<br>typealias [Conj](index.md#1066020643%2FClasslikes%2F-617900156)&lt;[S](index.md#1066020643%2FClasslikes%2F-617900156), [A](index.md#1066020643%2FClasslikes%2F-617900156)&gt; = [Snoc](-snoc/index.md)&lt;[S](index.md#1066020643%2FClasslikes%2F-617900156), [A](index.md#1066020643%2FClasslikes%2F-617900156)&gt; |
| [Cons](-cons/index.md) | [common]<br>fun interface [Cons](-cons/index.md)&lt;[S](-cons/index.md), [A](-cons/index.md)&gt;<br>[Cons](-cons/index.md) provides a [Prism](../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) between [S](-cons/index.md) and its first element [A](-cons/index.md) and tail [S](-cons/index.md). It provides a convenient way to attach or detach elements to the left side of a structure [S](-cons/index.md). |
| [FilterIndex](-filter-index/index.md) | [common]<br>fun interface [FilterIndex](-filter-index/index.md)&lt;[S](-filter-index/index.md), [I](-filter-index/index.md), [A](-filter-index/index.md)&gt;<br>[FilterIndex](-filter-index/index.md) provides a [Every](../arrow.optics/index.md#176863642%2FClasslikes%2F-617900156) for a structure [S](-filter-index/index.md) with all its foci [A](-filter-index/index.md) whose index [I](-filter-index/index.md) satisfies a predicate. |
| [Index](-index/index.md) | [common]<br>fun interface [Index](-index/index.md)&lt;[S](-index/index.md), [I](-index/index.md), [A](-index/index.md)&gt;<br>[Index](-index/index.md) provides an [Optional](../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) for a structure [S](-index/index.md) to focus in an optional [A](-index/index.md) at a given index [I](-index/index.md). |
| [Snoc](-snoc/index.md) | [common]<br>fun interface [Snoc](-snoc/index.md)&lt;[S](-snoc/index.md), [A](-snoc/index.md)&gt;<br>[Snoc](-snoc/index.md) defines a [Prism](../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) between a [S](-snoc/index.md) and its [init](-snoc/index.md) and last element [A](-snoc/index.md) and thus can be seen as the reverse of [Cons](-cons/index.md). It provides a way to attach or detach elements on the end side of a structure. |

## Functions

| Name | Summary |
|---|---|
| [remove](remove.md) | [common]<br>fun &lt;[S](remove.md), [I](remove.md), [A](remove.md)&gt; [At](-at/index.md)&lt;[S](remove.md), [I](remove.md), [Option](../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](remove.md)&gt;&gt;.[remove](remove.md)(i: [I](remove.md)): ([S](remove.md)) -&gt; [S](remove.md)<br>Lift deletion of a value associated with a key in a Map-like container<br>[common]<br>fun &lt;[S](remove.md), [I](remove.md), [A](remove.md)&gt; [At](-at/index.md)&lt;[S](remove.md), [I](remove.md), [Option](../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](remove.md)&gt;&gt;.[remove](remove.md)(s: [S](remove.md), i: [I](remove.md)): [S](remove.md)<br>Delete a value associated with a key in a Map-like container |
