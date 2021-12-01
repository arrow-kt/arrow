//[arrow-core](../../index.md)/[arrow.core](index.md)/[unweave](unweave.md)

# unweave

[common]\
fun &lt;[A](unweave.md), [B](unweave.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](unweave.md)&gt;.[unweave](unweave.md)(ffa: ([A](unweave.md)) -&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](unweave.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](unweave.md)&gt;

Fair conjunction. Similarly to interleave

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   listOf(1,2,3).unweave { i -&gt; listOf("$i, ${i + 1}") }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-iterable-15.kt -->

[common]\
fun &lt;[A](unweave.md), [B](unweave.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](unweave.md)&gt;.[unweave](unweave.md)(ffa: ([A](unweave.md)) -&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](unweave.md)&gt;): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](unweave.md)&gt;

Fair conjunction. Similarly to interleave

import arrow.core.unweave\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result = sequenceOf(1,2,3).unweave { i -&gt; sequenceOf("$i, ${i + 1}") }\
  //sampleEnd\
  println(result.toList())\
}<!--- KNIT example-sequence-14.kt -->
