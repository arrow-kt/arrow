//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[NonEmptyList](index.md)

# NonEmptyList

[common]\
class [NonEmptyList](index.md)&lt;out [A](index.md)&gt;(head: [A](index.md), tail: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt;) : [AbstractList](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-abstract-list/index.html)&lt;[A](index.md)&gt; 

NonEmptyList is a data type used in **Λrrow** to model ordered lists that guarantee to have at least one value. NonEmptyList is available in the arrow-core module under the import arrow.core.NonEmptyList

##  nonEmptyListOf

A NonEmptyList guarantees the list always has at least 1 element.

import arrow.core.nonEmptyListOf\
\
val value =\
//sampleStart\
 // nonEmptyListOf() // does not compile\
 nonEmptyListOf(1, 2, 3, 4, 5) // NonEmptyList&lt;Int&gt;\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-nonemptylist-01.kt -->

##  head

Unlike List[0], NonEmptyList.head it's a safe operation that guarantees no exception throwing.

import arrow.core.nonEmptyListOf\
\
val value =\
//sampleStart\
 nonEmptyListOf(1, 2, 3, 4, 5).head\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-nonemptylist-02.kt -->

##  foldLeft

When we fold over a NonEmptyList, we turn a NonEmptyList&lt; A &gt; into B by providing a **seed** value and a **function** that carries the state on each iteration over the elements of the list. The first argument is a function that addresses the **seed value**, this can be any object of any type which will then become the resulting typed value. The second argument is a function that takes the current state and element in the iteration and returns the new state after transformations have been applied.

import arrow.core.NonEmptyList\
import arrow.core.nonEmptyListOf\
\
//sampleStart\
fun sumNel(nel: NonEmptyList&lt;Int&gt;): Int =\
 nel.foldLeft(0) { acc, n -&gt; acc + n }\
val value = sumNel(nonEmptyListOf(1, 1, 1, 1))\
//sampleEnd\
fun main() {\
 println("value = $value")\
}<!--- KNIT example-nonemptylist-03.kt -->

##  map

map allows us to transform A into B in NonEmptyList&lt; A &gt;

import arrow.core.nonEmptyListOf\
\
val value =\
//sampleStart\
 nonEmptyListOf(1, 1, 1, 1).map { it + 1 }\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-nonemptylist-04.kt -->

##  Combining NonEmptyLists

###  flatMap

flatMap allows us to compute over the contents of multiple NonEmptyList&lt; * &gt; values

import arrow.core.NonEmptyList\
import arrow.core.nonEmptyListOf\
\
//sampleStart\
val nelOne: NonEmptyList&lt;Int&gt; = nonEmptyListOf(1, 2, 3)\
val nelTwo: NonEmptyList&lt;Int&gt; = nonEmptyListOf(4, 5)\
\
val value = nelOne.flatMap { one -&gt;\
 nelTwo.map { two -&gt;\
   one + two\
 }\
}\
//sampleEnd\
fun main() {\
 println("value = $value")\
}<!--- KNIT example-nonemptylist-05.kt -->

###  zip

Λrrow contains methods that allow you to preserve type information when computing over different NonEmptyList typed values.

import arrow.core.NonEmptyList\
import arrow.core.nonEmptyListOf\
import arrow.core.zip\
import java.util.UUID\
\
//sampleStart\
data class Person(val id: UUID, val name: String, val year: Int)\
\
// Note each NonEmptyList is of a different type\
val nelId: NonEmptyList&lt;UUID&gt; = nonEmptyListOf(UUID.randomUUID(), UUID.randomUUID())\
val nelName: NonEmptyList&lt;String&gt; = nonEmptyListOf("William Alvin Howard", "Haskell Curry")\
val nelYear: NonEmptyList&lt;Int&gt; = nonEmptyListOf(1926, 1900)\
\
val value = nelId.zip(nelName, nelYear) { id, name, year -&gt;\
 Person(id, name, year)\
}\
//sampleEnd\
fun main() {\
 println("value = $value")\
}<!--- KNIT example-nonemptylist-06.kt -->

###  Summary

<ul><li>NonEmptyList is **used to model lists that guarantee at least one element**</li><li>We can easily construct values of NonEmptyList with nonEmptyListOf</li><li>foldLeft, map, flatMap and others are used to compute over the internal contents of a NonEmptyList value.</li><li>a.zip(b, c) { ... } can be used to compute over multiple NonEmptyList values preserving type information and **abstracting over arity** with zip</li></ul>

