//[arrow-optics](../../index.md)/[arrow.optics.dsl](index.md)/[at](at.md)

# at

[common]\
fun &lt;[T](at.md), [S](at.md), [I](at.md), [A](at.md)&gt; [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](at.md)&gt;.[at](at.md)(AT: [At](../arrow.optics.typeclasses/-at/index.md)&lt;[S](at.md), [I](at.md), [A](at.md)&gt;, i: [I](at.md)): [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](at.md)&gt;

DSL to compose [At](../arrow.optics.typeclasses/-at/index.md) with a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) for a structure [S](at.md) to focus in on [A](at.md) at given index [I](at.md).

#### Receiver

[Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) with a focus in [S](at.md)

#### Return

[Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) with a focus in [A](at.md) at given index [I](at.md).

## Parameters

common

| | |
|---|---|
| AT | [At](../arrow.optics.typeclasses/-at/index.md) instance to provide a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) to zoom into [S](at.md) at [I](at.md) |
| i | index [I](at.md) to zoom into [S](at.md) and find focus [A](at.md) |

[common]\
fun &lt;[T](at.md), [S](at.md), [I](at.md), [A](at.md)&gt; [Iso](../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](at.md)&gt;.[at](at.md)(AT: [At](../arrow.optics.typeclasses/-at/index.md)&lt;[S](at.md), [I](at.md), [A](at.md)&gt;, i: [I](at.md)): [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](at.md)&gt;

DSL to compose [At](../arrow.optics.typeclasses/-at/index.md) with an [Iso](../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156) for a structure [S](at.md) to focus in on [A](at.md) at given index [I](at.md).

#### Receiver

[Iso](../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156) with a focus in [S](at.md)

#### Return

[Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) with a focus in [A](at.md) at given index [I](at.md).

## Parameters

common

| | |
|---|---|
| AT | [At](../arrow.optics.typeclasses/-at/index.md) instance to provide a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) to zoom into [S](at.md) at [I](at.md) |
| i | index [I](at.md) to zoom into [S](at.md) and find focus [A](at.md) |

[common]\
fun &lt;[T](at.md), [S](at.md), [I](at.md), [A](at.md)&gt; [Prism](../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](at.md)&gt;.[at](at.md)(AT: [At](../arrow.optics.typeclasses/-at/index.md)&lt;[S](at.md), [I](at.md), [A](at.md)&gt;, i: [I](at.md)): [Optional](../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](at.md)&gt;

DSL to compose [At](../arrow.optics.typeclasses/-at/index.md) with a [Prism](../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) for a structure [S](at.md) to focus in on [A](at.md) at given index [I](at.md).

#### Receiver

[Prism](../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) with a focus in [S](at.md)

#### Return

[Optional](../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) with a focus in [A](at.md) at given index [I](at.md).

## Parameters

common

| | |
|---|---|
| AT | [At](../arrow.optics.typeclasses/-at/index.md) instance to provide a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) to zoom into [S](at.md) at [I](at.md) |
| i | index [I](at.md) to zoom into [S](at.md) and find focus [A](at.md) |

[common]\
fun &lt;[T](at.md), [S](at.md), [I](at.md), [A](at.md)&gt; [Optional](../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](at.md)&gt;.[at](at.md)(AT: [At](../arrow.optics.typeclasses/-at/index.md)&lt;[S](at.md), [I](at.md), [A](at.md)&gt;, i: [I](at.md)): [Optional](../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](at.md)&gt;

DSL to compose [At](../arrow.optics.typeclasses/-at/index.md) with an [Optional](../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) for a structure [S](at.md) to focus in on [A](at.md) at given index [I](at.md).

#### Receiver

[Optional](../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) with a focus in [S](at.md)

#### Return

[Optional](../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) with a focus in [A](at.md) at given index [I](at.md).

## Parameters

common

| | |
|---|---|
| AT | [At](../arrow.optics.typeclasses/-at/index.md) instance to provide a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) to zoom into [S](at.md) at [I](at.md) |
| i | index [I](at.md) to zoom into [S](at.md) and find focus [A](at.md) |

