//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Either](../index.md)/[Companion](index.md)/[lift](lift.md)

# lift

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](lift.md), [B](lift.md), [C](lift.md)&gt; [lift](lift.md)(f: ([B](lift.md)) -&gt; [C](lift.md)): ([Either](../index.md)&lt;[A](lift.md), [B](lift.md)&gt;) -&gt; [Either](../index.md)&lt;[A](lift.md), [C](lift.md)&gt;

Lifts a function (B) -&gt; C to the [Either](../index.md) structure returning a polymorphic function that can be applied over all [Either](../index.md) values in the shape of Either

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
 //sampleStart\
 val f = Either.lift&lt;Int, CharSequence, String&gt; { s: CharSequence -&gt; "$s World" }\
 val either: Either&lt;Int, CharSequence&gt; = "Hello".right()\
 val result = f(either)\
 //sampleEnd\
 println(result)\
}

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](lift.md), [B](lift.md), [C](lift.md), [D](lift.md)&gt; [lift](lift.md)(fa: ([A](lift.md)) -&gt; [C](lift.md), fb: ([B](lift.md)) -&gt; [D](lift.md)): ([Either](../index.md)&lt;[A](lift.md), [B](lift.md)&gt;) -&gt; [Either](../index.md)&lt;[C](lift.md), [D](lift.md)&gt;
