//[arrow-core](../../index.md)/[arrow.typeclasses](index.md)

# Package arrow.typeclasses

## Types

| Name | Summary |
|---|---|
| [Monoid](-monoid/index.md) | [common]<br>interface [Monoid](-monoid/index.md)&lt;[A](-monoid/index.md)&gt; : [Semigroup](-semigroup/index.md)&lt;[A](-monoid/index.md)&gt; |
| [Semigroup](-semigroup/index.md) | [common]<br>fun interface [Semigroup](-semigroup/index.md)&lt;[A](-semigroup/index.md)&gt; |
| [Semiring](-semiring/index.md) | [common]<br>interface [Semiring](-semiring/index.md)&lt;[A](-semiring/index.md)&gt;<br>The [Semiring](-semiring/index.md) type class for a given type A combines both a commutative additive [Monoid](-monoid/index.md) and a multiplicative [Monoid](-monoid/index.md). It requires the multiplicative [Monoid](-monoid/index.md) to distribute over the additive one. The operations of the multiplicative [Monoid](-monoid/index.md) have been renamed to [one](-semiring/one.md) and [combineMultiplicate](-semiring/combine-multiplicate.md) for easier use. |

## Properties

| Name | Summary |
|---|---|
| [DoubleInstanceDeprecation](-double-instance-deprecation.md) | [common]<br>const val [DoubleInstanceDeprecation](-double-instance-deprecation.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [FloatInstanceDeprecation](-float-instance-deprecation.md) | [common]<br>const val [FloatInstanceDeprecation](-float-instance-deprecation.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
