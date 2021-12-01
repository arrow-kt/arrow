//[arrow-core](../../index.md)/[arrow.core](index.md)/[leftPadZip](left-pad-zip.md)

# leftPadZip

[common]\
inline fun &lt;[A](left-pad-zip.md), [B](left-pad-zip.md), [C](left-pad-zip.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](left-pad-zip.md)&gt;.[leftPadZip](left-pad-zip.md)(other: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](left-pad-zip.md)&gt;, fab: ([A](left-pad-zip.md)?, [B](left-pad-zip.md)) -&gt; [C](left-pad-zip.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[C](left-pad-zip.md)&gt;

Returns a List containing the result of applying some transformation (A?, B) -&gt; C on a zip, excluding all cases where the right value is null.

Example:

import arrow.core.*\
\
//sampleStart\
val left = listOf(1, 2).leftPadZip(listOf("a")) { l, r -&gt; l to r }      // Result: [Pair(1, "a")]\
val right = listOf(1).leftPadZip(listOf("a", "b")) { l, r -&gt; l to r }   // Result: [Pair(1, "a"), Pair(null, "b")]\
val both = listOf(1, 2).leftPadZip(listOf("a", "b")) { l, r -&gt; l to r } // Result: [Pair(1, "a"), Pair(2, "b")]\
//sampleEnd\
\
fun main() {\
  println("left = $left")\
  println("right = $right")\
  println("both = $both")\
}<!--- KNIT example-iterable-03.kt -->

[common]\
fun &lt;[A](left-pad-zip.md), [B](left-pad-zip.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](left-pad-zip.md)&gt;.[leftPadZip](left-pad-zip.md)(other: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](left-pad-zip.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](left-pad-zip.md)?, [B](left-pad-zip.md)&gt;&gt;

Returns a List> containing the zipped values of the two lists with null for padding on the left.

Example:

import arrow.core.*\
\
//sampleStart\
val padRight = listOf(1, 2).leftPadZip(listOf("a"))        // Result: [Pair(1, "a")]\
val padLeft = listOf(1).leftPadZip(listOf("a", "b"))       // Result: [Pair(1, "a"), Pair(null, "b")]\
val noPadding = listOf(1, 2).leftPadZip(listOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]\
//sampleEnd\
\
fun main() {\
  println("padRight = $padRight")\
  println("padLeft = $padLeft")\
  println("noPadding = $noPadding")\
}<!--- KNIT example-iterable-04.kt -->

[common]\
fun &lt;[A](left-pad-zip.md), [B](left-pad-zip.md), [C](left-pad-zip.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](left-pad-zip.md)&gt;.[leftPadZip](left-pad-zip.md)(other: [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](left-pad-zip.md)&gt;, fab: ([A](left-pad-zip.md)?, [B](left-pad-zip.md)) -&gt; [C](left-pad-zip.md)): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[C](left-pad-zip.md)&gt;

Returns a Sequence containing the result of applying some transformation (A?, B) -&gt; C on a zip, excluding all cases where the right value is null.

Example:

import arrow.core.leftPadZip\
\
//sampleStart\
val left = sequenceOf(1, 2).leftPadZip(sequenceOf(3)) { l, r -&gt; l?.plus(r) ?: r }    // Result: [4]\
val right = sequenceOf(1).leftPadZip(sequenceOf(3, 4)) { l, r -&gt; l?.plus(r) ?: r }   // Result: [4, 4]\
val both = sequenceOf(1, 2).leftPadZip(sequenceOf(3, 4)) { l, r -&gt; l?.plus(r) ?: r } // Result: [4, 6]\
//sampleEnd\
\
fun main() {\
  println("left = $left")\
  println("right = $right")\
  println("both = $both")\
}<!--- KNIT example-sequence-05.kt -->

[common]\
fun &lt;[A](left-pad-zip.md), [B](left-pad-zip.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](left-pad-zip.md)&gt;.[leftPadZip](left-pad-zip.md)(other: [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](left-pad-zip.md)&gt;): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](left-pad-zip.md)?, [B](left-pad-zip.md)&gt;&gt;

Returns a Sequence> containing the zipped values of the two sequences with null for padding on the left.

Example:

import arrow.core.leftPadZip\
\
//sampleStart\
val padRight = sequenceOf(1, 2).leftPadZip(sequenceOf("a"))        // Result: [Pair(1, "a")]\
val padLeft = sequenceOf(1).leftPadZip(sequenceOf("a", "b"))       // Result: [Pair(1, "a"), Pair(null, "b")]\
val noPadding = sequenceOf(1, 2).leftPadZip(sequenceOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]\
//sampleEnd\
\
fun main() {\
  println("padRight = $padRight")\
  println("padLeft = $padLeft")\
  println("noPadding = $noPadding")\
}<!--- KNIT example-sequence-06.kt -->
