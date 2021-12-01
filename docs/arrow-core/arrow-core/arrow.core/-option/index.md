//[arrow-core](../../../index.md)/[arrow.core](../index.md)/[Option](index.md)

# Option

[common]\
sealed class [Option](index.md)&lt;out [A](index.md)&gt;

If you have worked with Java at all in the past, it is very likely that you have come across a NullPointerException at some time (other languages will throw similarly named errors in such a case). Usually this happens because some method returns null when you weren't expecting it and, thus, isn't dealing with that possibility in your client code. A value of null is often abused to represent an absent optional value. Kotlin tries to solve the problem by getting rid of null values altogether, and providing its own special syntax [Null-safety machinery based on ?](https://kotlinlang.org/docs/reference/null-safety.html).

Arrow models the absence of values through the Option datatype similar to how Scala, Haskell, and other FP languages handle optional values.

Option&lt;A&gt; is a container for an optional value of type A. If the value of type A is present, the Option&lt;A&gt; is an instance of Some&lt;A&gt;, containing the present value of type A. If the value is absent, the Option&lt;A&gt; is the object None.

import arrow.core.Option\
import arrow.core.Some\
import arrow.core.none\
\
//sampleStart\
val someValue: Option&lt;String&gt; = Some("I am wrapped in something")\
val emptyValue: Option&lt;String&gt; = none()\
//sampleEnd\
fun main() {\
 println("value = $someValue")\
 println("emptyValue = $emptyValue")\
}<!--- KNIT example-option-01.kt -->

Let's write a function that may or may not give us a string, thus returning Option&lt;String&gt;:

import arrow.core.None\
import arrow.core.Option\
import arrow.core.Some\
\
//sampleStart\
fun maybeItWillReturnSomething(flag: Boolean): Option&lt;String&gt; =\
 if (flag) Some("Found value") else None\
//sampleEnd<!--- KNIT example-option-02.kt -->

Using getOrElse, we can provide a default value "No value" when the optional argument None does not exist:

import arrow.core.None\
import arrow.core.Option\
import arrow.core.Some\
import arrow.core.getOrElse\
\
fun maybeItWillReturnSomething(flag: Boolean): Option&lt;String&gt; =\
 if (flag) Some("Found value") else None\
\
val value1 =\
//sampleStart\
 maybeItWillReturnSomething(true)\
    .getOrElse { "No value" }\
//sampleEnd\
fun main() {\
 println(value1)\
}<!--- KNIT example-option-03.kt -->import arrow.core.None\
import arrow.core.Option\
import arrow.core.Some\
import arrow.core.getOrElse\
\
fun maybeItWillReturnSomething(flag: Boolean): Option&lt;String&gt; =\
 if (flag) Some("Found value") else None\
\
val value2 =\
//sampleStart\
 maybeItWillReturnSomething(false)\
  .getOrElse { "No value" }\
//sampleEnd\
fun main() {\
 println(value2)\
}<!--- KNIT example-option-04.kt -->

Checking whether option has value:

import arrow.core.None\
import arrow.core.Option\
import arrow.core.Some\
\
fun maybeItWillReturnSomething(flag: Boolean): Option&lt;String&gt; =\
 if (flag) Some("Found value") else None\
\
 //sampleStart\
val valueSome = maybeItWillReturnSomething(true) is None\
val valueNone = maybeItWillReturnSomething(false) is None\
//sampleEnd\
fun main() {\
 println("valueSome = $valueSome")\
 println("valueNone = $valueNone")\
}<!--- KNIT example-option-05.kt -->

Creating a Option&lt;T&gt; of a T?. Useful for working with values that can be nullable:

import arrow.core.Option\
\
//sampleStart\
val myString: String? = "Nullable string"\
val option: Option&lt;String&gt; = Option.fromNullable(myString)\
//sampleEnd\
fun main () {\
 println("option = $option")\
}<!--- KNIT example-option-06.kt -->

Option can also be used with when statements:

import arrow.core.None\
import arrow.core.Option\
import arrow.core.Some\
\
//sampleStart\
val someValue: Option&lt;Double&gt; = Some(20.0)\
val value = when(someValue) {\
 is Some -&gt; someValue.value\
 is None -&gt; 0.0\
}\
//sampleEnd\
fun main () {\
 println("value = $value")\
}<!--- KNIT example-option-07.kt -->import arrow.core.None\
import arrow.core.Option\
import arrow.core.Some\
\
//sampleStart\
val noValue: Option&lt;Double&gt; = None\
val value = when(noValue) {\
 is Some -&gt; noValue.value\
 is None -&gt; 0.0\
}\
//sampleEnd\
fun main () {\
 println("value = $value")\
}<!--- KNIT example-option-08.kt -->

An alternative for pattern matching is folding. This is possible because an option could be looked at as a collection or foldable structure with either one or zero elements.

One of these operations is map. This operation allows us to map the inner value to a different type while preserving the option:

import arrow.core.None\
import arrow.core.Option\
import arrow.core.Some\
\
//sampleStart\
val number: Option&lt;Int&gt; = Some(3)\
val noNumber: Option&lt;Int&gt; = None\
val mappedResult1 = number.map { it * 1.5 }\
val mappedResult2 = noNumber.map { it * 1.5 }\
//sampleEnd\
fun main () {\
 println("number = $number")\
 println("noNumber = $noNumber")\
 println("mappedResult1 = $mappedResult1")\
 println("mappedResult2 = $mappedResult2")\
}<!--- KNIT example-option-09.kt -->

Another operation is fold. This operation will extract the value from the option, or provide a default if the value is None

import arrow.core.Option\
import arrow.core.Some\
\
val fold =\
//sampleStart\
 Some(3).fold({ 1 }, { it * 3 })\
//sampleEnd\
fun main () {\
 println(fold)\
}<!--- KNIT example-option-10.kt -->import arrow.core.Option\
import arrow.core.none\
\
val fold =\
//sampleStart\
 none&lt;Int&gt;().fold({ 1 }, { it * 3 })\
//sampleEnd\
fun main () {\
 println(fold)\
}<!--- KNIT example-option-11.kt -->

Arrow also adds syntax to all datatypes so you can easily lift them into the context of Option where needed.

import arrow.core.some\
\
//sampleStart\
 val some = 1.some()\
 val none = none&lt;String&gt;()\
//sampleEnd\
fun main () {\
 println("some = $some")\
 println("none = $none")\
}<!--- KNIT example-option-12.kt -->import arrow.core.toOption\
\
//sampleStart\
val nullString: String? = null\
val valueFromNull = nullString.toOption()\
\
val helloString: String? = "Hello"\
val valueFromStr = helloString.toOption()\
//sampleEnd\
fun main () {\
 println("valueFromNull = $valueFromNull")\
 println("valueFromStr = $valueFromStr")\
}<!--- KNIT example-option-13.kt -->

You can easily convert between A? and Option&lt;A&gt; by using the toOption() extension or Option.fromNullable constructor.

import arrow.core.firstOrNone\
import arrow.core.toOption\
\
//sampleStart\
val foxMap = mapOf(1 to "The", 2 to "Quick", 3 to "Brown", 4 to "Fox")\
\
val empty = foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() }.toOption()\
val filled = Option.fromNullable(foxMap.entries.firstOrNull { it.key == 5 }?.value.let { it?.toCharArray() })\
\
//sampleEnd\
fun main() {\
 println("empty = $empty")\
 println("filled = $filled")\
}<!--- KNIT example-option-14.kt -->

