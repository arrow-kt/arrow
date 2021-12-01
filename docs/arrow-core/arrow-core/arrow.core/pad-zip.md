//[arrow-core](../../index.md)/[arrow.core](index.md)/[padZip](pad-zip.md)

# padZip

[common]\
fun &lt;[A](pad-zip.md), [B](pad-zip.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](pad-zip.md)&gt;.[padZip](pad-zip.md)(other: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](pad-zip.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](pad-zip.md)?, [B](pad-zip.md)?&gt;&gt;

Returns a List> containing the zipped values of the two lists with null for padding.

Example:

import arrow.core.*\
\
//sampleStart\
val padRight = listOf(1, 2).padZip(listOf("a"))        // Result: [Pair(1, "a"), Pair(2, null)]\
val padLeft = listOf(1).padZip(listOf("a", "b"))       // Result: [Pair(1, "a"), Pair(null, "b")]\
val noPadding = listOf(1, 2).padZip(listOf("a", "b"))  // Result: [Pair(1, "a"), Pair(2, "b")]\
//sampleEnd\
\
fun main() {\
  println("padRight = $padRight")\
  println("padLeft = $padLeft")\
  println("noPadding = $noPadding")\
}<!--- KNIT example-iterable-01.kt -->

[common]\
inline fun &lt;[A](pad-zip.md), [B](pad-zip.md), [C](pad-zip.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](pad-zip.md)&gt;.[padZip](pad-zip.md)(other: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](pad-zip.md)&gt;, fa: ([A](pad-zip.md)?, [B](pad-zip.md)?) -&gt; [C](pad-zip.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[C](pad-zip.md)&gt;

Returns a List containing the result of applying some transformation (A?, B?) -&gt; C on a zip.

Example:

import arrow.core.*\
\
//sampleStart\
val padZipRight = listOf(1, 2).padZip(listOf("a")) { l, r -&gt; l to r }     // Result: [Pair(1, "a"), Pair(2, null)]\
val padZipLeft = listOf(1).padZip(listOf("a", "b")) { l, r -&gt; l to r }    // Result: [Pair(1, "a"), Pair(null, "b")]\
val noPadding = listOf(1, 2).padZip(listOf("a", "b")) { l, r -&gt; l to r }  // Result: [Pair(1, "a"), Pair(2, "b")]\
//sampleEnd\
\
fun main() {\
  println("padZipRight = $padZipRight")\
  println("padZipLeft = $padZipLeft")\
  println("noPadding = $noPadding")\
}<!--- KNIT example-iterable-02.kt -->

[common]\
fun &lt;[A](pad-zip.md), [B](pad-zip.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](pad-zip.md)&gt;.[padZip](pad-zip.md)(other: [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](pad-zip.md)&gt;): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](pad-zip.md)?, [B](pad-zip.md)?&gt;&gt;

Returns a Sequence> containing the zipped values of the two sequences with null for padding.

Example:

import arrow.core.padZip\
\
//sampleStart\
val padRight = sequenceOf(1, 2).padZip(sequenceOf("a"))       // Result: [Pair(1, "a"), Pair(2, null)]\
val padLeft = sequenceOf(1).padZip(sequenceOf("a", "b"))      // Result: [Pair(1, "a"), Pair(null, "b")]\
val noPadding = sequenceOf(1, 2).padZip(sequenceOf("a", "b")) // Result: [Pair(1, "a"), Pair(2, "b")]\
//sampleEnd\
\
fun main() {\
  println("padRight = $padRight")\
  println("padLeft = $padLeft")\
  println("noPadding = $noPadding")\
}<!--- KNIT example-sequence-07.kt -->

[common]\
fun &lt;[A](pad-zip.md), [B](pad-zip.md), [C](pad-zip.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](pad-zip.md)&gt;.[padZip](pad-zip.md)(other: [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](pad-zip.md)&gt;, fa: ([A](pad-zip.md)?, [B](pad-zip.md)?) -&gt; [C](pad-zip.md)): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[C](pad-zip.md)&gt;

Returns a Sequence containing the result of applying some transformation (A?, B?) -&gt; C on a zip.

Example:

import arrow.core.padZip\
\
//sampleStart\
val padZipRight = sequenceOf(1, 2).padZip(sequenceOf(3)) { l, r -&gt; (l?:0) + (r?:0) }  // Result: [4, 2]\
val padZipLeft = sequenceOf(1).padZip(sequenceOf(3, 4)) { l, r -&gt; (l?:0) + (r?:0) }   // Result: [4, 4]\
val noPadding = sequenceOf(1, 2).padZip(sequenceOf(3, 4)) { l, r -&gt; (l?:0) + (r?:0) } // Result: [4, 6]\
//sampleEnd\
\
fun main() {\
  println("padZipRight = $padZipRight")\
  println("padZipLeft = $padZipLeft")\
  println("noPadding = $noPadding")\
}<!--- KNIT example-sequence-08.kt -->

[common]\
fun &lt;[K](pad-zip.md), [A](pad-zip.md), [B](pad-zip.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](pad-zip.md), [A](pad-zip.md)&gt;.[padZip](pad-zip.md)(other: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](pad-zip.md), [B](pad-zip.md)&gt;): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](pad-zip.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](pad-zip.md)?, [B](pad-zip.md)?&gt;&gt;

Align two structures as in zip, but filling in blanks with null.

[common]\
fun &lt;[K](pad-zip.md), [A](pad-zip.md), [B](pad-zip.md), [C](pad-zip.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](pad-zip.md), [A](pad-zip.md)&gt;.[padZip](pad-zip.md)(other: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](pad-zip.md), [B](pad-zip.md)&gt;, fa: ([K](pad-zip.md), [A](pad-zip.md)?, [B](pad-zip.md)?) -&gt; [C](pad-zip.md)): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](pad-zip.md), [C](pad-zip.md)&gt;
