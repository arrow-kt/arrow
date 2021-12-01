//[arrow-core](../../index.md)/[arrow.core](index.md)/[align](align.md)

# align

[common]\
inline fun &lt;[A](align.md), [B](align.md), [C](align.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](align.md)&gt;.[align](align.md)(b: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](align.md)&gt;, fa: ([Ior](-ior/index.md)&lt;[A](align.md), [B](align.md)&gt;) -&gt; [C](align.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[C](align.md)&gt;

Combines two structures by taking the union of their shapes and combining the elements with the given function.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   listOf("A", "B").align(listOf(1, 2, 3)) {\
     "$it"\
   }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-iterable-07.kt -->

[common]\
fun &lt;[A](align.md), [B](align.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](align.md)&gt;.[align](align.md)(b: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](align.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Ior](-ior/index.md)&lt;[A](align.md), [B](align.md)&gt;&gt;

Combines two structures by taking the union of their shapes and using Ior to hold the elements.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
    listOf("A", "B").align(listOf(1, 2, 3))\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-iterable-08.kt -->

[common]\
fun &lt;[A](align.md), [B](align.md), [C](align.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](align.md)&gt;.[align](align.md)(b: [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](align.md)&gt;, fa: ([Ior](-ior/index.md)&lt;[A](align.md), [B](align.md)&gt;) -&gt; [C](align.md)): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[C](align.md)&gt;

Combines two structures by taking the union of their shapes and combining the elements with the given function.

import arrow.core.align\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   sequenceOf("A", "B").align(sequenceOf(1, 2, 3)) {\
     "$it"\
   }\
  //sampleEnd\
  println(result.toList())\
}<!--- KNIT example-sequence-01.kt -->

[common]\
fun &lt;[A](align.md), [B](align.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](align.md)&gt;.[align](align.md)(b: [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](align.md)&gt;): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[Ior](-ior/index.md)&lt;[A](align.md), [B](align.md)&gt;&gt;

Combines two structures by taking the union of their shapes and using Ior to hold the elements.

import arrow.core.align\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
    sequenceOf("A", "B").align(sequenceOf(1, 2, 3))\
  //sampleEnd\
  println(result.toList())\
}<!--- KNIT example-sequence-02.kt -->

[common]\
fun &lt;[K](align.md), [A](align.md), [B](align.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](align.md), [A](align.md)&gt;.[align](align.md)(b: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](align.md), [B](align.md)&gt;): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](align.md), [Ior](-ior/index.md)&lt;[A](align.md), [B](align.md)&gt;&gt;

Combines two structures by taking the union of their shapes and using Ior to hold the elements.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   mapOf("1" to 1, "2" to 2).align(mapOf("1" to 1, "2" to 2, "3" to 3))\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-map-03.kt -->

[common]\
fun &lt;[K](align.md), [A](align.md), [B](align.md), [C](align.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](align.md), [A](align.md)&gt;.[align](align.md)(b: [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](align.md), [B](align.md)&gt;, fa: ([Map.Entry](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/-entry/index.html)&lt;[K](align.md), [Ior](-ior/index.md)&lt;[A](align.md), [B](align.md)&gt;&gt;) -&gt; [C](align.md)): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](align.md), [C](align.md)&gt;

Combines two structures by taking the union of their shapes and combining the elements with the given function.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   mapOf("1" to 1, "2" to 2).align(mapOf("1" to 1, "2" to 2, "3" to 3)) { (_, a) -&gt;\
     "$a"\
   }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-map-04.kt -->
