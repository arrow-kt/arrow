//[arrow-optics](../../index.md)/[arrow.optics.typeclasses](index.md)/[remove](remove.md)

# remove

[common]\
fun &lt;[S](remove.md), [I](remove.md), [A](remove.md)&gt; [At](-at/index.md)&lt;[S](remove.md), [I](remove.md), [Option](../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](remove.md)&gt;&gt;.[remove](remove.md)(s: [S](remove.md), i: [I](remove.md)): [S](remove.md)

Delete a value associated with a key in a Map-like container

#### Receiver

[At](-at/index.md) to provide a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) where an [Option](../../../arrow-core/arrow-core/arrow.core/-option/index.md) focus can be found at index [I](remove.md) for a structure [S](remove.md).

#### Return

[S](remove.md) where focus [A](remove.md) was removed at index [I](remove.md)

## Parameters

common

| | |
|---|---|
| s | [S](remove.md) structure to zoom into and find focus [A](remove.md). |
| i | index [I](remove.md) to zoom into [S](remove.md) and find focus [A](remove.md) |

[common]\
fun &lt;[S](remove.md), [I](remove.md), [A](remove.md)&gt; [At](-at/index.md)&lt;[S](remove.md), [I](remove.md), [Option](../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[A](remove.md)&gt;&gt;.[remove](remove.md)(i: [I](remove.md)): ([S](remove.md)) -&gt; [S](remove.md)

Lift deletion of a value associated with a key in a Map-like container

#### Receiver

[At](-at/index.md) to provide a [Lens](../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) where an [Option](../../../arrow-core/arrow-core/arrow.core/-option/index.md) focus can be found at index [I](remove.md) for a structure [S](remove.md).

#### Return

function that takes [S](remove.md) and returns a new [S](remove.md) where focus [A](remove.md) was removed at index [I](remove.md)

## Parameters

common

| | |
|---|---|
| i | index [I](remove.md) to zoom into [S](remove.md) and find focus [A](remove.md) |
