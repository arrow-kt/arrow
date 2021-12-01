//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Ior](../index.md)/[Companion](index.md)/[lift](lift.md)

# lift

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](lift.md), [B](lift.md), [C](lift.md)&gt; [lift](lift.md)(f: ([B](lift.md)) -&gt; [C](lift.md)): ([Ior](../index.md)&lt;[A](lift.md), [B](lift.md)&gt;) -&gt; [Ior](../index.md)&lt;[A](lift.md), [C](lift.md)&gt;

Lifts a function (B) -&gt; C to the [Ior](../index.md) structure returning a polymorphic function that can be applied over all [Ior](../index.md) values in the shape of Ior

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
 //sampleStart\
 val f = Ior.lift&lt;Int, CharSequence, String&gt; { s: CharSequence -&gt; "$s World" }\
 val ior: Ior&lt;Int, CharSequence&gt; = Ior.Right("Hello")\
 val result = f(ior)\
 //sampleEnd\
 println(result)\
}

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

fun &lt;[A](lift.md), [B](lift.md), [C](lift.md), [D](lift.md)&gt; [lift](lift.md)(fa: ([A](lift.md)) -&gt; [C](lift.md), fb: ([B](lift.md)) -&gt; [D](lift.md)): ([Ior](../index.md)&lt;[A](lift.md), [B](lift.md)&gt;) -&gt; [Ior](../index.md)&lt;[C](lift.md), [D](lift.md)&gt;
