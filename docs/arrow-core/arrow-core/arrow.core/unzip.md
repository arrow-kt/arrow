//[arrow-core](../../index.md)/[arrow.core](index.md)/[unzip](unzip.md)

# unzip

[common]\
fun &lt;[A](unzip.md), [B](unzip.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](unzip.md), [B](unzip.md)&gt;&gt;.[unzip](unzip.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](unzip.md)&gt;, [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](unzip.md)&gt;&gt;

unzips the structure holding the resulting elements in an Pair

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
     listOf("A" to 1, "B" to 2).unzip()\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-iterable-09.kt -->

[common]\
inline fun &lt;[A](unzip.md), [B](unzip.md), [C](unzip.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[C](unzip.md)&gt;.[unzip](unzip.md)(fc: ([C](unzip.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](unzip.md), [B](unzip.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](unzip.md)&gt;, [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](unzip.md)&gt;&gt;

after applying the given function unzip the resulting structure into its elements.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   listOf("A:1", "B:2", "C:3").unzip { e -&gt;\
     e.split(":").let {\
       it.first() to it.last()\
     }\
   }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-iterable-10.kt -->

[common]\
fun &lt;[A](unzip.md), [B](unzip.md)&gt; [NonEmptyList](-non-empty-list/index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](unzip.md), [B](unzip.md)&gt;&gt;.[unzip](unzip.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[NonEmptyList](-non-empty-list/index.md)&lt;[A](unzip.md)&gt;, [NonEmptyList](-non-empty-list/index.md)&lt;[B](unzip.md)&gt;&gt;

fun &lt;[A](unzip.md), [B](unzip.md), [C](unzip.md)&gt; [NonEmptyList](-non-empty-list/index.md)&lt;[C](unzip.md)&gt;.[unzip](unzip.md)(f: ([C](unzip.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](unzip.md), [B](unzip.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[NonEmptyList](-non-empty-list/index.md)&lt;[A](unzip.md)&gt;, [NonEmptyList](-non-empty-list/index.md)&lt;[B](unzip.md)&gt;&gt;

fun &lt;[A](unzip.md), [B](unzip.md)&gt; [Option](-option/index.md)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](unzip.md), [B](unzip.md)&gt;&gt;.[unzip](unzip.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](-option/index.md)&lt;[A](unzip.md)&gt;, [Option](-option/index.md)&lt;[B](unzip.md)&gt;&gt;

inline fun &lt;[A](unzip.md), [B](unzip.md), [C](unzip.md)&gt; [Option](-option/index.md)&lt;[C](unzip.md)&gt;.[unzip](unzip.md)(f: ([C](unzip.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](unzip.md), [B](unzip.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](-option/index.md)&lt;[A](unzip.md)&gt;, [Option](-option/index.md)&lt;[B](unzip.md)&gt;&gt;

[common]\
fun &lt;[A](unzip.md), [B](unzip.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](unzip.md), [B](unzip.md)&gt;&gt;.[unzip](unzip.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](unzip.md)&gt;, [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](unzip.md)&gt;&gt;

unzips the structure holding the resulting elements in an Pair

import arrow.core.unzip\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result = sequenceOf("A" to 1, "B" to 2).unzip()\
  //sampleEnd\
  println("(${result.first.toList()}, ${result.second.toList()})")\
}<!--- KNIT example-sequence-15.kt -->

[common]\
fun &lt;[A](unzip.md), [B](unzip.md), [C](unzip.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[C](unzip.md)&gt;.[unzip](unzip.md)(fc: ([C](unzip.md)) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](unzip.md), [B](unzip.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](unzip.md)&gt;, [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](unzip.md)&gt;&gt;

after applying the given function unzip the resulting structure into its elements.

import arrow.core.unzip\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   sequenceOf("A:1", "B:2", "C:3").unzip { e -&gt;\
     e.split(":").let {\
       it.first() to it.last()\
     }\
   }\
  //sampleEnd\
  println("(${result.first.toList()}, ${result.second.toList()})")\
}<!--- KNIT example-sequence-16.kt -->

[common]\
fun &lt;[K](unzip.md), [A](unzip.md), [B](unzip.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unzip.md), [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](unzip.md), [B](unzip.md)&gt;&gt;.[unzip](unzip.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unzip.md), [A](unzip.md)&gt;, [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unzip.md), [B](unzip.md)&gt;&gt;

Unzips the structure holding the resulting elements in an Pair

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
     mapOf("first" to ("A" to 1), "second" to ("B" to 2)).unzip()\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-map-07.kt -->

[common]\
fun &lt;[K](unzip.md), [A](unzip.md), [B](unzip.md), [C](unzip.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unzip.md), [C](unzip.md)&gt;.[unzip](unzip.md)(fc: ([Map.Entry](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/-entry/index.html)&lt;[K](unzip.md), [C](unzip.md)&gt;) -&gt; [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[A](unzip.md), [B](unzip.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unzip.md), [A](unzip.md)&gt;, [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unzip.md), [B](unzip.md)&gt;&gt;

After applying the given function unzip the resulting structure into its elements.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   mapOf("first" to "A:1", "second" to "B:2", "third" to "C:3").unzip { (_, e) -&gt;\
     e.split(":").let {\
       it.first() to it.last()\
     }\
   }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-map-08.kt -->
