//[arrow-core](../../index.md)/[arrow.core](index.md)/[widen](widen.md)

# widen

[common]\
fun &lt;[A](widen.md), [C](widen.md), [B](widen.md) : [C](widen.md)&gt; [Either](-either/index.md)&lt;[A](widen.md), [B](widen.md)&gt;.[widen](widen.md)(): [Either](-either/index.md)&lt;[A](widen.md), [C](widen.md)&gt;

Given [B](widen.md) is a sub type of [C](widen.md), re-type this value from Either to Either

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val string: Either&lt;Int, String&gt; = "Hello".right()\
  val chars: Either&lt;Int, CharSequence&gt; =\
    string.widen&lt;Int, CharSequence, String&gt;()\
  //sampleEnd\
  println(chars)\
}<!--- KNIT example-either-68.kt -->

[common]\
fun &lt;[A](widen.md), [C](widen.md), [B](widen.md) : [C](widen.md)&gt; [Ior](-ior/index.md)&lt;[A](widen.md), [B](widen.md)&gt;.[widen](widen.md)(): [Ior](-ior/index.md)&lt;[A](widen.md), [C](widen.md)&gt;

Given [B](widen.md) is a sub type of [C](widen.md), re-type this value from Ior to Ior

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val string: Ior&lt;Int, String&gt; = Ior.Right("Hello")\
  val chars: Ior&lt;Int, CharSequence&gt; =\
    string.widen&lt;Int, CharSequence, String&gt;()\
  //sampleEnd\
  println(chars)\
}<!--- KNIT example-ior-26.kt -->

[common]\
fun &lt;[B](widen.md), [A](widen.md) : [B](widen.md)&gt; [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[A](widen.md)&gt;.[widen](widen.md)(): [Iterable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-iterable/index.html)&lt;[B](widen.md)&gt;

Given [A](widen.md) is a sub type of [B](widen.md), re-type this value from Iterable<A> to Iterable<B>

Kind -> Kind

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
 //sampleStart\
 val result: Iterable&lt;CharSequence&gt; =\
   listOf("Hello World").widen()\
 //sampleEnd\
 println(result)\
}

[common]\
fun &lt;[B](widen.md), [A](widen.md) : [B](widen.md)&gt; [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[A](widen.md)&gt;.[widen](widen.md)(): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[B](widen.md)&gt;

fun &lt;[K](widen.md), [B](widen.md), [A](widen.md) : [B](widen.md)&gt; [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](widen.md), [A](widen.md)&gt;.[widen](widen.md)(): [Map](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-map/index.html)&lt;[K](widen.md), [B](widen.md)&gt;

[common]\
fun &lt;[B](widen.md), [A](widen.md) : [B](widen.md)&gt; [Option](-option/index.md)&lt;[A](widen.md)&gt;.[widen](widen.md)(): [Option](-option/index.md)&lt;[B](widen.md)&gt;

Given [A](widen.md) is a sub type of [B](widen.md), re-type this value from Option<A> to Option<B>

Option<A> -> Option<B>

import arrow.core.Option\
import arrow.core.some\
import arrow.core.widen\
\
fun main(args: Array&lt;String&gt;) {\
 val result: Option&lt;CharSequence&gt; =\
 //sampleStart\
 "Hello".some().map({ "$it World" }).widen()\
 //sampleEnd\
 println(result)\
}

[common]\
fun &lt;[B](widen.md), [A](widen.md) : [B](widen.md)&gt; [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[A](widen.md)&gt;.[widen](widen.md)(): [Sequence](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.sequences/-sequence/index.html)&lt;[B](widen.md)&gt;

Given [A](widen.md) is a sub type of [B](widen.md), re-type this value from Sequence<A> to Sequence<B>

Kind -> Kind

import arrow.core.widen\
\
fun main(args: Array&lt;String&gt;) {\
 //sampleStart\
 val result: Sequence&lt;CharSequence&gt; =\
   sequenceOf("Hello World").widen()\
 //sampleEnd\
 println(result)\
}

[common]\
fun &lt;[E](widen.md), [B](widen.md), [A](widen.md) : [B](widen.md)&gt; [Validated](-validated/index.md)&lt;[E](widen.md), [A](widen.md)&gt;.[widen](widen.md)(): [Validated](-validated/index.md)&lt;[E](widen.md), [B](widen.md)&gt;

Given [A](widen.md) is a sub type of [B](widen.md), re-type this value from Validated to Validated

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val string: Validated&lt;Int, String&gt; = "Hello".invalid()\
  val chars: Validated&lt;Int, CharSequence&gt; =\
    string.widen&lt;Int, CharSequence, String&gt;()\
  //sampleEnd\
  println(chars)\
}<!--- KNIT example-validated-20.kt -->
