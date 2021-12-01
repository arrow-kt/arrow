//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Validated](../index.md)/[Companion](index.md)/[lift](lift.md)

# lift

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

inline fun &lt;[E](lift.md), [A](lift.md), [B](lift.md)&gt; [lift](lift.md)(crossinline f: ([A](lift.md)) -&gt; [B](lift.md)): ([Validated](../index.md)&lt;[E](lift.md), [A](lift.md)&gt;) -&gt; [Validated](../index.md)&lt;[E](lift.md), [B](lift.md)&gt;

Lifts a function A -&gt; B to the [Validated](../index.md) structure.

A -&gt; B -&gt; Validated&lt;E, A&gt; -&gt; Validated&lt;E, B&gt;

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  val result =\
  //sampleStart\
  Validated.lift { s: CharSequence -&gt; "$s World" }("Hello".valid())\
  //sampleEnd\
  println(result)\
}<!--- KNIT example-validated-15.kt -->

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

inline fun &lt;[A](lift.md), [B](lift.md), [C](lift.md), [D](lift.md)&gt; [lift](lift.md)(crossinline fl: ([A](lift.md)) -&gt; [C](lift.md), crossinline fr: ([B](lift.md)) -&gt; [D](lift.md)): ([Validated](../index.md)&lt;[A](lift.md), [B](lift.md)&gt;) -&gt; [Validated](../index.md)&lt;[C](lift.md), [D](lift.md)&gt;

Lifts two functions to the Bifunctor type.

import arrow.core.*\
\
fun main(args: Array&lt;String&gt;) {\
  //sampleStart\
  val f = Validated.lift(String::toUpperCase, Int::inc)\
  val res1 = f("test".invalid())\
  val res2 = f(1.valid())\
  //sampleEnd\
  println("res1: $res1")\
  println("res2: $res2")\
}<!--- KNIT example-validated-16.kt -->
