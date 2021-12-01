//[arrow-core](../../index.md)/[arrow.core](index.md)/[ifThen](if-then.md)

# ifThen

[common]\
inline fun &lt;[A](if-then.md), [B](if-then.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](if-then.md)&gt;.[ifThen](if-then.md)(fb: [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](if-then.md)&gt;, ffa: ([A](if-then.md)) -&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](if-then.md)&gt;): [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](if-then.md)&gt;

Logical conditional. The equivalent of Prolog's soft-cut. If its first argument succeeds at all, then the results will be fed into the success branch. Otherwise, the failure branch is taken.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   listOf(1,2,3).ifThen(listOf("empty")) { i -&gt;\
     listOf("$i, ${i + 1}")\
   }\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-iterable-16.kt -->

[common]\
fun &lt;[A](if-then.md), [B](if-then.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](if-then.md)&gt;.[ifThen](if-then.md)(fb: [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](if-then.md)&gt;, ffa: ([A](if-then.md)) -&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](if-then.md)&gt;): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](if-then.md)&gt;

Logical conditional. The equivalent of Prolog's soft-cut. If its first argument succeeds at all, then the results will be fed into the success branch. Otherwise, the failure branch is taken.

import arrow.core.ifThen\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val result =\
   sequenceOf(1,2,3).ifThen(sequenceOf("empty")) { i -&gt;\
     sequenceOf("$i, ${i + 1}")\
   }\
  //sampleEnd\
  println(result.toList())\
}<!--- KNIT example-sequence-03.kt -->
