//[arrow-core](../../../index.md)/[arrow.typeclasses](../index.md)/[Semigroup](index.md)

# Semigroup

[common]\
fun interface [Semigroup](index.md)&lt;[A](index.md)&gt;

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [combine](combine.md) | [common]<br>abstract fun [A](index.md).[combine](combine.md)(b: [A](index.md)): [A](index.md)<br>Combine two [A](index.md) values. |
| [maybeCombine](maybe-combine.md) | [common]<br>open fun [A](index.md).[maybeCombine](maybe-combine.md)(b: [A](index.md)?): [A](index.md) |
| [plus](plus.md) | [common]<br>open operator fun [A](index.md).[plus](plus.md)(b: [A](index.md)): [A](index.md) |

## Inheritors

| Name |
|---|
| [Monoid](../-monoid/index.md) |
| [Semigroup.Companion](-companion/-non-empty-list-semigroup/index.md) |
