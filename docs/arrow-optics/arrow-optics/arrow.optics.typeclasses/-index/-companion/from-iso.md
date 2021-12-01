//[arrow-optics](../../../../index.md)/[arrow.optics.typeclasses](../../index.md)/[Index](../index.md)/[Companion](index.md)/[fromIso](from-iso.md)

# fromIso

[common]\
fun &lt;[S](from-iso.md), [A](from-iso.md), [I](from-iso.md), [B](from-iso.md)&gt; [fromIso](from-iso.md)(ID: [Index](../index.md)&lt;[A](from-iso.md), [I](from-iso.md), [B](from-iso.md)&gt;, iso: [Iso](../../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156)&lt;[S](from-iso.md), [A](from-iso.md)&gt;): [Index](../index.md)&lt;[S](from-iso.md), [I](from-iso.md), [B](from-iso.md)&gt;

Lift an instance of [Index](../index.md) using an [Iso](../../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156).

#### Return

[Index](../index.md) for a structure [S](from-iso.md) to focus in an optional [A](from-iso.md) at a given index [I](from-iso.md)

## Parameters

common

| | |
|---|---|
| ID | [Index](../index.md) instance to provide a [Optional](../../../arrow.optics/index.md#-1955528147%2FClasslikes%2F-617900156) to focus into [S](from-iso.md) at [I](from-iso.md) |
| iso | [Iso](../../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156) that defines an isomorphism between a type [S](from-iso.md) and [A](from-iso.md) |