## Constructors

| | |
|---|---|
| [NonEmptyList](-non-empty-list.md) | [common]<br>fun &lt;out [A](index.md)&gt; [NonEmptyList](-non-empty-list.md)(head: [A](index.md), tail: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt;) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [align](align.md) | [common]<br>fun &lt;[B](align.md)&gt; [align](align.md)(b: [NonEmptyList](index.md)&lt;[B](align.md)&gt;): [NonEmptyList](index.md)&lt;[Ior](../-ior/index.md)&lt;[A](index.md), [B](align.md)&gt;&gt; |
| [coflatMap](coflat-map.md) | [common]<br>fun &lt;[B](coflat-map.md)&gt; [coflatMap](coflat-map.md)(f: ([NonEmptyList](index.md)&lt;[A](index.md)&gt;) -&gt; [B](coflat-map.md)): [NonEmptyList](index.md)&lt;[B](coflat-map.md)&gt; |
| [contains](index.md#-1071816952%2FFunctions%2F-1961959459) | [common]<br>open operator override fun [contains](index.md#-1071816952%2FFunctions%2F-1961959459)(element: [A](index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [containsAll](index.md#707016467%2FFunctions%2F-1961959459) | [common]<br>open override fun [containsAll](index.md#707016467%2FFunctions%2F-1961959459)(elements: [Collection](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-collection/index.html)&lt;[A](index.md)&gt;): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [equals](equals.md) | [common]<br>open operator override fun [equals](equals.md)(other: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [extract](extract.md) | [common]<br>fun [extract](extract.md)(): [A](index.md) |
| [flatMap](flat-map.md) | [common]<br>inline fun &lt;[B](flat-map.md)&gt; [flatMap](flat-map.md)(f: ([A](index.md)) -&gt; [NonEmptyList](index.md)&lt;[B](flat-map.md)&gt;): [NonEmptyList](index.md)&lt;[B](flat-map.md)&gt; |
| [foldLeft](fold-left.md) | [common]<br>inline fun &lt;[B](fold-left.md)&gt; [foldLeft](fold-left.md)(b: [B](fold-left.md), f: ([B](fold-left.md), [A](index.md)) -&gt; [B](fold-left.md)): [B](fold-left.md) |
| [get](get.md) | [common]<br>open operator override fun [get](get.md)(index: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [A](index.md) |
| [hashCode](hash-code.md) | [common]<br>open override fun [hashCode](hash-code.md)(): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [indexOf](index.md#650270306%2FFunctions%2F-1961959459) | [common]<br>open override fun [indexOf](index.md#650270306%2FFunctions%2F-1961959459)(element: [A](index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [isEmpty](is-empty.md) | [common]<br>open override fun [isEmpty](is-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [iterator](index.md#1248735623%2FFunctions%2F-1961959459) | [common]<br>open operator override fun [iterator](index.md#1248735623%2FFunctions%2F-1961959459)(): [Iterator](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterator/index.html)&lt;[A](index.md)&gt; |
| [lastIndexOf](index.md#-720747284%2FFunctions%2F-1961959459) | [common]<br>open override fun [lastIndexOf](index.md#-720747284%2FFunctions%2F-1961959459)(element: [A](index.md)): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [listIterator](index.md#158404745%2FFunctions%2F-1961959459) | [common]<br>open override fun [listIterator](index.md#158404745%2FFunctions%2F-1961959459)(): [ListIterator](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list-iterator/index.html)&lt;[A](index.md)&gt;<br>open override fun [listIterator](index.md#1759949543%2FFunctions%2F-1961959459)(index: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [ListIterator](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list-iterator/index.html)&lt;[A](index.md)&gt; |
| [map](map.md) | [common]<br>inline fun &lt;[B](map.md)&gt; [map](map.md)(f: ([A](index.md)) -&gt; [B](map.md)): [NonEmptyList](index.md)&lt;[B](map.md)&gt; |
| [padZip](pad-zip.md) | [common]<br>fun &lt;[B](pad-zip.md)&gt; [padZip](pad-zip.md)(other: [NonEmptyList](index.md)&lt;[B](pad-zip.md)&gt;): [NonEmptyList](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md)?, [B](pad-zip.md)?&gt;&gt; |
| [plus](plus.md) | [common]<br>operator fun [plus](plus.md)(a: @[UnsafeVariance](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unsafe-variance/index.html)[A](index.md)): [NonEmptyList](index.md)&lt;[A](index.md)&gt;<br>operator fun [plus](plus.md)(l: [NonEmptyList](index.md)&lt;@[UnsafeVariance](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unsafe-variance/index.html)[A](index.md)&gt;): [NonEmptyList](index.md)&lt;[A](index.md)&gt;<br>operator fun [plus](plus.md)(l: [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;@[UnsafeVariance](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unsafe-variance/index.html)[A](index.md)&gt;): [NonEmptyList](index.md)&lt;[A](index.md)&gt; |
| [salign](salign.md) | [common]<br>fun [salign](salign.md)(SA: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;@[UnsafeVariance](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unsafe-variance/index.html)[A](index.md)&gt;, b: [NonEmptyList](index.md)&lt;@[UnsafeVariance](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unsafe-variance/index.html)[A](index.md)&gt;): [NonEmptyList](index.md)&lt;[A](index.md)&gt; |
| [subList](index.md#-1763229096%2FFunctions%2F-1961959459) | [common]<br>open override fun [subList](index.md#-1763229096%2FFunctions%2F-1961959459)(fromIndex: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), toIndex: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt; |
| [toList](to-list.md) | [common]<br>fun [toList](to-list.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt; |
| [toString](to-string.md) | [common]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [zip](zip.md) | [common]<br>fun &lt;[B](zip.md)&gt; [zip](zip.md)(fb: [NonEmptyList](index.md)&lt;[B](zip.md)&gt;): [NonEmptyList](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](zip.md)&gt;&gt;<br>inline fun &lt;[B](zip.md), [Z](zip.md)&gt; [zip](zip.md)(b: [NonEmptyList](index.md)&lt;[B](zip.md)&gt;, map: ([A](index.md), [B](zip.md)) -&gt; [Z](zip.md)): [NonEmptyList](index.md)&lt;[Z](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [Z](zip.md)&gt; [zip](zip.md)(b: [NonEmptyList](index.md)&lt;[B](zip.md)&gt;, c: [NonEmptyList](index.md)&lt;[C](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md)) -&gt; [Z](zip.md)): [NonEmptyList](index.md)&lt;[Z](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [Z](zip.md)&gt; [zip](zip.md)(b: [NonEmptyList](index.md)&lt;[B](zip.md)&gt;, c: [NonEmptyList](index.md)&lt;[C](zip.md)&gt;, d: [NonEmptyList](index.md)&lt;[D](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md)) -&gt; [Z](zip.md)): [NonEmptyList](index.md)&lt;[Z](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [Z](zip.md)&gt; [zip](zip.md)(b: [NonEmptyList](index.md)&lt;[B](zip.md)&gt;, c: [NonEmptyList](index.md)&lt;[C](zip.md)&gt;, d: [NonEmptyList](index.md)&lt;[D](zip.md)&gt;, e: [NonEmptyList](index.md)&lt;[E](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md)) -&gt; [Z](zip.md)): [NonEmptyList](index.md)&lt;[Z](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [Z](zip.md)&gt; [zip](zip.md)(b: [NonEmptyList](index.md)&lt;[B](zip.md)&gt;, c: [NonEmptyList](index.md)&lt;[C](zip.md)&gt;, d: [NonEmptyList](index.md)&lt;[D](zip.md)&gt;, e: [NonEmptyList](index.md)&lt;[E](zip.md)&gt;, f: [NonEmptyList](index.md)&lt;[F](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md)) -&gt; [Z](zip.md)): [NonEmptyList](index.md)&lt;[Z](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [Z](zip.md)&gt; [zip](zip.md)(b: [NonEmptyList](index.md)&lt;[B](zip.md)&gt;, c: [NonEmptyList](index.md)&lt;[C](zip.md)&gt;, d: [NonEmptyList](index.md)&lt;[D](zip.md)&gt;, e: [NonEmptyList](index.md)&lt;[E](zip.md)&gt;, f: [NonEmptyList](index.md)&lt;[F](zip.md)&gt;, g: [NonEmptyList](index.md)&lt;[G](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md)) -&gt; [Z](zip.md)): [NonEmptyList](index.md)&lt;[Z](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [Z](zip.md)&gt; [zip](zip.md)(b: [NonEmptyList](index.md)&lt;[B](zip.md)&gt;, c: [NonEmptyList](index.md)&lt;[C](zip.md)&gt;, d: [NonEmptyList](index.md)&lt;[D](zip.md)&gt;, e: [NonEmptyList](index.md)&lt;[E](zip.md)&gt;, f: [NonEmptyList](index.md)&lt;[F](zip.md)&gt;, g: [NonEmptyList](index.md)&lt;[G](zip.md)&gt;, h: [NonEmptyList](index.md)&lt;[H](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md)) -&gt; [Z](zip.md)): [NonEmptyList](index.md)&lt;[Z](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [Z](zip.md)&gt; [zip](zip.md)(b: [NonEmptyList](index.md)&lt;[B](zip.md)&gt;, c: [NonEmptyList](index.md)&lt;[C](zip.md)&gt;, d: [NonEmptyList](index.md)&lt;[D](zip.md)&gt;, e: [NonEmptyList](index.md)&lt;[E](zip.md)&gt;, f: [NonEmptyList](index.md)&lt;[F](zip.md)&gt;, g: [NonEmptyList](index.md)&lt;[G](zip.md)&gt;, h: [NonEmptyList](index.md)&lt;[H](zip.md)&gt;, i: [NonEmptyList](index.md)&lt;[I](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md)) -&gt; [Z](zip.md)): [NonEmptyList](index.md)&lt;[Z](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md), [Z](zip.md)&gt; [zip](zip.md)(b: [NonEmptyList](index.md)&lt;[B](zip.md)&gt;, c: [NonEmptyList](index.md)&lt;[C](zip.md)&gt;, d: [NonEmptyList](index.md)&lt;[D](zip.md)&gt;, e: [NonEmptyList](index.md)&lt;[E](zip.md)&gt;, f: [NonEmptyList](index.md)&lt;[F](zip.md)&gt;, g: [NonEmptyList](index.md)&lt;[G](zip.md)&gt;, h: [NonEmptyList](index.md)&lt;[H](zip.md)&gt;, i: [NonEmptyList](index.md)&lt;[I](zip.md)&gt;, j: [NonEmptyList](index.md)&lt;[J](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md)) -&gt; [Z](zip.md)): [NonEmptyList](index.md)&lt;[Z](zip.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [all](all.md) | [common]<br>val [all](all.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt; |
| [head](head.md) | [common]<br>val [head](head.md): [A](index.md) |
| [size](size.md) | [common]<br>open override val [size](size.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [tail](tail.md) | [common]<br>val [tail](tail.md): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt; |

## Extensions

| Name | Summary |
|---|---|
| [combine](../../arrow.typeclasses/-semigroup/-companion/-non-empty-list-semigroup/combine.md) | [common]<br>open override fun [NonEmptyList](index.md)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?&gt;.[combine](../../arrow.typeclasses/-semigroup/-companion/-non-empty-list-semigroup/combine.md)(b: [NonEmptyList](index.md)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?&gt;): [NonEmptyList](index.md)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?&gt;<br>Combine two [A](../../arrow.typeclasses/-semigroup/index.md) values. |
| [compareTo](../compare-to.md) | [common]<br>operator fun &lt;[A](../compare-to.md) : [Comparable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)&lt;[A](../compare-to.md)&gt;&gt; [NonEmptyList](index.md)&lt;[A](../compare-to.md)&gt;.[compareTo](../compare-to.md)(other: [NonEmptyList](index.md)&lt;[A](../compare-to.md)&gt;): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [flatten](../flatten.md) | [common]<br>fun &lt;[A](../flatten.md)&gt; [NonEmptyList](index.md)&lt;[NonEmptyList](index.md)&lt;[A](../flatten.md)&gt;&gt;.[flatten](../flatten.md)(): [NonEmptyList](index.md)&lt;[A](../flatten.md)&gt; |
| [maybeCombine](../../arrow.typeclasses/-semigroup/-companion/-non-empty-list-semigroup/index.md#1716120099%2FFunctions%2F-1961959459) | [common]<br>open fun [NonEmptyList](index.md)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?&gt;.[maybeCombine](../../arrow.typeclasses/-semigroup/-companion/-non-empty-list-semigroup/index.md#1716120099%2FFunctions%2F-1961959459)(b: [NonEmptyList](index.md)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?&gt;?): [NonEmptyList](index.md)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?&gt; |
| [plus](../../arrow.typeclasses/-semigroup/-companion/-non-empty-list-semigroup/index.md#-1055836867%2FFunctions%2F-1961959459) | [common]<br>open operator fun [NonEmptyList](index.md)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?&gt;.[plus](../../arrow.typeclasses/-semigroup/-companion/-non-empty-list-semigroup/index.md#-1055836867%2FFunctions%2F-1961959459)(b: [NonEmptyList](index.md)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?&gt;): [NonEmptyList](index.md)&lt;[Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)?&gt; |
| [sequenceEither](../sequence-either.md) | [common]<br>fun &lt;[E](../sequence-either.md), [A](../sequence-either.md)&gt; [NonEmptyList](index.md)&lt;[Either](../-either/index.md)&lt;[E](../sequence-either.md), [A](../sequence-either.md)&gt;&gt;.[sequenceEither](../sequence-either.md)(): [Either](../-either/index.md)&lt;[E](../sequence-either.md), [NonEmptyList](index.md)&lt;[A](../sequence-either.md)&gt;&gt; |
| [sequenceOption](../sequence-option.md) | [common]<br>fun &lt;[A](../sequence-option.md)&gt; [NonEmptyList](index.md)&lt;[Option](../-option/index.md)&lt;[A](../sequence-option.md)&gt;&gt;.[sequenceOption](../sequence-option.md)(): [Option](../-option/index.md)&lt;[NonEmptyList](index.md)&lt;[A](../sequence-option.md)&gt;&gt; |
| [sequenceValidated](../sequence-validated.md) | [common]<br>fun &lt;[E](../sequence-validated.md), [A](../sequence-validated.md)&gt; [NonEmptyList](index.md)&lt;[Validated](../-validated/index.md)&lt;[E](../sequence-validated.md), [A](../sequence-validated.md)&gt;&gt;.[sequenceValidated](../sequence-validated.md)(semigroup: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../sequence-validated.md)&gt;): [Validated](../-validated/index.md)&lt;[E](../sequence-validated.md), [NonEmptyList](index.md)&lt;[A](../sequence-validated.md)&gt;&gt; |
| [traverseEither](../traverse-either.md) | [common]<br>inline fun &lt;[E](../traverse-either.md), [A](../traverse-either.md), [B](../traverse-either.md)&gt; [NonEmptyList](index.md)&lt;[A](../traverse-either.md)&gt;.[traverseEither](../traverse-either.md)(f: ([A](../traverse-either.md)) -&gt; [Either](../-either/index.md)&lt;[E](../traverse-either.md), [B](../traverse-either.md)&gt;): [Either](../-either/index.md)&lt;[E](../traverse-either.md), [NonEmptyList](index.md)&lt;[B](../traverse-either.md)&gt;&gt; |
| [traverseOption](../traverse-option.md) | [common]<br>inline fun &lt;[A](../traverse-option.md), [B](../traverse-option.md)&gt; [NonEmptyList](index.md)&lt;[A](../traverse-option.md)&gt;.[traverseOption](../traverse-option.md)(f: ([A](../traverse-option.md)) -&gt; [Option](../-option/index.md)&lt;[B](../traverse-option.md)&gt;): [Option](../-option/index.md)&lt;[NonEmptyList](index.md)&lt;[B](../traverse-option.md)&gt;&gt; |
| [traverseValidated](../traverse-validated.md) | [common]<br>inline fun &lt;[E](../traverse-validated.md), [A](../traverse-validated.md), [B](../traverse-validated.md)&gt; [NonEmptyList](index.md)&lt;[A](../traverse-validated.md)&gt;.[traverseValidated](../traverse-validated.md)(semigroup: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[E](../traverse-validated.md)&gt;, f: ([A](../traverse-validated.md)) -&gt; [Validated](../-validated/index.md)&lt;[E](../traverse-validated.md), [B](../traverse-validated.md)&gt;): [Validated](../-validated/index.md)&lt;[E](../traverse-validated.md), [NonEmptyList](index.md)&lt;[B](../traverse-validated.md)&gt;&gt; |
| [unzip](../unzip.md) | [common]<br>fun &lt;[A](../unzip.md), [B](../unzip.md)&gt; [NonEmptyList](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](../unzip.md), [B](../unzip.md)&gt;&gt;.[unzip](../unzip.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[NonEmptyList](index.md)&lt;[A](../unzip.md)&gt;, [NonEmptyList](index.md)&lt;[B](../unzip.md)&gt;&gt;<br>fun &lt;[A](../unzip.md), [B](../unzip.md), [C](../unzip.md)&gt; [NonEmptyList](index.md)&lt;[C](../unzip.md)&gt;.[unzip](../unzip.md)(f: ([C](../unzip.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](../unzip.md), [B](../unzip.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[NonEmptyList](index.md)&lt;[A](../unzip.md)&gt;, [NonEmptyList](index.md)&lt;[B](../unzip.md)&gt;&gt; |