###  Transforming the inner contents

import arrow.core.Some\
\
fun main() {\
val value =\
 //sampleStart\
   Some(1).map { it + 1 }\
 //sampleEnd\
 println(value)\
}<!--- KNIT example-option-15.kt -->

###  Computing over independent values

import arrow.core.Some\
\
 val value =\
//sampleStart\
 Some(1).zip(Some("Hello"), Some(20.0), ::Triple)\
//sampleEnd\
fun main() {\
 println(value)\
}<!--- KNIT example-option-16.kt -->

###  Computing over dependent values ignoring absence

import arrow.core.computations.option\
import arrow.core.Some\
import arrow.core.Option\
\
suspend fun value(): Option&lt;Int&gt; =\
//sampleStart\
 option {\
   val a = Some(1).bind()\
   val b = Some(1 + a).bind()\
   val c = Some(1 + b).bind()\
   a + b + c\
}\
//sampleEnd\
suspend fun main() {\
 println(value())\
}<!--- KNIT example-option-17.kt -->import arrow.core.computations.option\
import arrow.core.Some\
import arrow.core.none\
import arrow.core.Option\
\
suspend fun value(): Option&lt;Int&gt; =\
//sampleStart\
 option {\
   val x = none&lt;Int&gt;().bind()\
   val y = Some(1 + x).bind()\
   val z = Some(1 + y).bind()\
   x + y + z\
 }\
