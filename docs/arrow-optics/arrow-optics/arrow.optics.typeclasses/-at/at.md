//[arrow-optics](../../../index.md)/[arrow.optics.typeclasses](../index.md)/[At](index.md)/[at](at.md)

# at

[common]\
abstract fun [at](at.md)(i: [I](index.md)): [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156)&lt;[S](index.md), [A](index.md)&gt;

Get a [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) for a structure [S](index.md) with focus in [A](index.md) at index [i](at.md).

#### Return

[Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) with a focus in [A](index.md) at given index [I](index.md).

## Parameters

common

| | |
|---|---|
| i | index [I](index.md) to zoom into [S](index.md) and find focus [A](index.md) |

[common]\
open fun &lt;[T](at.md)&gt; [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;

DSL to compose [At](index.md) with a [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).

#### Receiver

[Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) with a focus in [S](index.md)

#### Return

[Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) with a focus in [A](index.md) at given index [I](index.md).

## Parameters

common

| | |
|---|---|
| i | index [I](index.md) to zoom into [S](index.md) and find focus [A](index.md) |

[common]\
open fun &lt;[T](at.md)&gt; [Iso](../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;

DSL to compose [At](index.md) with an [Iso](../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).

#### Receiver

[Iso](../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156) with a focus in [S](index.md)

#### Return

[Lens](../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) with a focus in [A](index.md) at given index [I](index.md).

## Parameters

common

| | |
|---|---|
| i | index [I](index.md) to zoom into [S](index.md) and find focus [A](index.md) |

[common]\
open fun &lt;[T](at.md)&gt; [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;

DSL to compose [At](index.md) with a [Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).

#### Receiver

[Prism](../../arrow.optics/index.md#1394331700%2FClasslikes%2F-617900156) with a focus in [S](index.md)

#### Return

[Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) with a focus in [A](index.md) at given index [I](index.md).

## Parameters

common

| | |
|---|---|
| i | index [I](index.md) to zoom into [S](index.md) and find focus [A](index.md) |

[common]\
open fun &lt;[T](at.md)&gt; [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;

DSL to compose [At](index.md) with an [Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).

#### Receiver

[Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) with a focus in [S](index.md)

#### Return

[Optional](../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) with a focus in [A](index.md) at given index [I](index.md).

## Parameters

common

| | |
|---|---|
| i | index [I](index.md) to zoom into [S](index.md) and find focus [A](index.md) |

[common]\
open fun &lt;[T](at.md)&gt; [Getter](../../arrow.optics/-getter/index.md)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Getter](../../arrow.optics/-getter/index.md)&lt;[T](at.md), [A](index.md)&gt;

DSL to compose [At](index.md) with a [Getter](../../arrow.optics/-getter/index.md) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).

#### Receiver

[Getter](../../arrow.optics/-getter/index.md) with a focus in [S](index.md)

#### Return

[Getter](../../arrow.optics/-getter/index.md) with a focus in [A](index.md) at given index [I](index.md).

## Parameters

common

| | |
|---|---|
| i | index [I](index.md) to zoom into [S](index.md) and find focus [A](index.md) |

[common]\
open fun &lt;[T](at.md)&gt; [Setter](../../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Setter](../../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;

DSL to compose [At](index.md) with a [Setter](../../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).

#### Receiver

[Setter](../../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156) with a focus in [S](index.md)

#### Return

[Setter](../../arrow.optics/index.md#744232174%2FClasslikes%2F-617900156) with a focus in [A](index.md) at given index [I](index.md).

## Parameters

common

| | |
|---|---|
| i | index [I](index.md) to zoom into [S](index.md) and find focus [A](index.md) |

[common]\
open fun &lt;[T](at.md)&gt; [Traversal](../../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Traversal](../../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156)&lt;[T](at.md), [A](index.md)&gt;

DSL to compose [At](index.md) with a [Traversal](../../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).

#### Receiver

[Traversal](../../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156) with a focus in [S](index.md)

#### Return

[Traversal](../../arrow.optics/index.md#153853783%2FClasslikes%2F-617900156) with a focus in [A](index.md) at given index [I](index.md).

## Parameters

common

| | |
|---|---|
| i | index [I](index.md) to zoom into [S](index.md) and find focus [A](index.md) |

[common]\
open fun &lt;[T](at.md)&gt; [Fold](../../arrow.optics/-fold/index.md)&lt;[T](at.md), [S](index.md)&gt;.[at](at.md)(i: [I](index.md)): [Fold](../../arrow.optics/-fold/index.md)&lt;[T](at.md), [A](index.md)&gt;

DSL to compose [At](index.md) with a [Fold](../../arrow.optics/-fold/index.md) for a structure [S](index.md) to focus in on [A](index.md) at given index [I](index.md).

#### Receiver

[Fold](../../arrow.optics/-fold/index.md) with a focus in [S](index.md)

#### Return

[Fold](../../arrow.optics/-fold/index.md) with a focus in [A](index.md) at given index [I](index.md).

## Parameters

common

| | |
|---|---|
| i | index [I](index.md) to zoom into [S](index.md) and find focus [A](index.md) |
