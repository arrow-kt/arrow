//[arrow-core](../../index.md)/[arrow.core](index.md)/[rightPadZip](right-pad-zip.md)

# rightPadZip

[common]\
inline fun &lt;[A](right-pad-zip.md), [B](right-pad-zip.md), [C](right-pad-zip.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](right-pad-zip.md)&gt;.[rightPadZip](right-pad-zip.md)(other: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](right-pad-zip.md)&gt;, fa: ([A](right-pad-zip.md), [B](right-pad-zip.md)?) -&gt; [C](right-pad-zip.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[C](right-pad-zip.md)&gt;

Returns a List containing the result of applying some transformation (A, B?) -&gt; C on a zip, excluding all cases where the left value is null.

Example:

import arrow.core.*\
\
//sampleStart\
val left = listOf(1, 2).rightPadZip(listOf("a")) { l, r -&gt; l to r }      // Result: [Pair(1, "a"), Pair(null, "b")]\
val right = listOf(1).rightPadZip(listOf("a", "b")) { l, r -&gt; l to r }   // Result: [Pair(1, "a")]\
val both = listOf(1, 2).rightPadZip(listOf("a", "b")) { l, r -&gt; l to r } // Result: [Pair(1, "a"), Pair(2, "b")]\
//sampleEnd\
\
fun main() {\
  println("left = $left")\
  println("right = $right")\
  println("both = $both")\
}<!--- KNIT example-iterable-05.kt -->

[common]\
fun &lt;[A](right-pad-zip.md), [B](right-pad-zip.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](right-pad-zip.md)&gt;.[rightPadZip](right-pad-zip.md)(other: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](right-pad-zip.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](right-pad-zip.md), [B](right-pad-zip.md)?&gt;&gt;

Returns a List> containing the zipped values of the two lists with null for padding on the right.

Example:

import arrow.core.*\
\
//sampleStart\
val padRight = listOf(1, 2).rightPadZip(listOf("a"))        // Result: [Pair(1, "a"), Pair(2, null)]\
val padLeft = listOf(1).rightPadZip(listOf("a", "b"))       // Result: [Pair(1, "a")]\
val noPadding = listOf(1, 2).rightPadZip(listOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]\
//sampleEnd\
\
fun main() {\
  println("padRight = $padRight")\
  println("padLeft = $padLeft")\
  println("noPadding = $noPadding")\
}<!--- KNIT example-iterable-06.kt -->

[common]\
fun &lt;[A](right-pad-zip.md), [B](right-pad-zip.md), [C](right-pad-zip.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](right-pad-zip.md)&gt;.[rightPadZip](right-pad-zip.md)(other: [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](right-pad-zip.md)&gt;, fa: ([A](right-pad-zip.md), [B](right-pad-zip.md)?) -&gt; [C](right-pad-zip.md)): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[C](right-pad-zip.md)&gt;

Returns a Sequence containing the result of applying some transformation (A, B?) -&gt; C on a zip, excluding all cases where the left value is null.

Example:

import arrow.core.rightPadZip\
\
//sampleStart\
val left = sequenceOf(1, 2).rightPadZip(sequenceOf(3)) { l, r -&gt; l + (r?:0) }    // Result: [4, 2]\
val right = sequenceOf(1).rightPadZip(sequenceOf(3, 4)) { l, r -&gt; l + (r?:0) }   // Result: [4]\
val both = sequenceOf(1, 2).rightPadZip(sequenceOf(3, 4)) { l, r -&gt; l + (r?:0) } // Result: [4, 6]\
//sampleEnd\
\
fun main() {\
  println("left = $left")\
  println("right = $right")\
  println("both = $both")\
}<!--- KNIT example-sequence-09.kt -->

[common]\
fun &lt;[A](right-pad-zip.md), [B](right-pad-zip.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](right-pad-zip.md)&gt;.[rightPadZip](right-pad-zip.md)(other: [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](right-pad-zip.md)&gt;): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](right-pad-zip.md), [B](right-pad-zip.md)?&gt;&gt;

Returns a Sequence> containing the zipped values of the two sequences with null for padding on the right.

Example:

import arrow.core.rightPadZip\
\
//sampleStart\
val padRight = sequenceOf(1, 2).rightPadZip(sequenceOf("a"))        // Result: [Pair(1, "a"), Pair(2, null)]\
val padLeft = sequenceOf(1).rightPadZip(sequenceOf("a", "b"))       // Result: [Pair(1, "a")]\
val noPadding = sequenceOf(1, 2).rightPadZip(sequenceOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]\
//sampleEnd\
\
fun main() {\
  println("padRight = $padRight")\
  println("padLeft = $padLeft")\
  println("noPadding = $noPadding")\
}<!--- KNIT example-sequence-10.kt -->