[common]\
fun &lt;[T](at.md), [S](at.md), [I](at.md), [A](at.md)&gt; [Getter](../arrow.optics/-getter/index.md)&lt;[T](at.md), [S](at.md)&gt;.[at](at.md)(AT: [At](../arrow.optics.typeclasses/-at/index.md)&lt;[S](at.md), [I](at.md), [A](at.md)&gt;, i: [I](at.md)): [Getter](../arrow.optics/-getter/index.md)&lt;[T](at.md), [A](at.md)&gt;

DSL to compose [At](../arrow.optics.typeclasses/-at/index.md) with a [Getter](../arrow.optics/-getter/index.md) for a structure [S](at.md) to focus in on [A](at.md) at given index [I](at.md).

#### Receiver

[Getter](../arrow.optics/-getter/index.md) with a focus in [S](at.md)

#### Return

[Getter](../arrow.optics/-getter/index.md) with a focus in [A](at.md) at given index [I](at.md).

## Parameters

common

| | |
|---|---|
| AT | [At](../arrow.optics.typeclasses/-at/index.md) instance to provide a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) to zoom into [S](at.md) at [I](at.md) |
| i | index [I](at.md) to zoom into [S](at.md) and find focus [A](at.md) |

[common]\
fun &lt;[T](at.md), [S](at.md), [I](at.md), [A](at.md)&gt; [Setter](../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](at.md)&gt;.[at](at.md)(AT: [At](../arrow.optics.typeclasses/-at/index.md)&lt;[S](at.md), [I](at.md), [A](at.md)&gt;, i: [I](at.md)): [Setter](../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](at.md)&gt;

DSL to compose [At](../arrow.optics.typeclasses/-at/index.md) with a [Setter](../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156) for a structure [S](at.md) to focus in on [A](at.md) at given index [I](at.md).

#### Receiver

[Setter](../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156) with a focus in [S](at.md)

#### Return

[Setter](../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156) with a focus in [A](at.md) at given index [I](at.md).

## Parameters

common

| | |
|---|---|
| AT | [At](../arrow.optics.typeclasses/-at/index.md) instance to provide a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) to zoom into [S](at.md) at [I](at.md) |
| i | index [I](at.md) to zoom into [S](at.md) and find focus [A](at.md) |

[common]\
fun &lt;[T](at.md), [S](at.md), [I](at.md), [A](at.md)&gt; [Traversal](../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](at.md)&gt;.[at](at.md)(AT: [At](../arrow.optics.typeclasses/-at/index.md)&lt;[S](at.md), [I](at.md), [A](at.md)&gt;, i: [I](at.md)): [Traversal](../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](at.md)&gt;

DSL to compose [At](../arrow.optics.typeclasses/-at/index.md) with a [Traversal](../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156) for a structure [S](at.md) to focus in on [A](at.md) at given index [I](at.md).

#### Receiver

[Traversal](../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156) with a focus in [S](at.md)

#### Return

[Traversal](../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156) with a focus in [A](at.md) at given index [I](at.md).

## Parameters

common

| | |
|---|---|
| AT | [At](../arrow.optics.typeclasses/-at/index.md) instance to provide a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) to zoom into [S](at.md) at [I](at.md) |
| i | index [I](at.md) to zoom into [S](at.md) and find focus [A](at.md) |

[common]\
fun &lt;[T](at.md), [S](at.md), [I](at.md), [A](at.md)&gt; [Fold](../arrow.optics/-fold/index.md)&lt;[T](at.md), [S](at.md)&gt;.[at](at.md)(AT: [At](../arrow.optics.typeclasses/-at/index.md)&lt;[S](at.md), [I](at.md), [A](at.md)&gt;, i: [I](at.md)): [Fold](../arrow.optics/-fold/index.md)&lt;[T](at.md), [A](at.md)&gt;

DSL to compose [At](../arrow.optics.typeclasses/-at/index.md) with a [Fold](../arrow.optics/-fold/index.md) for a structure [S](at.md) to focus in on [A](at.md) at given index [I](at.md).

#### Receiver

[Fold](../arrow.optics/-fold/index.md) with a focus in [S](at.md)

#### Return

[Fold](../arrow.optics/-fold/index.md) with a focus in [A](at.md) at given index [I](at.md).

## Parameters

common

| | |
|---|---|
| AT | [At](../arrow.optics.typeclasses/-at/index.md) instance to provide a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) to zoom into [S](at.md) at [I](at.md) |
| i | index [I](at.md) to zoom into [S](at.md) and find focus [A](at.md) |