//sampleEnd\
suspend fun main() {\
 println(value())\
}<!--- KNIT example-option-18.kt -->

##  Credits

Contents partially adapted from [Scala Exercises Option Tutorial](https://www.scala-exercises.org/std_lib/options) Originally based on the Scala Koans.

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [common]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [align](align.md) | [common]<br>infix fun &lt;[B](align.md)&gt; [align](align.md)(b: [Option](index.md)&lt;[B](align.md)&gt;): [Option](index.md)&lt;[Ior](../-ior/index.md)&lt;[A](index.md), [B](align.md)&gt;&gt;<br>Align two options (this on the left and [b](align.md) on the right) as one Option of [Ior](../-ior/index.md).<br>[common]<br>inline fun &lt;[B](align.md), [C](align.md)&gt; [align](align.md)(b: [Option](index.md)&lt;[B](align.md)&gt;, f: ([Ior](../-ior/index.md)&lt;[A](index.md), [B](align.md)&gt;) -&gt; [C](align.md)): [Option](index.md)&lt;[C](align.md)&gt;<br>Align two options (this on the left and [b](align.md) on the right) as one Option of [Ior](../-ior/index.md), and then, if it's not [None](../-none/index.md), map it using [f](align.md). |
| [all](all.md) | [common]<br>inline fun [all](all.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true if this option is empty '''or''' the predicate $predicate returns true when applied to this $option's value. |
| [and](and.md) | [common]<br>infix fun &lt;[X](and.md)&gt; [and](and.md)(value: [Option](index.md)&lt;[X](and.md)&gt;): [Option](index.md)&lt;[X](and.md)&gt; |
| [crosswalk](crosswalk.md) | [common]<br>inline fun &lt;[B](crosswalk.md)&gt; [crosswalk](crosswalk.md)(f: ([A](index.md)) -&gt; [Option](index.md)&lt;[B](crosswalk.md)&gt;): [Option](index.md)&lt;[Option](index.md)&lt;[B](crosswalk.md)&gt;&gt; |
| [crosswalkMap](crosswalk-map.md) | [common]<br>inline fun &lt;[K](crosswalk-map.md), [V](crosswalk-map.md)&gt; [crosswalkMap](crosswalk-map.md)(f: ([A](index.md)) -&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](crosswalk-map.md), [V](crosswalk-map.md)&gt;): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](crosswalk-map.md), [Option](index.md)&lt;[V](crosswalk-map.md)&gt;&gt; |
| [crosswalkNull](crosswalk-null.md) | [common]<br>inline fun &lt;[B](crosswalk-null.md)&gt; [crosswalkNull](crosswalk-null.md)(f: ([A](index.md)) -&gt; [B](crosswalk-null.md)?): [Option](index.md)&lt;[B](crosswalk-null.md)&gt;? |
| [exists](exists.md) | [common]<br>inline fun [exists](exists.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true if this option is nonempty '''and''' the predicate $p returns true when applied to this $option's value. Otherwise, returns false. |
| [filter](filter.md) | [common]<br>inline fun [filter](filter.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Option](index.md)&lt;[A](index.md)&gt;<br>Returns this $option if it is nonempty '''and''' applying the predicate $p to this $option's value returns true. Otherwise, return $none. |
| [filterNot](filter-not.md) | [common]<br>inline fun [filterNot](filter-not.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Option](index.md)&lt;[A](index.md)&gt;<br>Returns this $option if it is nonempty '''and''' applying the predicate $p to this $option's value returns false. Otherwise, return $none. |
| [findOrNull](find-or-null.md) | [common]<br>inline fun [findOrNull](find-or-null.md)(predicate: ([A](index.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [A](index.md)?<br>Returns the $option's value if this option is nonempty '''and''' the predicate $p returns true when applied to this $option's value. Otherwise, returns null. |
| [flatMap](flat-map.md) | [common]<br>inline fun &lt;[B](flat-map.md)&gt; [flatMap](flat-map.md)(f: ([A](index.md)) -&gt; [Option](index.md)&lt;[B](flat-map.md)&gt;): [Option](index.md)&lt;[B](flat-map.md)&gt;<br>Returns the result of applying $f to this $option's value if this $option is nonempty. Returns $none if this $option is empty. Slightly different from map in that $f is expected to return an $option (which could be $none). |
| [fold](fold.md) | [common]<br>inline fun &lt;[R](fold.md)&gt; [fold](fold.md)(ifEmpty: () -&gt; [R](fold.md), ifSome: ([A](index.md)) -&gt; [R](fold.md)): [R](fold.md) |
| [foldLeft](fold-left.md) | [common]<br>inline fun &lt;[B](fold-left.md)&gt; [foldLeft](fold-left.md)(initial: [B](fold-left.md), operation: ([B](fold-left.md), [A](index.md)) -&gt; [B](fold-left.md)): [B](fold-left.md) |
| [foldMap](fold-map.md) | [common]<br>inline fun &lt;[B](fold-map.md)&gt; [foldMap](fold-map.md)(MB: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[B](fold-map.md)&gt;, f: ([A](index.md)) -&gt; [B](fold-map.md)): [B](fold-map.md) |
| [isDefined](is-defined.md) | [common]<br>fun [isDefined](is-defined.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true if the option is an instance of [Some](../-some/index.md), false otherwise. |
| [isEmpty](is-empty.md) | [common]<br>abstract fun [isEmpty](is-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns true if the option is [None](../-none/index.md), false otherwise. |
| [isNotEmpty](is-not-empty.md) | [common]<br>fun [isNotEmpty](is-not-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [map](map.md) | [common]<br>inline fun &lt;[B](map.md)&gt; [map](map.md)(f: ([A](index.md)) -&gt; [B](map.md)): [Option](index.md)&lt;[B](map.md)&gt;<br>Returns a Some<$B> containing the result of applying $f to this $option's value if this $option is nonempty. Otherwise return $none. |
| [mapNotNull](map-not-null.md) | [common]<br>inline fun &lt;[B](map-not-null.md)&gt; [mapNotNull](map-not-null.md)(f: ([A](index.md)) -&gt; [B](map-not-null.md)?): [Option](index.md)&lt;[B](map-not-null.md)&gt;<br>Returns $none if the result of applying $f to this $option's value is null. Otherwise returns the result. |
| [nonEmpty](non-empty.md) | [common]<br>fun [nonEmpty](non-empty.md)(): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>alias for [isDefined](is-defined.md) |
| [orNull](or-null.md) | [common]<br>fun [orNull](or-null.md)(): [A](index.md)? |
| [padZip](pad-zip.md) | [common]<br>fun &lt;[B](pad-zip.md)&gt; [padZip](pad-zip.md)(other: [Option](index.md)&lt;[B](pad-zip.md)&gt;): [Option](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md)?, [B](pad-zip.md)?&gt;&gt;<br>inline fun &lt;[B](pad-zip.md), [C](pad-zip.md)&gt; [padZip](pad-zip.md)(other: [Option](index.md)&lt;[B](pad-zip.md)&gt;, f: ([A](index.md)?, [B](pad-zip.md)?) -&gt; [C](pad-zip.md)): [Option](index.md)&lt;[C](pad-zip.md)&gt; |
| [pairLeft](pair-left.md) | [common]<br>fun &lt;[L](pair-left.md)&gt; [pairLeft](pair-left.md)(left: [L](pair-left.md)): [Option](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[L](pair-left.md), [A](index.md)&gt;&gt; |
| [pairRight](pair-right.md) | [common]<br>fun &lt;[R](pair-right.md)&gt; [pairRight](pair-right.md)(right: [R](pair-right.md)): [Option](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [R](pair-right.md)&gt;&gt; |
| [reduceOrNull](reduce-or-null.md) | [common]<br>inline fun &lt;[B](reduce-or-null.md)&gt; [reduceOrNull](reduce-or-null.md)(initial: ([A](index.md)) -&gt; [B](reduce-or-null.md), operation: ([B](reduce-or-null.md), [A](index.md)) -&gt; [B](reduce-or-null.md)): [B](reduce-or-null.md)? |
| [reduceRightEvalOrNull](reduce-right-eval-or-null.md) | [common]<br>inline fun &lt;[B](reduce-right-eval-or-null.md)&gt; [reduceRightEvalOrNull](reduce-right-eval-or-null.md)(initial: ([A](index.md)) -&gt; [B](reduce-right-eval-or-null.md), operation: ([A](index.md), acc: [Eval](../-eval/index.md)&lt;[B](reduce-right-eval-or-null.md)&gt;) -&gt; [Eval](../-eval/index.md)&lt;[B](reduce-right-eval-or-null.md)&gt;): [Eval](../-eval/index.md)&lt;[B](reduce-right-eval-or-null.md)?&gt; |
| [replicate](replicate.md) | [common]<br>fun [replicate](replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)): [Option](index.md)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt;&gt; |
| [tap](tap.md) | [common]<br>inline fun [tap](tap.md)(f: ([A](index.md)) -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Option](index.md)&lt;[A](index.md)&gt;<br>The given function is applied as a fire and forget effect if this is a some. When applied the result is ignored and the original Some value is returned |
| [tapNone](tap-none.md) | [common]<br>inline fun [tapNone](tap-none.md)(f: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)): [Option](index.md)&lt;[A](index.md)&gt;<br>The given function is applied as a fire and forget effect if this is a None. When applied the result is ignored and the original None value is returned |
| [toEither](to-either.md) | [common]<br>inline fun &lt;[L](to-either.md)&gt; [toEither](to-either.md)(ifEmpty: () -&gt; [L](to-either.md)): [Either](../-either/index.md)&lt;[L](to-either.md), [A](index.md)&gt; |
| [toList](to-list.md) | [common]<br>fun [toList](to-list.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](index.md)&gt; |
| [toString](to-string.md) | [common]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [traverse](traverse.md) | [common]<br>inline fun &lt;[B](traverse.md)&gt; [traverse](traverse.md)(fa: ([A](index.md)) -&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](traverse.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Option](index.md)&lt;[B](traverse.md)&gt;&gt; |
| [traverseEither](traverse-either.md) | [common]<br>inline fun &lt;[AA](traverse-either.md), [B](traverse-either.md)&gt; [traverseEither](traverse-either.md)(fa: ([A](index.md)) -&gt; [Either](../-either/index.md)&lt;[AA](traverse-either.md), [B](traverse-either.md)&gt;): [Either](../-either/index.md)&lt;[AA](traverse-either.md), [Option](index.md)&lt;[B](traverse-either.md)&gt;&gt; |
| [traverseValidated](traverse-validated.md) | [common]<br>inline fun &lt;[AA](traverse-validated.md), [B](traverse-validated.md)&gt; [traverseValidated](traverse-validated.md)(fa: ([A](index.md)) -&gt; [Validated](../-validated/index.md)&lt;[AA](traverse-validated.md), [B](traverse-validated.md)&gt;): [Validated](../-validated/index.md)&lt;[AA](traverse-validated.md), [Option](index.md)&lt;[B](traverse-validated.md)&gt;&gt; |
| [void](void.md) | [common]<br>fun [void](void.md)(): [Option](index.md)&lt;[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)&gt; |
| [zip](zip.md) | [common]<br>fun &lt;[B](zip.md)&gt; [zip](zip.md)(other: [Option](index.md)&lt;[B](zip.md)&gt;): [Option](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](index.md), [B](zip.md)&gt;&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, map: ([A](index.md), [B](zip.md)) -&gt; [C](zip.md)): [Option](index.md)&lt;[C](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md)) -&gt; [D](zip.md)): [Option](index.md)&lt;[D](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md)) -&gt; [E](zip.md)): [Option](index.md)&lt;[E](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md)) -&gt; [F](zip.md)): [Option](index.md)&lt;[F](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, f: [Option](index.md)&lt;[F](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md)) -&gt; [G](zip.md)): [Option](index.md)&lt;[G](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, f: [Option](index.md)&lt;[F](zip.md)&gt;, g: [Option](index.md)&lt;[G](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md)) -&gt; [H](zip.md)): [Option](index.md)&lt;[H](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, f: [Option](index.md)&lt;[F](zip.md)&gt;, g: [Option](index.md)&lt;[G](zip.md)&gt;, h: [Option](index.md)&lt;[H](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md)) -&gt; [I](zip.md)): [Option](index.md)&lt;[I](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, f: [Option](index.md)&lt;[F](zip.md)&gt;, g: [Option](index.md)&lt;[G](zip.md)&gt;, h: [Option](index.md)&lt;[H](zip.md)&gt;, i: [Option](index.md)&lt;[I](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md)) -&gt; [J](zip.md)): [Option](index.md)&lt;[J](zip.md)&gt;<br>inline fun &lt;[B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md), [K](zip.md)&gt; [zip](zip.md)(b: [Option](index.md)&lt;[B](zip.md)&gt;, c: [Option](index.md)&lt;[C](zip.md)&gt;, d: [Option](index.md)&lt;[D](zip.md)&gt;, e: [Option](index.md)&lt;[E](zip.md)&gt;, f: [Option](index.md)&lt;[F](zip.md)&gt;, g: [Option](index.md)&lt;[G](zip.md)&gt;, h: [Option](index.md)&lt;[H](zip.md)&gt;, i: [Option](index.md)&lt;[I](zip.md)&gt;, j: [Option](index.md)&lt;[J](zip.md)&gt;, map: ([A](index.md), [B](zip.md), [C](zip.md), [D](zip.md), [E](zip.md), [F](zip.md), [G](zip.md), [H](zip.md), [I](zip.md), [J](zip.md)) -&gt; [K](zip.md)): [Option](index.md)&lt;[K](zip.md)&gt; |

## Inheritors

| Name |
|---|
| [None](../-none/index.md) |
| [Some](../-some/index.md) |

## Extensions

| Name | Summary |
|---|---|
| [combine](../combine.md) | [common]<br>fun &lt;[A](../combine.md)&gt; [Option](index.md)&lt;[A](../combine.md)&gt;.[combine](../combine.md)(SGA: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[A](../combine.md)&gt;, b: [Option](index.md)&lt;[A](../combine.md)&gt;): [Option](index.md)&lt;[A](../combine.md)&gt; |
| [combineAll](../combine-all.md) | [common]<br>fun &lt;[A](../combine-all.md)&gt; [Option](index.md)&lt;[A](../combine-all.md)&gt;.[combineAll](../combine-all.md)(MA: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[A](../combine-all.md)&gt;): [A](../combine-all.md) |
| [compareTo](../compare-to.md) | [common]<br>operator fun &lt;[A](../compare-to.md) : [Comparable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)&lt;[A](../compare-to.md)&gt;&gt; [Option](index.md)&lt;[A](../compare-to.md)&gt;.[compareTo](../compare-to.md)(other: [Option](index.md)&lt;[A](../compare-to.md)&gt;): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [ensure](../ensure.md) | [common]<br>inline fun &lt;[A](../ensure.md)&gt; [Option](index.md)&lt;[A](../ensure.md)&gt;.[ensure](../ensure.md)(error: () -&gt; [Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html), predicate: ([A](../ensure.md)) -&gt; [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)): [Option](index.md)&lt;[A](../ensure.md)&gt; |
| [filterIsInstance](../filter-is-instance.md) | [common]<br>inline fun &lt;[B](../filter-is-instance.md)&gt; [Option](index.md)&lt;*&gt;.[filterIsInstance](../filter-is-instance.md)(): [Option](index.md)&lt;[B](../filter-is-instance.md)&gt;<br>Returns an Option containing all elements that are instances of specified type parameter [B](../filter-is-instance.md). |
| [flatten](../flatten.md) | [common]<br>fun &lt;[A](../flatten.md)&gt; [Option](index.md)&lt;[Option](index.md)&lt;[A](../flatten.md)&gt;&gt;.[flatten](../flatten.md)(): [Option](index.md)&lt;[A](../flatten.md)&gt; |
| [getOrElse](../get-or-else.md) | [common]<br>inline fun &lt;[T](../get-or-else.md)&gt; [Option](index.md)&lt;[T](../get-or-else.md)&gt;.[getOrElse](../get-or-else.md)(default: () -&gt; [T](../get-or-else.md)): [T](../get-or-else.md)<br>Returns the option's value if the option is nonempty, otherwise return the result of evaluating default. |
| [handleError](../handle-error.md) | [common]<br>inline fun &lt;[A](../handle-error.md)&gt; [Option](index.md)&lt;[A](../handle-error.md)&gt;.[handleError](../handle-error.md)(f: ([Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) -&gt; [A](../handle-error.md)): [Option](index.md)&lt;[A](../handle-error.md)&gt; |
| [handleErrorWith](../handle-error-with.md) | [common]<br>inline fun &lt;[A](../handle-error-with.md)&gt; [Option](index.md)&lt;[A](../handle-error-with.md)&gt;.[handleErrorWith](../handle-error-with.md)(f: ([Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) -&gt; [Option](index.md)&lt;[A](../handle-error-with.md)&gt;): [Option](index.md)&lt;[A](../handle-error-with.md)&gt; |
| [or](../or.md) | [common]<br>infix fun &lt;[T](../or.md)&gt; [Option](index.md)&lt;[T](../or.md)&gt;.[or](../or.md)(value: [Option](index.md)&lt;[T](../or.md)&gt;): [Option](index.md)&lt;[T](../or.md)&gt; |
| [orElse](../or-else.md) | [common]<br>inline fun &lt;[A](../or-else.md)&gt; [Option](index.md)&lt;[A](../or-else.md)&gt;.[orElse](../or-else.md)(alternative: () -&gt; [Option](index.md)&lt;[A](../or-else.md)&gt;): [Option](index.md)&lt;[A](../or-else.md)&gt;<br>Returns this option's if the option is nonempty, otherwise returns another option provided lazily by default. |
| [redeem](../redeem.md) | [common]<br>inline fun &lt;[A](../redeem.md), [B](../redeem.md)&gt; [Option](index.md)&lt;[A](../redeem.md)&gt;.[redeem](../redeem.md)(fe: ([Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) -&gt; [B](../redeem.md), fb: ([A](../redeem.md)) -&gt; [B](../redeem.md)): [Option](index.md)&lt;[B](../redeem.md)&gt; |
| [redeemWith](../redeem-with.md) | [common]<br>inline fun &lt;[A](../redeem-with.md), [B](../redeem-with.md)&gt; [Option](index.md)&lt;[A](../redeem-with.md)&gt;.[redeemWith](../redeem-with.md)(fe: ([Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)) -&gt; [Option](index.md)&lt;[B](../redeem-with.md)&gt;, fb: ([A](../redeem-with.md)) -&gt; [Option](index.md)&lt;[B](../redeem-with.md)&gt;): [Option](index.md)&lt;[B](../redeem-with.md)&gt; |
| [replicate](../replicate.md) | [common]<br>fun &lt;[A](../replicate.md)&gt; [Option](index.md)&lt;[A](../replicate.md)&gt;.[replicate](../replicate.md)(n: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), MA: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[A](../replicate.md)&gt;): [Option](index.md)&lt;[A](../replicate.md)&gt; |
| [rethrow](../rethrow.md) | [common]<br>fun &lt;[A](../rethrow.md)&gt; [Option](index.md)&lt;[Either](../-either/index.md)&lt;[Unit](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html), [A](../rethrow.md)&gt;&gt;.[rethrow](../rethrow.md)(): [Option](index.md)&lt;[A](../rethrow.md)&gt; |
| [salign](../salign.md) | [common]<br>fun &lt;[A](../salign.md)&gt; [Option](index.md)&lt;[A](../salign.md)&gt;.[salign](../salign.md)(SA: [Semigroup](../../arrow.typeclasses/-semigroup/index.md)&lt;[A](../salign.md)&gt;, b: [Option](index.md)&lt;[A](../salign.md)&gt;): [Option](index.md)&lt;[A](../salign.md)&gt; |
| [separateEither](../separate-either.md) | [common]<br>fun &lt;[A](../separate-either.md), [B](../separate-either.md)&gt; [Option](index.md)&lt;[Either](../-either/index.md)&lt;[A](../separate-either.md), [B](../separate-either.md)&gt;&gt;.[separateEither](../separate-either.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](index.md)&lt;[A](../separate-either.md)&gt;, [Option](index.md)&lt;[B](../separate-either.md)&gt;&gt;<br>Separate the inner [Either](../-either/index.md) value into the [Either.Left](../-either/-left/index.md) and [Either.Right](../-either/-right/index.md). |
| [separateValidated](../separate-validated.md) | [common]<br>fun &lt;[A](../separate-validated.md), [B](../separate-validated.md)&gt; [Option](index.md)&lt;[Validated](../-validated/index.md)&lt;[A](../separate-validated.md), [B](../separate-validated.md)&gt;&gt;.[separateValidated](../separate-validated.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](index.md)&lt;[A](../separate-validated.md)&gt;, [Option](index.md)&lt;[B](../separate-validated.md)&gt;&gt;<br>Separate the inner [Validated](../-validated/index.md) value into the [Validated.Invalid](../-validated/-invalid/index.md) and [Validated.Valid](../-validated/-valid/index.md). |
| [sequence](../sequence.md) | [common]<br>fun &lt;[A](../sequence.md)&gt; [Option](index.md)&lt;[Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](../sequence.md)&gt;&gt;.[sequence](../sequence.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Option](index.md)&lt;[A](../sequence.md)&gt;&gt; |
| [sequenceEither](../sequence-either.md) | [common]<br>fun &lt;[A](../sequence-either.md), [B](../sequence-either.md)&gt; [Option](index.md)&lt;[Either](../-either/index.md)&lt;[A](../sequence-either.md), [B](../sequence-either.md)&gt;&gt;.[sequenceEither](../sequence-either.md)(): [Either](../-either/index.md)&lt;[A](../sequence-either.md), [Option](index.md)&lt;[B](../sequence-either.md)&gt;&gt; |
| [sequenceValidated](../sequence-validated.md) | [common]<br>fun &lt;[A](../sequence-validated.md), [B](../sequence-validated.md)&gt; [Option](index.md)&lt;[Validated](../-validated/index.md)&lt;[A](../sequence-validated.md), [B](../sequence-validated.md)&gt;&gt;.[sequenceValidated](../sequence-validated.md)(): [Validated](../-validated/index.md)&lt;[A](../sequence-validated.md), [Option](index.md)&lt;[B](../sequence-validated.md)&gt;&gt; |
| [toMap](../to-map.md) | [common]<br>fun &lt;[K](../to-map.md), [V](../to-map.md)&gt; [Option](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[K](../to-map.md), [V](../to-map.md)&gt;&gt;.[toMap](../to-map.md)(): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](../to-map.md), [V](../to-map.md)&gt; |
| [unalign](../unalign.md) | [common]<br>fun &lt;[A](../unalign.md), [B](../unalign.md)&gt; [Option](index.md)&lt;[Ior](../-ior/index.md)&lt;[A](../unalign.md), [B](../unalign.md)&gt;&gt;.[unalign](../unalign.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](index.md)&lt;[A](../unalign.md)&gt;, [Option](index.md)&lt;[B](../unalign.md)&gt;&gt;<br>inline fun &lt;[A](../unalign.md), [B](../unalign.md), [C](../unalign.md)&gt; [Option](index.md)&lt;[C](../unalign.md)&gt;.[unalign](../unalign.md)(f: ([C](../unalign.md)) -&gt; [Ior](../-ior/index.md)&lt;[A](../unalign.md), [B](../unalign.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](index.md)&lt;[A](../unalign.md)&gt;, [Option](index.md)&lt;[B](../unalign.md)&gt;&gt; |
| [unite](../unite.md) | [common]<br>fun &lt;[A](../unite.md)&gt; [Option](index.md)&lt;[Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](../unite.md)&gt;&gt;.[unite](../unite.md)(MA: [Monoid](../../arrow.typeclasses/-monoid/index.md)&lt;[A](../unite.md)&gt;): [Option](index.md)&lt;[A](../unite.md)&gt; |
| [uniteEither](../unite-either.md) | [common]<br>fun &lt;[A](../unite-either.md), [B](../unite-either.md)&gt; [Option](index.md)&lt;[Either](../-either/index.md)&lt;[A](../unite-either.md), [B](../unite-either.md)&gt;&gt;.[uniteEither](../unite-either.md)(): [Option](index.md)&lt;[B](../unite-either.md)&gt; |
| [uniteValidated](../unite-validated.md) | [common]<br>fun &lt;[A](../unite-validated.md), [B](../unite-validated.md)&gt; [Option](index.md)&lt;[Validated](../-validated/index.md)&lt;[A](../unite-validated.md), [B](../unite-validated.md)&gt;&gt;.[uniteValidated](../unite-validated.md)(): [Option](index.md)&lt;[B](../unite-validated.md)&gt; |
| [unzip](../unzip.md) | [common]<br>fun &lt;[A](../unzip.md), [B](../unzip.md)&gt; [Option](index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](../unzip.md), [B](../unzip.md)&gt;&gt;.[unzip](../unzip.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](index.md)&lt;[A](../unzip.md)&gt;, [Option](index.md)&lt;[B](../unzip.md)&gt;&gt;<br>inline fun &lt;[A](../unzip.md), [B](../unzip.md), [C](../unzip.md)&gt; [Option](index.md)&lt;[C](../unzip.md)&gt;.[unzip](../unzip.md)(f: ([C](../unzip.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](../unzip.md), [B](../unzip.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](index.md)&lt;[A](../unzip.md)&gt;, [Option](index.md)&lt;[B](../unzip.md)&gt;&gt; |
| [widen](../widen.md) | [common]<br>fun &lt;[B](../widen.md), [A](../widen.md) : [B](../widen.md)&gt; [Option](index.md)&lt;[A](../widen.md)&gt;.[widen](../widen.md)(): [Option](index.md)&lt;[B](../widen.md)&gt;<br>Given [A](../widen.md) is a sub type of [B](../widen.md), re-type this value from Option<A> to Option<B> |
