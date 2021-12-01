//[arrow-core](../../index.md)/[arrow.core](index.md)/[interleave](interleave.md)

# interleave

[common]\
fun &lt;[A](interleave.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](interleave.md)&gt;.[interleave](interleave.md)(other: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](interleave.md)&gt;): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](interleave.md)&gt;

interleave both computations in a fair way.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val tags = List(10) { "#" }\
  val result =\
   tags.interleave(listOf("A", "B", "C"))\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-iterable-14.kt -->

[common]\
fun &lt;[A](interleave.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](interleave.md)&gt;.[interleave](interleave.md)(other: [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](interleave.md)&gt;): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](interleave.md)&gt;

interleave both computations in a fair way.

import arrow.core.interleave\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val tags = generateSequence { "#" }.take(10)\
  val result =\
   tags.interleave(sequenceOf("A", "B", "C"))\
  //sampleEnd\
  println(result.toList())\
}<!--- KNIT example-sequence-04.kt -->
