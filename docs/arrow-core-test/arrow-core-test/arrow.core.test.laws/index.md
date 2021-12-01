//[arrow-core-test](../../index.md)/[arrow.core.test.laws](index.md)

# Package arrow.core.test.laws

## Types

| Name | Summary |
|---|---|
| [FxLaws](-fx-laws/index.md) | [common]<br>object [FxLaws](-fx-laws/index.md) |
| [Law](-law/index.md) | [common]<br>data class [Law](-law/index.md)(name: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), test: suspend TestContext.() -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) |
| [MonoidLaws](-monoid-laws/index.md) | [common]<br>object [MonoidLaws](-monoid-laws/index.md) |
| [SemigroupLaws](-semigroup-laws/index.md) | [common]<br>object [SemigroupLaws](-semigroup-laws/index.md) |
| [SemiringLaws](-semiring-laws/index.md) | [common]<br>object [SemiringLaws](-semiring-laws/index.md) |

## Functions

| Name | Summary |
|---|---|
| [equalUnderTheLaw](equal-under-the-law.md) | [common]<br>fun &lt;[A](equal-under-the-law.md)&gt; [A](equal-under-the-law.md).[equalUnderTheLaw](equal-under-the-law.md)(b: [A](equal-under-the-law.md), f: ([A](equal-under-the-law.md), [A](equal-under-the-law.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = { a, b -&gt; a == b }): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
