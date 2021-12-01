//[arrow-optics](../../../../index.md)/[arrow.optics.typeclasses](../../index.md)/[At](../index.md)/[Companion](index.md)/[fromIso](from-iso.md)

# fromIso

[common]\
fun &lt;[S](from-iso.md), [U](from-iso.md), [I](from-iso.md), [A](from-iso.md)&gt; [fromIso](from-iso.md)(AT: [At](../index.md)&lt;[U](from-iso.md), [I](from-iso.md), [A](from-iso.md)&gt;, iso: [Iso](../../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156)&lt;[S](from-iso.md), [U](from-iso.md)&gt;): [At](../index.md)&lt;[S](from-iso.md), [I](from-iso.md), [A](from-iso.md)&gt;

Lift an instance of [At](../index.md) using an [Iso](../../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156).

#### Return

[At](../index.md) to provide [Lens](../../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) for structure [S](from-iso.md) with focus in [A](from-iso.md) at given index [I](from-iso.md)

## Parameters

common

| | |
|---|---|
| AT | [At](../index.md) that can provide [Lens](../../../arrow.optics/index.md#-141055921%2FClasslikes%2F-617900156) for a structure [U](from-iso.md) with a focus in [A](from-iso.md) with given index [I](from-iso.md). |
| iso | [Iso](../../../arrow.optics/index.md#1786632304%2FClasslikes%2F-617900156) that defines an isomorphism between [S](from-iso.md) and [U](from-iso.md) |
