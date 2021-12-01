//[arrow-optics](../../../index.md)/[arrow.optics](../index.md)/[Fold](index.md)

# Fold

[common]\
interface [Fold](index.md)&lt;[S](index.md), [A](index.md)&gt;

A [Fold](index.md) is an optic that allows to focus into structure and get multiple results.

[Fold](index.md) is a generalisation of an instance of Foldable and is implemented in terms of foldMap.

## Parameters

common

| | |
|---|---|
| S | the source of a [Fold](index.md) |
| A | the target of a [Fold](index.md) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [all](all.md) | [common]<br>open fun [all](all.md)(source: [S](index.md), predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if all targets satisfy the predicate |
| [any](any.md) | [common]<br>open fun [any](any.md)(source: [S](index.md), predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true if at least one focus matches the given [predicate](any.md). |
| [choice](choice.md) | [common]<br>open infix fun &lt;[C](choice.md)&gt; [choice](choice.md)(other: [Fold](index.md)&lt;[C](choice.md), [A](index.md)&gt;): [Fold](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[S](index.md), [C](choice.md)&gt;, [A](index.md)&gt;<br>Join two [Fold](index.md) with the same target |
| [combineAll](combine-all.md) | [common]<br>open fun [combineAll](combine-all.md)(M: [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[A](index.md)&gt;, source: [S](index.md)): [A](index.md)<br>Alias for fold. |
| [compose](compose.md) | [common]<br>open infix fun &lt;[C](compose.md)&gt; [compose](compose.md)(other: [Fold](index.md)&lt;in [A](index.md), out [C](compose.md)&gt;): [Fold](index.md)&lt;[S](index.md), [C](compose.md)&gt;<br>Compose a [Fold](index.md) with a [Fold](index.md) |
| [exists](exists.md) | [common]<br>open fun [exists](exists.md)(source: [S](index.md), predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check whether at least one element satisfies the predicate. |
| [findOrNull](find-or-null.md) | [common]<br>open fun [findOrNull](find-or-null.md)(source: [S](index.md), predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [A](index.md)?<br>Find the first element matching the predicate, if one exists. |
| [firstOrNull](first-or-null.md) | [common]<br>open fun [firstOrNull](first-or-null.md)(source: [S](index.md)): [A](index.md)?<br>Get the first target or null |
| [fold](fold.md) | [common]<br>open fun [fold](fold.md)(M: [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[A](index.md)&gt;, source: [S](index.md)): [A](index.md)<br>Fold using the given [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md) instance. |
| [foldMap](fold-map.md) | [common]<br>abstract fun &lt;[R](fold-map.md)&gt; [foldMap](fold-map.md)(M: [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[R](fold-map.md)&gt;, source: [S](index.md), map: ([A](index.md)) -&gt; [R](fold-map.md)): [R](fold-map.md)<br>Map each target to a type R and use a Monoid to fold the results |
| [getAll](get-all.md) | [common]<br>open fun [getAll](get-all.md)(source: [S](index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt;<br>Get all targets of the [Fold](index.md) |
| [isEmpty](is-empty.md) | [common]<br>open fun [isEmpty](is-empty.md)(source: [S](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if there is no target |
| [isNotEmpty](is-not-empty.md) | [common]<br>open fun [isNotEmpty](is-not-empty.md)(source: [S](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if there is at least one target |
| [lastOrNull](last-or-null.md) | [common]<br>open fun [lastOrNull](last-or-null.md)(source: [S](index.md)): [A](index.md)?<br>Get the last target or null |
| [left](left.md) | [common]<br>open fun &lt;[C](left.md)&gt; [left](left.md)(): [Fold](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[S](index.md), [C](left.md)&gt;, [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](index.md), [C](left.md)&gt;&gt;<br>Create a sum of the [Fold](index.md) and a type [C](left.md) |
| [plus](plus.md) | [common]<br>open operator fun &lt;[C](plus.md)&gt; [plus](plus.md)(other: [Fold](index.md)&lt;in [A](index.md), out [C](plus.md)&gt;): [Fold](index.md)&lt;[S](index.md), [C](plus.md)&gt; |
| [right](right.md) | [common]<br>open fun &lt;[C](right.md)&gt; [right](right.md)(): [Fold](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[C](right.md), [S](index.md)&gt;, [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[C](right.md), [A](index.md)&gt;&gt;<br>Create a sum of a type [C](right.md) and the [Fold](index.md) |
| [size](size.md) | [common]<br>open fun [size](size.md)(source: [S](index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Calculate the number of targets |

## Inheritors

| Name |
|---|
| [PEvery](../-p-every/index.md) |
| [Getter](../-getter/index.md) |
| [PIso](../-p-iso/index.md) |
| [PLens](../-p-lens/index.md) |
| [POptional](../-p-optional/index.md) |
| [PPrism](../-p-prism/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [at](../../arrow.optics.dsl/at.md) | [common]<br>fun &lt;[T](../../arrow.optics.dsl/at.md), [S](../../arrow.optics.dsl/at.md), [I](../../arrow.optics.dsl/at.md), [A](../../arrow.optics.dsl/at.md)&gt; [Fold](index.md)&lt;[T](../../arrow.optics.dsl/at.md), [S](../../arrow.optics.dsl/at.md)&gt;.[at](../../arrow.optics.dsl/at.md)(AT: [At](../../arrow.optics.typeclasses/-at/index.md)&lt;[S](../../arrow.optics.dsl/at.md), [I](../../arrow.optics.dsl/at.md), [A](../../arrow.optics.dsl/at.md)&gt;, i: [I](../../arrow.optics.dsl/at.md)): [Fold](index.md)&lt;[T](../../arrow.optics.dsl/at.md), [A](../../arrow.optics.dsl/at.md)&gt;<br>DSL to compose [At](../../arrow.optics.typeclasses/-at/index.md) with a [Fold](index.md) for a structure [S](../../arrow.optics.dsl/at.md) to focus in on [A](../../arrow.optics.dsl/at.md) at given index [I](../../arrow.optics.dsl/at.md). |
| [every](../../arrow.optics.dsl/every.md) | [common]<br>fun &lt;[T](../../arrow.optics.dsl/every.md), [S](../../arrow.optics.dsl/every.md), [A](../../arrow.optics.dsl/every.md)&gt; [Fold](index.md)&lt;[T](../../arrow.optics.dsl/every.md), [S](../../arrow.optics.dsl/every.md)&gt;.[every](../../arrow.optics.dsl/every.md)(TR: [Every](../index.md#176863642%2FClasslikes%2F-617900156)&lt;[S](../../arrow.optics.dsl/every.md), [A](../../arrow.optics.dsl/every.md)&gt;): [Fold](index.md)&lt;[T](../../arrow.optics.dsl/every.md), [A](../../arrow.optics.dsl/every.md)&gt;<br>DSL to compose [Traversal](../index.md#153853783%2FClasslikes%2F-617900156) with a [Fold](index.md) for a structure [S](../../arrow.optics.dsl/every.md) to see all its foci [A](../../arrow.optics.dsl/every.md) |
| [index](../../arrow.optics.dsl/--index--.md) | [common]<br>fun &lt;[T](../../arrow.optics.dsl/--index--.md), [S](../../arrow.optics.dsl/--index--.md), [I](../../arrow.optics.dsl/--index--.md), [A](../../arrow.optics.dsl/--index--.md)&gt; [Fold](index.md)&lt;[T](../../arrow.optics.dsl/--index--.md), [S](../../arrow.optics.dsl/--index--.md)&gt;.[index](../../arrow.optics.dsl/--index--.md)(ID: [Index](../../arrow.optics.typeclasses/-index/index.md)&lt;[S](../../arrow.optics.dsl/--index--.md), [I](../../arrow.optics.dsl/--index--.md), [A](../../arrow.optics.dsl/--index--.md)&gt;, i: [I](../../arrow.optics.dsl/--index--.md)): [Fold](index.md)&lt;[T](../../arrow.optics.dsl/--index--.md), [A](../../arrow.optics.dsl/--index--.md)&gt;<br>DSL to compose [Index](../../arrow.optics.typeclasses/-index/index.md) with a [Fold](index.md) for a structure [S](../../arrow.optics.dsl/--index--.md) to focus in on [A](../../arrow.optics.dsl/--index--.md) at given index [I](../../arrow.optics.dsl/--index--.md) |
| [some](../../arrow.optics.dsl/some.md) | [common]<br>val &lt;[T](../../arrow.optics.dsl/some.md), [S](../../arrow.optics.dsl/some.md)&gt; [Fold](index.md)&lt;[T](../../arrow.optics.dsl/some.md), [Option](../../../../arrow-core/arrow-core/arrow.core/-option/index.md)&lt;[S](../../arrow.optics.dsl/some.md)&gt;&gt;.[some](../../arrow.optics.dsl/some.md): [Fold](index.md)&lt;[T](../../arrow.optics.dsl/some.md), [S](../../arrow.optics.dsl/some.md)&gt;<br>DSL to compose a [Prism](../index.md#1394331700%2FClasslikes%2F-617900156) with focus [arrow.core.Some](../../../../arrow-core/arrow-core/arrow.core/-some/index.md) with a [Fold](index.md) with a focus of [Option](../../../../arrow-core/arrow-core/arrow.core/-option/index.md)<[S](../../arrow.optics.dsl/some.md)> |
