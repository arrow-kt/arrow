//[arrow-core](../../index.md)/[arrow.core](index.md)/[split](split.md)

# split

[common]\
fun &lt;[A](split.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](split.md)&gt;.[split](split.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](split.md)&gt;, [A](split.md)&gt;?

attempt to split the computation, giving access to the first result.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   listOf("A", "B", "C").split()\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-iterable-13.kt -->

[common]\
fun &lt;[A](split.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](split.md)&gt;.[split](split.md)(): [Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-pair/index.html)&lt;[Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](split.md)&gt;, [A](split.md)&gt;?

attempt to split the computation, giving access to the first result.

import arrow.core.split\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result = sequenceOf("A", "B", "C").split()\
  //sampleEnd\
  result?.let { println("(${it.first.toList()}, ${it.second.toList()})") }\
}<!--- KNIT example-sequence-11.kt -->
