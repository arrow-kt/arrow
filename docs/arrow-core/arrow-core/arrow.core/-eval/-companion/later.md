//[arrow-core](../../../../index.md)/[arrow.core](../../index.md)/[Eval](../index.md)/[Companion](index.md)/[later](later.md)

# later

[common]\

@[JvmStatic](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.jvm/-jvm-static/index.html)

inline fun &lt;[A](later.md)&gt; [later](later.md)(crossinline f: () -&gt; [A](later.md)): [Eval.Later](../-later/index.md)&lt;[A](later.md)&gt;

Creates an Eval instance from a function deferring it's evaluation until .value() is invoked memoizing the computed value.

## Parameters

common

| | |
|---|---|
| f | is a function or computation that will be called only once when .value() is invoked for the first time.<br>import arrow.core.*<br>fun main() {<br>//sampleStart<br>  val lazyEvaled = Eval.later { "expensive computation" }<br>  println(lazyEvaled.value())<br>//sampleEnd<br>}<!--- KNIT example-eval-03.kt --><br>"expensive computation" is only computed once since the results are memoized and multiple calls to value() will just return the cached value. |
