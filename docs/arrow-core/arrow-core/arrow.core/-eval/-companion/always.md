//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Eval](../index.md)/[Companion](index.md)/[always](always.md)

# always

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

inline fun &lt;[A](always.md)&gt; [always](always.md)(crossinline f: () -&gt; [A](always.md)): [Eval.Always](../-always/index.md)&lt;[A](always.md)&gt;

Creates an Eval instance from a function deferring it's evaluation until .value() is invoked recomputing each time .value() is invoked.

## Parameters

common

| | |
|---|---|
| f | is a function or computation that will be called every time .value() is invoked.<br>import arrow.core.*<br>fun main() {<br>//sampleStart<br>  val alwaysEvaled = Eval.always { "expensive computation" }<br>  println(alwaysEvaled.value())<br>//sampleEnd<br>}<!--- KNIT example-eval-04.kt --><br>"expensive computation" is computed every time value() is invoked. |
