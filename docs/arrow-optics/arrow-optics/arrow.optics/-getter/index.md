//[arrow-optics](../../../index.md)/[arrow.optics](../index.md)/[Getter](index.md)

# Getter

[common]\
fun interface [Getter](index.md)&lt;[S](index.md), [A](index.md)&gt; : [Fold](../-fold/index.md)&lt;[S](index.md), [A](index.md)&gt; 

A [Getter](index.md) is an optic that allows to see into a structure and getting a focus.

A [Getter](index.md) can be seen as a get function:

<ul><li>get: (S) -&gt; A meaning we can look into an S and get an A</li></ul>

## Parameters

common

| | |
|---|---|
| S | the source of a [Getter](index.md) |
| A | the focus of a [Getter](index.md) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [all](../-fold/all.md) | [common]<br>open fun [all](../-fold/all.md)(source: [S](index.md), predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if all targets satisfy the predicate |
| [any](../-fold/any.md) | [common]<br>open fun [any](../-fold/any.md)(source: [S](index.md), predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true if at least one focus matches the given [predicate](../-fold/any.md). |
| [choice](../-fold/choice.md) | [common]<br>open infix fun &lt;[C](../-fold/choice.md)&gt; [choice](../-fold/choice.md)(other: [Fold](../-fold/index.md)&lt;[C](../-fold/choice.md), [A](index.md)&gt;): [Fold](../-fold/index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[S](index.md), [C](../-fold/choice.md)&gt;, [A](index.md)&gt;<br>Join two [Fold](../-fold/index.md) with the same target<br>[common]<br>open infix fun &lt;[C](choice.md)&gt; [choice](choice.md)(other: [Getter](index.md)&lt;[C](choice.md), [A](index.md)&gt;): [Getter](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[S](index.md), [C](choice.md)&gt;, [A](index.md)&gt;<br>Join two [Getter](index.md) with the same focus |
| [combineAll](../-fold/combine-all.md) | [common]<br>open fun [combineAll](../-fold/combine-all.md)(M: [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[A](index.md)&gt;, source: [S](index.md)): [A](index.md)<br>Alias for fold. |
| [compose](../-fold/compose.md) | [common]<br>open infix fun &lt;[C](../-fold/compose.md)&gt; [compose](../-fold/compose.md)(other: [Fold](../-fold/index.md)&lt;in [A](index.md), out [C](../-fold/compose.md)&gt;): [Fold](../-fold/index.md)&lt;[S](index.md), [C](../-fold/compose.md)&gt;<br>Compose a [Fold](../-fold/index.md) with a [Fold](../-fold/index.md)<br>[common]<br>open infix fun &lt;[C](compose.md)&gt; [compose](compose.md)(other: [Getter](index.md)&lt;in [A](index.md), out [C](compose.md)&gt;): [Getter](index.md)&lt;[S](index.md), [C](compose.md)&gt;<br>Compose a [Getter](index.md) with a [Getter](index.md) |
| [exists](../-fold/exists.md) | [common]<br>open fun [exists](../-fold/exists.md)(source: [S](index.md), predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check whether at least one element satisfies the predicate. |
| [findOrNull](../-fold/find-or-null.md) | [common]<br>open fun [findOrNull](../-fold/find-or-null.md)(source: [S](index.md), predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [A](index.md)?<br>Find the first element matching the predicate, if one exists. |
| [first](first.md) | [common]<br>open fun &lt;[C](first.md)&gt; [first](first.md)(): [Getter](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[S](index.md), [C](first.md)&gt;, [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [C](first.md)&gt;&gt;<br>Create a product of the [Getter](index.md) and a type [C](first.md) |
| [firstOrNull](../-fold/first-or-null.md) | [common]<br>open fun [firstOrNull](../-fold/first-or-null.md)(source: [S](index.md)): [A](index.md)?<br>Get the first target or null |
| [fold](../-fold/fold.md) | [common]<br>open fun [fold](../-fold/fold.md)(M: [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[A](index.md)&gt;, source: [S](index.md)): [A](index.md)<br>Fold using the given [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md) instance. |
| [foldMap](fold-map.md) | [common]<br>open override fun &lt;[R](fold-map.md)&gt; [foldMap](fold-map.md)(M: [Monoid](../../../../arrow-core/arrow-core/arrow.typeclasses/-monoid/index.md)&lt;[R](fold-map.md)&gt;, source: [S](index.md), map: ([A](index.md)) -&gt; [R](fold-map.md)): [R](fold-map.md)<br>Map each target to a type R and use a Monoid to fold the results |
| [get](get.md) | [common]<br>abstract fun [get](get.md)(source: [S](index.md)): [A](index.md)<br>Get the focus of a [Getter](index.md) |
| [getAll](../-fold/get-all.md) | [common]<br>open fun [getAll](../-fold/get-all.md)(source: [S](index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt;<br>Get all targets of the [Fold](../-fold/index.md) |
| [isEmpty](../-fold/is-empty.md) | [common]<br>open fun [isEmpty](../-fold/is-empty.md)(source: [S](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if there is no target |
| [isNotEmpty](../-fold/is-not-empty.md) | [common]<br>open fun [isNotEmpty](../-fold/is-not-empty.md)(source: [S](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if there is at least one target |
| [lastOrNull](../-fold/last-or-null.md) | [common]<br>open fun [lastOrNull](../-fold/last-or-null.md)(source: [S](index.md)): [A](index.md)?<br>Get the last target or null |
| [left](left.md) | [common]<br>open override fun &lt;[C](left.md)&gt; [left](left.md)(): [Getter](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[S](index.md), [C](left.md)&gt;, [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[A](index.md), [C](left.md)&gt;&gt;<br>Create a sum of the [Getter](index.md) and type [C](left.md) |
| [plus](../-fold/plus.md) | [common]<br>open operator fun &lt;[C](../-fold/plus.md)&gt; [plus](../-fold/plus.md)(other: [Fold](../-fold/index.md)&lt;in [A](index.md), out [C](../-fold/plus.md)&gt;): [Fold](../-fold/index.md)&lt;[S](index.md), [C](../-fold/plus.md)&gt;<br>open operator fun &lt;[C](plus.md)&gt; [plus](plus.md)(other: [Getter](index.md)&lt;in [A](index.md), out [C](plus.md)&gt;): [Getter](index.md)&lt;[S](index.md), [C](plus.md)&gt; |
| [right](right.md) | [common]<br>open override fun &lt;[C](right.md)&gt; [right](right.md)(): [Getter](index.md)&lt;[Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[C](right.md), [S](index.md)&gt;, [Either](../../../../arrow-core/arrow-core/arrow.core/-either/index.md)&lt;[C](right.md), [A](index.md)&gt;&gt;<br>Create a sum of type [C](right.md) and the [Getter](index.md) |
| [second](second.md) | [common]<br>open fun &lt;[C](second.md)&gt; [second](second.md)(): [Getter](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[C](second.md), [S](index.md)&gt;, [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[C](second.md), [A](index.md)&gt;&gt;<br>Create a product of type [C](second.md) and the [Getter](index.md) |
| [size](../-fold/size.md) | [common]<br>open fun [size](../-fold/size.md)(source: [S](index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Calculate the number of targets |
| [split](split.md) | [common]<br>open infix fun &lt;[C](split.md), [D](split.md)&gt; [split](split.md)(other: [Getter](index.md)&lt;[C](split.md), [D](split.md)&gt;): [Getter](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[S](index.md), [C](split.md)&gt;, [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [D](split.md)&gt;&gt;<br>Pair two disjoint [Getter](index.md) |
| [zip](zip.md) | [common]<br>open infix fun &lt;[C](zip.md)&gt; [zip](zip.md)(other: [Getter](index.md)&lt;[S](index.md), [C](zip.md)&gt;): [Getter](index.md)&lt;[S](index.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [C](zip.md)&gt;&gt;<br>Zip two [Getter](index.md) optics with the same source [S](index.md) |

## Inheritors

| Name |
|---|
| [PIso](../-p-iso/index.md) |
| [PLens](../-p-lens/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [at](../../arrow.optics.dsl/at.md) | [common]<br>fun &lt;[T](../../arrow.optics.dsl/at.md), [S](../../arrow.optics.dsl/at.md), [I](../../arrow.optics.dsl/at.md), [A](../../arrow.optics.dsl/at.md)&gt; [Getter](index.md)&lt;[T](../../arrow.optics.dsl/at.md), [S](../../arrow.optics.dsl/at.md)&gt;.[at](../../arrow.optics.dsl/at.md)(AT: [At](../../arrow.optics.typeclasses/-at/index.md)&lt;[S](../../arrow.optics.dsl/at.md), [I](../../arrow.optics.dsl/at.md), [A](../../arrow.optics.dsl/at.md)&gt;, i: [I](../../arrow.optics.dsl/at.md)): [Getter](index.md)&lt;[T](../../arrow.optics.dsl/at.md), [A](../../arrow.optics.dsl/at.md)&gt;<br>DSL to compose [At](../../arrow.optics.typeclasses/-at/index.md) with a [Getter](index.md) for a structure [S](../../arrow.optics.dsl/at.md) to focus in on [A](../../arrow.optics.dsl/at.md) at given index [I](../../arrow.optics.dsl/at.md). |
