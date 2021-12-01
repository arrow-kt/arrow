//[arrow-core](../../index.md)/[arrow.core](index.md)/[unalign](unalign.md)

# unalign

[common]\
fun &lt;[A](unalign.md), [B](unalign.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[Ior](-ior/index.md)&lt;[A](unalign.md), [B](unalign.md)&gt;&gt;.[unalign](unalign.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](unalign.md)&gt;, [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](unalign.md)&gt;&gt;

splits a union into its component parts.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   listOf(("A" to 1).bothIor(), ("B" to 2).bothIor(), "C".leftIor())\
     .unalign()\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-iterable-11.kt -->

[common]\
inline fun &lt;[A](unalign.md), [B](unalign.md), [C](unalign.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[C](unalign.md)&gt;.[unalign](unalign.md)(fa: ([C](unalign.md)) -&gt; [Ior](-ior/index.md)&lt;[A](unalign.md), [B](unalign.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](unalign.md)&gt;, [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](unalign.md)&gt;&gt;

after applying the given function, splits the resulting union shaped structure into its components parts

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
     listOf(1, 2, 3).unalign {\
       it.leftIor()\
     }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-iterable-12.kt -->

[common]\
fun &lt;[A](unalign.md), [B](unalign.md)&gt; [Option](-option/index.md)&lt;[Ior](-ior/index.md)&lt;[A](unalign.md), [B](unalign.md)&gt;&gt;.[unalign](unalign.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](-option/index.md)&lt;[A](unalign.md)&gt;, [Option](-option/index.md)&lt;[B](unalign.md)&gt;&gt;

inline fun &lt;[A](unalign.md), [B](unalign.md), [C](unalign.md)&gt; [Option](-option/index.md)&lt;[C](unalign.md)&gt;.[unalign](unalign.md)(f: ([C](unalign.md)) -&gt; [Ior](-ior/index.md)&lt;[A](unalign.md), [B](unalign.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Option](-option/index.md)&lt;[A](unalign.md)&gt;, [Option](-option/index.md)&lt;[B](unalign.md)&gt;&gt;

[common]\
fun &lt;[A](unalign.md), [B](unalign.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[Ior](-ior/index.md)&lt;[A](unalign.md), [B](unalign.md)&gt;&gt;.[unalign](unalign.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](unalign.md)&gt;, [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](unalign.md)&gt;&gt;

splits an union into its component parts.

import arrow.core.bothIor\
import arrow.core.leftIor\
import arrow.core.unalign\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result = sequenceOf(("A" to 1).bothIor(), ("B" to 2).bothIor(), "C".leftIor()).unalign()\
  //sampleEnd\
  println("(${result.first.toList()}, ${result.second.toList()})")\
}<!--- KNIT example-sequence-12.kt -->

[common]\
fun &lt;[A](unalign.md), [B](unalign.md), [C](unalign.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[C](unalign.md)&gt;.[unalign](unalign.md)(fa: ([C](unalign.md)) -&gt; [Ior](-ior/index.md)&lt;[A](unalign.md), [B](unalign.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](unalign.md)&gt;, [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](unalign.md)&gt;&gt;

after applying the given function, splits the resulting union shaped structure into its components parts

import arrow.core.leftIor\
import arrow.core.unalign\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result = sequenceOf(1, 2, 3).unalign { it.leftIor() }\
  //sampleEnd\
  println("(${result.first.toList()}, ${result.second.toList()})")\
}<!--- KNIT example-sequence-13.kt -->

[common]\
fun &lt;[K](unalign.md), [A](unalign.md), [B](unalign.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unalign.md), [Ior](-ior/index.md)&lt;[A](unalign.md), [B](unalign.md)&gt;&gt;.[unalign](unalign.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unalign.md), [A](unalign.md)&gt;, [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unalign.md), [B](unalign.md)&gt;&gt;

Splits a union into its component parts.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   mapOf(\
     "first" to ("A" to 1).bothIor(),\
     "second" to ("B" to 2).bothIor(),\
     "third" to "C".leftIor()\
   ).unalign()\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-map-05.kt -->

[common]\
fun &lt;[K](unalign.md), [A](unalign.md), [B](unalign.md), [C](unalign.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unalign.md), [C](unalign.md)&gt;.[unalign](unalign.md)(fa: ([Map.Entry](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/-entry/index.html)&lt;[K](unalign.md), [C](unalign.md)&gt;) -&gt; [Ior](-ior/index.md)&lt;[A](unalign.md), [B](unalign.md)&gt;): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unalign.md), [A](unalign.md)&gt;, [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](unalign.md), [B](unalign.md)&gt;&gt;

after applying the given function, splits the resulting union shaped structure into its components parts

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
     mapOf("1" to 1, "2" to 2, "3" to 3)\
       .unalign { it.leftIor() }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-map-06.kt -->
