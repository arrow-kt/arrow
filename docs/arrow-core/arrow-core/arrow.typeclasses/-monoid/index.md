//[arrow-core](../../../index.md)/[arrow.typeclasses](../index.md)/[Monoid](index.md)

# Monoid

[common]\
interface [Monoid](index.md)&lt;[A](index.md)&gt; : [Semigroup](../-semigroup/index.md)&lt;[A](index.md)&gt;

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [combine](../-semigroup/combine.md) | [common]<br>abstract fun [A](index.md).[combine](../-semigroup/combine.md)(b: [A](index.md)): [A](index.md)<br>Combine two [A](../-semigroup/index.md) values. |
| [combineAll](combine-all.md) | [common]<br>open fun [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)&lt;[A](index.md)&gt;.[combineAll](combine-all.md)(): [A](index.md)<br>Combine an [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html) of [A](index.md) values.<br>[common]<br>open fun [combineAll](combine-all.md)(elems: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt;): [A](index.md)<br>Combine an array of [A](index.md) values. |
| [empty](empty.md) | [common]<br>abstract fun [empty](empty.md)(): [A](index.md)<br>A zero value for this A |
| [maybeCombine](../-semigroup/maybe-combine.md) | [common]<br>open fun [A](index.md).[maybeCombine](../-semigroup/maybe-combine.md)(b: [A](index.md)?): [A](index.md) |
| [plus](../-semigroup/plus.md) | [common]<br>open operator fun [A](index.md).[plus](../-semigroup/plus.md)(b: [A](index.md)): [A](index.md) |
