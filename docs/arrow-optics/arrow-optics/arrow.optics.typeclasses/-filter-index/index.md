//[arrow-optics](../../../index.md)/[arrow.optics.typeclasses](../index.md)/[FilterIndex](index.md)

# FilterIndex

[common]\
fun interface [FilterIndex](index.md)&lt;[S](index.md), [I](index.md), [A](index.md)&gt;

[FilterIndex](index.md) provides a [Every](../../arrow.optics/index.md#176863642%2FClasslikes%2F-617900156) for a structure [S](index.md) with all its foci [A](index.md) whose index [I](index.md) satisfies a predicate.

## Parameters

common

| | |
|---|---|
| S | source of [Every](../../arrow.optics/index.md#176863642%2FClasslikes%2F-617900156) |
| I | index that uniquely identifies every focus of the [Every](../../arrow.optics/index.md#176863642%2FClasslikes%2F-617900156) |
| A | focus that is supposed to be unique for a given pair [S](index.md) and [I](index.md) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [filter](filter.md) | [common]<br>abstract fun [filter](filter.md)(p: [Predicate](../../../../arrow-core/arrow.core/-predicate/index.md)&lt;[I](index.md)&gt;): [Every](../../arrow.optics/index.md#176863642%2FClasslikes%2F-617900156)&lt;[S](index.md), [A](index.md)&gt;<br>Filter the foci [A](index.md) of a [Every](../../arrow.optics/index.md#176863642%2FClasslikes%2F-617900156) with the predicate [p](filter.md). |
